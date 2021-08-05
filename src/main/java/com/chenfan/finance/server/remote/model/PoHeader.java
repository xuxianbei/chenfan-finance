package com.chenfan.finance.server.remote.model;

import com.chenfan.finance.enums.PayTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liulingqiong
 * @date 2020-6-24
 */
@Data
public class PoHeader {
    private Integer poId;

    private String poCode;

    private Integer newPlanOrderMainId;

    private Integer vendorId;

    private String vendorCode;

    private Integer brandId;

    private String brandName;

    private Integer state;

    private String orderType;

    private Integer childOrderType;

    private Integer printCount;

    private Integer accessoryRequisitionsId;

    private String accessoryRequisitionsCode;

    private String accountBillCode;

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

    private String cwhCode;

    private String sysOrgCode;

    private String sysCompanyCode;

    private String createName;

    private Long createBy;

    private Date createDate;

    private String updateName;

    private Long updateBy;

    private Date updateDate;

    private Integer isDelete;

    private BigDecimal hirePurchase;

    private Integer returnId;

    private String returnCode;

    private Integer poType;

    private String sourceNumber;

    private Integer sourceType;

    private Integer sourceOrderId;

    private Integer accessoryInfoId;
    /**
     * 公司Id
     */
    private Long companyId;

    private String vendorLetterhead;
    /**
     * 理货单主表ID
     */
    private Long tallyId;

    /**
     * 理货单编号
     */
    private String tallyCode;

    private BigDecimal totalArrivalQty;

    private BigDecimal totalQcQty;

    private BigDecimal totalRdRecordQty;

    private BigDecimal totalShortageQty;

    private BigDecimal totalRejectionQty;

    private BigDecimal totalQualifiedQty;

    /**
     * 数据类型 0老结算数据 1新结算数据
     */
    private Integer dataType;

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public PoHeader() {

    }

    public PoHeader(Integer poId, BigDecimal money, BigDecimal percent, PayTypeEnum payTypeEnum) {
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

    public Integer getNewPlanOrderMainId() {
        return newPlanOrderMainId;
    }

    public void setNewPlanOrderMainId(Integer newPlanOrderMainId) {
        this.newPlanOrderMainId = newPlanOrderMainId;
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
        this.orderType = orderType;
    }

    public Integer getChildOrderType() {
        return childOrderType;
    }

    public void setChildOrderType(Integer childOrderType) {
        this.childOrderType = childOrderType;
    }

    public Integer getAccessoryRequisitionsId() {
        return accessoryRequisitionsId;
    }

    public void setAccessoryRequisitionsId(Integer accessoryRequisitionsId) {
        this.accessoryRequisitionsId = accessoryRequisitionsId;
    }

    public String getAccessoryRequisitionsCode() {
        return accessoryRequisitionsCode;
    }

    public void setAccessoryRequisitionsCode(String accessoryRequisitionsCode) {
        this.accessoryRequisitionsCode = accessoryRequisitionsCode;
    }

    public Integer getPrintCount() {
        return printCount;
    }

    public void setPrintCount(Integer printCount) {
        this.printCount = printCount;
    }

    public String getAccountBillCode() {
        return accountBillCode;
    }

    public void setAccountBillCode(String accountBillCode) {
        this.accountBillCode = accountBillCode == null ? null : accountBillCode.trim();
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

    public String getCwhCode() {
        return cwhCode;
    }

    public void setCwhCode(String cwhCode) {
        this.cwhCode = cwhCode == null ? null : cwhCode.trim();
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

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public BigDecimal getHirePurchase() {
        return hirePurchase;
    }

    public void setHirePurchase(BigDecimal hirePurchase) {
        this.hirePurchase = hirePurchase;
    }

    public Integer getReturnId() {
        return returnId;
    }

    public void setReturnId(Integer returnId) {
        this.returnId = returnId;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode == null ? null : returnCode.trim();
    }

    public Integer getPoType() {
        return poType;
    }

    public void setPoType(Integer poType) {
        this.poType = poType;
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
}