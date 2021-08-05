package com.chenfan.finance.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 开票
 * @author: xuxianbei
 * Date: 2021/3/5
 * Time: 16:55
 * Version:V1.0
 */
@Data
public class TaxInvoiceVo {

    /**
     * 普通发票内部编号
     */
    private Long taxInvoiceId;

    /**
     * 开票流水号
     */
    private String taxInvoiceNo;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 发票状态： 1待提交、2审批中、3待收款、4已开票、5已核销、6已撤回
     */
    private Integer taxInvoiceStatus;

    /**
     * 开票抬头状态
     */
    private String invoiceTitle;

    /**
     * 发票号
     */
    private String invoiceNo;

    /**
     * 发票日期
     */
    private LocalDateTime invoiceDate;

    /**
     * 核销状态(0=未核销,1=部分核销,2=全部核销)
     */
    private Integer clearStatus;

    /**
     * 开票类型：1普票，2专票
     */
    private String taxInvoiceType;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 创建人名称
     */
    private String createName;


    /**
     * 合同号
     */
    private String  contractNo;

    /**
     * 我司财务主体
     */
    private String financeEntity;

    /**
     * 财务合同金额
     */
    private BigDecimal contractMoney;

    /**
     * 开票金额
     */
    private BigDecimal invoicelDebit;



}
