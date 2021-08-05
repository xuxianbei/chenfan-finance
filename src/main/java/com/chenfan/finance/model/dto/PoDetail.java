package com.chenfan.finance.model.dto;

import com.chenfan.finance.commons.utils.validategroup.Complete;
import com.chenfan.finance.commons.utils.validategroup.Create;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author mbji
 */
@Data
public class PoDetail implements Serializable {
    private static final long serialVersionUID = -6925196230127491768L;
    @NotNull(groups = {Default.class, Complete.class})
    private Integer poDetailId;
    @NotNull(groups = {Default.class, Complete.class})
    private Long poId;
    @NotNull(groups = Create.class)
    private String poCode;
    @NotNull(groups = Create.class)
    private Integer inventoryId;
    private String inventoryCode;
    private String productCode;
    @NotNull(groups = Create.class)
    private BigDecimal quantity;
    @NotNull(groups = Create.class)
    private BigDecimal taxUnitPrice;
    @NotNull(groups = Create.class)
    private BigDecimal unitPrice;
    @NotNull(groups = Create.class)
    private BigDecimal taxRate;
    @NotNull(groups = Create.class)
    private BigDecimal freeTaxMoney;
    @NotNull(groups = Create.class)
    private BigDecimal includedTaxMoney;
    @NotNull(groups = Create.class)
    private BigDecimal taxMoney;
    private String exchName;
    @NotNull(groups = Create.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date conStartDate;
    @NotNull(groups = Create.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date conEndDate;
    private Integer gsp;

    private Integer preOrderDetailId;

    private Integer appvouchDetailId;

    private String remark;
    @NotNull(groups = {Default.class})
    private Integer detailStatus;

    private Boolean isDelete;

    private Date updateDate;

    private Integer accessoryRequisitionsInfoId;

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getPreOrderDetailId() {
        return preOrderDetailId;
    }

    public void setPreOrderDetailId(Integer preOrderDetailId) {
        this.preOrderDetailId = preOrderDetailId;
    }

    public Integer getPoDetailId() {
        return poDetailId;
    }

    public void setPoDetailId(Integer poDetailId) {
        this.poDetailId = poDetailId;
    }

    public Long getPoId() {
        return poId;
    }

    public void setPoId(Long poId) {
        this.poId = poId;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode == null ? null : poCode.trim();
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode == null ? null : inventoryCode.trim();
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode == null ? null : productCode.trim();
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTaxUnitPrice() {
        return taxUnitPrice;
    }

    public void setTaxUnitPrice(BigDecimal taxUnitPrice) {
        this.taxUnitPrice = taxUnitPrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getFreeTaxMoney() {
        return freeTaxMoney;
    }

    public void setFreeTaxMoney(BigDecimal freeTaxMoney) {
        this.freeTaxMoney = freeTaxMoney;
    }

    public BigDecimal getIncludedTaxMoney() {
        return includedTaxMoney;
    }

    public void setIncludedTaxMoney(BigDecimal includedTaxMoney) {
        this.includedTaxMoney = includedTaxMoney;
    }

    public BigDecimal getTaxMoney() {
        return taxMoney;
    }

    public void setTaxMoney(BigDecimal taxMoney) {
        this.taxMoney = taxMoney;
    }

    public String getExchName() {
        return exchName;
    }

    public void setExchName(String exchName) {
        this.exchName = exchName == null ? null : exchName.trim();
    }

    public Date getConStartDate() {
        return conStartDate;
    }

    public void setConStartDate(Date conStartDate) {
        this.conStartDate = conStartDate;
    }

    public Date getConEndDate() {
        return conEndDate;
    }

    public void setConEndDate(Date conEndDate) {
        this.conEndDate = conEndDate;
    }

    public Integer getGsp() {
        return gsp;
    }

    public void setGsp(Integer gsp) {
        this.gsp = gsp;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getDetailStatus() {
        return detailStatus;
    }

    public void setDetailStatus(Integer detailStatus) {
        this.detailStatus = detailStatus;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }
}