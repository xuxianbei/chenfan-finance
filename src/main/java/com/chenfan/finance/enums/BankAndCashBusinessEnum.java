package com.chenfan.finance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: xuxianbei
 * Date: 2020/8/26
 * Time: 19:52
 * Version:V1.0
 */
@AllArgsConstructor
@Getter
public enum BankAndCashBusinessEnum {

    /**
     * 实收
     */
    ARAP_TYPE_DEBIT("1", "实收"),
    /**
     * 实付
     */
    ARAP_TYPE_CREDIT("2", "实付"),
    /**
     * 预收
     */
    ARAP_TYPE_DEBIT_PRE("3", "预收"),
    /**
     * 预付
     */
    ARAP_TYPE_CREDIT_PRE("4", "预付");

    private String code;
    private String msg;

}
