package com.chenfan.finance.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author mbji
 */
@Data
public class AdvancepayApply {
    private Integer advancePayId;

    private String advancePayCode;

    private String roleId;

    private String firstRoleId;

    private Long poId;

    private String poCode;

    private Integer brandId;

    private String brandName;

    private Integer vendorId;

    private String vendorCode;
    //待确认_0,已确认_1,已审核_2,已提交_3，财务已审_4 待打款_5，已驳回_6, 已关闭_7, 已打款_8
    private Integer state;

    /**
     * 付款类型
     */
    private String paymentType;

    /**
     * 定金配置id
     */
    private Integer paymentConfId;

    private String taskPerson;

    private String duties;

    private String receiptDepartment;

    private String bank;

    private String payment;

    private String bankAccount;

    private Integer isArrive;

    private Integer enclosure;

    private String paymentUse;

    private String moneyCapital;

    private BigDecimal money;

    private String department;

    private String managDirector;

    private String accountant;

    private String financechief;

    private String generalmanager;

    private String cashier;

    private Long verifyBy;

    private String verifyName;

    private Date verifyDate;

    private String createName;

    private Long createBy;

    private Date createDate;

    private String updateName;

    private Long updateBy;

    private Date updateDate;

    private String updatePriceReason;

    private Boolean isDelete;
    /**
     * 组员确认名称
     */
    private String applyConfirmName;
    /**
     * 组员确认时间
     */
    private Date applyConfirmDate;
    /**
     * 组长审核，审核人名称
     */
    private String confirmName;
    /**
     * 组长审核时间
     */
    private Date confirmDate;
    /**
     * 供应链实习助理名称
     */
    private String supplychainInternName;
    /**
     * 供应链实习助理操作时间
     */
    private Date supplychainInternDate;
    /**
     * 财务审核员名称
     */
    private String finaceName;
    /**
     * 财务审核员操作时间
     */
    private Date finaceDate;
    /**
     * 财务主管名称
     */
    private String finaceGMName;
    /**
     * 财务主管操作时间
     */
    private Date finaceGMDate;
    /**
     * COO或者COO助理
     */
    private String cooOrHelperName;
    /**
     * COO或者COO助理操作时间
     */
    private  Date cooOrHelperDate;

    /**
     * 打款截图
     */
    private String imgUrls;

    /**
     * 出纳已打款时间
     */
    private  Date cashierDate;

    /**
     * 物料类型 0成衣1辅料
     */
    private Integer materialType;

    /**
     * 公司Id
     */
    private Long companyId;
    /**
     * 户名
     */
    private String accname;

    public Integer getMaterialType() {
        return materialType;
    }

    public void setMaterialType(Integer materialType) {
        this.materialType = materialType;
    }

    public AdvancepayApply() {
    }

    public AdvancepayApply(Integer advancePayId, String roleId, Integer state, String taskPerson) {
        this.advancePayId = advancePayId;
        this.roleId = roleId;
        this.state = state;
        this.taskPerson = taskPerson;
    }


    public Integer getAdvancePayId() {
        return advancePayId;
    }

    public void setAdvancePayId(Integer advancePayId) {
        this.advancePayId = advancePayId;
    }

    public String getAdvancePayCode() {
        return advancePayCode;
    }

    public void setAdvancePayCode(String advancePayCode) {
        this.advancePayCode = advancePayCode == null ? null : advancePayCode.trim();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }

    public String getFirstRoleId() {
        return firstRoleId;
    }

    public void setFirstRoleId(String firstRoleId) {
        this.firstRoleId = firstRoleId == null ? null : firstRoleId.trim();
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType == null ? null : paymentType.trim();
    }

    public String getTaskPerson() {
        return taskPerson;
    }

    public void setTaskPerson(String taskPerson) {
        this.taskPerson = taskPerson == null ? null : taskPerson.trim();
    }

    public String getDuties() {
        return duties;
    }

    public void setDuties(String duties) {
        this.duties = duties == null ? null : duties.trim();
    }

    public String getReceiptDepartment() {
        return receiptDepartment;
    }

    public void setReceiptDepartment(String receiptDepartment) {
        this.receiptDepartment = receiptDepartment == null ? null : receiptDepartment.trim();
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank == null ? null : bank.trim();
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment == null ? null : payment.trim();
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount == null ? null : bankAccount.trim();
    }

