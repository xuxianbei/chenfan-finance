package com.chenfan.finance.model.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * 导入 字 段类型 Integer,Long,Double,Date,String,Boolean
 */
@Data
public class AlipayOriginExcelDTO implements java.io.Serializable {


    @Excel(name = "账务流水号")
    private String financeNo;

    @Excel(name = "业务流水号")
    private String businessNo;

    @Excel(name = "商户订单号")
    private String tid;

    @Excel(name = "商品名称")
    private String goodsName;

    @Excel(name = "发生时间")
    private String accountDate;

    @Excel(name = "对方账号")
    private String oppositeAccount;


    @Excel(name = "收入金额（+元）")
    private String incomeAmount;

    @Excel(name = "支出金额（-元）")
    private String expendAmount;

    @Excel(name = "账户余额（元）")
    private String accountLeftAmount;


    @Excel(name = "交易渠道", replace = {"支付宝_0"})
    private String payWay;


    @Excel(name = "业务类型", replace = {"交易付款_0", "在线支付_1", "交易退款_2", "交易分账_3", "其它_4", "收费_5", "提现_6", "转账_7", "红包退回_8","保证金_9","退款_10","退款（交易退款）_11","提现_12","分账_13","批量代发到账户_14"})
    private String businessType;

    @Excel(name = "备注")
    private String remark;

    public void setFinanceNo(String financeNo) {
        this.financeNo = StringUtils.trim(financeNo);
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = StringUtils.trim(businessNo);
    }

    public void setTid(String tid) {
        this.tid = StringUtils.trim(tid);
    }



    public void setAccountDate(String accountDate) {
        accountDate = StringUtils.trim(accountDate);
        if (accountDate.length() == 16) {
            accountDate = accountDate + ":00";
        }
        this.accountDate = accountDate;
    }

    public void setOppositeAccount(String oppositeAccount) {
        this.oppositeAccount = StringUtils.trim(oppositeAccount);
    }

    public void setIncomeAmount(String incomeAmount) {
        this.incomeAmount = StringUtils.trim(incomeAmount);
    }

    public void setExpendAmount(String expendAmount) {
        this.expendAmount = StringUtils.trim(expendAmount);
    }

    public void setAccountLeftAmount(String accountLeftAmount) {
        this.accountLeftAmount = StringUtils.trim(accountLeftAmount);
    }

    public void setPayWay(String payWay) {
        this.payWay = StringUtils.trim(payWay);
    }

    public void setBusinessType(String businessType) {
        this.businessType = StringUtils.trim(businessType);
    }

    public void setRemark(String remark) {

        this.remark = StringUtils.trim(remark);
    }

    public String getFinanceNo() {
        return financeNo;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public String getTid() {
        return tid;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getAccountDate() {
        return accountDate;
    }

    public String getOppositeAccount() {
        return oppositeAccount;
    }

    public String getIncomeAmount() {
        return incomeAmount;
    }

    public String getExpendAmount() {
        return expendAmount;
    }

    public String getAccountLeftAmount() {
        return accountLeftAmount;
    }

    public String getPayWay() {
        return payWay;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getRemark() {
        return remark;
    }
}
