package com.chenfan.finance.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.chenfan.finance.server.remote.vo.ExcutionSettleInfoVO;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/5/12
 * Time: 19:47
 * Version:V1.0
 */
@Data
public class CfBankAndCashBatchImport {

    /**
     * 帐单号
     */
    @Excel(name = "账单编号")
    private String invoiceNo;

    /**
     * 打款-入账公司-id  前端使用
     */
    private String payCompanyId;

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
     * 结算信息
     */
    private ExcutionSettleInfoVO excutionSettleInfoVO;
}
