package com.chenfan.finance.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 核销明细- 总计
 *
 * @author: xuxianbei
 * Date: 2020/8/25
 * Time: 16:47
 * Version:V1.0
 */
@Data
@ApiModel(value = "财务-核销核销费用总计")
public class ClearHeaderDetailTotalVo {

    /**
     * 总计-应收
     */
    private BigDecimal clearDebit;

    /**
     * 总计-应付
     */
    private BigDecimal clearCredit;

    /**
     * 费用总计类型：AR：收，AP：付
     */
    private String clearType;

    /**
     * 费用总计
     */
    private BigDecimal clearBalance;

    /**
     * 本次核销应收
     */
    private BigDecimal nowClearDebit;

    /**
     * 本次核销应付
     */
    private BigDecimal nowClearCredit;

    /**
     * 本次核销总计类型(AR=debit; AP=credit)
     */
    private String nowClearType;

    /**
     * 本次核销总计
     */
    private BigDecimal nowClearBalance;

    /**
     * 上次核销余额应收
     */
    private BigDecimal lastBalanceDebit;

    /**
     * 上次核销余额应付
     */
    private BigDecimal lastBalanceCredit;

    /**
     * 上次核销余额类型(AR=debit; AP=credit)
     */
    private String lastBalanceType;

    /**
     * 上次核销余额
     */
    private BigDecimal lastBalanceBalance;

    /**
     * 本次核销余额应收
     */
    private BigDecimal nowBalanceDebit;

    /**
     * 本次核销余额应付
     */
    private BigDecimal nowBalanceCredit;

    /**
     * 本次核销余额类型(AR=debit; AP=credit)'
     */
    private String nowBalanceType;

    /**
     * 本次核销余额
     */
    private BigDecimal nowBalanceBalance;

}
