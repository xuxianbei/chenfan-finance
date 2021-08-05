package com.chenfan.finance.model.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * toc_sales_trade_order
 * @author 
 */
@Data
@TableName("ldb.m_wdt_sales_trade_order")
public class TocSalesTradeOrder implements Serializable {
    private Integer recId;

    private Integer tradeId;

    private Integer specId;

    /**
     * 平台ID
     */
    private Integer platformId;

    /**
     * 原始子订单号
     */
    private String srcOid;

    /**
     * 如果货品是由组合装拆分的，这里填写组合装ID
     */
    private Integer suiteId;

    /**
     * 原始订单号
     */
    private String srcTid;

    /**
     * 是否是赠品 0非赠品 1自动赠送 2手工赠送
     */
    private Integer giftType;

    /**
     * 退款状态(冗余字段) 0无退款1取消退款,2已申请退款,3等待退货,4等待收货,5退款成功---(原始子订单关闭，这里也是退款状态)
     */
    private Integer refundStatus;

    /**
     * 1担保2非担保3在线非担保(冗余字段)
     */
    private Integer guaranteeMode;

    /**
     * 发货条件 1款到发货 2货到付款(包含部分货到付款) 3分期付款--(冗余字段)
     */
    private Integer deliveryTerm;

    /**
     * 关联发货(冗余字段)
     */
    private String bindOid;

    /**
     * 货品数量
     */
    private BigDecimal num;

    /**
     * 销售单价，手工新建时使用货品属性中的“零售价”
     */
    private BigDecimal price;

    /**
     * 实发数量,删除操作等于将此值设置为0
     */
    private BigDecimal actualNum;

    /**
     * 售后退款数量
     */
    private BigDecimal refundNum;

    /**
     * 成交价,原始单折扣及分摊之后的价格
     */
    private BigDecimal orderPrice;

    /**
     * 进入ERP后再次调整的价格，默认值与order_price一致
     */
    private BigDecimal sharePrice;

    /**
     * 手工调整价,正数为加价,负数为减价,暂未处理
     */
    private BigDecimal adjust;

    /**
     * 总折扣金额
     */
    private BigDecimal discount;

    /**
     * 分摊后合计应收=share_price*num,share_price是根据share_amount反推的,因此share_price可能有精度损失
     */
    private BigDecimal shareAmount;

    /**
     * share_amount备份值,退款恢复使用
     */
    private BigDecimal shareAmount2;

    /**
     * 邮费分摊
     */
    private BigDecimal sharePost;

    /**
     * 已支付金额
     */
    private BigDecimal paid;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 货品名称
     */
    private String goodsName;

    /**
     * 货品id
     */
    private Integer goodsId;

    /**
     * 货品编号
     */
    private String goodsNo;

    /**
     * 规格名
     */
    private String specName;

    /**
     * 商家编码
     */
    private String specNo;

    /**
     * 规格码
     */
    private String specCode;

    /**
     * 组合装商家编码
     */
    private String suiteNo;

    /**
     * 如果是组合装拆分的，此为组合装名称
     */
    private String suiteName;

    /**
     * 组合装数量,不受拆分合并影响
     */
    private BigDecimal suiteNum;

    /**
     * 组合装分摊后总价
     */
    private BigDecimal suiteAmount;

    /**
     * 组合装优惠
     */
    private BigDecimal suiteDiscount;

    /**
     * 打印组合装而非组合装的单品
     */
    private Integer isPrintSuite;

    /**
     * 平台货品名称
     */
    private String apiGoodsName;

    /**
     * 平台规格名
     */
    private String apiSpecName;

    /**
     * 预估单个货品重量
     */
    private BigDecimal weight;

    /**
     * 佣金
     */
    private BigDecimal commission;

    /**
     * 1销售商品 2原材料 3包装 4周转材料5虚拟商品 0其它
     */
    private Integer goodsType;

    /**
     * 使用二进制的与或来操作，识别商品的一些属性
比如
     */
    private Integer flag;

    /**
     * 是否允许0成本
     */
    private Integer isZeroCost;

    /**
     * 库存保留情况 0未保留（取消的订单或完成）1无库存记录 2未付款 3已保留待审核 4待发货 5预订单库存
     */
    private Integer stockReserved;

    /**
     * 平台已发货
     */
    private Integer isConsigned;

    /**
     * 大件类型0非大件1普通大件2独立大件
     */
    private Integer largeType;

    /**
     * 发票类别，0 不需要，1普通发票，2增值税发票
     */
    private Integer invoiceType;

    /**
     * 发票内容
     */
    private String invoiceContent;

    /**
     * 订单内部来源1手机2聚划算
     */
    private Integer fromMask;

    /**
     * 线上订单，标记是否打款
     */
    private Integer isReceived;

    /**
     * 是否主子订单,为发货算法使用
     */
    private Integer isMaster;

    /**
     * 类目id
     */
    private Integer cid;

    private String remark;

    private Date modified;

    private Date created;


    private static final long serialVersionUID = 1L;
}