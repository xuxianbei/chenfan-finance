package com.chenfan.finance.model.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * toc_api_trade
 * @author 
 */
@Data
@TableName("ldb.m_wdt_api_trade")
public class TocApiTrade implements Serializable {


    private Integer platformId;

    private Integer shopId;

    /**
     * 平台订单编号
     */
    private String tid;

    /**
     * 处理状态10待递交20已递交30部分发货40已发货50部分结算60已完成70已取消
     */
    private Integer processStatus;

    /**
     * 订单平台的状态 10未确认 20待尾款 30待发货 40部分发货 50已发货 60已签收 70已完成 80已退款 90已关闭(付款前取消)
     */
    private Integer tradeStatus;

    /**
     * 担保交易类别：1担保交易2非担保交易3非担保在线交易（ecshop）
     */
    private Integer guaranteeMode;

    /**
     * 0未付款1部分付款2已付款
     */
    private Integer payStatus;

    /**
     * 发货条件 1款到发货 2货到付款(包含部分货到付款) 3分期付款 4挂帐单
     */
    private Integer deliveryTerm;

    /**
     * 支付方式: 1在线转帐 2现金，3银行转账，4邮局汇款 5预付款 6刷卡
     */
    private Integer payMethod;

    /**
     * 退款状态 0无退款 1申请退款 2部分退款 3全部退款
     */
    private Integer refundStatus;

    /**
     * 是否第一次抓取下来，第一次抓取下来后，递交时改为0，当订单有变化时modify_flag有变化，但此值为0
     */
    private Integer isNew;

    /**
     * 有问题的订单，1无效货品 2货品数量不对 4未指定仓库 8订单无货品
     */
    private Integer badReason;

    /**
     * 订单修改标记，以二进制位表示
     */
    private Integer modifyFlag;

    /**
     * 分销类别(从供应商有角度)：0非分销订单 1转供销(单子由别人给我们卖,不需要发货) 2代销 3经销 
     */
    private Integer fenxiaoType;

    /**
     * 转供销情况下，采购单ID，从分销商角度看
     */
    private String purchaseId;

    /**
     * 分销订单的分销商id,或转供销时供应商id
     */
    private String fenxiaoNick;

    /**
     * 子订单个数
     */
    private Short orderCount;

    /**
     * 货品总数量，用于递交时检验
     */
    private BigDecimal goodsCount;

    /**
     * 下单时间
     */
    private LocalDateTime tradeTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 交易结束时间
     */
    private LocalDateTime endTime;

    /**
     * 买家备注
     */
    private String buyerMessage;

    /**
     * 客服备注
     */
    private String remark;

    /**
     * 标旗
     */
    private Integer remarkFlag;

    /**
     * 买家帐号ID
     */
    private String buyerNick;

    /**
     * 买家姓名
     */
    private String buyerName;

    private String buyerEmail;

    private String buyerArea;

    /**
     * 证件类别 1 身份证号
     */
    private Integer idCardType;

    /**
     * 证件号码
     */
    private String idCard;

    /**
     * 平台支付订单ID,如支付宝的订单号
     */
    private String payId;

    /**
     * 买家支付宝帐号
     */
    private String payAccount;

    private String receiverName;

    /**
     * 国家
     */
    private Short receiverCountry;

    /**
     * 省份ID
     */
    private Integer receiverProvince;

    /**
     * 城市id
     */
    private Integer receiverCity;

    /**
     * 地区ID
     */
    private Integer receiverDistrict;

    /**
     * 地址，不包含省市区
     */
    private String receiverAddress;

    /**
     * 收件人手机
     */
    private String receiverMobile;

    /**
     * 收件人电话
     */
    private String receiverTelno;

    /**
     * 收件人邮编
     */
    private String receiverZip;

    /**
     * 省市区用空格分隔
     */
    private String receiverArea;

    /**
     * 京东几环
     */
    private String receiverRing;

