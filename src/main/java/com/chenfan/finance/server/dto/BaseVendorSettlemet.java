package com.chenfan.finance.server.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Wen.Xiao
 * @Description //
 * @Date 2021/5/12  16:53
 * @Version 1.0
 */
public class BaseVendorSettlemet {
    private Long vendorSettlemetId;

    private Long vendorId;

    private String cvenRegCode;

    private String cvenBank;

    private String cvenAccount;
    /**
     * 法人代表
     */
    private String legalRepresentative;

    private String vendorLetterhead;

    private String accname;
    /**
     * 是否是一般纳税人 0是 1不是
     */
    private String generalTaxpayer;

    private String registeredCapital;

    private Date companyRegistrationTime;

    private String cvenBankCode;

    private String cvenExchName;
    /**
     * 企业类型
     */
    private String ivenGspType;

    private Integer ivenGspAuth;

    private Integer bvenOverseas;

    private Integer bvenAccPeriodMng;

    private BigDecimal ivenTaxRate;

    private String cvenLperson;

    private String createName;

    private Long createBy;

    private Date createDate;

    private String updateName;

    private Long updateBy;

    private Date updateDate;

    private Long companyId;

    public Long getVendorSettlemetId() {
        return vendorSettlemetId;
    }

    public void setVendorSettlemetId(Long vendorSettlemetId) {
        this.vendorSettlemetId = vendorSettlemetId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getCvenRegCode() {
        return cvenRegCode;
    }

    public void setCvenRegCode(String cvenRegCode) {
        this.cvenRegCode = cvenRegCode == null ? null : cvenRegCode.trim();
    }

    public String getCvenBank() {
        return cvenBank;
    }

    public void setCvenBank(String cvenBank) {
        this.cvenBank = cvenBank == null ? null : cvenBank.trim();
    }

    public String getCvenAccount() {
        return cvenAccount;
    }

    public void setCvenAccount(String cvenAccount) {
        this.cvenAccount = cvenAccount == null ? null : cvenAccount.trim();
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative == null ? null : legalRepresentative.trim();
    }

    public String getVendorLetterhead() {
        return vendorLetterhead;
    }

    public void setVendorLetterhead(String vendorLetterhead) {
        this.vendorLetterhead = vendorLetterhead == null ? null : vendorLetterhead.trim();
    }

    public String getAccname() {
        return accname;
    }

    public void setAccname(String accname) {
        this.accname = accname == null ? null : accname.trim();
    }

    public String getGeneralTaxpayer() {
        return generalTaxpayer;
    }

    public void setGeneralTaxpayer(String generalTaxpayer) {
        this.generalTaxpayer = generalTaxpayer == null ? null : generalTaxpayer.trim();
    }

    public String getRegisteredCapital() {
        return registeredCapital;
    }

    public void setRegisteredCapital(String registeredCapital) {
        this.registeredCapital = registeredCapital == null ? null : registeredCapital.trim();
    }

    public Date getCompanyRegistrationTime() {
        return companyRegistrationTime;
    }

    public void setCompanyRegistrationTime(Date companyRegistrationTime) {
        this.companyRegistrationTime = companyRegistrationTime;
    }

    public String getCvenBankCode() {
        return cvenBankCode;
    }

    public void setCvenBankCode(String cvenBankCode) {
        this.cvenBankCode = cvenBankCode == null ? null : cvenBankCode.trim();
    }

    public String getCvenExchName() {
        return cvenExchName;
    }

    public void setCvenExchName(String cvenExchName) {
        this.cvenExchName = cvenExchName == null ? null : cvenExchName.trim();
    }

    public String getIvenGspType() {
        return ivenGspType;
    }

    public void setIvenGspType(String ivenGspType) {
        this.ivenGspType = ivenGspType;
    }

    public Integer getIvenGspAuth() {
        return ivenGspAuth;
    }

    public void setIvenGspAuth(Integer ivenGspAuth) {
        this.ivenGspAuth = ivenGspAuth;
    }

    public Integer getBvenOverseas() {
        return bvenOverseas;
    }

    public void setBvenOverseas(Integer bvenOverseas) {
        this.bvenOverseas = bvenOverseas;
    }

    public Integer getBvenAccPeriodMng() {
        return bvenAccPeriodMng;
    }

    public void setBvenAccPeriodMng(Integer bvenAccPeriodMng) {
        this.bvenAccPeriodMng = bvenAccPeriodMng;
    }

    public BigDecimal getIvenTaxRate() {
        return ivenTaxRate;
    }

    public void setIvenTaxRate(BigDecimal ivenTaxRate) {
        this.ivenTaxRate = ivenTaxRate;
    }

    public String getCvenLperson() {
        return cvenLperson;
    }

    public void setCvenLperson(String cvenLperson) {
        this.cvenLperson = cvenLperson == null ? null : cvenLperson.trim();
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
