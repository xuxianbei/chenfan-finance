package com.chenfan.finance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: xuxianbei
 * Date: 2020/8/29
 * Time: 10:35
 * Version:V1.0
 */
@AllArgsConstructor
@Getter
public enum  BankAndCashStatusEnum {
    /**
     * 草稿
     */
    BANK_AND_CASH_STATUS_1(1, "草稿"),
    /**
     * 已入账（待核销）
     */
    BANK_AND_CASH_STATUS_2(2, "已入账（待核销）"),
    /**
     * 部分核销
     */
    BANK_AND_CASH_STATUS_3(3, "部分核销"),
    /**
     * 已核销
     */
    BANK_AND_CASH_STATUS_4(4, "已核销"),

    BANK_AND_CASH_STATUS_DELETE(0, "已删除"),

    BANK_AND_CASH_STATUS_CANCEL(5, "作废");

    private Integer code;
    private String msg;


    public static String getStatusMsg(Integer code) {
        for (BankAndCashStatusEnum value : values()) {
            if (value.name().matches("BANK_AND_CASH_STATUS(.*)")) {
                if (value.getCode().equals(code)) {
                    return value.getMsg();
                }
            }
        }
        return "";
    }
}
