package com.chenfan.finance.commons.purchaseexceptions;

import com.chenfan.common.exception.BusinessException;
import com.chenfan.common.exception.ResultState;
import com.chenfan.common.exception.SystemState;

/**
 * @author mbji
 */
public class PurchaseException extends BusinessException{
    public PurchaseException(String msg) {
        super(SystemState.BUSINESS_ERROR.code(),msg);

    }
}
