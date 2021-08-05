package com.chenfan.finance.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.ccp.plug.rpc.service.NotifyRemoteService;
import com.chenfan.ccp.plug.rpc.service.impl.DefaultNotifyRemoteServiceImpl;
import com.chenfan.ccp.util.start.ApplicationContextUtil;
import com.chenfan.common.vo.TaskFlowVo;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.dao.CfBsOperationLogDao;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.model.CfBsOperationLog;
import com.chenfan.finance.model.CfCharge;
import com.chenfan.finance.producer.U8Produce;
import com.chenfan.finance.server.McnRemoteServer;
import com.chenfan.finance.server.TaskRemoteServer;
import com.chenfan.finance.server.remote.request.CommentAddDTO;
import com.dingtalk.api.DingTalkClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.Assert;

import javax.validation.ValidationException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liran
 */
@Slf4j
public class OperateUtil {

    /**
     * 根据属性名称保留金额为两位小数
     * @param origin
     * @param propNames
     */
    public static void onConvertedDecimal(Object origin, String ...propNames){
            for (String propName:propNames) {
                try {
                        Field field = origin.getClass().getDeclaredField(propName);
                        field.setAccessible(true);
                        if(field.getType().isInstance(BigDecimal.ZERO)){
                            BigDecimal o = (BigDecimal)field.get(origin);
                            if(Objects.nonNull(o)){
                                field.set(origin, o.setScale(2,BigDecimal.ROUND_HALF_UP));
                            }
                        }
                }catch (Exception e){
                  log.error("反射赋值对象属性异常", e);
                }
            }
    }
    public static void onConvertedDecimalBySuper(Object origin, String ...propNames){
        for (String propName:propNames) {
            try {
                Field field = origin.getClass().getSuperclass().getDeclaredField(propName);
                field.setAccessible(true);
                if(field.getType().isInstance(BigDecimal.ZERO)){
                    BigDecimal o = (BigDecimal)field.get(origin);
                    if(Objects.nonNull(o)){
                        field.set(origin, o.setScale(2,BigDecimal.ROUND_HALF_UP));
                    }
                }
            }catch (Exception e){
                log.error("反射赋值对象属性异常", e);
            }
        }
    }

    /**
     * 根据反射赋值
     * @param origin
     * @param propName
     * @param value
     * @return
     */
    public static Object setValueByPropName(Object origin, String propName, Object value) {
        try {
            Field field = origin.getClass().getDeclaredField(propName);
            field.setAccessible(true);
            if (field.getType().isInstance(LocalDateTime.now()) && value.getClass().isInstance(new Date())) {
                value = dateToLocalDateTime((Date) value);
            }
            field.set(origin, value);
        } catch (Exception e) {
            log.error("反射赋值对象属性异常", e);
            return null;
        }
        return value;
    }

    /**
     * @param origin
     * @param userVO
     */

    public static void onSave(Object origin, UserVO userVO) {
        if (userVO == null) {
            throw new ValidationException("无法获取到登录用户");
        }
        setValueByPropName(origin, "createBy", userVO.getUserId());
        setValueByPropName(origin, "updateBy", userVO.getUserId());
        Date now = new Date();
        setValueByPropName(origin, "createDate", now);
        setValueByPropName(origin, "updateDate", now);
        setValueByPropName(origin, "createName", userVO.getRealName());
        setValueByPropName(origin, "updateName", userVO.getRealName());

        setValueByPropName(origin, "companyId", userVO.getCompanyId());

    }

    /**
     * @param origin
     * @param userVO
     */

    public static void onUpdate(Object origin, UserVO userVO) {
        if (userVO == null) {
            throw new ValidationException("无法获取到登录用户");
        }
        setValueByPropName(origin, "updateBy", userVO.getUserId());
        Date now = new Date();
        setValueByPropName(origin, "updateDate", now);
        setValueByPropName(origin, "updateName", userVO.getRealName());
    }

