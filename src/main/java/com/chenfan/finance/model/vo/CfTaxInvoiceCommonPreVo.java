package com.chenfan.finance.model.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 开票-预新增
 * @author: xuxianbei
 * Date: 2021/3/9
 * Time: 14:25
 * Version:V1.0
 */
@Data
public class CfTaxInvoiceCommonPreVo {

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
     * 开票账户
     */
    private String billingAccount;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 费用列表
     */
    private List<CfChargeCommonVo> chargeCommonVoList;

    /**
     * 拆分类型： 0；全费用；1：拆分费用
     */
    private Integer splitType;

    /**
     * 分批开票信息
     */
    private List<SplitVo> splitVos;

}
