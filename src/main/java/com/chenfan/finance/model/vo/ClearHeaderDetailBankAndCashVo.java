package com.chenfan.finance.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: xuxianbei
 * Date: 2020/8/25
 * Time: 16:05
 * Version:V1.0
 */
@Data
@ApiModel(value = "财务-核销实收付款单")
public class ClearHeaderDetailBankAndCashVo {

    /**
     * 收付流水号
     */
    private String recordSeqNo;

    /**
     * 记录类型 1支付宝，2微信支付，3现金，4支票，5汇款
     */
    private String recordType;

    /**
     * 支票号
     */
    private String checkNo;

    /**
     * 收/付日期
     */
    private Date arapDate;

    /**
     * 总金额
     */
    private BigDecimal amount;

    /**
     * 余额 未核销金额	 金额-已核销金额（已核销金额取核销号）
     */
    private BigDecimal balanceBalance;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    /**
     * v1.4.0
     * 备注
     */
    private String remark;

    /**
     * v1.4.0
     * 出账公司  交易方-公司名称
     */
    private String paymentBranch;

    /**
     * v1.4.0
     * 打款-入账公司--出入账公司信息
     */
    private String payCompany;

    /**
     * v1.4.0
     * 上次余额
     */
    private BigDecimal lastBalance;

    /**
     * v1.4.0
     * 单据收付类型 1实收，2实付，3预收，4预付
     */
    private String arapType;


    /**
     * v4.9.0
     * 打款-入账银行名称--出入账公司信息
     */
    private String payBank;

    /**
     * v4.9.0
     * 打款-入账银行账号--出入账公司信息
     */
    private String payBankNo;
}
