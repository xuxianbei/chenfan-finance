package com.chenfan.finance.model.vo;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.chenfan.finance.model.Pay;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author mbji
 */
@Data
public class AdvancepayApplyVo {

    private String title;

    private int year;

    private int month;

    private int day;

    private Integer button;

    private Integer advancePayId;


    @Excel(name = "付款申请单号",width = 30)
    private String advancePayCode;
    @Excel(name = "申请人",width = 30)
    private String createName;
    @Excel(name = "单据状态",replace = {"待确认_0","已确认_1","已审核_2","已提交_3","已付款_4","已完成_5","已驳回_6","已关闭_7","已打款_8"},width = 30)
    private Integer state;

    /**
     * 采购数量不区分sku
     */
    //@Excel(name = "采购数量",width = 30)
    private BigDecimal ppdQuantity;
    /**
     * 付款类型
     */
    @Excel(name = "付款类型",width = 30)
    private String paymentTypeOption;
    @Excel(name = "品牌",width = 30)
    private String brandName;
    @Excel(name = "供应商",width = 30)
    private String vendorName;

    @Excel(name = "付款金额",width = 30)
    private BigDecimal money;

    /**
     * 是否到仓
     */
    @Excel(name = "是否到仓",width = 30)
    private String arrive;
    //@Excel(name = "含税单价",width = 30)
    private BigDecimal taxUnitPrice;

    /**
     * 入库数量
     */
    @Excel(name = "入库数量",width = 30)
    private BigDecimal rrdQuantity;

    /**
     * 入库金额
     */
    @Excel(name = "入库金额",width = 30)
    private BigDecimal rrdMoney;

    private String roleId;

    private String firstRoleId;

    private Long poId;

    private String poCode;

    private Integer brandId;

    private Integer vendorId;

    private String vendorCode;

    /**
     * 付款类型
     */
    private String paymentType;

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

    private String department;

    private String managDirector;

    private String accountant;

    private String financechief;


    private String generalmanager;

    private String cashier;

    private Long verifyBy;

    private String verifyName;

    private Date verifyDate;

    private Long createBy;

    private Date createDate;

    private String updateName;

    private Long updateBy;

    private Date updateDate;

    private Boolean isDelete;


    private String venAbbName;
    private String departName;

    private Integer id;

    private BigDecimal bargainRatio;

    private BigDecimal bargain;

    private BigDecimal retainage;

    private BigDecimal retainageRatio;

    private String updatePriceReason;

    private String customerName;

    private String financialBody;

    private List<Pay> pays;

    private Integer poType;

    private Integer paymentConfId;

    public List<Pay> getPays() {
        return pays;
    }

    public void setPays(List<Pay> pays) {
        this.pays = pays;
    }

    public Integer getPoType() {
        return poType;
    }

    public void setPoType(Integer poType) {
        this.poType = poType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getFinancialBody() {
        return financialBody;
    }

    public void setFinancialBody(String financialBody) {
        this.financialBody = financialBody;
    }

    public AdvancepayApplyVo() {
    }

    public AdvancepayApplyVo(Integer advancePayId, String roleId, Integer state, String taskPerson) {
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
        this.id = advancePayId;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVenAbbName() {
        return venAbbName;
    }

    public void setVenAbbName(String venAbbName) {
        this.venAbbName = venAbbName;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public Integer getId() {
        return advancePayId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getButton() {
        return button;
    }

    public void setButton(Integer button) {
        this.button = button;
    }

    public BigDecimal getRrdQuantity() {
        return rrdQuantity;
    }

    public void setRrdQuantity(BigDecimal rrdQuantity) {
        this.rrdQuantity = rrdQuantity;
    }

    public BigDecimal getRrdMoney() {
        return rrdMoney;
    }

    public void setRrdMoney(BigDecimal rrdMoney) {
        this.rrdMoney = rrdMoney;
    }

    public BigDecimal getBargainRatio() {
        return bargainRatio;
    }

    public void setBargainRatio(BigDecimal bargainRatio) {
        this.bargainRatio = bargainRatio;
    }

    public BigDecimal getRetainageRatio() {
        return retainageRatio;
    }

    public void setRetainageRatio(BigDecimal retainageRatio) {
        this.retainageRatio = retainageRatio;
    }

    public String getArrive() {
        return arrive;
    }

    public void setArrive(String arrive) {
        this.arrive = arrive;
    }

    public String getPaymentTypeOption() {
        return paymentTypeOption;
    }

    public void setPaymentTypeOption(String paymentTypeOption) {
        this.paymentTypeOption = paymentTypeOption;
    }


    public BigDecimal getPpdQuantity() {
        return ppdQuantity;
    }

    public void setPpdQuantity(BigDecimal ppdQuantity) {
        this.ppdQuantity = ppdQuantity;
    }

    public BigDecimal getTaxUnitPrice() {
        return taxUnitPrice;
    }

    public void setTaxUnitPrice(BigDecimal taxUnitPrice) {
        this.taxUnitPrice = taxUnitPrice;
    }

    public String getUpdatePriceReason() {
        return updatePriceReason;
    }

    public void setUpdatePriceReason(String updatePriceReason) {
        this.updatePriceReason = updatePriceReason;
    }
}