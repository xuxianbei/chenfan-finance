package com.chenfan.finance.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 财务_核销费用明细（cf_clear_detail）
 * </p>
 *
 * @author lywang
 * @since 2020-08-22
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class CfClearDetailU8VO implements Serializable {
    /**
     * 核销单明细id
     */
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

    /**
     * 费用号
     */
    private String chargeCode;


    /**
     * 费用审核状态 1草稿、2已提交、3已审核、4已驳回、5已作废、0已删除
     */
    private Integer checkStatus;


    /**
     * 应收/应付审核日期
     */
    private LocalDateTime arapCheckDate;

    /**
     * 费用来源类型
     */
    private String chargeSource;


    /**
     * 费用来源明细
     */
    private String chargeSourceDetailCode;

    /**
     * 品牌
     */
    private Long brandId;


    /**
     * 数量
     */
    private Integer chargeQty;

    /**
     * 单价(pp)
     */
    private BigDecimal pricePp;

    /**
     * 总价(pp)
     */
    private BigDecimal amountPp;


    /**
     * 费用期间
     */
    private String chargeMonthBelongTo;

    /**
     * 发票号
     */
    private String taxInvoiceNo;

    /**
     * 发票日期
     */
    private LocalDateTime taxInvoiceDate;

    /**
     * 对方帐单号
     */
    private String customerInvoiceNo;


    /**
     * 帐单日期
     */
    private LocalDateTime invoiceDate;

    /**
     * 核销流水号
     */
    private String clearNo;

    /**
     * 实收金额(核销后反写)
     */
    private BigDecimal actualAmount;

    /**
     * 实收日期
     */
    private LocalDateTime actualDate;

    /**
     * 实收历史日期
     */
    private String actualHistoryDate;

    /**
     * 应收日期
     */
    private LocalDateTime chargeDate;

    /**
     * 是否费用审核过(1=未审过,2=已审过)
     */
    private Integer chargeHistoryChecked;


    /**
     * 入账时间
     */
    private LocalDateTime invoiceEntranceDate;


    /**
     * 款号
     */
    private String productCode;

}
