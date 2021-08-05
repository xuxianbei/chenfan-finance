package com.chenfan.finance.model.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * toc_expend_order
 * @author 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TocExpendOrder implements Serializable {
    /**
     * 订单管理订单编号
     */
    private String tradeNo;
    private Integer shopId;
    /**
     * 原始订单子单
     */
    private String oid;

    /**
     * 支付宝流水号
     */
    private String financeNo;

    /**
     * 原始订单主单号
     */
    private String tid;

    /**
     * 子订单金额(原始金额)
     */
    private BigDecimal totalShareAmount;

    /**
     * 支付宝到账金额流水金额
     */
    private BigDecimal detailReceived;

    /**
     * 退款金额
     */
    private BigDecimal totalAmtAfterShare;

    /**
     * 店铺支付宝账号
     */
    private String shopAlipayAccount;

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
    private Integer saleQty;

    /**
     * 销售的SKU NO
     */
    private String saleSpecNo;

    /**
     * 销售的SPU NO
     */
    private String saleGoodsNo;

    private String saleGoodsName;

    private String saleSpecName;

    /**
     * 退款的数量
     */
    private Integer refundQyt;

    /**
     * 根据什么匹配的
     */
    private Integer checkType;

    /**
     * 销售单价
     */
    private BigDecimal price;
    /**
     *
     */
    private String refundNo;

    private static final long serialVersionUID = 1L;
}