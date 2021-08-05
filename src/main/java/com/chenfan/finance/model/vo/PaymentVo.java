package com.chenfan.finance.model.vo;

import lombok.Data;

import java.math.BigDecimal;
/**
 * @author liran
 */
@Data
public class PaymentVo {

    private String productCode;
    private String payment;
    private BigDecimal proportion;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public BigDecimal getProportion() {
        return proportion;
    }

    public void setProportion(BigDecimal proportion) {
        this.proportion = proportion;
    }
}
