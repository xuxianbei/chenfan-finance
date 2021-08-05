package com.chenfan.finance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: xuxianbei
 * Date: 2020/9/4
 * Time: 10:58
 * Version:V1.0
 */
@AllArgsConstructor
@Getter
public enum InvoiceHeaderEnum implements GetCode {
    /**
     * 未核销
     */
    CLEAR_STATUS_NO(0, "未核销"),
    /**
     * 部分核销
     */
    CLEAR_STATUS_PAR(1, "部分核销"),
    /**
     * 全部核销
     */
    CLEAR_STATUS_ALL(2, "全部核销"),

    SETTLE_TEMPLATE_IN_RED(1, "内部红人执行单模板"),
    SETTLE_TEMPLATE_OUT_RED(2, "外部红人执行单模板"),
    SETTLE_TEMPLATE_THREE(3, "红人采购费/年度返点/客户返点模板"),

    INVOICE_STATUS_ZERO(0, "已删除"),
    INVOICE_STATUS_ONE(1, "待提交"),
    INVOICE_STATUS_SIX(6, "财务已审核"),
    INVOICE_STATUS_SEVEN(7, "已作废"),
    INVOICE_STATUS_NINE(9, "已核销"),
    INVOICE_STATUS_TEN(10, "审批中"),
    INVOICE_STATUS_ELEVNE(11, "待打款, 待开票"),
    INVOICE_STATUS_TWELVE(12, "已开票"),
    INVOICE_STATUS_THIRTEEN(13, "已撤回"),
    INVOICE_STATUS_FOUTTEEN(14, "审批拒绝"),
    INVOICE_STATUS_FIVETEEN(15, "打款中"),
    UNDEFINE(999, "未定义"),

    ACCOUNT_TYPE_THIRD_PARTY_ACCOUNT(4, "第三方账户"),



    CUSTOMER_INVOICE_WAY_TAX_INVOICE(1, "开票"),
    CUSTOMER_INVOICE_WAY_TAX_INVOICE_NONE(2, "无票"),
    CUSTOMER_INVOICE_WAY_TAX_INVOICE_LATER(3, "后补票"),

    JOB_TYPE_THREE(3, "MCN"),

    ACCOUNT_TYPE_FOUR(4, "第三方账户"),

    CHARGE_TYPE_TWO(2, "客户返点费"),

    CHARGE_TYPE_THREE(3, "MCN收入"),

    CHARGE_TYPE_FIVE(5, "年度返点费"),

    CHARGE_TYPE_SIX(6, "平台手续费");



    private Integer code;
    private String desc;


    public static InvoiceHeaderEnum valueOfCustomerInvoiceStatus(int code) {
        for (InvoiceHeaderEnum value : values()) {
            if (value.name().contains("CUSTOMER_INVOICE_WAY")) {
                if (value.getCode() == code) {
                    return value;
                }
            }
        }
        return UNDEFINE;
    }

    public static InvoiceHeaderEnum valueOfInvoiceStatus(int code) {
        for (InvoiceHeaderEnum value : values()) {
            if (value.name().contains("INVOICE_STATUS")) {
                if (value.getCode() == code) {
                    return value;
                }
            }
        }
        return UNDEFINE;
    }
}
