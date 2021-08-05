package com.chenfan.finance.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单开票核销详情
 *
 * @author: xuxianbei
 * Date: 2021/3/3
 * Time: 11:11
 * Version:V1.0
 */
@Data
public class CfInvoiceClearDetailVo {

    /**
     * 结算金额
     */
    private BigDecimal invoiceSettlementMoney;

    /**
     * 核销流水号
     */
    private String clearNo;

    /**
     * 本次核销总计
     */
    private BigDecimal nowClearBalance;

    /**
     * 本次核销余额
     */
    private BigDecimal nowBalanceBalance;

    /**
     * 核销时间
     */
    private LocalDateTime clearDate;

    /**
     * 备注
     */
    private String remark;

}
