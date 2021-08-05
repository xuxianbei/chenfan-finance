package com.chenfan.finance.exception;

import com.chenfan.common.exception.BusinessException;

/**
 * @author: xuxianbei
 * Date: 2021/3/1
 * Time: 10:43
 * Version:V1.0
 */
public class FinanceBizState {
    public static final BusinessException DATE_ERROR = new BusinessException(ModuleBizState.DATE_ERROR);
    public static final BusinessException USER_LOGIN_ERROR = new BusinessException(ModuleBizState.USER_LOGIN_ERROR);
    public static final BusinessException SYSTEM_BUSY = new BusinessException(ModuleBizState.SYSTEM_BUSY);
    public static final BusinessException BANK_AND_CASH_EXCEL_OUTPUT_ERROR = new BusinessException(ModuleBizState.BANK_AND_CASH_EXCEL_OUTPUT_ERROR);
    public static final BusinessException IMPORT_SIZE_ERROR = new BusinessException(ModuleBizState.IMPORT_SIZE_ERROR);
    public static final BusinessException UNDEFINE_ERROR = new BusinessException(ModuleBizState.UNDEFINE_ERROR);
    public static final BusinessException STATE_ERROR = new BusinessException(ModuleBizState.STATE_ERROR);
    public static final BusinessException HOT_MAN_VERTIFY_ERROR = new BusinessException(ModuleBizState.HOT_MAN_VERTIFY_ERROR);
    public static final BusinessException BANK_CASH_BATCH_DELETE_ERROR = new BusinessException(ModuleBizState.BANK_CASH_BATCH_DELETE_ERROR);
    public static final BusinessException CF_CLEAR_HEAD_ERROR = new BusinessException(ModuleBizState.DONT_EDIT);
    public static final BusinessException CF_CLEAR_HEAD_DONT_COMMIT = new BusinessException(ModuleBizState.CF_CLEAR_HEAD_DONT_COMMIT);
    public static final BusinessException SPLIT_CHARGECOMMON_PART_ERROR = new BusinessException(ModuleBizState.SPLIT_CHARGECOMMON_PART_ERROR);



    public static BusinessException format(ModuleBizState value, String msg) {
        value.setMessage(String.format(value.message(), msg));
        return new BusinessException(value);
    }
}
