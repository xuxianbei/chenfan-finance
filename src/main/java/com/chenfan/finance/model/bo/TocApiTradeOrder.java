package com.chenfan.finance.model.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * toc_api_trade_order
 * @author 
 */
@Data
@TableName("ldb.m_wdt_api_trade_order")
public class TocApiTradeOrder implements Serializable {


    private Integer platformId;

    private String tid;

    private String oid;

    /**
     * 平台的状态 10未确认 20待尾款 30待发货 40部分发货 50已发货 60已签收 70已完成 80已退款 90已关闭
     */
    private Integer status;

    /**
     * 处理状态 10待递交20已递交30部分发货40已发货50部分结算60已完成70已取消
     */
    private Integer processStatus;

    /**
     * 退款标记：0无退款1取消退款,2已申请退款,3等待退货,4等待收货,5退款成功
     */
    private Integer refundStatus;

    /**
     * 退款处理状态，1待审核 2已同意 3已拒绝 4待收货 5已完成 6已关闭
     */
    private Integer processRefundStatus;

    /**
     * 0正常货品 1虚拟货品 2服务
     */
    private Integer orderType;

    /**
     * 发票类别，0 不需要，1普通发票，2增值税发票
     */
    private Integer invoiceType;

    /**
     * 发票内容
     */
    private String invoiceContent;

    /**
     * 关联子订单
     */
    private String bindOid;

    /**
     * 平台货品ID
     */
    private String goodsId;

    /**
     * 平台规格id
     */
    private String specId;

    /**
     * 商家编码
     */
    private String goodsNo;

    /**
     * 规格商家编码
     */
    private String specNo;

    /**
     * 平台货品名
     */
    private String goodsName;

    /**
     * 平台规格名
     */
    private String specName;

    /**
     * 平台规格码,暂时没用
     */
    private String specCode;

    /**
     * 平台退款单id
     */
    private String refundId;

    /**
     * 是否无效货品
     */
    private Integer isInvalidGoods;

    /**
     * 货品数量
     */
    private BigDecimal num;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 手工调整的优惠金额
     */
    private BigDecimal adjustAmount;

    /**
     * 平台折扣,不包含手工调整和分摊优惠
     */
    private BigDecimal discount;

    /**
     * 分摊折扣,退款不变
     */
    private BigDecimal shareDiscount;

    /**
     * 总价格，不包含邮费
商品价格 * 商品数量 + 手工调整金额 - 订单优惠金额
     */
    private BigDecimal totalAmount;

    /**
     * 分摊后子订单价格,退款保持不变
     */
    private BigDecimal shareAmount;

    /**
     * 邮费分摊,退款后为0
     */
    private BigDecimal sharePost;

    /**
     * 其它分摊,暂时未用
     */
    private BigDecimal shareOther;

    /**
     * 费用分摊,暂时未用
     */
    private BigDecimal shareCost;

    /**
     * 退款金额,退款完成后才有效
     */
    private BigDecimal refundAmount;

    /**
     * 分摊已付金额
     */
    private BigDecimal paid;

    /**
     * 佣金
     */
    private BigDecimal commission;

    /**
     * 修改标记
     */
    private Integer modifyFlag;

    /**
     * 其它标记,用于扩展
     */
    private Integer otherFlags;

    /**
     * 是否是赠品 0非赠品 1自动赠送 2手工赠送
     */
    private Integer giftType;

    /**
     * 仓库类型0可选任意仓库1普通仓库2物流宝3科捷
     */
    private Integer wmsType;

    /**
     * 是否已经自动流转到wms
     */
    private Integer isAutoWms;

    /**
     * 仓库编号
     */
    private String warehouseNo;

    /**
     * 外部WMS中出库单号
     */
    private String stockoutNo;

    /**
     * 发货后物流单号
     */
    private String logisticsNo;

    /**
     * 平台类目
     */
    private String cid;

    /**
     * 备注
     */
    private String remark;

    private LocalDateTime modified;

    private LocalDateTime created;



    private static final long serialVersionUID = 1L;
}