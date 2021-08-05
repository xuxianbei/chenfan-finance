package com.chenfan.finance.service.impl;

import com.chenfan.finance.enums.ChargeEnum;
import com.chenfan.finance.model.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 核销保存上下文
 *
 * @author: xuxianbei
 * Date: 2020/8/27
 * Time: 14:03
 * Version:V1.0
 */
@Data
public class ClearHeaderSaveDbContext {
    /**
     * 实收付
     */
    private List<CfBankAndCash> cfBankAndCashs;

    /**
     * 上一次实收付未核销余额总计
     */
    private BigDecimal lastBankAndCashSum;

    /**
     * 上一次费用已核销费用总计
     */
    private BigDecimal lastActualAmoutSum;
    /**
     * 费用
     */
    private List<CfCharge> cfCharges;

    /**
     * 核销主表
     */
    private CfClearHeader cfClearHeader;

    /**
     * 核销明细
     */
    private List<CfClearDetail> cfClearDetails;

    /**
     * 核销明细 收
     */
    private List<CfClearDetail> debits = new ArrayList<>();

    /**
     * 核销明细 付
     */
    private List<CfClearDetail> credits = new ArrayList<>();

    /**
     * 财务帐单
     */
    private CfInvoiceHeader cfInvoiceHeader;


    /**
     * 账单费用总计
     */
    private final BigDecimal InvoiceSum;


    /**
     * 账单费用总计收
     */
    private final BigDecimal InvoiceDebit;

    /**
     * 账单费用总计付
     */
    private final BigDecimal InvoiceCredit;


    /**
     * 上次账单费用总计  有正负
     */
    private BigDecimal lastInvoiceSum;


    /**
     * 上次账单费用总计收
     */
    private BigDecimal lastInvoiceDebit;

    /**
     * 上次账单费用总计付
     */
    private BigDecimal lastInvoiceCredit;


    /**
     * 本次实收付总计  只有正数
     */
    private BigDecimal bankAndCashSum;

    /**
     * 实收付类型 应收、应付
     */
    private ChargeEnum chargeEnum;


}
