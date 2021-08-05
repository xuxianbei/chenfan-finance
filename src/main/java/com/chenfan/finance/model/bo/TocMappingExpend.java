package com.chenfan.finance.model.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * toc_mapping_expend
 * @author 
 */
@Data
public class TocMappingExpend implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 财务流水号
     */
    private String financeNo;

    /**
     * 主订单号
     */
    private String tid;

    /**
     * 子订单号
     */
    private String oid;

    /**
     * 退款单号
     */
    private String taobaoRefundNo;

    /**
     * 店铺支付宝账号
     */
    private String shopAlipayAccount;

    /**
     * 主订单支付日期
     */
    private LocalDateTime payDate;

    /**
     * 到账日期
     */
    private LocalDateTime receivedDate;

    /**
     * 货号
     */
    private String spuCode;

    /**
     * 存货编码
     */
    private String skuCode;

    /**
     * 退款数量
     */
    private Integer refundQty;

    /**
     * 申请退款金额
     */
    private BigDecimal actualRefundAmt;

    /**
     * 退款到账金额
     */
    private BigDecimal expenseAmt;

    /**
     * 账务流水号（逗号分割）
     */
    private String accountNos;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 店铺id
     */
    private Integer shopId;

    /**
     * 店铺简称
     */
    private String shopNickname;

    /**
     * 对账结果（1-成功，0-失败）
     */
    private Integer checkFlag;

    /**
     * 入库单号
     */
    private String recordCode;

    private static final long serialVersionUID = 1L;
}