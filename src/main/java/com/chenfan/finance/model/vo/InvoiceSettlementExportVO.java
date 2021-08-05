package com.chenfan.finance.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * @author liran
 */
@Data
public class InvoiceSettlementExportVO {
    @Data
    public static class InvoiceSettlementExport{
        @Excel(name = "结算单号", orderNum = "1")
        private String settlementNo;

        @Excel(name = "采购单号", orderNum = "2")
        private String poCode;

        @Excel(name="销售类型",replace = {"直播款_1","订单款_2"," _null"},orderNum ="3" )
        private Integer salesType;

        @Excel(name = "制单人", orderNum = "4")
        private String createBy;

        @Excel(name = "单据状态",
                replace = {"待提交到财务_1","待开票_5", "作废_8","已删除_0","已核销_9","已付款_11"," _null","已申请付款_12"},
                orderNum = "5")
        private Integer state;

        @Excel(name = "品牌", orderNum = "6")
        private String brandName;

        @Excel(name = "供应商简称", orderNum = "7")
        private String vendorAbbName;

        @Excel(name = "供应商全称", orderNum = "8")
        private String vendorName;

        @Excel(name = "结算开始时间", orderNum = "9")
        private String settlementStartDate;

        @Excel(name = "结算截止日期", orderNum = "10")
        private String settlementEndDate;

        @Excel(name = "货号", orderNum = "11")
        private String productCode;

        @Excel(name = "货品名称", orderNum = "12")
        private String productName;

        @Excel(name = "不含税单价", orderNum = "13")
        private BigDecimal freeTaxPrice;

        @Excel(name = "加价不含税单价", orderNum = "14")
        private BigDecimal upFreeTaxPrice;

        @Excel(name = "到货数量", orderNum = "15")
        private Integer arrivalQty;

        @Excel(name = "退次数量", orderNum = "16")
        private Integer defectiveRejectionQty;

        @Excel(name = "拒收数量", orderNum = "17")
        private Integer rejectionQty;

        //@Excel(name = "历史未结算日期", orderNum = "17")
        private String historyUnCountDate;

        //@Excel(name = "历史未结算截止日期", orderNum = "18")
        private String historyUnCountDateEnd;

        // @Excel(name = "历史结算数量", orderNum = "19")
        private Integer historyCount;

        @Excel(name = "本期结算数量", orderNum = "18")
        private Integer settlementQty;

        @Excel(name = "本期结算金额", orderNum = "19")
        private BigDecimal settlementAmount;

        @Excel(name = "不含税总金额", orderNum = "20")
        private BigDecimal freeTaxTotalPrice;

        @Excel(name = "税额", orderNum = "21")
        private BigDecimal taxPrice;

        @Excel(name = "质检扣费", orderNum = "22")
        private BigDecimal qaDeductions;

        @Excel(name = "红字扣费", orderNum = "23")
        private BigDecimal redDeductions;

        @Excel(name = "税差", orderNum = "24")
        private BigDecimal taxDiff;

        @Excel(name = "其他扣费", orderNum = "25")
        private BigDecimal othersDeductions;

        @Excel(name = "延期数量", orderNum = "26")
        private Integer delayCount;

        @Excel(name = "延期扣款金额", orderNum = "27")
        private BigDecimal postponeDeductionsTotal;

        @Excel(name = "预付款合同金额", orderNum = "28")
        private BigDecimal advancepayAmount;

        @Excel(name = "预付款实付金额", orderNum = "29")
        private BigDecimal advancepayAmountActual;

        @Excel(name = "本期实付金额", orderNum = "30")
        private BigDecimal realMoney;

        @Excel(name = "开票时间", orderNum = "31")
        private String purchaseInvoiceDate;

        @Excel(name = "发票号码", orderNum = "32")
        private String purchaseInvoiceNo;

        @Excel(name = "制单日期", orderNum = "33")
        private String createTime;

