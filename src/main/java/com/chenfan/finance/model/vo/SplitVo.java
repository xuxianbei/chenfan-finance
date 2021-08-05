package com.chenfan.finance.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 分批信息
 * @author: xuxianbei
 * Date: 2021/4/16
 * Time: 16:06
 * Version:V1.0
 */
@Data
public class SplitVo {

    /**
     * 费用编号
     */
    private String chargeCode;

    /**
     * 总价
     */
    private BigDecimal amountPp;

    /**
     * 申请开票： 0：否； 1：是
     */
    private Integer applyTaxInvoice;
}
