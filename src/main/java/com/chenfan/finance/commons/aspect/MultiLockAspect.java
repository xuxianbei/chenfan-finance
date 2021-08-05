package com.chenfan.finance.commons.aspect;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chenfan.common.exception.BusinessException;
import com.chenfan.common.exception.SystemState;
import com.chenfan.finance.commons.annotation.MultiLockInt;
import com.chenfan.finance.utils.AuthorizationUtil;
import lombok.extern.slf4j.Slf4j;

import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @Author Wen.Xiao
 * @Description // 多数据锁逻辑
 * @Date 2021/5/31  16:58
 * @Version 1.0
 */
@Component
@Aspect
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 3)
public class MultiLockAspect {


    @Resource
    private RedissonClient redissonClient;
    @Resource
    private AuthorizationUtil authorizationUtil;

    @Pointcut("@within(com.chenfan.finance.commons.annotation.MultiLockInt)||@annotation(com.chenfan.finance.commons.annotation.MultiLockInt)")
    public void pointcutLock() {
    }

    @Before("pointcutLock()")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        MultiLockInt multiLockInt = signature.getMethod().getAnnotation(MultiLockInt.class);
        Method method = signature.getMethod();
        log.info("用户：{} 请求方法：{} 开始加锁", authorizationUtil.getUserName(),method.getDeclaringClass().getName()+"."+method.getName());
        List<Object> objs = CollectionUtils.arrayToList(joinPoint.getArgs());
        doLock(multiLockInt,objs);
        log.info("用户：{} 请求方法：{} 加锁完成",authorizationUtil.getUserName(),method.getDeclaringClass().getName()+"."+method.getName());
    }
    private void doLock(MultiLockInt multiLockInt, List<Object> objs){
        RLock multiLock =null;
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestURI = attributes.getRequest().getRequestURI();
        String[] paramNames = multiLockInt.paramNames();
        Class<? extends Object>    aClass = multiLockInt.paramClass();
        boolean  check = multiLockInt.isCheck();
        String   msg = multiLockInt.unlockEnum().getErrMsg();
        boolean isCollections = multiLockInt.isCollections();

        //为-1 的时候是单参数，直接获取一个
        String reqMsg = JSON.toJSONString(objs.get(multiLockInt.baseParamIndex()-1));
        String paramKey="";
        //直接获取参数
        List list=new ArrayList();
        if(paramNames==null||paramNames.length==0){
            if(isCollections){
                list=JSON.parseArray(reqMsg).toJavaList(aClass);
            }else {
                list.add(reqMsg);
            }
        }else {
            JSONObject reqJson=JSON.parseObject(reqMsg);
            paramKey=paramNames[paramNames.length-1];
            for (String name: paramNames) {
                if(Objects.equals(name,paramKey)){
                    if(isCollections){
                        JSONArray jsonArray = reqJson.getJSONArray(name);
                        if(Objects.nonNull(jsonArray)){
                            list=jsonArray.toJavaList(aClass);
                        }
                    }else {
                        list.add(reqJson);
                    }
                }else {
                    reqJson=reqJson.getJSONObject(name);
                }
            }
        }
        if(check&&CollectionUtils.isEmpty(list)){
            throw new BusinessException(SystemState.BUSINESS_ERROR.code(),"请求参数："+paramKey+" 不能为空！！！");
        }
        if(list!=null&&list.size()>0){
            RLock [] rLocks = new RLock[list.size()];;
            for(int i = 0,length = list.size(); i < length ;i ++){
                String key = MD5.create().digestHex(requestURI + list.get(i));
                log.info("加锁的KEY:{}",key);
                RLock lock = redissonClient.getLock(key);
                rLocks[i] = lock;
            }
            multiLock = redissonClient.getMultiLock(rLocks);
            if(!multiLock.tryLock()){
                throw new BusinessException(SystemState.PARAM_ERROR.code(),msg);
            }
        }
        CFRequestHolder.setThreadLocal(multiLock);

    }

    @AfterReturning(returning = "obj", pointcut = "pointcutLock()")
    public void doAfterReturning(Object obj) {
        try {
            RLock threadLocal = CFRequestHolder.getThreadLocal();
            if(Objects.nonNull(threadLocal)){
                threadLocal.unlock();
                log.info("释放业务锁");
            }
        }catch (Exception e){
            log.error("释放锁异常：",e);
        }
    }
    @AfterThrowing(throwing = "ex", pointcut = "pointcutLock()")
    public void doAfterThrowing(Throwable ex) {
        try {
            RLock threadLocal = CFRequestHolder.getThreadLocal();
            if(Objects.nonNull(threadLocal)){
                threadLocal.unlock();
                log.info("释放业务锁");
            }
        }catch (Exception e){
            log.error("释放锁异常：",e);
        }
    }
}
