package com.chenfan.finance.enums;

import lombok.Getter;

/**
 * @author liran
 */

@Getter
public enum OperateLockEnum {

    /**
     * 结算单创建
     */
    BASE_UN_LOCK("","当前数据处理中,请稍后重试"),
    /**
     * 创建账单
     */
    CHARGE_INVOICE_CREATE("CHARGE_INVOICE_CREATE", "当前所选部分费用生成账单中,请稍后重试"),
    /**
     * 红字抵扣
     */
    UPDATE_ADJUST_MONEY("UPDATE_ADJUST_MONEY","当前抵扣的数据正在被其他蓝字账单使用,请稍后重试"),
    /**
     * 结算单创建
     */
    SETTLEMENT_CREATE("SETTLEMENT_CREATE", "结算单创建中,请稍后重试"),
    ;

    private String key;
    private String errMsg;

    OperateLockEnum(String key, String msg) {
        this.key = key;
        this.errMsg = msg;
    }

}
