package com.chenfan.finance.commons.annotation;


import com.chenfan.finance.controller.CfInvoiceHeaderController;
import com.chenfan.finance.enums.OperateLockEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description // 多数据锁注解使用
 * @Date 2021/5/31  16:55
 * @Version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiLockInt {
    /**
     * @see CfInvoiceHeaderController
     * @return
     */
    String[] paramNames() default {};

    /**
     *
     * @return
     */
    OperateLockEnum unlockEnum() default OperateLockEnum.BASE_UN_LOCK;
    /**
     * 是否是必须参数，如果是的话，那么没拿到值会报错
     * @return
     */
    boolean isCheck() default false;

    /**
     *
     * @return
     */
    boolean isCollections() default true;
    /**
     * 接收参数的位置(多参数时使用)
     * @return
     */
    int baseParamIndex() default 1;

    /**
     * 请求参数数据类型
     * @return
     */
    Class paramClass() default Long.class;

}
