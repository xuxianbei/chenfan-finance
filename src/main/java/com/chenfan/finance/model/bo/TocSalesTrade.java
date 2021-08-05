package com.chenfan.finance.model.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * toc_sales_trade
 * @author 
 */
@Data
@TableName("ldb.m_wdt_sales_trade")
public class TocSalesTrade implements Serializable {
    private Integer tradeId;

    private String tradeNo;

    private Byte platformId;

    private Short shopId;

    private Short warehouseId;

    /**
     * 仓库类型,选择仓库时仓库类型要不变,-1表示不限仓库类型
     */
    private Integer warehouseType;

    /**
     * 原始单号，如果有多个，以","分隔
且以增序排列,不重复
,过长将被裁剪
     */
    private String srcTids;

    /**
     * 平台支付帐号，目前仅有支付宝
     */
    private String payAccount;

    /**
     * 订单状态 5已取消 10待付款 12待尾款 13待选仓 15等未付16延时审核 19预订单前处理 20前处理(赠品，合并，拆分)21委外前处理22抢单前处理 25预订单 27待抢单 30待客审 35待财审 40待递交仓库 45递交仓库中 50已递交仓库 53未确认 55已确认 95已发货 100已签收 105部分打款 110已完成
     */
    private Integer tradeStatus;

    /**
     * 用于多级审核
     */
    private Integer checkStep;

    /**
     * 出库状态1验货2称重4出库8物流同步16分拣
     */
    private Integer consignStatus;

    /**
     * 订单来源 1API抓单，2手工建单 3excel导入 4现款销售 5外部推送
     */
    private Integer tradeFrom;

    /**
     * 1网店销售2线下零售3售后换货4批发业务5保修换新6保修完成 .. 101自定义类型1 102自定义类型2 103自定义类型3 104自定义类型4 105自定义类型5 106自定义类型6
     */
    private Integer tradeType;

    /**
     * 发货条件 1款到发货 2货到付款(包含部分货到付款) 3分期付款--(冗余字段) 4挂账单
     */
    private Integer deliveryTerm;

    /**
     * 冻结原因
     */
    private Short freezeReason;

    /**
     * 退款状态 0无退款 1申请退款 2部分退款 3全部退款
     */
    private Integer refundStatus;

    /**
     * 未合并标记,1有未付款订单,2有同名未合并订单
     */
    private Integer unmergeMask;

    /**
     * 分销类别 0非分销订单 1代销 2经销,未使用
     */
    private Integer fenxiaoType;

    /**
     * 分销商nick,未使用
     */
    private String fenxiaoNick;

    /**
     * 下单时间
     */
    private LocalDateTime tradeTime;

    /**
     * 支付时间
     */
    private  LocalDateTime payTime;

    /**
     * 延时此进一步处理，等未付或延时审核 激活时间
     */
    private Integer delayToTime;

    /**
     * 货品数量
     */
    private BigDecimal goodsCount;

    /**
     * 货品种类数
     */
    private Short goodsTypeCount;

    /**
     * 单一货品商家编码,多种货品为空,组合装时为组合装编码
     */
    private String singleSpecNo;

    /**
     * 原货品数量
     */
    private BigDecimal rawGoodsCount;

    /**
     * 原货品种类数
     */
    private Short rawGoodsTypeCount;

    /**
     * 0普通客户1经销商
     */
    private Integer customerType;

    /**
     * 买家ID
     */
    private Long customerId;

    /**
     * 买家网名
     */
    private String buyerNick;

    /**
     * 证件类别 1 身份证号
     */
    private Integer idCardType;

    /**
     * 证件号码
     */
    private String idCard;

    private String receiverName;

    private Short receiverCountry;

    private Integer receiverProvince;

    private Integer receiverCity;

    private Integer receiverDistrict;

    private String receiverAddress;

    /**
     * 手机
     */
    private String receiverMobile;

    /**
     * 固话
     */
    private String receiverTelno;

    private String receiverZip;

    /**
     * 省市区空格分隔
     */
    private String receiverArea;

    /**
     * 京东几环,未使用
     */
    private String receiverRing;

    /**
     * 大头笔
     */
    private String receiverDtb;

    /**
     * 配送时间,如周一至周五,未使用
     */
    private String toDeliverTime;

    /**
     * 配送中心,未使用
     */
    private String preChargeTime;

    /**
     * 币种
     */
    private String currency;

    /**
     * 是否送货前通知(京东),未使用
     */
    private Integer isPrevNotify;

    /**
     * 物流公司ID
     */
    private Short logisticsId;

    /**
     * 物流单号
     */
    private String logisticsNo;

    /**
     * 客户备注
     */
    private String buyerMessage;

    /**
     * 客服备注
     */
    private String csRemark;

