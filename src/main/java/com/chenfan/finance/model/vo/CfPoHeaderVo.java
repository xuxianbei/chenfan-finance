package com.chenfan.finance.model.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
/**
 * @author liran
 */
@Data
public class CfPoHeaderVo {

    /**
     * 采购单号
     */
    private Long poId;

    private String poCode;

    /**
     * 订单号
     */
    private String sourceNumber;

    /**
     * 订单类型
     */
    private Integer sourceType;

    /**
     * 首/返单
     */
    private Integer orderType;

    /**
     * 供应商id
     */
    private Long vendorId;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 上新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createDate;

    private String vendorName;

    /**
     * 总下单量
     */
    private BigDecimal quantity;

    /**
     * 总欠货量
     */
    private BigDecimal shortCount;

    /**
     * 总到货量
     */
    private BigDecimal arriveCount;

    private String vendorCode;

    private String createDateString;

    private String remark;

    private Long brandId;

    private String customerName;

    private String venAbbName;

    private BigDecimal taxUnitPrice;

    private BigDecimal qualifiedQuantityCount;

    private String financialBody;

    private Integer poType;

    /**
     * 首返单子类型
     */
    private Integer childOrderType;

    private String orderTypeString;
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getPoId() {
        return poId;
    }

    public void setPoId(Long poId) {
        this.poId = poId;
    }

    public String getSourceNumber() {
        return sourceNumber;
    }

    public void setSourceNumber(String sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getShortCount() {
        return shortCount;
    }

    public void setShortCount(BigDecimal shortCount) {
        this.shortCount = shortCount;
    }

    public BigDecimal getArriveCount() {
        return arriveCount;
    }

    public void setArriveCount(BigDecimal arriveCount) {
        this.arriveCount = arriveCount;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public String getCreateDateString() {
        return createDateString;
    }

    public void setCreateDateString(String createDateString) {
        this.createDateString = createDateString;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getVenAbbName() {
        return venAbbName;
    }

    public void setVenAbbName(String venAbbName) {
        this.venAbbName = venAbbName;
    }

    public BigDecimal getTaxUnitPrice() {
        return taxUnitPrice;
    }

    public void setTaxUnitPrice(BigDecimal taxUnitPrice) {
        this.taxUnitPrice = taxUnitPrice;
    }

    public BigDecimal getQualifiedQuantityCount() {
        return qualifiedQuantityCount;
    }

    public void setQualifiedQuantityCount(BigDecimal qualifiedQuantityCount) {
        this.qualifiedQuantityCount = qualifiedQuantityCount;
    }

    public String getFinancialBody() {
        return financialBody;
    }

    public void setFinancialBody(String financialBody) {
        this.financialBody = financialBody;
    }
}
