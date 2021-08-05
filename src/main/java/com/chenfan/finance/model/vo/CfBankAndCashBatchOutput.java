package com.chenfan.finance.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/5/12
 * Time: 20:17
 * Version:V1.0
 */
@Data
public class CfBankAndCashBatchOutput {

    /**
     * 帐单号
     */
    @Excel(name = "账单编号")
    private String invoiceNo;

    /**
     * 打款-入账公司--出入账公司信息
     */
    @Excel(name = "我司打款户名")
    private String payCompany;

    /**
     * 打款-入账银行名称--出入账公司信息
     */
    @Excel(name = "我司打款银行")
    private String payBank;

    /**
     * 打款-入账银行账号--出入账公司信息
     */
    @Excel(name = "我司打款账号")
    private String payBankNo;

    /**
     * 实收付备注
     */
    @Excel(name = "备注")
    private String remark;

    /**
     * 错误原因
     */
    @Excel(name = "错误原因", width = 35)
    private String error;

}
