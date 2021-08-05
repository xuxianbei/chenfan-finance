package com.chenfan.finance.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lqliu
 * @date 2020-7-23
 */
@Data
public class InventoryPrice {

    private String inventoryCode;

    private BigDecimal taxRate;

    private BigDecimal unitPrice;

    private BigDecimal taxUnitPrice;

    private Integer vendorId;
}