    /**
     * 客服备注旗子
     */
    private Integer remarkFlag;

    /**
     * 打印备注
     */
    private String printRemark;

    /**
     * 便签条数
     */
    private Short noteCount;

    /**
     * 买家留言条数
     */
    private Integer buyerMessageCount;

    /**
     * 客服备注条数
     */
    private Integer csRemarkCount;

    /**
     * 客服备注变化0,未变化1平台变化,2手工修改,4发票手工修改
     */
    private Short csRemarkChangeCount;

    /**
     * 货款总额（未扣除优惠）
     */
    private BigDecimal goodsAmount;

    /**
     * 邮费
     */
    private BigDecimal postAmount;

    /**
     * 其它收费，暂不用
     */
    private BigDecimal otherAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discount;

    /**
     * 应收金额
     */
    private BigDecimal receivable;

    /**
     * 优惠变化金额,更新货品和数量
     */
    private BigDecimal discountChange;

    /**
     * 客户使用的预存款
     */
    private BigDecimal tradePrepay;

    /**
     * 款到发货金额,paid>=dap_amount才可发货
     */
    private BigDecimal dapAmount;

    /**
     * 货到付款金额,包含ext_cod_fee
     */
    private BigDecimal codAmount;

    /**
     * 分期付款金额
     */
    private BigDecimal piAmount;

    /**
     * 买家付的我们收不到的cod费用
     */
    private BigDecimal extCodFee;

    /**
     * 货款预估成本
     */
    private BigDecimal goodsCost;

    /**
     * 预估邮费成本
     */
    private BigDecimal postCost;

    /**
     * 其它成本
     */
    private BigDecimal otherCost;

    /**
     * 预估利润
     */
    private BigDecimal profit;

    /**
     * 已支付金额
     */
    private BigDecimal paid;

    /**
     * 预估称重kg 订单货品重量加上包装的重量
     */
    private BigDecimal weight;

    /**
     * 体积
     */
    private BigDecimal volume;

    /**
     * 税额
     */
    private BigDecimal tax;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 佣金
     */
    private BigDecimal commission;

    /**
     * 发票类别0不需要1普通发票2普通增值税发票3专用增值税发票
     */
    private Integer invoiceType;

    /**
     * 发票抬头
     */
    private String invoiceTitle;

    /**
     * 发票内容
     */
    private String invoiceContent;

    /**
     * 发票ID，目前只设0-1，1表示已开发票
     */
    private Integer invoiceId;

    /**
     * 业务员，应该记录在原始单上
     */
    private Integer salesmanId;

    /**
     * 销售积分,未使用
     */
    private Integer salesScore;

    /**
     * 审核员工ID
     */
    private Integer checkerId;

    /**
     * 财审操作员ID
     */
    private Integer fcheckerId;

    /**
     * 签出员工id
     */
    private Integer checkouterId;

    /**
     * 线上包裹拆分数
     */
    private Integer splitPackageNum;

    /**
     * 背景色标记
     */
    private Short flagId;

    /**
     * 异常订单(bit位)，1无库存记录 2地址发生变化 4发票变化 8仓库变化16备注变化32平台更换货品64退款...512抢单异常
     */
    private Integer badReason;

    /**
     * 不可合并拆分
     */
    private Integer isSealed;

    /**
     * 赠品标记1已处理过赠品,但没有匹配任何策略2自动赠送4手工赠送6即有自动也有手工
     */
    private Integer giftMask;

    /**
     * 拆分订单，原单ID，用于避免自动合并,大件拆分为（原订单的id值），自动拆分为负值（原订单的-id值）
     */
    private Integer splitFromTradeId;

    /**
     * 包含大件类型，1普通套件2独立套件,未使用-1非单发件
     */
    private Integer largeType;

    /**
     * 出库单号,内部或外部仓库的订单号
     */
    private String stockoutNo;

    /**
     * 未使用
     */
    private Integer logisticsTemplateId;

    /**
     * 未使用
     */
    private Integer sendbillTemplateId;

    /**
     * 驳回原因
     */
    private Short revertReason;

    /**
     * 取消原因
     */
    private Short cancelReason;

    /**
     * 催未付款订单短信发送标记
     */
    private Integer isUnpaymentSms;

    /**
     * 包装id
     */
    private Integer packageId;

    /**
     * 订单标记位 1使用智选物流 2 航空禁运 4 预订单自动转审核失败 8 预占用待发货库存 16 订单货品指定批次 32 自动流转仓库 64 部分发货 128 全部发货
     */
    private Integer tradeMask;

    /**
     * 保留
     */
    private String reserve;

    /**
     * 版本号
     */
    private Short versionId;

    private LocalDateTime modified;

    private LocalDateTime created;



    private static final long serialVersionUID = 1L;
}