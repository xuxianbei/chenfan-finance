package com.chenfan.finance.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lr
 */
@Data
public class AdvancepayApplyInvoiceToBank {

    private String advancePayCode;

    private BigDecimal money;

    private BigDecimal actMoney;


}