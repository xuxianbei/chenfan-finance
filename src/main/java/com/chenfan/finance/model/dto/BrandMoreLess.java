package com.chenfan.finance.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 2062
 */
@Data
public class BrandMoreLess {
    private Integer brandMoreLessId;

    private Integer brandId;

    private Integer lowestCount;

    private Integer highestCount;

    private BigDecimal moreLess;

    public Integer getBrandMoreLessId() {
        return brandMoreLessId;
    }

    public void setBrandMoreLessId(Integer brandMoreLessId) {
        this.brandMoreLessId = brandMoreLessId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getLowestCount() {
        return lowestCount;
    }

    public void setLowestCount(Integer lowestCount) {
        this.lowestCount = lowestCount;
    }

    public Integer getHighestCount() {
        return highestCount;
    }

    public void setHighestCount(Integer highestCount) {
        this.highestCount = highestCount;
    }

    public BigDecimal getMoreLess() {
        return moreLess;
    }

    public void setMoreLess(BigDecimal moreLess) {
        this.moreLess = moreLess;
    }
}