package com.chenfan.finance.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xiongbin
 * @date 2020-08-28
 */
@Data
public class CfInvoiceHeaderDetailVO implements Serializable {
    private static final long serialVersionUID = -7148621769238665350L;

    /**
     * 普通帐单内部编号
     */
    private Long invoiceId;

    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 账单状态
     * {@link com.chenfan.finance.enums.InvoiceStatusEnum}
     */
    private Integer invoiceStatus;

    /**
     * 结算单状态
     * {@link com.chenfan.finance.enums.SettlementStatusEnum}
     */
    private Integer settlementStatus;
    /**
     * 结算金额
     */
    private BigDecimal settlementAmount;
    /**
     * 结算余额
     */
    private BigDecimal balanceOfStatement;
    /**
     * 销售类型(1. 直播; 2. 正常)
     */
    private Integer salesType;

    /**
     * 发票状态(1=未填写、2=未校验、3=已校验)
     */
    private Integer customerInvoiceStatus;

    /**
     * 帐单收付类型(应收应付类型(0=收debit, 1=付credit))
     */
    private String invoiceType;

    /**
     * 业务类型(采购1; 销售订单2)
     */
    private String jobType;

    /**
     * 品牌id
     */
    private Long brandId;

    private String brandName;

    /**
     * 结算主体
     */
    private String balance;
    /**
     * 结算主体简称
     */
    private String balanceName;
    /**
     * 帐单抬头
     */
    private String invoiceTitle;

    /**
     * 帐单抬头名称
     */
    private String invoiceTitleName;

    /**
     * 银行
     */
    private String bank;

    /**
     * 银行帐号
     */
    private String bankAccounts;

    /**
     * 账单币种
     */
    private String invoiceCurrencyCode;

    /**
     * 账单汇率
     */
    private BigDecimal invoiceExchangeRate;

    /**
     * 帐单金额-应收总金额
     */
    private BigDecimal invoicelDebit;

    /**
     * 帐单金额-应付总金额
     */
    private BigDecimal invoicelCredit;

    /**
     * 应收付日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime arapDate;

    /**
     * 帐单日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime invoiceDate;

    /**
     * 账单制作人id
     */
    private Long invoiceBy;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人登录名称
     */
    private Long createBy;

    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createDate;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 更新人登录名称
     */
    private Long updateBy;

    /**
     * 更新日期
     */
    private LocalDateTime updateDate;

    /**
     * 更新人名称
     */
    private String updateName;

    /**
     * 公司
     */
    private Long companyId;

    /**
     * 核销状态(0=未核销,1=部分核销,2=全部核销)
     */
    private Integer clearStatus;

    /**
     * 核销金额
     */
    private BigDecimal clearAmount;

    private List<ChargeInvoiceDetailVO> chargeList;

    private List<CfInvoiceSettlementVo> settlementList;

    private List<ChargeInVO> chargeInList;

    /**
     * 调整后延期扣款金额
     */
    private BigDecimal adjustDelayMoney;
    /**
     * 调整后本期实付金额
     */
    private BigDecimal adjustRealMoney;

    private List<CfInvoiceHeaderClearVo> cfInvoiceHeaderClearVos;

    /**
     * 账单费用详情条数
     */
    private Integer chargeListSize;

    /**
     * 结算明细条数
     */
    private Integer settlementListSize;

    /**
     * 结算比例
     */
    private String settlementRate;
    /**
     * 供应商名称
     */
    private String vendorName;
    /**
     * 供应商简称
     */
    private String vendorAbbName;
    /**
     * 调整后质检扣款金额
     */
    private BigDecimal adjustQcMoney;

    /**
     * 红字扣减
     */
    private BigDecimal adjustRedMoney;

    /**
     * 税差
     */
    private BigDecimal adjustTaxMoney;

    /**
     * 其他扣费
     */
    private BigDecimal adjustOtherMoney;

    /**
     * 结算开始时间
     */
    private LocalDateTime dateStart;

    /**
     * 结算结束时间
     */
    private LocalDateTime dateEnd;

    /**
     * 供应商id
     */
    private Integer vendorId;

    /**
     * 审核人
     */
    private String reviewer;

    /**
     * 审核时间
     */
    private LocalDateTime reviewerDate;

    /**
     * 预付款
     */
    private BigDecimal advancePayMoney;


    /**
     * 关联的红字结算单号
     */
    private List<String> associatedInvoiceSettlementNoList;

    /**
     * 关联的红字账单
     */
    private List<String> associatedInvoiceNoList;
    /**
     * 关联的蓝字结算单号
     */
    private String associatedInvoiceSettlementNo;

    /**
     * 抵关联的蓝字账单
     */
    private String associatedInvoiceNo;
    /**
     * 物料类型 0成衣 1物料
     */
    private Integer materialType;

    /**
     * 关联的红字账单信息
     */
    private List<CfInvoiceHeaderListVO> associatedInvoiceList;
}
