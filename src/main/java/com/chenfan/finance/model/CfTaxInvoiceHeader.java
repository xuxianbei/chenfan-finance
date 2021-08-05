package com.chenfan.finance.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.chenfan.finance.utils.pageinfo.BaseInfoCustomTenantIdFill;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * <p>
 * 发票主表
 * </p>
 *
 * @author lizhejin
 * @since 2021-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_tax_invoice_header")
public class CfTaxInvoiceHeader implements Serializable, BaseInfoCustomTenantIdFill {

    private static final long serialVersionUID = 1L;

    /**
     * 普通发票内部编号
     */
    @TableId(value = "tax_invoice_id", type = IdType.AUTO)
    private Long taxInvoiceId;

    /**
     * 开票流水号
     */
    private String taxInvoiceNo;

    /**
     * 发票状态： 开票状态： 1待提交、2审批中、3审批拒绝、4待开票、5已开票、6已核销、7已撤回、8已作废
     */
    private Integer taxInvoiceStatus;

    /**
     * 开票类型：1普票，2专票
     */
    private String taxInvoiceType;

    /**
     * 开票方式(1=开票、2=无票、)
     */
    private Integer taxInvoiceWay;

    /**
     * 业务类型(1= MCN)
     */
    private String jobType;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 开票抬头
     */
    private String invoiceTitle;

    /**
     * 开票抬头状态
     */
    private String invoiceTitleType;

    /**
     * 纳税人识别号
     */
    private String taxpayerIdentificationNumber;

    /**
     * 开票地址
     */
    private String billingAddress;

    /**
     * 银行账户
     */
    private String billingAccount;

    /**
     * 开票电话
     */
    private String billingTel;

    /**
     * 开户银行
     */
    private String billingBank;

    /**
     * 帐单金额-应收总金额
     */
    private BigDecimal invoicelDebit;

    /**
     * 开票内容
     */
    private String billingContent;

    /**
     * 备注
     */
    private String remark;

    /**
     * 发票号
     */
    private String invoiceNo;

    /**
     * 发票日期
     */
    private LocalDateTime invoiceDate;

    /**
     * 发票财务账期；精确到月
     */
    private String paymentDays;

    /**
     * 发票备注
     */
    private String invoiceRemark;

    /**
     * 核销状态(0=未核销,1=部分核销,2=全部核销)
     */
    private Integer clearStatus;

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
     * 更新时间
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
     * 租户id
     */
    private Long tenantId;

    /**
     * 审批流ID
     */
    private Long flowId;


}
