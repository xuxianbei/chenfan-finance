package com.chenfan.finance.model.bo;

import com.chenfan.common.dto.PagerDTO;
import com.chenfan.finance.commons.utils.validategroup.Audit;
import com.chenfan.finance.commons.utils.validategroup.Create;
import com.chenfan.finance.commons.utils.validategroup.Update;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author mbji
 */
@Data
public class AdvancePayBo extends PagerDTO {

    /**
     * 采购付款状态筛选条件 只能为1或0 代表已完成或者未完成
     */
    private Integer poPayState;
    private Integer payType;
    private BigDecimal payValue;

    /**
     * 订金或尾款
     */
    private Integer firstOrLastPay;

    /**
     * 采购单号
     */
    private String poCode;
    /**
     * 品牌id
     */
    private Integer brandId;
    /**
     * 供应商id
     */
    private Integer vendorId;
    /**
     * 申请人
     */
    private String createName;

    /**
     * 申请人id
     */
    private Long createBy;
    /**
     * 单据状态
     */
    private Integer state;

    /**
     * 百分比
     */
    private BigDecimal percent;
    /**
     * 企业非企业
     */
    private Integer shopType;

    /**
     * 当前登录人id
     */
    private Integer currentUsrId;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 定金或者尾款比例
     */
    private BigDecimal proportion;
    /**
     * 定金或者尾款比名称
     */
    private String proportionName;

    private int start;
    /**
     * 是否到仓
     */
    private Integer arrive;
    /**
     * 创建时间-开始
     */
    private String createDateBegin;
    /**
     * 创建时间-结束
     */
    private String createDateEnd;

    private BigDecimal bargainRatio;

    private BigDecimal retainageRatio;

    private BigDecimal bargain;

    private BigDecimal retainage;

    /**
     * ---------------------------------下面dto的字段---------------------------------------
     */
    private Integer advancePayId;
    /**
     * 付款申请单号
     */
    private String advancePayCode;

    private String roleId;

    private String firstRoleId;

    private Long poId;

    private String brandName;


    private String vendorCode;

    /**
     * 付款类型 订金、尾款
     */
    private String paymentType;
    /**
     * 定金配置id
     */
    private Integer paymentConfId;

    /**
     * 任务人
     */
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

    private Date createDate;
    private String updateName;
    private Long updateBy;

    private Date updateDate;

    private Boolean isDelete;

    private String updatePriceReason;
    private String paymentTypeOption;

    private String imgUrls;

    /**
     * 物料类型 0成衣1辅料
     */
    private Integer materialType;

    private BigDecimal firstPayment;
    /**
     * 户名
     */
    private String accname;

    private List<Integer> poIds;

    public List<Integer> getPoIds() {
        return poIds;
    }

    public void setPoIds(List<Integer> poIds) {
        this.poIds = poIds;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public BigDecimal getPayValue() {
        return payValue;
    }

    public void setPayValue(BigDecimal payValue) {
        this.payValue = payValue;
    }

    public void setStart(int start) {
        this.start = start;
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
        this.advancePayCode = advancePayCode;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getFirstRoleId() {
        return firstRoleId;
    }

    public void setFirstRoleId(String firstRoleId) {
        this.firstRoleId = firstRoleId;
    }

    public Long getPoId() {
        return poId;
    }

    public void setPoId(Long poId) {
        this.poId = poId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getTaskPerson() {
        return taskPerson;
    }

    public void setTaskPerson(String taskPerson) {
        this.taskPerson = taskPerson;
    }

    public String getDuties() {
        return duties;
    }

    public void setDuties(String duties) {
        this.duties = duties;
    }

    public String getReceiptDepartment() {
        return receiptDepartment;
    }

    public void setReceiptDepartment(String receiptDepartment) {
        this.receiptDepartment = receiptDepartment;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
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
        this.paymentUse = paymentUse;
    }

    public String getMoneyCapital() {
        return moneyCapital;
    }

    public void setMoneyCapital(String moneyCapital) {
        this.moneyCapital = moneyCapital;
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
        this.department = department;
    }

    public String getManagDirector() {
        return managDirector;
    }

    public void setManagDirector(String managDirector) {
        this.managDirector = managDirector;
    }

    public String getAccountant() {
        return accountant;
    }

    public void setAccountant(String accountant) {
        this.accountant = accountant;
    }

    public String getFinancechief() {
        return financechief;
    }

    public void setFinancechief(String financechief) {
        this.financechief = financechief;
    }

    public String getGeneralmanager() {
        return generalmanager;
    }

    public void setGeneralmanager(String generalmanager) {
        this.generalmanager = generalmanager;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
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
        this.verifyName = verifyName;
    }

    public Date getVerifyDate() {
        return verifyDate;
    }

    public void setVerifyDate(Date verifyDate) {
        this.verifyDate = verifyDate;
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
        this.updateName = updateName;
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

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getShopType() {
        return shopType;
    }

    public void setShopType(Integer shopType) {
        this.shopType = shopType;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public Integer getFirstOrLastPay() {
        return firstOrLastPay;
    }

    public void setFirstOrLastPay(Integer firstOrLastPay) {
        this.firstOrLastPay = firstOrLastPay;
    }

    public Integer getCurrentUsrId() {
        return currentUsrId;
    }

    public void setCurrentUsrId(Integer currentUsrId) {
        this.currentUsrId = currentUsrId;
    }

    public String getOperation() {
        return operation;
    }

    public BigDecimal getProportion() {
        return proportion;
    }

    public void setProportion(BigDecimal proportion) {
        this.proportion = proportion;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Integer getPaymentConfId() {
        return paymentConfId;
    }

    public void setPaymentConfId(Integer paymentConfId) {
        this.paymentConfId = paymentConfId;
    }

    public String getProportionName() {
        return proportionName;
    }

    public void setProportionName(String proportionName) {
        this.proportionName = proportionName;
    }

    public Integer getArrive() {
        return arrive;
    }

    public void setArrive(Integer arrive) {
        this.arrive = arrive;
    }

    public String getUpdatePriceReason() {
        return updatePriceReason;
    }

    public void setUpdatePriceReason(String updatePriceReason) {
        this.updatePriceReason = updatePriceReason;
    }

    public String getPaymentTypeOption() {
        return paymentTypeOption;
    }

    public void setPaymentTypeOption(String paymentTypeOption) {
        this.paymentTypeOption = paymentTypeOption;
    }

    public String getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(String imgUrls) {
        this.imgUrls = imgUrls;
    }

    public String getCreateDateBegin() {
        return createDateBegin;
    }

    public void setCreateDateBegin(String createDateBegin) {
        this.createDateBegin = createDateBegin;
    }

    public String getCreateDateEnd() {
        return createDateEnd;
    }

    public void setCreateDateEnd(String createDateEnd) {
        this.createDateEnd = createDateEnd;
    }
}
