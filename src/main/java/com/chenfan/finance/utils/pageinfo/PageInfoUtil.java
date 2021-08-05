package com.chenfan.finance.utils.pageinfo;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.ccp.common.result.R;
import com.chenfan.code.generate.CodeGenerateClient;
import com.chenfan.code.generate.vo.CodeGenerateVO;
import com.chenfan.common.config.Constant;
import com.chenfan.common.dto.PagerDTO;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.dao.ApprovalFlowMapper;
import com.chenfan.finance.dao.CfMultyImageMapper;
import com.chenfan.finance.enums.MultyImageEnum;
import com.chenfan.finance.exception.FinanceBizState;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.ApprovalFlow;
import com.chenfan.finance.model.CfMultyImage;
import com.chenfan.finance.server.BaseInfoRemoteServer;
import com.chenfan.finance.server.remote.model.BaseDicts;
import com.chenfan.finance.service.common.ApprovalJson;
import com.chenfan.finance.utils.AuthorizationUtil;
import com.chenfan.finance.utils.FileUtil;
import com.chenfan.finance.utils.RpcUtil;
import com.chenfan.finance.utils.pageinfo.excel.CustomExcelImportService;
import com.chenfan.process.client.ProcessClient;
import com.chenfan.process.vo.ApprovalVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: xuxianbei
 * Date: 2020/8/31
 * Time: 14:49
 * Version:V1.0
 */
@Slf4j
@Component
public class PageInfoUtil implements ApplicationContextAware {

    @Resource
    protected ApprovalFlowMapper approvalFlowMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private CodeGenerateClient codeGenerateClient;

    private ApplicationContext applicationContext;

    @Autowired
    private ProcessClient processClient;

    @Autowired
    private BaseInfoRemoteServer baseInfoRemoteServer;

    @Resource
    private CfMultyImageMapper cfMultyImageMapper;


    public static LocalDateTime dateToLocalDateTime(Date dateA) {
        return TimeThreadSafeUtils.dateToLocalDateTime(dateA);
    }


