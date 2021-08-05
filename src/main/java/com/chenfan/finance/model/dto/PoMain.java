package com.chenfan.finance.model.dto;

import com.chenfan.finance.enums.PayTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author mbji
 */
@Data
public class PoMain {
    private Integer poId;

    private String poCode;

    private Integer vendorId;

    private String vendorCode;

    private Integer brandId;

    private String brandName;

    private Integer state;

    private String orderType;

    private Byte printCount;

    private Integer appvouchId;

    private String appvouchCode;

    private Integer hsStatus;

    private BigDecimal retainage;

    private BigDecimal retainageRatio;

    private BigDecimal bargain;

    private BigDecimal bargainRatio;

    private Long verifyBy;

    private String verifyName;

    private Date verifyDate;

    private Long closeBy;

    private String closeName;

    private Date closeDate;

    private String remark;

    private String sysOrgCode;

    private String sysCompanyCode;

    private String createName;

    private Long createBy;

    private Date createDate;

    private String updateName;
    private Date updateDate;

    private Long updateBy;


    private Boolean isDelete;

    private Integer childOrderType;

    private String vendorLevel;

    /**
     * 预订单主表id
     */
    private Integer preOrderId;
    /**
     * 预订单单号
     */
    private String preOrderCode;

    private Integer newPlanOrderMainId;

    public String getVendorLevel() {
        return vendorLevel;
    }

    public void setVendorLevel(String vendorLevel) {
        this.vendorLevel = vendorLevel;
    }

    public PoMain() {
    }

    public PoMain(Integer poId, BigDecimal money, BigDecimal percent, PayTypeEnum payTypeEnum) {
        this.poId = poId;
        if (PayTypeEnum.FIRST_PAYMENT.equals(payTypeEnum)) {
            this.bargain = money;
            this.bargainRatio = percent;
        }
        if (PayTypeEnum.FINAL_PAYMENT.equals(payTypeEnum)) {
            this.retainage = money;
            this.retainageRatio = percent;
        }
    }


    public PoMain(Integer poId, Integer state) {
        this.poId = poId;
        this.state = state;
    }

    public Integer getPoId() {
        return poId;
    }

    public void setPoId(Integer poId) {
        this.poId = poId;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode == null ? null : poCode.trim();
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode == null ? null : vendorCode.trim();
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
        this.brandName = brandName == null ? null : brandName.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType == null ? null : orderType.trim();
    }

    public Byte getPrintCount() {
        return printCount;
    }

    public void setPrintCount(Byte printCount) {
        this.printCount = printCount;
    }

    public Integer getAppvouchId() {
        return appvouchId;
    }

    public void setAppvouchId(Integer appvouchId) {
        this.appvouchId = appvouchId;
    }

    public String getAppvouchCode() {
        return appvouchCode;
    }

    public void setAppvouchCode(String appvouchCode) {
        this.appvouchCode = appvouchCode == null ? null : appvouchCode.trim();
    }

    public Integer getHsStatus() {
        return hsStatus;
    }

    public void setHsStatus(Integer hsStatus) {
        this.hsStatus = hsStatus;
    }

    public BigDecimal getRetainage() {
        return retainage;
    }

    public void setRetainage(BigDecimal retainage) {
        this.retainage = retainage;
    }

    public BigDecimal getRetainageRatio() {
        return retainageRatio;
    }

    public void setRetainageRatio(BigDecimal retainageRatio) {
        this.retainageRatio = retainageRatio;
    }

    public BigDecimal getBargain() {
        return bargain;
    }

    public void setBargain(BigDecimal bargain) {
        this.bargain = bargain;
    }

    public BigDecimal getBargainRatio() {
        return bargainRatio;
    }

    public void setBargainRatio(BigDecimal bargainRatio) {
        this.bargainRatio = bargainRatio;
    }

    public Long getVerifyBy() {
        return verifyBy;
    }

    public void setVerifyBy(Long verifyBy) {
        this.verifyBy = verifyBy;
    }

    public String getVerifyName() {
        return verifyName;
    }

    public void setVerifyName(String verifyName) {
        this.verifyName = verifyName == null ? null : verifyName.trim();
    }

    public Date getVerifyDate() {
        return verifyDate;
    }

    public void setVerifyDate(Date verifyDate) {
        this.verifyDate = verifyDate;
    }

    public Long getCloseBy() {
        return closeBy;
    }

    public void setCloseBy(Long closeBy) {
        this.closeBy = closeBy;
    }

    public String getCloseName() {
        return closeName;
    }

    public void setCloseName(String closeName) {
        this.closeName = closeName == null ? null : closeName.trim();
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getChildOrderType() {
        return childOrderType;
    }

    public void setChildOrderType(Integer childOrderType) {
        this.childOrderType = childOrderType;
    }

    public Integer getPreOrderId() {
        return preOrderId;
    }

    public void setPreOrderId(Integer preOrderId) {
        this.preOrderId = preOrderId;
    }

    public String getPreOrderCode() {
        return preOrderCode;
    }

    public void setPreOrderCode(String preOrderCode) {
        this.preOrderCode = preOrderCode;
    }
}