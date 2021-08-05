package com.chenfan.finance.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * author:   tangwei
 * Date:     2021/3/5 15:34
 * Description: mcn收入合同关联费用
 */
@Data
public class McnChargeCommonVO implements Serializable {

    private static final long serialVersionUID = 4399373128655609448L;

    /**
     * 费用内部编号id
     */
    private Long chargeId;

    /**
     * 费用号
     */
    private String chargeCode;

    /**
     * 1= MCN
     */
    private Integer chargeSourceType;

    /**
     * 费用种类 1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费
     */
    private Integer chargeType;

    /**
     * 费用审核状态 1草稿、2已提交、3已审核、4已驳回、5已作废、0已删除
     */
    private Integer checkStatus;

    /**
     * AR=收、AP=付；
     */
    private String arapType;

    /**
     * 费用来源单号:1.MCN收入取收入合同编号SR20201231675;2.红人分成费,客户返点费,红人采购费:取执行单号：ZXD20201231675;3.年度返点费 取年度返点申请单号
     */
    private String chargeSourceCode;

    /**
     * 费用来源明细: 默认取值等于来源单号，其中MCN收入费用记执行单号
     */
    private String chargeSourceDetail;

    /**
     * 总价(pp)
     */
    private BigDecimal amountPp;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    private String financeEntity;

    /**
     * 发票号
     */
    private String taxInvoiceNo;

    /**
     * 发票流水号
     */
    private String invoiceNo;

    /**
     * 账单抬头
     */
    private String invoiceTitle;

    /**
     * 核销余额
     */
    private BigDecimal clearBalanceAmount;

    /**
     * 核销流水号 多个用逗号隔开
     */
    private String clearNos;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 拆分类型： 0：全费用；1：拆分费用
     * version: mcn v1.1.2
     */
    private Integer splitType;

    /**
     * v1.4.0核销id
     */
    private Long clearId;
}