    /**
     * 初始化实例并填充默认值
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T initEntityAndFill(Class<T> tClass) {
        T t = null;
        try {
            t = tClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        objectFillDefault(t);
        return t;
    }

    /**
     * 初始化实例
     *
     * @param tClass
     * @param init
     * @param <T>
     * @return
     */
    public static <T> T initEntity(Class<T> tClass, Object init, String... ignoreProperties) {
        T t = null;
        try {
            t = tClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        if (Objects.nonNull(ignoreProperties)) {
            BeanUtils.copyProperties(init, t, ignoreProperties);
        } else {
            BeanUtils.copyProperties(init, t);
        }
        return t;
    }

    /**
     * 初始化实例
     *
     * @param tClass
     * @param init
     * @param <T>
     * @return
     */
    public static <T> T initEntity(Class<T> tClass, Object init) {
        return initEntity(tClass, init, null);
    }

    /**
     * 类型转换
     *
     * @param totalList 从数据库中拿到的list
     * @param volist    返回界面的list
     * @param <T>
     * @return
     */
    public static <T> PageInfo<T> toPageInfo(List<?> totalList, List<T> volist) {
        PageInfo<T> resultPageInfo = new PageInfo(volist);
        PageInfo old = new PageInfo<>(totalList);
        BeanUtils.copyProperties(old, resultPageInfo);
        resultPageInfo.setList(volist);
        resultPageInfo.setTotal(old.getTotal());
        return resultPageInfo;
    }

    /**
     * 类型转换,分页
     * 主要针对简单对象转换。
     *
     * @param totalList 从数据库中拿到的list
     * @param <T>
     * @return
     */
    public static <T, R> PageInfo<T> toPageInfo(List<R> totalList, Class<T> clazz, BiConsumer<R, T> biConsumer) {
        List<T> ts = listToCustomList(totalList, clazz, biConsumer);
        return PageInfoUtil.toPageInfo(totalList, ts);
    }

    /**
     * 类型转换,分页
     * 主要针对简单对象转换。
     *
     * @param totalList 从数据库中拿到的list
     * @param <T>
     * @return
     */
    public static <T> PageInfo<T> toPageInfo(List<?> totalList, Class<T> clazz) {
        return toPageInfo(totalList, clazz, null);
    }

    public static <T, R> List<T> listToCustomList(List<R> oldItems, Class<T> clazz, BiConsumer biConsumer) {
        List<T> ts = oldItems.stream().map(item -> {
            T t = null;
            try {
                t = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            BeanUtils.copyProperties(item, t);
            if (Objects.nonNull(biConsumer)) {
                biConsumer.accept(item, t);
            }

            return t;
        }).collect(Collectors.toList());
        return ts;
    }

    public static <T> List<T> listToCustomList(List<?> oldItems, Class<T> clazz) {
        return listToCustomList(oldItems, clazz, null);
    }

    /**
     * 封装分页
     *
     * @param pagerDTO
     */
    public static void startPage(PagerDTO pagerDTO) {
        PageHelper.startPage(pagerDTO.getPageNum(), pagerDTO.getPageSize());
    }


    /**
     * list<T>去重得到List<R>
     *
     * @param items
     * @param mapper
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> lambdaToList(List<T> items, Function<? super T, ? extends R> mapper) {
        assert CollectionUtils.isNotEmpty(items);
        return items.stream().map(mapper).filter(t -> {
            boolean result = Objects.nonNull(t);
            if (result && t instanceof String) {
                result = result && StringUtils.isNotBlank((String) t);
            }
            return result;
        }).distinct().collect(Collectors.toList());
    }

    /**
     * 在springCopayProperties 增加了BigDecimal null 处理为 0
     * 增加String null 处理为空
     *
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
        objectFillDefault(target);
    }

    /**
     * 导出excel 按照目标类型
     *
     * @param list
     * @param tClass
     * @param sheetName
     * @param response
     * @param <T>
     */
    public static <T> void exportExcel(List<?> list, Class<T> tClass, String sheetName, HttpServletResponse response) {
        try {
            FileUtil.exportExcel(list, null, sheetName, tClass,
                    sheetName + ".xls", response);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }


    /**
     * 属性拷贝List
     *
     * @param sources
     * @param targetClazz
     */
    public static <T> List<T> copyPropertiesList(List<?> sources, Class<T> targetClazz, Consumer<T> consumer) {
        return sources.stream().map(value -> {
            try {
                T t = targetClazz.newInstance();
                copyProperties(value, t);
                if (Objects.nonNull(consumer)) {
                    consumer.accept(t);
                }
                return t;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

    }

    /**
     * 对象 BigDecimal填充BigDecimal。ZERO
     * 先简单实现吧
     *
     * @param target
     */
    private static void objectFillDefault(Object target) {
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.getType().equals(BigDecimal.class)) {
                    if (Objects.isNull(field.get(target))) {
                        field.set(target, BigDecimal.ZERO);
                    }
                } else if (field.getType().equals(String.class)) {
                    if (Objects.isNull(field.get(target))) {
                        field.set(target, Strings.EMPTY);
                    }
                } else if (field.getType().equals(Long.class)) {
                    if (Objects.nonNull(field.get(target)) && (Long) field.get(target) == -1) {
                        field.set(target, null);
                    }
                } else if (field.getType().equals(Integer.class)) {
                    if (Objects.nonNull(field.get(target)) && (Integer) field.get(target) == -1) {
                        field.set(target, null);
                    }
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * String 逗号分隔添加
     *
     * @param value
     * @param value2
     * @return
     */
    public static String stringAdd(String value, String value2) {
        if (StringUtils.isEmpty(value)) {
            return value2;
        }
        Assert.isTrue(StringUtils.isNotEmpty(value2), ModuleBizState.DATE_ERROR.message());
        List<String> list = new ArrayList(Arrays.asList(value.split(",")));
        list.add(value2);
        String result = list.toString();
        result = result.replace(" ", "");
        return result.substring(1, result.length() - 1);
    }


    /**
     * String 逗号分隔删除
     *
     * @param value
     * @param value2
     * @return
     */
    public static String stringSubtract(String value, String value2) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        Assert.isTrue(StringUtils.isNotEmpty(value2), ModuleBizState.DATE_ERROR.message());
        List<String> list = new ArrayList(Arrays.asList(value.split(",")));
        if (!list.contains(value2)){
            return value;
        }
        list.remove(value2);
        String result = list.toString();
        result = result.replace(" ", "");
        return result.substring(1, result.length() - 1);
    }

    /**
     * 校验非0金额
     *
     * @param amountPp
     */
    public static void vertifyBigDecimal(BigDecimal amountPp) {
        Assert.isTrue(amountPp.compareTo(BigDecimal.ZERO) > 0, ModuleBizState.DATE_ERROR_BIGDECIMAL_ZERO.message());
    }

    /**
     * 基础属性拷贝
     *
     * @param oldBase
     * @param newBase
     */
    public static void copyPropertiesBaseInfo(BaseInfoGet oldBase, BaseInfoSet newBase) {
        if (Objects.nonNull(oldBase)) {
            newBase.setCreateBy(oldBase.getCreateBy());
            newBase.setCreateDate(oldBase.getCreateDate());
            newBase.setCreateName(oldBase.getCreateName());
            newBase.setUpdateBy(oldBase.getUpdateBy());
            newBase.setUpdateName(oldBase.getUpdateName());
            newBase.setUpdateDate(oldBase.getUpdateDate());
        }
    }

    /**
     * 缓存设置
     *
     * @param business
     * @param key
     * @param value
     * @param tiemout
     * @param timeUnit
     */
    public void cacheSet(String business, Long key, String value, long tiemout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(business + ":" + key, value, tiemout, timeUnit);
    }

    /**
     * 缓存获取
     *
     * @param business
     * @param key
     */
    public String cacheGet(String business, Long key) {
        return (String) redisTemplate.opsForValue().get(business + ":" + key);
    }


    /**
     * 尝试加锁， 超时时间1分钟
     * 注意：这里没有判断Thread
     *
     * @param business
     * @param key
     */
    public boolean tryLock(String business, Long key) {
        return redisTemplate.opsForValue().setIfAbsent(business + ":" + key, "", 60, TimeUnit.SECONDS);
    }

    /**
     * 尝试加锁  如果失败提示业务繁忙，请稍后重试
     *
     * @param business
     * @param key
     */
    public boolean tryLockBusinessTip(String business, Long key) {
        if (!tryLock(business, key)) {
            throw FinanceBizState.SYSTEM_BUSY;
        }
        return true;
    }

    /**
     * 尝试加锁
     *
     * @param business
     * @param key
     */
    public boolean tryUnLock(String business, Long key) {
        return redisTemplate.delete(business + ":" + key);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取图片
     *
     * @param invoice
     * @param invoiceId
     */
    public List<CfMultyImage> multyImages(MultyImageEnum invoice, Long invoiceId) {
        return cfMultyImageMapper.selectList(Wrappers.lambdaQuery(CfMultyImage.class).eq(CfMultyImage::getBusinessType, invoice.getCode())
                .eq(CfMultyImage::getBusinessId, invoiceId));
    }


    @Data
    static class MessageJudge {
        private String message;
        private Boolean judge;
    }

    /**
     * 成功，失败。
     *
     * @param add
     * @return
     */
    public static <T> Response<T> smartSuccess(T add) {
        MessageJudge messageJudge = new MessageJudge();
        messageJudge = getMessageJudge(add, messageJudge);
        if (messageJudge.getJudge()) {
            return Response.success(add);
        } else {
            return new Response<>(Constant.FAIL, messageJudge.getMessage());
        }
    }

    private static <T> MessageJudge getMessageJudge(T add, MessageJudge messageJudge) {
        if (add instanceof Integer) {
            messageJudge.setJudge((Integer) add > 0);
            messageJudge.setMessage("操作失败");
        } else {
            messageJudge.setJudge(true);
        }
        return messageJudge;
    }

    /**
     * 产生业务单号，通过rpc
     *
     * @param key
     * @return
     */
    public String generateBusinessNum(String key) {
        CodeGenerateVO codeGenerateVO = RpcUtil.getObjException(codeGenerateClient.generate(key), FinanceBizState.DATE_ERROR.getMessage());
        Assert.isTrue(Objects.nonNull(codeGenerateVO) && StringUtils.isNotBlank(codeGenerateVO.getCode()), FinanceBizState.DATE_ERROR.getMessage());
        return codeGenerateVO.getCode();
    }


    /**
     * 基础信息填充
     * 待优化
     *
     * @param baseInfoSet
     */
    public void baseInfoFill(BaseInfoSet baseInfoSet) {
        UserVO userVO = getUser();
        if (baseInfoSet instanceof BaseInfoCustomTenantIdFill) {
            ((BaseInfoCustomTenantIdFill) baseInfoSet).setTenantId(userVO.getTenantId() == null ? 1 : userVO.getTenantId());
            ((BaseInfoCustomTenantIdFill) baseInfoSet).setCompanyId(userVO.getCompanyId());
        }
        baseInfoSet.setCreateBy(userVO.getUserId());
        baseInfoSet.setCreateName(userVO.getRealName());
        baseInfoSet.setCreateDate(LocalDateTime.now());
        baseInfoSet.setCreateDate(new Date());
    }

    /**
     * 基础信息填充
     * 针对老表处理
     *
     * @param baseInfoSet
     */
    public void baseInfoFillOld(BaseInfoSet baseInfoSet) {
        baseInfoFill(baseInfoSet);
        UserVO userVO = getUser();
        baseInfoSet.setUpdateName(userVO.getRealName());
        baseInfoSet.setUpdateBy(userVO.getUserId());
    }

    public String getToken() {
        return authorizationUtil.getToken();
    }

    public UserVO getUser() {
        UserVO userVo = authorizationUtil.getUser();
        if (Objects.isNull(userVo)) {
            throw FinanceBizState.USER_LOGIN_ERROR;
        }
        return userVo;
    }


    /**
     * 开启审批流
     *
     * @param data
     * @param clazz
     * @param processId
     * @param id
     * @return 审批流Id
     */
    public Long startProcess(Object data, Class<?> clazz, Long processId, Long id) {
        UserVO userVO = getUser();
        ApprovalJson approvalJson = new ApprovalJson();
        approvalJson.setClassName(clazz.getSimpleName());
        approvalJson.setJson(JSONObject.toJSONString(data));
        R<Long> flowIdR = processClient.startProcess(processId, userVO.getUserId(), userVO.getRealName(),
                JSONObject.toJSONString(approvalJson));
        Long flowId = RpcUtil.getObjException(flowIdR, ModuleBizState.SYSTEM_BUSY.message(), "approvalRemoteService 访问失败");
        //保存审批流
        ApprovalFlow approvalFlow = new ApprovalFlow();
        approvalFlow.setApprovalId(flowId);
        approvalFlow.setSrcId(id);
        approvalFlow.setProcessId(processId);
        approvalFlow.setActiveStatus(1);
        approvalFlowMapper.insert(approvalFlow);
        return flowId;
    }

    /**
     * 停止审批流
     *
     * @param processId
     * @return
     */
    public Long stopProcess(Long processId) {
        if (Objects.nonNull(processId)) {
            try {
                R<Long> r = processClient.revokeApproval(processId, getUser().getUserId(), getUser().getRealName());
                return RpcUtil.getObjNoException(r);
            } catch (Exception e) {

            }
        }
        return 1L;
    }


    /**
     * 审批流获取当前用户名
     *
     * @param srcId
     * @param processId
     * @return
     */
    public String processGetCurrentName(Long srcId, Long processId) {
        List<ApprovalFlow> approvalFlows = approvalFlowMapper.selectList(Wrappers.lambdaQuery(ApprovalFlow.class).orderByDesc(ApprovalFlow::getId)
                .eq(ApprovalFlow::getSrcId, srcId).eq(ApprovalFlow::getProcessId, processId));
        if (CollectionUtils.isNotEmpty(approvalFlows)) {
            R<ApprovalVo> approvalVoR = processClient.getApprovalById(approvalFlows.get(0).getApprovalId());
            return RpcUtil.getObjException(approvalVoR, ModuleBizState.SYSTEM_BUSY.message(), "processClient 服务异常").getUserName();
        } else {
            return Strings.EMPTY;
        }
    }

    /**
     * 获取审批流id
     *
     * @param srcId
     * @param processId
     * @return
     */
    public Long processGetApprovalId(Long srcId, Long processId) {
        List<ApprovalFlow> approvalFlows = approvalFlowMapper.selectList(Wrappers.lambdaQuery(ApprovalFlow.class).orderByDesc(ApprovalFlow::getId)
                                                                                 .eq(ApprovalFlow::getSrcId, srcId).eq(ApprovalFlow::getProcessId, processId));
        if (CollectionUtils.isNotEmpty(approvalFlows)) {
            return  approvalFlows.get(0).getApprovalId();
        }
        return  null;
    }


    /**
     * 填充审批流
     *
     * @return
     */
    public void fillProcessFlowDesc(Long srcId, Long processId, Object object) {
        Assert.isTrue(Objects.nonNull(srcId), ModuleBizState.DATE_ERROR.message());
        List<ApprovalFlow> approvalFlows = approvalFlowMapper.selectList(Wrappers.lambdaQuery(ApprovalFlow.class).orderByDesc(ApprovalFlow::getId)
                .eq(ApprovalFlow::getSrcId, srcId).eq(ApprovalFlow::getProcessId, processId));
        if (CollectionUtils.isNotEmpty(approvalFlows)) {
            Class<?> clazz = object.getClass();
            try {
                Field field = clazz.getDeclaredField("flowId");
                field.setAccessible(true);
                field.set(object, approvalFlows.get(0).getApprovalId());
                field.setAccessible(false);
                Field field2 = clazz.getDeclaredField("flowIds");
                field2.setAccessible(true);
                field2.set(object, PageInfoUtil.lambdaToList(approvalFlows, ApprovalFlow::getApprovalId));
                field2.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 导入EXCEL
     *
     * @param inputstream
     * @param pojoClass
     * @param params
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> importExcel(InputStream inputstream, Class<?> pojoClass,
                                          ImportParams params) throws Exception {
        return new CustomExcelImportService().importExcelByIs(inputstream, pojoClass, params, false).getList();
    }

    public static <T> List<T> importExcel(
            MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        return importExcel(file, titleRows, headerRows, pojoClass, null);
    }

    public static <T> List<T> importExcel(
            MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass, Predicate<T> predicate) {
        if (file == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list;
        try {
            list = importExcel(file.getInputStream(), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("excel文件不能为空");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (Objects.nonNull(predicate)) {
            list = list.stream().filter(predicate).collect(Collectors.toList());
        }
        return list;
    }


    private final static Pattern NUMBER_PATTERN = Pattern.compile("^[-\\+]?[\\d]*$");

    private final static Pattern DAY_DATE_PATTERN = Pattern.compile("^\\d{4}\\/\\d{1,2}\\/\\d{1,2}");

    private final static Pattern MONTH_DATE_PATTERN = Pattern.compile("^\\d{4}\\/\\d{1,2}");

    /**
     * 判断是否数字
     *
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        return NUMBER_PATTERN.matcher(str).matches();
    }

    /**
     * 判断是否日期：格式为yyyy/mm/dd
     */
    public static boolean isDayDate(String input) {
        return DAY_DATE_PATTERN.matcher(input).matches();
    }

    public static boolean isMonthDate(String input) {
        return MONTH_DATE_PATTERN.matcher(input).matches();
    }


    /**
     * 集合合并
     *
     * @param lists
     * @param <T>
     * @return
     */
    public static <T> List<T> sonToSum(List<List<T>> lists) {
        return lists.stream().flatMap(t -> t.stream()).collect(Collectors.toList());
    }


    /**
     * 数据字典-财务主体 name-code
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public Map<String, String> getDictsCWZT() {
        return getDictsNameCode("CWZT");
    }

    /**
     * 数据字典-支付方式  name-code
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public Map<String, String> getDictsPayChannel() {
        return getDictsNameCode(PAYMENT_MODE);
    }


    /**
     * 数据字典 name-code
     *
     * @param businessType
     * @return
     */
    public Map<String, String> getDictsNameCode(String businessType) {
        List<BaseDicts> dictList = getDict(businessType);
        return dictList.stream().collect(Collectors.toMap(BaseDicts::getDictsNameC, BaseDicts::getDictsCode));
    }


    /**
     * 支付方式
     */
    public static final String PAYMENT_MODE = "Payment_Mode";

    /**
     * 数据字典 code-name
     *
     * @param businessType
     * @return
     */
    public Map<String, String> getDictsCodeName(String businessType) {
        List<BaseDicts> dictList = getDict(businessType);
        return dictList.stream().collect(Collectors.toMap(BaseDicts::getDictsCode, BaseDicts::getDictsNameC));
    }


    /**
     * 数据字典
     *
     * @param businessType
     * @return
     */
    public List<BaseDicts> getDict(String businessType) {
        Response<List<BaseDicts>> dictList = baseInfoRemoteServer.getDictList(businessType);
        return RpcUtil.getObjException(dictList, ModuleBizState.SYSTEM_BUSY.message(), "baseInfoRemoteServer 调用失败");
    }

    public static <T> PageInfo<T> emptyPageInfo() {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setList(new ArrayList<>());
        //兼容前端分页
        pageInfo.setPageNum(1);
        return pageInfo;
    }

}
