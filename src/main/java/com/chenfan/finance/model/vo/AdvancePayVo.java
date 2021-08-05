package com.chenfan.finance.model.vo;

import com.alibaba.fastjson.JSONArray;
import com.chenfan.finance.model.dto.AdvancepayApply;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author mbji
 */
@Data
public class AdvancePayVo extends AdvancepayApply implements Serializable {

    private static final long serialVersionUID = -8339545406071631815L;
    private String vendorName;

    private String venAbbName;

    private String departName;
    /**
     * 入库数量
     */
    private BigDecimal rrdQuantity;
    /**
     * 入库金额
     */
    private BigDecimal rrdMoney;

    private BigDecimal bargainRatio;

    private BigDecimal retainageRatio;
    /**
     * 是否到仓
     */
    private String arrive;

    private String paymentTypeOption;
    /**
     * 采购数量不区分sku
     */
    private BigDecimal ppdQuantity;

    private BigDecimal taxUnitPrice;

    private String updatePriceReason;
    /**
     * 财务审核时间
     */
    private String finaceDateString;
    /**
     * 财务复核时间
     */
    private String finaceGmDateString;
    /**
     * 出纳已打款时间
     */
    private String cashierDateString;

    /**
     * 采购付款进度状态
     */
    private String poPayState;

    private JSONArray imgList;

    /**
     * 采购单的采购类型 0成衣1辅料
     */
    private Integer poType;

    /**
     * 预付款申请单的物料类型 0成衣1辅料
     */
    private Integer apaMaterialType;

    /**
     * 预付款申请单的物料类型String
     */
    private String apaMaterialTypeString;

    /**
     * 采购单的采购类型String
     */
    private String poTypeString;

    /**
     * 品牌财务主体
     */
    private String financialBody;

    private String createDateString;

    private String accname;


    public BigDecimal getPpdQuantity() {
        return ppdQuantity;
    }

    public void setPpdQuantity(BigDecimal ppdQuantity) {
        this.ppdQuantity = ppdQuantity;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
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
        this.rrdMoney = rrdMoney.setScale(BigDecimal.ROUND_HALF_UP);
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

    public BigDecimal getTaxUnitPrice() {
        return taxUnitPrice;
    }

    public void setTaxUnitPrice(BigDecimal taxUnitPrice) {
        this.taxUnitPrice = taxUnitPrice;
    }
    @Override
    public String getUpdatePriceReason() {
        return updatePriceReason;
    }
    @Override
    public void setUpdatePriceReason(String updatePriceReason) {
        this.updatePriceReason = updatePriceReason;
    }

    public String getCreateDateString() {
        return createDateString;
    }

    public void setCreateDateString(String createDateString) {
        this.createDateString = createDateString;
    }
}
