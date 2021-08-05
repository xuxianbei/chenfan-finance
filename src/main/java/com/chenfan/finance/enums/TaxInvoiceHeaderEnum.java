package com.chenfan.finance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 开票枚举
 *
 * @author: xuxianbei
 * Date: 2021/3/8
 * Time: 10:32
 * Version:V1.0
 */
@AllArgsConstructor
@Getter
public enum TaxInvoiceHeaderEnum implements GetCode {

    TAX_INVOICE_STATUS_ONE(1, "待提交"),
    TAX_INVOICE_STATUS_TWO(2, "审批中"),
    TAX_INVOICE_STATUS_THREE(3, "审批拒绝"),
    TAX_INVOICE_STATUS_FOUR(4, "待开票"),
    TAX_INVOICE_STATUS_FIVE(5, "已开票"),
    TAX_INVOICE_STATUS_SIX(6, "已核销"),
    TAX_INVOICE_STATUS_SEVEN(7, "已撤回"),
    TAX_INVOICE_STATUS_EIGHT(8, "已作废");

    private Integer code;
    private String desc;

    public static TaxInvoiceHeaderEnum getByCode(Integer code){
        for (TaxInvoiceHeaderEnum ta:values()) {
            if(Objects.equals(ta.getCode(),code)){
                return ta;
            }
        }
        return null;
    }
}
