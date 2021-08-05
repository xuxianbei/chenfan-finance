package com.chenfan.finance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 费用状态枚举
 *
 * @author: xuxianbei
 * Date: 2020/8/27
 * Time: 11:39
 * Version:V1.0
 */
@AllArgsConstructor
@Getter
public enum ChargeEnum {
    //0=收debit, 1=付credit
    ARAP_TYPE_AR("0", "AR", "debit", "收"),
    ARAP_TYPE_AP("1", "AP", "credit", "付");

    private String id;
    private String code;
    private String code2;
    private String msg;

    /**
     * 校验数值
     * @param code
     * @return
     */
    public static boolean vertify(String code) {
        for (ChargeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
    public static  ChargeEnum getByBankAndCashBusinessCode(String code ){
        switch (code){
            case "1":
            case "3":
                return ChargeEnum.ARAP_TYPE_AR;
            case "2":
            case "4":
                return ChargeEnum.ARAP_TYPE_AP;
            default:
                break;
        }
        return null;
    }
}
