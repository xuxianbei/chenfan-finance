package com.chenfan.finance.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 2062
 */
@Data
public class PayApplyInfo {
    /**
     * 含税总额
     */
    private BigDecimal includedTaxMoneyCount;
    /**
     * 供应商名称
     */
    private String vendorCode;

    /**
     * 供应商id
     */
    private Integer vendorId;

    /**
     * 供应商名称
     */
    private String vendorName;
    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 品牌id
     */
    private Integer brandId;

    private String accName;

    /**
     * 开户银行
     */
    private String bank;

    /**
     * 银行账号
     */
    private String bankAccount;

    private Integer  poType;

    /**
     * 定金金额
     */
    private BigDecimal bargain;

    /**
     * 尾款金额
     */
    private BigDecimal retainage;

    /**
     * 定金比例
     */
    private  BigDecimal proportion;

    /**
     * 尾款比例
     */
    private BigDecimal tailProportion;

    private BigDecimal payValue;

    private String payType;

    private Integer paymentConfId;

    private Integer firstOrLastPay;

    private BigDecimal firstPayment;

    public Integer getFirstOrLastPay() {
        return firstOrLastPay;
    }

    public void setFirstOrLastPay(Integer firstOrLastPay) {
        this.firstOrLastPay = firstOrLastPay;
    }

    public Integer getPaymentConfId() {
        return paymentConfId;
    }

    public void setPaymentConfId(Integer paymentConfId) {
        this.paymentConfId = paymentConfId;
    }

    public BigDecimal getPayValue() {
        return payValue;
    }

    public void setPayValue(BigDecimal payValue) {
        this.payValue = payValue;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigDecimal getIncludedTaxMoneyCount() {
        return includedTaxMoneyCount;
    }

    public void setIncludedTaxMoneyCount(BigDecimal includedTaxMoneyCount) {
        this.includedTaxMoneyCount = includedTaxMoneyCount;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public BigDecimal getBargain() {
        return bargain;
    }

    public void setBargain(BigDecimal bargain) {
        this.bargain = bargain;
    }

    public BigDecimal getRetainage() {
        return retainage;
    }

    public void setRetainage(BigDecimal retainage) {
        this.retainage = retainage;
    }

    public Integer getPoType() {
        return poType;
    }

    public void setPoType(Integer poType) {
        this.poType = poType;
    }

    public BigDecimal getProportion() {
        return proportion;
    }

    public void setProportion(BigDecimal proportion) {
        this.proportion = proportion;
    }

    public BigDecimal getTailProportion() {
        return tailProportion;
    }

    public void setTailProportion(BigDecimal tailProportion) {
        this.tailProportion = tailProportion;
    }


}