    /**
     * Date -> LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static Date getEndTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    static ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public static ReentrantLock getLock(String key, OperateLockEnum type) {
        key = type.getKey() + key;
        Assert.isTrue(lockMap.get(key) == null, type.getErrMsg() + "，中...，请稍后再试");
        lockMap.putIfAbsent(key, new ReentrantLock());
        return lockMap.get(key);
    }

    public static void removeLock(String key, OperateLockEnum type, ReentrantLock lock) {
        key = type.getKey() + key;
        if (lockMap.get(key).equals(lock)) {
            lockMap.remove(key);
        }
    }

    /**
     * 插入操作日字记录 (如果需要作废的时候发送EOP 通知，请在调用前使用CFRequestHolder.setUserThreadLocal(UserVo user) )
     * @param operationBsTypeEnum 业务类型
     * @param operationTypeEnum 操作类型
     * @param businessCode 业务编码
     * @param businessId 业务主键
     */
    public static void insertOperationLog(OperationBsTypeEnum operationBsTypeEnum,
                                          OperationTypeEnum operationTypeEnum,String businessCode,Long businessId){
        try {
            CfBsOperationLog cfBsOperationLog = new CfBsOperationLog();
            AuthorizationUtil bean = ApplicationContextUtil.getContext().getBean(AuthorizationUtil.class);
            UserVO user = bean.getUser();
            cfBsOperationLog.setOperationContent(CFRequestHolder.getInvalidThreadLocal());
            cfBsOperationLog.setBusinessType(operationBsTypeEnum.getCode());
            cfBsOperationLog.setOperationType(operationTypeEnum.getCode());
            cfBsOperationLog.setOperationDate(LocalDateTime.now());
            cfBsOperationLog.setOperationUserId(user.getUserId());
            cfBsOperationLog.setOperationUserName(user.getRealName());
            cfBsOperationLog.setBusinessCode(businessCode);
            cfBsOperationLog.setBusinessId(businessId);
            insertOperationLog(cfBsOperationLog);
        }catch (RuntimeException r){
             log.error("插入业务日志异常",r);
        }
    }

    /**
     * 插入操作日志记录
     * @param cfBsOperationLog
     */
    public static void insertOperationLog(CfBsOperationLog cfBsOperationLog){
        try {
            CfBsOperationLogDao cfBsOperationLogDao = ApplicationContextUtil.getContext().getBean(CfBsOperationLogDao.class);
            cfBsOperationLogDao.insert(cfBsOperationLog);
            if(Objects.nonNull(CFRequestHolder.getUserThreadLocal())&&Objects.equals(cfBsOperationLog.getOperationType(),OperationTypeEnum.OPERATION_INVALID.getCode())){
                DefaultNotifyRemoteServiceImpl bean = ApplicationContextUtil.getContext().getBean(DefaultNotifyRemoteServiceImpl.class);
                UserVO userThreadLocal = CFRequestHolder.getUserThreadLocal();
                OperationBsTypeEnum enumByCode = OperationBsTypeEnum.getEnumByCode(cfBsOperationLog.getBusinessType());
                bean.saveNotify(cfBsOperationLog.getBusinessId(), MessageEnum.NOTIFY_MESSAGE.getNotifyType(), 1, -1L, "消息", enumByCode.getMessageUrl(), enumByCode.getJumpUrlType(), String.format("您有一笔%s【%s】被作废",enumByCode.getMsg(),cfBsOperationLog.getBusinessCode()), userThreadLocal.getUserId(), userThreadLocal.getRealName() ,null, "", cfBsOperationLog.getBusinessCode());
            }
            if(Objects.equals(cfBsOperationLog.getOperationType(),OperationTypeEnum.OPERATION_INVALID.getCode())&&Objects.equals(cfBsOperationLog.getBusinessType(),OperationBsTypeEnum.OPERATION_BS_MCN_TAX.getCode())){
                ApplicationContextUtil.getContext().getBean(McnRemoteServer.class).addComment(
                        CommentAddDTO.builder()
                                .businessCode(cfBsOperationLog.getBusinessCode())
                                .businessId(cfBsOperationLog.getBusinessId())
                                .businessType(NumberEnum.FIVE.getCode())
                                .commentContent(StringUtils.isNotBlank(cfBsOperationLog.getOperationContent())? "作废："+cfBsOperationLog.getOperationContent():"作废")
                        .build()
                );
            }
            CFRequestHolder.setInvalidThreadLocal(null);
        }catch (RuntimeException re){
            log.error("插入业务日志异常",re);
        }
    }

    /**
     * 获取当前业务的操作日字记录
     * @param operationBsTypeEnum  财务的业务类型
     * @param businessCode 业务编码 不建议使用此参数（日志数据此参数可能为null）
     * @param businessId 业务id 强烈建议使用此参数
     * @return
     */
    public static List<CfBsOperationLog> selectOperationLogsByBs(OperationBsTypeEnum operationBsTypeEnum, String businessCode,Long businessId){
        LambdaQueryWrapper<CfBsOperationLog> lambdaQueryWrapper = Wrappers.<CfBsOperationLog>lambdaQuery().eq(CfBsOperationLog::getBusinessType, operationBsTypeEnum.getCode());
        if(Objects.nonNull(businessId)){
            lambdaQueryWrapper.apply("business_id ={0}",businessId);
        }else {
            lambdaQueryWrapper.apply("business_code ={0}",businessCode);
        }
        CfBsOperationLogDao cfBsOperationLogDao = ApplicationContextUtil.getContext().getBean(CfBsOperationLogDao.class);
        List<CfBsOperationLog> cfBsOperationLogs = cfBsOperationLogDao.selectList(lambdaQueryWrapper.orderByDesc(CfBsOperationLog::getId));
        cfBsOperationLogs.forEach(x->x.setOperationTypeName(OperationTypeEnum.getMsgByCode(x.getOperationType())));
        return cfBsOperationLogs;
    }

}
