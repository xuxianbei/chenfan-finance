package com.chenfan.finance.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CfBankAndCashInvoiceExtend extends CfBankAndCash {
    /**
     * 预付款单号
     */
    private String advancePayNos;
    /**
     * 采购实付金额
     */
    private BigDecimal poMoney;
    /**
     * 预付款总金额
     */
    private BigDecimal advancePayMoney;
    /**
     * 本次实收付金额
     */
    private BigDecimal needPayMoney;

    /**
     * 账单流水号
     */
    private String invoiceNo;
    private Long  invoiceSettlementId;
}
