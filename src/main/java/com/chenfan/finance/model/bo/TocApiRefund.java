package com.chenfan.finance.model.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * toc_api_refund
 * @author 
 */
@Data
@TableName("ldb.m_wdt_api_refund")
public class TocApiRefund implements Serializable {
    /*private Long refundId;*/

    private Byte platformId;
    /**
     * 店铺id
     */
    private Short shopId;
    /**
     * refundNo 退款单号
     */
    private String refundNo;
    /**
     * 退款数量
     */
    @TableField(exist = false)
    private Integer num;

    @TableField(exist = false)
    private Boolean isOther;

    /**
     * 原始单号
     */
    private String tid;

    /**
     * 0取消订单1退款(未发货，退款申请)2退货3换货4退款不退货
     */
    private Integer type;

    /**
     * 平台状态：1取消退款,2已申请退款,3等待退货,4等待收货,5退款成功
     */
    private Integer status;

    /**
     * 处理状态0待递交 10已取消 20待审核 30已同意 40已拒绝 50待财审 60待收货 70部分到货 80已完成
     */
    private Integer processStatus;

    /**
     * 担保交易类别：1担保交易2非担保交易3非担保在线交易（ecshop）
     */
    private Integer guaranteeMode;

    /**
     * 1; 需要客服介入2; 客服已经介入3; 客服初审完成 4; 客服主管复审失败5; 客服处理完成6;
     */
    private Integer csStatus;

    /**
     * 0; 退款先行垫付申请 1; 退款先行垫付，垫付完成 2; 退款先行垫付，卖家拒绝收货 3; 退款先行垫付，垫付关闭 4; 退款先行垫付，垫付分账成功 5;
     */
    private Integer advanceStatus;

    /**
     * 1不允许拒绝2需要到网页版操作
     */
    private Integer opConstraint;

    /**
     * 退款版本
     */
    private String refundVersion;

    /**
     * 买家支付帐号
     */
    private String payAccount;

    /**
     * 支付订单号
     */
    private String payNo;

    /**
     * 申请退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 实际退款金额
     */
    private BigDecimal actualRefundAmount;

    /**
     * 当前状态超时时间
     */
    private LocalDateTime currentPhaseTimeout;

    /**
     * 标题
     */
    private String title;

    /**
     * 物流公司名称
     */
    private String logisticsName;

    /**
     * 物流单号
     */
    private String logisticsNo;

    /**
     * 昵称
     */
    private String buyerNick;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 退款创建时间
     */
    private Date refundTime;

    /**
     * 原始退款单的操作员
     */
    private Integer operatorId;

    /**
     * 是否售后退款单
     */
    private Integer isAftersale;

    /**
     * 标记掩码
     */
    private Integer refundMask;

    /**
     * 外部退款单，未经ERP处理过的订单
     */
    private Integer isExternal;

    /**
     * 退款原因
     */
    private String reason;

    /**
     * 退款说明
     */
    private String remark;

    /**
     * 修改掩码: 1是新订单 2状态变化 4金额变化
     */
    private Integer modifyFlag;

    private Integer tag;

    private LocalDateTime modified;

    private LocalDateTime created;

   /* private String batchTime;*/

    private static final long serialVersionUID = 1L;
}