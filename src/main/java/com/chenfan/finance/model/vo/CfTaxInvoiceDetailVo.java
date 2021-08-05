package com.chenfan.finance.model.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: xuxianbei
 * Date: 2021/3/6
 * Time: 10:25
 * Version:V1.0
 */
@Data
public class CfTaxInvoiceDetailVo {

    /**
     * 费用号
     */
    private String chargeCode;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费
     */
    private Integer chargeType;

    /**
     * arap类型(0=ar-收入 1=ap-支出)
     */
    private String arapType;

    /**
     * 收入
     */
    @JSONField(name = "amountPp")
    private BigDecimal invoiceDebit;

    /**
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    private String financeEntity;

    /**
     * 1= MCN
     */
    private Integer chargeSourceType;

    /**
     * 费用来源单号:1.MCN收入取收入合同编号SR20201231675;2.红人分成费,客户返点费,红人采购费:取执行单号：ZXD20201231675;3.年度返点费 取年度返点申请单号
     */
    private String chargeSourceCode;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;
}
