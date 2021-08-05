package com.chenfan.finance.model.dto;

import lombok.Data;

import java.util.List;
/**
 * @author liran
 */
@Data
public class InvoiceSettlementPercentDTO {

    /**
     * 账单id
     */
    private Long invoiceId;

    /**
     * 结算单id
     */
    private Long invoiceSettlementId;

    /**
     * 是否显示税额
     */
    private  Integer type;
}
