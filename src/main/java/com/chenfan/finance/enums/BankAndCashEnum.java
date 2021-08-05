package com.chenfan.finance.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @author: xuxianbei
 * Date: 2021/3/19
 * Time: 14:27
 * Version:V1.0
 */
@Getter
public enum BankAndCashEnum implements GetCode {
    BANK_AND_CASH_STATUS_ONE(1, "草稿（待提交）"),
    BANK_AND_CASH_STATUS_TWO(2, "已入账（待核销）"),
    BANK_AND_CASH_STATUS_THREE(3, "部分核销"),
    BANK_AND_CASH_STATUS_FOUR(4, "已核销"),
    BANK_AND_CASH_STATUS_FIVE(5, "作废"),
    BANK_AND_CASH_STATUS_ZERO(0, "已删除"),
    BANK_AND_CASH_STATUS_SIX(6, "审批中"),
    BANK_AND_CASH_STATUS_SEVEN(7, "审批拒绝"),
    BANK_AND_CASH_STATUS_EIGHT(8, "已撤回"),
    JOB_TYPE_MCN(3, "MCN");


    private Integer code;
    private String msg;

    BankAndCashEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static BankAndCashEnum getByCode(Integer code){
        for (BankAndCashEnum ta:values()) {
            if(Objects.equals(ta.getCode(),code)){
                return ta;
            }
        }
        return null;
    }
}