    public Integer getIsArrive() {
        return isArrive;
    }

    public void setIsArrive(Integer isArrive) {
        this.isArrive = isArrive;
    }

    public Integer getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(Integer enclosure) {
        this.enclosure = enclosure;
    }

    public String getPaymentUse() {
        return paymentUse;
    }

    public void setPaymentUse(String paymentUse) {
        this.paymentUse = paymentUse == null ? null : paymentUse.trim();
    }

    public String getMoneyCapital() {
        return moneyCapital;
    }

    public void setMoneyCapital(String moneyCapital) {
        this.moneyCapital = moneyCapital == null ? null : moneyCapital.trim();
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department == null ? null : department.trim();
    }

    public String getManagDirector() {
        return managDirector;
    }

    public void setManagDirector(String managDirector) {
        this.managDirector = managDirector == null ? null : managDirector.trim();
    }

    public String getAccountant() {
        return accountant;
    }

    public void setAccountant(String accountant) {
        this.accountant = accountant == null ? null : accountant.trim();
    }

    public String getFinancechief() {
        return financechief;
    }

    public void setFinancechief(String financechief) {
        this.financechief = financechief == null ? null : financechief.trim();
    }

    public String getGeneralmanager() {
        return generalmanager;
    }

    public void setGeneralmanager(String generalmanager) {
        this.generalmanager = generalmanager == null ? null : generalmanager.trim();
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier == null ? null : cashier.trim();
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

    public Integer getPaymentConfId() {
        return paymentConfId;
    }

    public void setPaymentConfId(Integer paymentConfId) {
        this.paymentConfId = paymentConfId;
    }

    public String getUpdatePriceReason() {
        return updatePriceReason;
    }

    public void setUpdatePriceReason(String updatePriceReason) {
        this.updatePriceReason = updatePriceReason;
    }

    public String getApplyConfirmName() {
        return applyConfirmName;
    }

    public void setApplyConfirmName(String applyConfirmName) {
        this.applyConfirmName = applyConfirmName;
    }

    public Date getApplyConfirmDate() {
        return applyConfirmDate;
    }

    public void setApplyConfirmDate(Date applyConfirmDate) {
        this.applyConfirmDate = applyConfirmDate;
    }

    public String getConfirmName() {
        return confirmName;
    }

    public void setConfirmName(String confirmName) {
        this.confirmName = confirmName;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getSupplychainInternName() {
        return supplychainInternName;
    }

    public void setSupplychainInternName(String supplychainInternName) {
        this.supplychainInternName = supplychainInternName;
    }

    public Date getSupplychainInternDate() {
        return supplychainInternDate;
    }

    public void setSupplychainInternDate(Date supplychainInternDate) {
        this.supplychainInternDate = supplychainInternDate;
    }

    public String getFinaceName() {
        return finaceName;
    }

    public void setFinaceName(String finaceName) {
        this.finaceName = finaceName;
    }

    public Date getFinaceDate() {
        return finaceDate;
    }

    public void setFinaceDate(Date finaceDate) {
        this.finaceDate = finaceDate;
    }

    public String getFinaceGMName() {
        return finaceGMName;
    }

    public void setFinaceGMName(String finaceGMName) {
        this.finaceGMName = finaceGMName;
    }

    public Date getFinaceGMDate() {
        return finaceGMDate;
    }

    public void setFinaceGMDate(Date finaceGMDate) {
        this.finaceGMDate = finaceGMDate;
    }

    public String getCooOrHelperName() {
        return cooOrHelperName;
    }

    public void setCooOrHelperName(String cooOrHelperName) {
        this.cooOrHelperName = cooOrHelperName;
    }

    public Date getCooOrHelperDate() {
        return cooOrHelperDate;
    }

    public void setCooOrHelperDate(Date cooOrHelperDate) {
        this.cooOrHelperDate = cooOrHelperDate;
    }

    public String getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(String imgUrls) {
        this.imgUrls = imgUrls;
    }

    public Date getCashierDate() {
        return cashierDate;
    }

    public void setCashierDate(Date cashierDate) {
        this.cashierDate = cashierDate;
    }
}