package com.chenfan.finance.commons.utils;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.jsr303.HibernateSupportedValidator;

import javax.validation.Validation;
import java.util.Collection;

import static com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex;

/**
 * @author 2062
 */
public class HibernateValidator<T> {

    /**
     * 校验单个对象
     *
     * @param t
     * @param clazz 分组校验，可传可不传
     * @param <T>
     * @return
     */
    public static <T> ComplexResult checkParamters(T t, Class... clazz) {
        FluentValidator fluentValidator = clazz.length > 0 ? FluentValidator.checkAll(clazz[0]) : FluentValidator.checkAll();
        return fluentValidator.failFast()
                .on(t, new HibernateValidator<T>().validator())
                .doValidate()
                .result(toComplex());
    }

    /**
     * 校验集合对象
     *
     * @param collection
     * @param clazz      分组校验，可传可不传
     * @param <T>
     * @return
     */
    public static <T> ComplexResult checkParamters(Collection<T> collection, Class... clazz) {
        FluentValidator fluentValidator = clazz.length > 0 ? FluentValidator.checkAll(clazz[0]) : FluentValidator.checkAll();
        return fluentValidator.failFast()
                .onEach(collection, new HibernateValidator<T>().validator())
                .doValidate()
                .result(toComplex());
    }

    /**
     * @param t
     * @param operationEnum
     * @param <T>
     * @return
     */
    public static <T> ComplexResult checkParamters(T t, com.chenfan.finance.commons.utils.OperationInterface operationEnum) {
        return FluentValidator.checkAll(operationEnum.getGroup()).failFast()
                .on(t, new HibernateValidator<T>().validator())
                .doValidate()
                .result(toComplex());
    }

    /**
     * @param collection
     * @param operationEnum
     * @param <T>
     * @return
     */
    public static <T> ComplexResult checkParamters(Collection<T> collection, com.chenfan.finance.commons.utils.OperationInterface operationEnum) {
        return FluentValidator.checkAll(operationEnum.getGroup()).failFast()
                .onEach(collection, new HibernateValidator<T>().validator())
                .doValidate()
                .result(toComplex());
    }

    /**
     * 返回错误信息
     *
     * @param ret
     * @return
     */
    public static String errorMsg(ComplexResult ret) {
        return ret.getErrors().get(0).getErrorMsg() + "，错误字段：" + ret.getErrors().get(0).getField();
    }

    public HibernateSupportedValidator<T> validator() {
        return new HibernateSupportedValidator<T>().setHiberanteValidator(Validation.buildDefaultValidatorFactory().getValidator());

    }
}
