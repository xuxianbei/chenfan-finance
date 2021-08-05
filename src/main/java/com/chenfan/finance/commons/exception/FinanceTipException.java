package com.chenfan.finance.commons.exception;

import com.chenfan.common.exception.BusinessException;
import com.chenfan.finance.exception.ModuleBizState;

/**
 * 财务提示异常
 * 非受检异常
 *
 * @author: xuxianbei
 * Date: 2020/8/26
 * Time: 20:02
 * Version:V1.0
 */
public class FinanceTipException extends BusinessException {
    public FinanceTipException(String msg) {
        super(ModuleBizState.DATE_ERROR.code(), msg);
    }

    public FinanceTipException(String msg, String... args) {
        super(ModuleBizState.DATE_ERROR.code(), String.format(msg, args));
    }
}
