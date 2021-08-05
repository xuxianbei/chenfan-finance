package com.chenfan.finance.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 账单结算表
 * </p>
 *
 * @author lizhejin
 * @since 2020-10-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_invoice_settlement")
public class CfInvoiceSettlement implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "invoice_settlement_id", type = IdType.AUTO)
    private Long invoiceSettlementId;

    /**
     * 结算code
     */
    private String invoiceSettlementNo;

    /**
     * 账单id
     */
    private Long invoiceId;

    /**
     * 账单code
     */
    private String invoiceNo;

     /**
     * 结算状态
      * params.row.invoiceSettlementStatus === 5 ? '待开票'
      * : params.row.invoiceSettlementStatus === 8 ? '作废'
      * : params.row.invoiceSettlementStatus === 0 ? '已删除'
      * : params.row.invoiceSettlementStatus === 9 ? '已核销'
      * : params.row.invoiceSettlementStatus === 11 ? '已付款'
      * : params.row.invoiceSettlementStatus === 12 ? '已申请付款'
     */
    private Integer invoiceSettlementStatus;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 结算比例
     */
    private BigDecimal invoiceSettlementRate;

    /**
     * 结算金额
     */
    private BigDecimal invoiceSettlementMoney;

    /**
     * 核销单号
     */
    private String clearNo;

    /**
     * 核销日期
     */
    private LocalDateTime clearDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 公司
     */
    private Long companyId;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 更新人名称
     */
    private String updateName;

    /**
     * 更新日期
     */
    private LocalDateTime updateDate;
    /**
     * 对方发票号
     */
    private String customerInvoiceNo;

    /**
     * 对方发票日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDateTime customerInvoiceDate;

    /**
     * 付款方式(1:汇票,2:汇款,3:现金,4:银行转账,5:支付宝转账)
     */
    private Integer paymentMethod;

    /**
     * 发票是否已给到(0:是,1:否)
     */
    private Integer isInvoice;

    /**
     * 付款用途备注
     */
    private String paymentRemark;

    /**
     * 附件张数
     */
    private Integer accessory;

    /**
     * 户名
     */
    private String accname;

    /**
     * 收款单位
     */
    private String vendorLetterHead;

    /**
     * 银行
     */
    private String bank;

    /**
     * 银行帐号
     */
    private String bankAccounts;

    /**
     * 付款申请单打印:日期
     */
    private Date paymentDate;
}
