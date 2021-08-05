package com.chenfan.finance.server.remote.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/5/7
 * Time: 15:32
 * Version:V1.0
 */
@Data
public class InvoiceTitleVO {
    /**
     * 开票抬头
     */
    private String invoiceTitle;

    /**
     * 银行名称
     */
    private String billingBank;

    /**
     * 银行账户
     */
    private String billingAccount;

    /**
     * 客户名称
     */
    private String customerNameC;

    /**
     * 客户id
     */
    private Long customerId;

    /**
     * 客户代码
     */
    private String customerCode;
}
