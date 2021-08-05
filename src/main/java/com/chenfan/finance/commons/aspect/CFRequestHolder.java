package com.chenfan.finance.commons.aspect;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.chenfan.common.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;

import java.math.BigDecimal;

/**
 * @Author Wen.Xiao
 * @Description // 财务请求线程持有,使用线程池的情况下，线程复用会导致数据被重复，所以每次使用前都需要set
 * @Date 2021/5/31  17:11
 * @Version 1.0
 */
@Slf4j
public class CFRequestHolder {

    private static final TransmittableThreadLocal<RLock> rLockTransmittableThreadLocalThread=new TransmittableThreadLocal();

    public static RLock getThreadLocal(){
        return rLockTransmittableThreadLocalThread.get();
    }

    public static void setThreadLocal(RLock multiLock ){
        rLockTransmittableThreadLocalThread.set(multiLock);
    }



    private static final TransmittableThreadLocal<String> invThreadLocalThread=new TransmittableThreadLocal();

    public static String getStringThreadLocal(){
        return invThreadLocalThread.get();
    }

    public static void setStringThreadLocal(String key ){
        invThreadLocalThread.set(key);
    }

    private static final TransmittableThreadLocal<BigDecimal> invBigThreadLocalThread=new TransmittableThreadLocal();

    public static BigDecimal getBigDecimalThreadLocal(){
        return invBigThreadLocalThread.get();
    }

    public static void setBigDecimalThreadLocal(BigDecimal key ){
        invBigThreadLocalThread.set(key);
    }


    public static final TransmittableThreadLocal<String> invalidThreadLocal=new TransmittableThreadLocal();

    public static String getInvalidThreadLocal() {
        return invalidThreadLocal.get();
    }
    public static void setInvalidThreadLocal(String key){
        invalidThreadLocal.set(key);
    }

    public static final TransmittableThreadLocal<UserVO> userThreadLocal=new TransmittableThreadLocal();
    public static UserVO getUserThreadLocal() {
        return userThreadLocal.get();
    }
    public static void setUserThreadLocal(UserVO userVO){
        userThreadLocal.set(userVO);
    }


    public static final TransmittableThreadLocal<Boolean> isUpdateThreadLocal=new TransmittableThreadLocal();
    public static Boolean getIsUpdateThreadLocal() {
        Boolean aBoolean = isUpdateThreadLocal.get();
        return aBoolean==null?false:aBoolean;
    }
    public static void setIsUpdateThreadLocal(Boolean userVO){
        isUpdateThreadLocal.set(userVO);
    }
}
