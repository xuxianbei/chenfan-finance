package com.chenfan.finance.model.vo;

import com.chenfan.finance.model.dto.CfChargeSaveQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 费收_费用(cf_charge）
 * </p>
 *
 * @author lr
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CfChargeDetailVO extends CfChargeSaveQuery {
    /**
     * 费用审核状态 1草稿、2已提交、3已审核、4已驳回、5已作废、0已删除
     */
    private Integer checkStatus;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 结算主体 供应商名称
     */
    private String balanceName;

    /**
     * 费用号
     */
    private String chargeCode;


    //核销合账单费用回显字段
    /**
     * 发票号
     */
    private String taxInvoiceNo;

    /**
     * 发票日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime taxInvoiceDate;


    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 帐单抬头
     */
    private String invoiceTitle;

    /**
     * 帐单抬头名称
     */
    private String invoiceTitleName;

    /**
     * 帐单日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime invoiceDate;
    /**
     * 入账时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime invoiceEntranceDate;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime actualDate;

    /**
     * 实收历史日期
     */
    private String actualHistoryDate;

    /**
     * `cvenBank` '开户银行',
     *   `cvenAccount`  '银行账号',
     */
    private String cvenBank;
    private String cvenAccount;
    /**
     * 应收/应付审核日期
     */
    private LocalDateTime arapCheckDate;

}
