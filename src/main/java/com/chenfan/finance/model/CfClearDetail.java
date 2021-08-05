package com.chenfan.finance.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 财务_核销费用明细（cf_clear_detail）
 * </p>
 *
 * @author lywang
 * @since 2020-08-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_clear_detail")
public class CfClearDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 核销单明细id
     */
    @TableId(value = "clear_detail_id", type = IdType.AUTO)
    private Long clearDetailId;

    /**
     * 核销单id
     */
    private Long clearId;

    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 帐单抬头
     */
    private String invoiceTitle;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 结算主体名称
     */
    private String balanceName;

    /**
     * 帐单抬头名称
     */
    private String invoiceTitleName;

    /**
     * 费用种类
     */
    private String chargeType;

    /**
     * 1= MCN
     */
    private Integer chargeSourceType;

    /**
     * 源币种
     */
    private String sourceCurrencyCode;

    /**
     * 源汇率
     */
    private BigDecimal sourceExchangeRate;

    /**
     * 费用id
     */
    private Long chargeId;

    /**
     * 应收/应付类型
     */
    private String arapType;

    /**
     * debit
     */
    private BigDecimal clearDebit;

    /**
     * credit
     */
    private BigDecimal clearCredit;

    /**
     * 已核销金额
     */
    private BigDecimal clearedAmount;

    /**
     * 上次余额
     */
    private BigDecimal lastBalance;

    /**
     * 本次余额
     */
    private BigDecimal nowBalance;

    /**
     * 实际核销金额
     */
    private BigDecimal actualClearAmount;

    /**
     * 费用来源id
     */
    private String chargeSourceCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 核销币种
     */
    private String currencyCode;

    /**
     * 核销汇率
     */
    private BigDecimal exchangeRate;


}
