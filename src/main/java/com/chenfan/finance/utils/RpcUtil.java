package com.chenfan.finance.utils;

import com.chenfan.ccp.common.result.R;
import com.chenfan.ccp.common.result.ResultCode;
import com.chenfan.code.generate.vo.CodeGenerateVO;
import com.chenfan.common.exception.BusinessException;
import com.chenfan.common.exception.SystemState;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Rpc 远程服务工具
 *
 * @author: xuxianbei
 * Date: 2020/9/4
 * Time: 14:39
 * Version:V1.0
 */
@Slf4j
public class RpcUtil {


    private static <T> boolean isSuccess(R<T> resModelResponse) {
        return Objects.nonNull(resModelResponse) && (resModelResponse.getCode() == ResponseCode.SUCCESS.getCode() ||
                resModelResponse.getCode() == ResultCode.SUCCESS.getCode());
    }

    private static <T> boolean isSuccess(Response<T> resModelResponse) {
        return Objects.nonNull(resModelResponse) && (resModelResponse.getCode() == ResponseCode.SUCCESS.getCode() ||
                resModelResponse.getCode() == ResultCode.SUCCESS.getCode());
    }

    public static <T> T getObjException(R<T> resModelResponse, String message) {
        if (isSuccess(resModelResponse)) {
            T t = resModelResponse.getObj();
            return t;
        } else {
            throw new BusinessException(SystemState.BUSINESS_ERROR.code(), message);
        }
    }

    public static <T> T getObjException(R<T> resModelResponse, String message, String logmessage) {
        if (isSuccess(resModelResponse)) {
            T t = resModelResponse.getObj();
            return t;
        } else {
            log.error(logmessage);
            throw new BusinessException(SystemState.BUSINESS_ERROR.code(), message);
        }
    }

    public static <T> T getObjException(Response<T> resModelResponse, String message) {
        if (isSuccess(resModelResponse)) {
            T t = resModelResponse.getObj();
            if (t != null) {
                return t;
            }
        }
        throw new BusinessException(SystemState.BUSINESS_ERROR.code(), message);
    }

    public static <T> T getObjException(Response<T> resModelResponse, String message, String logmessage) {
        if (isSuccess(resModelResponse)) {
            T t = resModelResponse.getObj();
            return t;
        } else {
            log.error(logmessage);
            throw new BusinessException(SystemState.BUSINESS_ERROR.code(), message);
        }
    }


    /**
     * 获取Rpc对象 不报错
     *
     * @param resModelResponse
     * @param <T>
     * @return
     */
    public static <T> T getObjNoException(R<T> resModelResponse) {
        if (isSuccess(resModelResponse)) {
            T t = resModelResponse.getObj();
            return t;
        } else {
            return null;
        }
    }

    /**
     * 获取Rpc对象 不报错
     *
     * @param resModelResponse
     * @param <T>
     * @return
     */
    public static <T> T getObjNoException(Response<T> resModelResponse) {
        if (isSuccess(resModelResponse)) {
            T t = resModelResponse.getObj();
            return t;
        } else {
            return null;
        }
    }

    /**
     * 获取Rpc对象 否取默认值
     *
     * @param resModelResponse
     * @param defaultObj
     * @param <T>
     * @return
     */
    public static <T> T getObjNoException(Response<T> resModelResponse, T defaultObj) {
        if (isSuccess(resModelResponse)) {
            T t = resModelResponse.getObj();
            return t;
        } else {
            return defaultObj;
        }
    }

}