    /**
     * 买家要求的送货日期(京东平台上有)
     */
    private String toDeliverTime;

    /**
     * 配送中心（京东）
     */
    private String preChargeTime;

    /**
     * 币种
     */
    private String currency;

    /**
     * 是否送货前通知(京东)
     */
    private Integer isPrevNotify;

    /**
     * 收件人的hash值
     */
    private String receiverHash;

    /**
     * 货款,未扣除优惠,退款不变
     */
    private BigDecimal goodsAmount;

    /**
     * 邮费
     */
    private BigDecimal postAmount;

    /**
     * 其它应从买家收取的服务费
     */
    private BigDecimal otherAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discount;

    /**
     * 应收金额，售前退款会变化
     */
    private BigDecimal receivable;

    /**
     * 买家已付金额，售前退款会变化
     */
    private BigDecimal paid;

    /**
     * 使用信用卡支付金额
     */
    private BigDecimal creditCardPaid;

    /**
     * 平台费用
     */
    private BigDecimal platformCost;

    /**
     * 已从平台收款的金额
     */
    private BigDecimal received;

    /**
     * 客户使用的预收款
     */
    private BigDecimal tradePrepay;

    /**
     * 款到发货金额
     */
    private BigDecimal dapAmount;

    /**
     * 货到付款金额
     */
    private BigDecimal codAmount;

    /**
     * 分期付款金额,暂时不用
     */
    private BigDecimal piAmount;

    /**
     * 我们收不到的货到付款费用
     */
    private BigDecimal extCodFee;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 佣金
     */
    private BigDecimal commission;

    /**
     * 物流类别，-1表示自由选择
     */
    private Integer logisticsType;

    /**
     * 发货后物流单号
     */
    private String logisticsNo;

    /**
     * 发票类别，0 不需要，1普通发票，2增值税发票
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
     * 订单来源： 1API抓单，2手工建单 3excel导入 4现款销售
     */
    private Integer tradeFrom;

    /**
     * 内部来源1手机订单2聚划算4服务子订单
     */
    private Integer tradeMask;

    /**
     * 0任意仓库1普通仓库2物流宝3京东仓储127其它
     */
    private Integer wmsType;

    /**
     * 外部仓库编号
     */
    private String warehouseNo;

    /**
     * WMS中订单编号
     */
    private String stockoutNo;

    /**
     * 是否自动流转到wms
     */
    private Integer isAutoWms;

    /**
     * 淘宝新增,物流到货时效
     */
    private Short arriveInterval;

    /**
     * 淘宝新增,物流到货时效，单位天
     */
    private Short consignInterval;

    /**
     * 淘宝新增,物流到货时效截单时间，格式 HH:mm
     */
    private String arriveCutTime;

    /**
     * 下单使用积分
     */
    private Integer score;

    /**
     * 实际使用的积分
     */
    private Integer realScore;

    /**
     * 交易获得的积分
     */
    private Integer gotScore;

    /**
     * 不可合并拆分
     */
    private Integer isSealed;

    /**
     * 自定义字段
     */
    private String custData;

    /**
     * 从备注提取的业务员
     */
    private Integer xSalesmanId;

    /**
     * 递交后的订单id
     */
    private Integer deliverTradeId;

    /**
     * 是否预订单
     */
    private Integer isPreorder;

    /**
     * 客户id
     */
    private Integer xCustomerId;

    /**
     * 从备注提取的物流id
     */
    private Short xLogisticsId;

    /**
     * 从备注提取的标记
     */
    private Integer xTradeFlag;

    /**
     * 从备注提取的冻结状态
     */
    private Integer xIsFreezed;

    /**
     * 从备注提取的仓库id
     */
    private Short xWarehouseId;

    /**
     * 抓单时已发货，未经系统系统处理的订单
     */
    private Integer isExternal;

    private LocalDateTime modified;

    private LocalDateTime created;



    private static final long serialVersionUID = 1L;
}