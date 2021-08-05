package com.chenfan.finance.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.chenfan.finance.enums.TocMappingTypeEnum;
import com.chenfan.finance.model.bo.TocExpendOrderStockOut;
import com.chenfan.finance.model.bo.TocIncomeOrderStockOut;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description // toc 报表响应数据
 * @Date 2021/4/26  15:21
 * @Version 1.0
 */
public class TocReportRp {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  TocReportRpBySuccess{
        @ApiModelProperty(value = "",hidden = false)
        private Integer checkType;
        @ApiModelProperty("支付宝流水编号")
        private String financeNo;
        @ApiModelProperty("淘宝原始订单号")
        private String tid;
        @ApiModelProperty("关联的订单子单号")
        private String oid;
        @ApiModelProperty("店铺支付宝账户")
        private String shopAlipayAccount;
        @ApiModelProperty("订单创建时间")
        private String createOrderDate;

        @ApiModelProperty("流水入账时间")
        private String receivedDate;

        @ApiModelProperty("出库时间")
        private String stockOutDate;
        @ApiModelProperty("消费者支付时间")
        private String payDate;

        @ApiModelProperty("商品款号")
        private String saleGoodsNo;

        @ApiModelProperty("商品名称")
        private String saleGoodsName;
        @ApiModelProperty("SKU_NAME")
        private String saleSpecName;
        @ApiModelProperty("SKU_NO")
        private String saleSpecNo;
        @ApiModelProperty("品牌编号")
        private Integer brandId;
        private String  brandName;
        @ApiModelProperty("销售数量")
        private Integer saleQty;
        @ApiModelProperty("出库数量")
        private BigDecimal stockOutQyt;
        @ApiModelProperty("子单总金额")
        private BigDecimal totalShareAmount;
        @ApiModelProperty("支付宝入账金额")
        private BigDecimal detailReceived;
        @ApiModelProperty("关联出库单号")
        private String stockOutNos;
        @ApiModelProperty("退款的数量")
        private Integer refundQyt;

        @ApiModelProperty("关联的出库单详情")
        private List<TocIncomeOrderStockOut>  stockDetailList;

        @ApiModelProperty("类型：收入、支出")
        private String type;
        /**
         * totalAmtAfterShare
         */
        @ApiModelProperty("该笔订单分摊金额")
        private BigDecimal totalAmtAfterShare;

        @ApiModelProperty("关联的订单管理编号")
        private String tradeNo;

        @ApiModelProperty("店铺id")
        private Integer shopId;

        @ApiModelProperty("是否推送到u8")
        private String pushState;

