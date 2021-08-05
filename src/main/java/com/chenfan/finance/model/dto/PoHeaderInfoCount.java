package com.chenfan.finance.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author llq
 */
@Data
public class PoHeaderInfoCount {
    /**
     * 下单数量总计
     */
    private BigDecimal totalOrderCount = BigDecimal.ZERO;
    /**
     * 质检在途总计
     */
    private BigDecimal totalQualityTestingInWay = BigDecimal.ZERO;
    /**
     * 合格到货量总计
     */
    private BigDecimal totalQualifiedQuantity = BigDecimal.ZERO;

    /**
     * 拒收数量总计
     */
    private BigDecimal totalUnQualifiedQuantity = BigDecimal.ZERO;

    /**
     * 累计入库数量总计
     */
    private BigDecimal totalInstorCount = BigDecimal.ZERO;
    /**
     * 含税金额总计
     */
    private BigDecimal totalIncludedTaxMoney = BigDecimal.ZERO;
    /**
     * 税额总计
     */
    private BigDecimal totalTaxMoney = BigDecimal.ZERO;

    private BigDecimal totalShortCount;
    /**
     * 累计到货量总计
     */
    private BigDecimal arriveTotalCount;


}
