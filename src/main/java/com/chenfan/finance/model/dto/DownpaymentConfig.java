package com.chenfan.finance.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 2062
 */
@Data
public class DownpaymentConfig {
    private Integer paymentConfId;

    private String paymentName;

    private BigDecimal proportion;

    private String tailName;

    private BigDecimal tailProportion;

    private String sysOrgCode;

    private String sysCompanyCode;

    private String createName;

    private Long createBy;

    private Date createDate;

    private String updateName;

    private Long updateBy;

    private Date updateDate;

    public Integer getPaymentConfId() {
        return paymentConfId;
    }

    public void setPaymentConfId(Integer paymentConfId) {
        this.paymentConfId = paymentConfId;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName == null ? null : paymentName.trim();
    }

    public BigDecimal getProportion() {
        return proportion;
    }

    public void setProportion(BigDecimal proportion) {
        this.proportion = proportion;
    }

    public String getTailName() {
        return tailName;
    }

    public void setTailName(String tailName) {
        this.tailName = tailName == null ? null : tailName.trim();
    }

    public BigDecimal getTailProportion() {
        return tailProportion;
    }

    public void setTailProportion(BigDecimal tailProportion) {
        this.tailProportion = tailProportion;
    }

    public String getSysOrgCode() {
        return sysOrgCode;
    }

    public void setSysOrgCode(String sysOrgCode) {
        this.sysOrgCode = sysOrgCode == null ? null : sysOrgCode.trim();
    }

    public String getSysCompanyCode() {
        return sysCompanyCode;
    }

    public void setSysCompanyCode(String sysCompanyCode) {
        this.sysCompanyCode = sysCompanyCode == null ? null : sysCompanyCode.trim();
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName == null ? null : createName.trim();
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName == null ? null : updateName.trim();
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}