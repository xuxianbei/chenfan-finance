package com.chenfan.finance.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 2062
 */
@Data
public class PriceDetailInfo {

    /**
     * 商品SKU
     */
    private String inventoryCode;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 含税单价
     */
    private BigDecimal taxPrice;

    /**
     * 不含税单价
     */
    private BigDecimal unitPrice;

    /**
     * 入库单code
     */
    private String recordCode;
    private String wdtRecordCode;
    private Integer vendorId;
    private Integer inventoryId;
    private Long rdId;
    private Integer poId;
    private String productCode;
}
