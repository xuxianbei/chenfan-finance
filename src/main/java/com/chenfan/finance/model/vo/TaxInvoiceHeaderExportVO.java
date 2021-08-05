package com.chenfan.finance.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * author:   tangwei
 * Date:     2021/5/14 13:45
 * Description: 发票导出
 */
@Data
public class TaxInvoiceHeaderExportVO implements Serializable {

    private static final long serialVersionUID = 822507628444213908L;

    /**
     * 普通发票内部编号
     */
    private Long taxInvoiceId;

    /**
     * 开票流水号
     */
    @Excel(name = "开票流水号")
    private String taxInvoiceNo;

    /**
     * 发票状态： 开票状态： 1待提交、2审批中、3审批拒绝、4待开票、5已开票、6已核销、7已撤回、8已作废
     */
    @Excel(name = "发票状态", replace = {"待提交_1", "审批中_2", "审批拒绝_3", "待开票_4", "已开票_5", "已核销_6", "已撤回_7", "已作废_8"})
    private Integer taxInvoiceStatus;

    /**
     * 开票抬头
     */
    @Excel(name = "开票抬头")
    private String invoiceTitle;

    /**
     * 纳税人识别号
     */
    @Excel(name = "纳税人识别号")
    private String taxpayerIdentificationNumber;

    /**
     * 开票地址
     */
    @Excel(name = "开票地址")
    private String billingAddress;

    /**
     * 开票电话
     */
    @Excel(name = "开票电话")
    private String billingTel;

    /**
     * 开户银行
     */
    @Excel(name = "开户行")
    private String billingBank;

    /**
     * 银行账户
     */
    @Excel(name = "银行卡号")
    private String billingAccount;

    /**
     * 帐单金额-应收总金额-开票金额
     */
    @Excel(name = "开票金额", type = 10)
    private BigDecimal invoicelDebit;

    /**
     * 开票内容
     */
    @Excel(name = "开票内容")
    private String billingContent;

    /**
     * 开票类型：1普票，2专票
     */
    @Excel(name = "开票类型")
    private String taxInvoiceType;

    /**
     * 开票方式(1=开票、2=无票、)
     */
    @Excel(name = "开票方式", replace = {"开票_1", "无票_2"})
    private Integer taxInvoiceWay;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

    /**
     * 发票号
     */
    @Excel(name = "发票号")
    private String invoiceNo;

    /**
     * 发票日期
     */
    @Excel(name = "开票日期", format = "yyyy-MM-dd HH:mm:dd")
    private LocalDateTime invoiceDate;

    /**
     * 发票财务账期；精确到月
     */
    @Excel(name = "财务账期")
    private String paymentDays;

    /**
     * 发票备注
     */
    @Excel(name = "发票备注")
    private String invoiceRemark;

    /**
     * 关联费用编号
     */
    @Excel(name = "关联费用编号")
    private String relatedFeeCode;

    /**
     * 核销状态(0=未核销,1=部分核销,2=全部核销)
     */
/*
    @Excel(name = "核销状态", replace = {"未核销_0", "部分核销_1", "全部核销_2", "_null"})
    private Integer clearStatus;
*/

    /**
     * 结算主体
     */
    @Excel(name = "结算主体")
    private String balance;

    /**
     * 财务主体
     */
    @Excel(name = "财务主体")
    private String financeEntity;

    /**
     * 来源单号
     */
    @Excel(name = "来源单号")
    private String chargeSourceCode;

    /**
     * 来源单号
     */
    @Excel(name = "费用创建人")
    private String createName;
}