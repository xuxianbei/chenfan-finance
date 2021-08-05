package com.chenfan.finance.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 2062
 */
@Data
public class Pay {

    private Integer payType;

    private BigDecimal payValue;

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public BigDecimal getPayValue() {
        return payValue;
    }

    public void setPayValue(BigDecimal payValue) {
        this.payValue = payValue;
    }
}
