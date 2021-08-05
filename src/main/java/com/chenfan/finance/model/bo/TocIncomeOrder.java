package com.chenfan.finance.model.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.chenfan.finance.enums.TocMappingTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * toc_income_order
 * @author 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TocIncomeOrder implements Serializable {

    /**
     * 订单管理订单编号
     */
    private String tradeNo;
    private Integer shopId;
    /**
     * 支付宝流水号
     */
    private String financeNo;


    private String tid;

    /**
     * 原始订单子单
     */
    private String oid;

    /**
     * 子订单金额(原始金额)
     */
    private BigDecimal totalShareAmount;

    /**
     * 子订单退款金额（0或者所有得）
     */
    private BigDecimal totalAmtRefund;

    /**
     * 子订单邮费（0或者总邮费）
     */
    private BigDecimal totalAmtPost;

    /**
     * 支付宝到账金额流水金额
     */
    private BigDecimal detailReceived;

    /**
     * 该笔顶的权益金额
     */
    private BigDecimal totalAmtEquity;

    /**
     * 子订单金额(原始金额-子订单退款金额-子订单邮费-该笔顶的权益金额)
     */
    private BigDecimal totalAmtAfterShare;

    /**
     * 出库金额
     */
    private BigDecimal totalStockOutAmt;

    /**
     * 店铺支付宝账号
     */
    private String shopAlipayAccount;

    /**
     * 订单创建时间
     */
    private LocalDateTime createOrderDate;

    private LocalDateTime stockOutDate;
    /**
     * 主订单支付日期
     */
    private LocalDateTime payDate;

    /**
     * 到账日期
     */
    private LocalDateTime receivedDate;

    /**
     * 销售数量，子单销售数量
     */
    private BigDecimal saleQty;

    /**
     * 销售规格编号
     */
    private String saleSpecNo;

    private String saleSpecName;

    private String saleGoodsNo;

    private String saleGoodsName;

    /**
     * 当前退款商品数量
     */
    private Integer refundQyt;
    /**
     * @see TocMappingTypeEnum
     */
    private Integer checkType;

    /**
     * 销售单价
     */
    private BigDecimal price;


    private String refundNo;


    @TableField(exist = false)
    private List<TocIncomeOrderStockOut> tocIncomeOrderStockOutList;


    private static final long serialVersionUID = 1L;
}