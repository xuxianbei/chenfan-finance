package com.chenfan.finance.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chenfan.finance.utils.pageinfo.BaseInfoCustomTenantIdFill;
import com.chenfan.finance.utils.pageinfo.BaseInfoGet;
import com.chenfan.finance.utils.pageinfo.BaseInfoSet;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@TableName("cf_invoice_header")
public class CfInvoiceHeader implements Serializable, BaseInfoCustomTenantIdFill, BaseInfoGet {

    private static final long serialVersionUID = 1L;

    /**
     * 普通帐单内部编号
     */
    @TableId(value = "invoice_id", type = IdType.AUTO)
    private Long invoiceId;

    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 账单状态（1待提交；2待结算；3部分结算；4全部结算；7已作废，0已删除,5 业务待审核,8 待提交财务,9已核销,10审批中,11待打款,12已开票，13已撤回
     */
    private Integer invoiceStatus;

    /**
     * 发票状态(1=未填写、2=未校验、3=已校验)
     */
    private Integer customerInvoiceStatus;

    /**
     * 开票方式(1=开票、2=无票、3=后补票)
     */
    private Integer customerInvoiceWay;

    /**
     * 应收应付类型
     */
    private String invoiceType;

    /**
     * 业务类型(货品采购1; 销售订单2; 3:MCN)
     */
    private String jobType;

    /**
     * 合同url
     */
    private String contractUrl;

    /**
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    private String financeEntity;

    /**
     * 品牌id
     */
    private Long brandId;

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
    private LocalDateTime arapDate;

    /**
     * 帐单日期
     */
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
     * 推送u8状态
     */
    private Boolean u8State;

    /**
     * 更新人名称
     */
    private String updateName;

    /**
     * 公司
     */
    private Long companyId;

    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 核销状态(0=未核销,1=部分核销,2=全部核销)
     */
    private Integer clearStatus;

    /**
     * 核销金额
     */
    private BigDecimal clearAmount;

    /**
     * 调整后延期扣款金额
     */
    private BigDecimal adjustDelayMoney;

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
     * 调整后本期实付金额
     */
    private BigDecimal adjustRealMoney;

    /**
     * 结算开始时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private LocalDateTime dateStart;

    /**
     * 结算结束时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private LocalDateTime dateEnd;

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
     * 打款方式
     */
    private Long accountId;

    /**
     * 打款名称
     */
    private String accountName;

    /**
     * 打款类型：1, "红人收款账户" 2, "客户收款账户" 3, "公司账户" 4, "第三方账户"
     */
    private Integer accountType;

    /**
     * 审批流
     */
    private Long flowId;


    /**
     * 创建时间
     */
    private LocalDateTime settleCreateDate;

    /**
     * 标题
     */
    private String settleTitle;

    /**
     * 描述
     */
    private String settleDescription;

    /**
     * 结算单模板(1, "内部红人执行单模板";2, "外部红人执行单模板";3, "红人采购费/年度返点/客户返点模板")
     */
    private Integer settleTemplate;

    /**
     * 抵冲结算单号
     */
    private String associatedInvoiceSettlementNo;

    /**
     * 抵冲账单号：associated_invoice_no
     */
    private String associatedInvoiceNo;


    /**
     * 物料类型 0成衣 1物料
     */
    private Integer materialType;

    /**
     * 订单款直播款
     */
    private Integer salesType;
}
