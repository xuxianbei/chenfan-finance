package com.chenfan.finance.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author mbji
 */
@Data
public class PoMainInfoCount {
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

    private BigDecimal totalShortGood;
    /**
     * 累计到货量总计
     */
    private BigDecimal arriveTotalCount;

    public BigDecimal getArriveTotalCount() {
        return arriveTotalCount;
    }

    public void setArriveTotalCount(BigDecimal arriveTotalCount) {
        this.arriveTotalCount = arriveTotalCount;
    }

    public BigDecimal getTotalShortGood() {
        return totalShortGood;
    }

    public void setTotalShortGood(BigDecimal totalShortGood) {
        this.totalShortGood = totalShortGood;
    }

    public BigDecimal getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(BigDecimal totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public BigDecimal getTotalQualityTestingInWay() {
        return totalQualityTestingInWay;
    }

    public void setTotalQualityTestingInWay(BigDecimal totalQualityTestingInWay) {
        this.totalQualityTestingInWay = totalQualityTestingInWay;
    }

    public BigDecimal getTotalQualifiedQuantity() {
        return totalQualifiedQuantity;
    }

    public void setTotalQualifiedQuantity(BigDecimal totalQualifiedQuantity) {
        this.totalQualifiedQuantity = totalQualifiedQuantity;
    }

    public BigDecimal getTotalUnQualifiedQuantity() {
        return totalUnQualifiedQuantity;
    }

    public void setTotalUnQualifiedQuantity(BigDecimal totalUnQualifiedQuantity) {
        this.totalUnQualifiedQuantity = totalUnQualifiedQuantity;
    }

    public BigDecimal getTotalInstorCount() {
        return totalInstorCount;
    }

    public void setTotalInstorCount(BigDecimal totalInstorCount) {
        this.totalInstorCount = totalInstorCount;
    }

    public BigDecimal getTotalIncludedTaxMoney() {
        return totalIncludedTaxMoney;
    }

    public void setTotalIncludedTaxMoney(BigDecimal totalIncludedTaxMoney) {
        this.totalIncludedTaxMoney = totalIncludedTaxMoney;
    }

    public BigDecimal getTotalTaxMoney() {
        return totalTaxMoney;
    }

    public void setTotalTaxMoney(BigDecimal totalTaxMoney) {
        this.totalTaxMoney = totalTaxMoney;
    }
}
