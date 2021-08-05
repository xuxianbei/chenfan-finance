package com.chenfan.finance.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: xuxianbei
 * Date: 2021/3/18
 * Time: 17:24
 * Version:V1.0
 */
@Data
public class CfBankAndCashImport {

    /**
     * 业务类型(1货品采购; 2销售订单;3MCN)
     */
    @Excel(name = "* 业务类型", replace = {"大货采购_1", "MCN_3"})
    private Integer jobType;

    /**
     * 单据收付类型 1实收，2实付，3预收，4预付
     */
    @Excel(name = "* 收付类型", replace = {"实收_1", "实付_2"})
    private String arapType;

    /**
     * 收付流水号
     */
    @Excel(name = "* 流水号")
    private String recordSeqNo;


    /**
     * 记录类型 1支付宝，2微信支付，3现金，4支票，5汇款
     */
    @Excel(name = "* 记录类型")
    private String recordType;

    /**
     * 总金额
     */
    @Excel(name = "* 金额")
    private BigDecimal amount;

    /**
     * 收/付日期
     */
    @Excel(name = "* 收付日期", importFormat = "yyyy-MM-dd HH:mm:ss")
    private Date arapDate;

    /**
     * 交易公司名称
     */
    @Excel(name = "*交易公司名称")
    private String paymentBranch;

    /**
     * 交易公司银行名称
     */
    @Excel(name = "交易公司银行名称")
    private String bank;

    /**
     * 交易公司银行账号
     */
    @Excel(name = "交易公司银行账号")
    private String bankNo;

    /**
     * 打款-入账公司--出入账公司信息
     */
    @Excel(name = "* 出入账公司名称")
    private String payCompany;

    /**
     * 打款-入账银行名称--出入账公司信息
     */
    @Excel(name = "* 出入账银行")
    private String payBank;

    /**
     * 打款-入账银行账号--出入账公司信息
     */
    @Excel(name = "* 出入账银行账号")
    private String payBankNo;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;
}
