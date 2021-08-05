package com.chenfan.finance.model.vo;

import com.chenfan.finance.model.CfInvoiceSettlement;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author:lywang
 * @Date:2020/10/21
 */
@Data
public class CfInvoiceSettlementVo extends CfInvoiceSettlement {
    private BigDecimal ClearMoney;

    /**
     * 帐单收付类型
     */
    private String invoiceType;

    /**
     * 业务类型(采购1; 销售订单2)
     */
    private String jobType;

    private String balanceName;

    private String settlementRate;
}
