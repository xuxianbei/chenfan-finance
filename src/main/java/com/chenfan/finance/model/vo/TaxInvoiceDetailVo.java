package com.chenfan.finance.model.vo;

import com.chenfan.finance.model.CfBsOperationLog;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 开票详情
 *
 * @author: xuxianbei
 * Date: 2021/3/5
 * Time: 16:58
 * Version:V1.0
 */
@Data
public class TaxInvoiceDetailVo {

    /**
     * 普通发票内部编号
     */
    private Long taxInvoiceId;

    /**
     * 发票状态： 开票状态： 1待提交、2审批中、3审批拒绝、4待开票、5已开票、6已核销、7已撤回、8已作废
     */
    private Integer taxInvoiceStatus;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 开票抬头
     */
    private String invoiceTitle;

    /**
     * 纳税人识别号
     */
    private String taxpayerIdentificationNumber;

    /**
     * 开票地址
     */
    private String billingAddress;

    /**
     * 开票电话
     */
    private String billingTel;

    /**
     * 开户银行
     */
    private String billingBank;

    /**
     * 银行账户
     */
    private String billingAccount;

    /**
     * 开票内容
     */
    private String billingContent;

    /**
     * 帐单金额-应收总金额
     */
    private BigDecimal invoicelDebit;

    /**
     * 开票类型：1普票，2专票
     */
    private String taxInvoiceType;

    /**
     * 开票方式(1=开票、2=无票、3=后补票)
     */
    private Integer taxInvoiceWay;

    /**
     * 备注
     */
    private String remark;

    /**
     * 费用明细
     */
    private List<CfTaxInvoiceDetailVo> invoiceDetailVoList;

    /**
     * 核销明细
     */
    private CfInvoiceClearDetailVo cfInvoiceClearDetailVo;

    /**
     * 全部审批流
     */
    private List<Long> flowIds;

    /**
     * 当前审批流
     */
    private Long flowId;


    /**
     * 发票号
     */
    private String customerInvoiceNo;

    /**
     * 发票日期
     */
    private LocalDateTime customerInvoiceDate;

    /**
     * 备注
     */
    private String customerRemark;


    /**
     * 发票财务账期；精确到月
     */
    private String paymentDays;

    /**
     * 拆分类型： 0；全费用；1：拆分费用
     */
    private Integer splitType;

    /**
     * 分批开票信息
     */
    private List<SplitVo> splitVos;

    /**
     * 操作日字记录
     */
    private List<CfBsOperationLog> cfBsOperationLogList;

}
