package com.chenfan.finance.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: xuxianbei
 * Date: 2020/8/25
 * Time: 16:17
 * Version:V1.0
 */
@Data
@ApiModel(value = "财务-核销费用明细")
public class ClearHeaderDetailInvoiceDetailVo {

    /**
     * 费用id
     */
    private Long chargeId;

    /**
     * 费用号
     */
    private String chargeCode;

    /**
     * 费用种类
     */
    private String chargeType;

    /**
     * 应收/应付类型
     */
    private String arapType;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 结算主体名称
     */
    private String balanceName;

    /**
     * 收入
     */
    private BigDecimal clearDebit;

    /**
     * 支出
     */
    private BigDecimal clearCredit;

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
     * 品牌id
     */
    private Long brandId;

    /**
     * 费用来源id
     */
    private String chargeSourceCode;

    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 帐单抬头
     */
    private String invoiceTitle;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    private String financeEntity;

    /**
     * 3= MCN
     */
    private Integer chargeSourceType;

    /**
     * v1.4.0
     * 创建人
     */
    private Long createBy;

    /**
     * v1.4.0
     * 创建人名称
     */
    private String createName;


    /**
     * v1.4.0
     * 发票号-费用开票后反写；
     */
    private String taxInvoiceNo;

    /**
     * v4.9.0
     * 合同编号
     */
    private String contractCode;

    /**
     * v4.9.0
     * 合同金额
     */
    private BigDecimal businessAmount;


}
