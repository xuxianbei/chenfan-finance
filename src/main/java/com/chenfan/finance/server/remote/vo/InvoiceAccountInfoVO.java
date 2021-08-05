package com.chenfan.finance.server.remote.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * author:   tangwei
 * Date:     2021/5/14 15:50
 * Description: 财务批量查询关联单据账户信息
 */
@Data
public class InvoiceAccountInfoVO implements Serializable {

    private static final long serialVersionUID = 3996844164148760523L;

    @ApiModelProperty(value = "1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费")
    private Integer chargeType;

    @ApiModelProperty(value = "来源单号")
    private String chargeSourceCode;

    /**
     * 财务确认分成金额
     */
    @ApiModelProperty("财务核准分成金额")
    private BigDecimal financeDevidedAmount;

    /**
     * 账户信息表主键id
     */
    @ApiModelProperty("账户信息表主键id")
    private Long accountId;

    @ApiModelProperty("账户类型：1红人账户，2客户账户")
    private Integer accountType;

    /**
     * 收款户名
     */
    @ApiModelProperty(value = "收款户名", example = "1")
    private String accountName;

    /**
     * 收款卡号
     */
    @ApiModelProperty(value = "收款卡号")
    private String accountNumber;

    /**
     * 身份证号码
     */
    @ApiModelProperty(value = "身份证号码")
    private String accountIdCard;

    /**
     * 开户行
     */
    @ApiModelProperty(value = "开户行")
    private String accountBank;

    /**
     * 开户支行
     */
    @ApiModelProperty(value = "开户支行")
    private String accountBranchBank;

    /**
     * 红人开户省
     */
    @ApiModelProperty("红人开户省")
    private String accountProvince;

    /**
     * 红人开户市
     */
    @ApiModelProperty("红人开户市")
    private String accountCity;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String accountPhone;

    /**
     * 品牌名称
     */
    @ApiModelProperty(value = "品牌名称")
    private String brandName;
}