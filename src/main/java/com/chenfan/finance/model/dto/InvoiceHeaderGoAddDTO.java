package com.chenfan.finance.model.dto;

import lombok.Data;

import java.util.List;
/**
 * @author liran
 */
@Data
public class InvoiceHeaderGoAddDTO {
    private List<Long> chargeIds;
    private String invoiceNo;
}
