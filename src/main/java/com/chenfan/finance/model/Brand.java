package com.chenfan.finance.model;

import lombok.Data;

/**
 * @author 2062
 */
@Data
public class Brand {

    private Integer brandId;

    private String brandName;

    /**
     * 客户名称
     */
    private String customerName;

    private String financialBody;

    public String getFinancialBody() {
        return financialBody;
    }

    public void setFinancialBody(String financialBody) {
        this.financialBody = financialBody;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