        @Excel(name = "审核人", orderNum = "34")
        private String auditBy;

        @Excel(name = "备注", orderNum = "35")
        private String remark;


    }
    @Data
    public static class InvoiceSettlementExportTax{
        @Excel(name = "结算单号", orderNum = "1")
        private String settlementNo;

        @Excel(name = "采购单号", orderNum = "2")
        private String poCode;

        @Excel(name = "制单人", orderNum = "3")
        private String createBy;



        @Excel(name = "单据状态",
                replace = {"待提交到财务_1","待开票_5", "作废_8","已删除_0","已核销_9","已付款_11"," _null"},
                orderNum = "4")
        private Integer state;

        @Excel(name = "品牌", orderNum = "5")
        private String brandName;

        @Excel(name = "供应商简称", orderNum = "6")
        private String vendorAbbName;

        @Excel(name = "供应商全称", orderNum = "7")
        private String vendorName;

        @Excel(name="销售类型",replace = {"直播款_1","订单款_2"," _null"},orderNum ="8" )
        private Integer salesType;

        @Excel(name = "结算开始时间", orderNum = "9")
        private String settlementStartDate;

        @Excel(name = "结算截止日期", orderNum = "10")
        private String settlementEndDate;

        @Excel(name = "货号", orderNum = "11")
        private String productCode;

        @Excel(name = "货品名称", orderNum = "12")
        private String productName;

        @Excel(name = "不含税单价", orderNum = "13")
        private BigDecimal freeTaxPrice;

        @Excel(name = "加价不含税单价", orderNum = "14")
        private BigDecimal upFreeTaxPrice;

        @Excel(name = "到货数量", orderNum = "15")
        private Integer arrivalQty;

        @Excel(name = "退次数量", orderNum = "16")
        private Integer defectiveRejectionQty;

        @Excel(name = "拒收数量", orderNum = "17")
        private Integer rejectionQty;

        //@Excel(name = "历史未结算日期", orderNum = "17")
        private String historyUnCountDate;

        //@Excel(name = "历史未结算截止日期", orderNum = "18")
        private String historyUnCountDateEnd;

        // @Excel(name = "历史结算数量", orderNum = "19")
        private Integer historyCount;

        @Excel(name = "本期结算数量", orderNum = "18")
        private Integer settlementQty;

        @Excel(name = "本期结算金额", orderNum = "19")
        private BigDecimal settlementAmount;

        @Excel(name = "不含税总金额", orderNum = "20")
        private BigDecimal freeTaxTotalPrice;



        @Excel(name = "质检扣费", orderNum = "21")
        private BigDecimal qaDeductions;

        @Excel(name = "红字扣费", orderNum = "22")
        private BigDecimal redDeductions;

        @Excel(name = "税差", orderNum = "23")
        private BigDecimal taxDiff;

        @Excel(name = "其他扣费", orderNum = "24")
        private BigDecimal othersDeductions;

        @Excel(name = "延期数量", orderNum = "25")
        private Integer delayCount;

        @Excel(name = "延期扣款金额", orderNum = "26")
        private BigDecimal postponeDeductionsTotal;

        @Excel(name = "预付款合同金额", orderNum = "27")
        private BigDecimal advancepayAmount;

        @Excel(name = "预付款实付金额", orderNum = "28")
        private BigDecimal advancepayAmountActual;

        @Excel(name = "本期实付金额", orderNum = "29")
        private BigDecimal realMoney;

        @Excel(name = "开票时间", orderNum = "30")
        private String purchaseInvoiceDate;

        @Excel(name = "发票号码", orderNum = "31")
        private String purchaseInvoiceNo;

        @Excel(name = "制单日期", orderNum = "32")
        private String createTime;

        @Excel(name = "审核人", orderNum = "33")
        private String auditBy;

        @Excel(name = "备注", orderNum = "34")
        private String remark;

    }



}