        /**
         * U8订单编号
         */
        @ApiModelProperty("U8订单编号")
        private String orderNo;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TocReportRpByWeek{


        /**
         * 品牌id
         */
        @ApiModelProperty("品牌id")
        private Integer brandId;

        private String  brandName;

        /**
         * 主键
         */
        @ApiModelProperty("主键-查询详情的")
        private Long detailId;

        /**
         * spu_code
         */
        @ApiModelProperty("spu_code")
        private String spuCode;

        /**
         * spu_name
         */
        @ApiModelProperty("spu_name")
        private String spuName;

        /**
         * sku_code
         */
        @ApiModelProperty("sku_code")
        private String skuCode;

        /**
         * sku_name
         */
        @ApiModelProperty("sku_name")
        private String skuName;

        /**
         * 本周此sku 销售或退款汇总
         */
        @ApiModelProperty("总数- 本周此sku 销售或退款汇总")
        private BigDecimal skuCount;

        /**
         * 此sku 对应的均价（总金额/数量）
         */
        @ApiModelProperty("均价-此sku 对应的均价（总金额/数量）")
        private BigDecimal skuPrice;

        /**
         * 总金额
         */
        @ApiModelProperty("总价")
        private BigDecimal totalMoney;

        @ApiModelProperty(value = "到账时间" ,hidden = true)
        private LocalDateTime daozhangt;
        /**
         * toc_u8_header 主键
         */
        @ApiModelProperty(value = "toc_u8_header 主键",hidden = true)
        private Long mappingId;

        /**
         * 关联的原始销售单子单
         */
        @ApiModelProperty("关联的原始销售单子单")
        private String oids;

        /**
         * 是否有对应的退款流水
         */
        @ApiModelProperty("是否有对应的退款流水")
        private Boolean havaRefundFinance;


        /**
         * 月份 eg202001
         */
        @ApiModelProperty("月份 eg202001")
        private String month;

        /**
         * 当前月第几周
         */
        @ApiModelProperty("当前月第几周")
        private Integer weeknOfMonth;

        /**
         * 类型：收入或者支出
         */
        @ApiModelProperty("类型：收入或者支出")
        private Integer type;



        /**
         * U8订单编号
         */
        @ApiModelProperty("U8订单编号")
        private String orderNo;

        /**
         * 统计开始时间
         */
        @ApiModelProperty("统计开始时间")
        private LocalDateTime countStartTime;

        /**
         * 统计结束时间
         */
        @ApiModelProperty("统计结束时间")
        private LocalDateTime countEndTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TocReportRpByDetail{


        /**
         * 支付宝流水号
         */
        @ApiModelProperty("支付宝流水号")
        private String financeNo;


        @ApiModelProperty("主订单号")
        private String tid;

        /**
         * 原始订单子单
         */
        @ApiModelProperty("子订单号")
        private String oid;

        /**
         * 店铺支付宝账号
         */
        @ApiModelProperty("店铺支付宝账号")
        private String shopAlipayAccount;


        /**
         * 到账日期
         */
        @ApiModelProperty("流水到账日期")
        private LocalDateTime receivedDate;





        /**
         * 销售规格编号
         */
        @ApiModelProperty("sku_NO")
        private String saleSpecNo;

        @ApiModelProperty("sku_NAME")
        private String saleSpecName;

        @ApiModelProperty("商品款号")
        private String saleGoodsNo;

        @ApiModelProperty("商品名称")
        private String saleGoodsName;

        @ApiModelProperty("销售数量，子单销售数量")
        private BigDecimal saleQty;

        @ApiModelProperty("出库数量")
        private BigDecimal stockOutQyt;

        /**
         * 当前退款商品数量
         */
        @ApiModelProperty("退货数量")
        private Integer refundQyt;

        /**
         * 子订单退款金额（0或者所有得）
         */
        @ApiModelProperty("子订单退款金额（0或者所有得）")
        private BigDecimal totalAmtRefund;
        /**
         * 子订单金额(原始金额)
         */
        @ApiModelProperty("子订单金额(原始金额)")
        private BigDecimal totalShareAmount;
        /**
         * 支付宝到账金额流水金额
         */
        @ApiModelProperty("支付宝到账金额流水金额")
        private BigDecimal detailReceived;


        /**
         * 子订单邮费（0或者总邮费）
         */
        @ApiModelProperty("子订单邮费（0或者总邮费）")
        private BigDecimal totalAmtPost;


        /**
         * 该笔顶的权益金额
         */
        @ApiModelProperty(" 该笔顶的权益金额")
        private BigDecimal totalAmtEquity;

        /**
         * 子订单金额(原始金额-子订单退款金额-子订单邮费-该笔顶的权益金额)
         */
        @ApiModelProperty("子订单金额(原始金额-子订单退款金额-子订单邮费-该笔顶的权益金额)")
        private BigDecimal totalAmtAfterShare;

        /**
         * 出库金额
         */
        @ApiModelProperty("出库金额")
        private BigDecimal totalStockOutAmt;



        /**
         * 主订单支付日期
         */
        @ApiModelProperty("主订单支付日期")
        private LocalDateTime payDate;


        /**
         * @see TocMappingTypeEnum
         */
        @ApiModelProperty(value = "",hidden = true)
        private Integer checkType;

        /**
         * 销售单价
         */
        @ApiModelProperty("销售单价")
        private BigDecimal price;


        @ApiModelProperty("关联退款订单号")
        private String refundNo;

        @ApiModelProperty("关联的订单管理编号")
        private String tradeNo;

        @ApiModelProperty("店铺id")
        private Integer shopId;

        @TableField(exist = false)
        @ApiModelProperty("关联出库单详情列表")
        private List<TocIncomeOrderStockOut> orderStockOuts;

        @ApiModelProperty("关联出库单号")
        private String stockOutNos;
    }

}
