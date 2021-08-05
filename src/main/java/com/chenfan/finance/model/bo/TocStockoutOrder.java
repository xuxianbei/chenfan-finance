package com.chenfan.finance.model.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * toc_stockout_order
 * @author 
 */
@Data
@TableName("ldb.m_wdt_stockout_order")
public class TocStockoutOrder implements Serializable {
    private Integer stockoutId;

    private String stockoutNo;

    /**
     * 源单据类别 1销售订单, 2调拨出库, 3采购退货出库, 4盘亏出库, 5生产出库, 6现款销售出库, 7其他出库, 8多发出库, 9纠错出库,10保修配件出库, 11初始化出库
     */
    private Integer srcOrderType;

    /**
     * 源单据ID
     */
    private Integer srcOrderId;

    /**
     * 源单号
     */
    private String srcOrderNo;

    /**
     * 外部WMS单号，如科杰
     */
    private String outerNo;

    /**
     * 处理状态
     */
    private Integer wmsStatus;

    /**
     * 接口处理错误信息
     */
    private String errorInfo;

    /**
     * 仓库类别
     */
    private Integer warehouseType;

    /**
     * 出库的仓库id
     */
    private Short warehouseId;

    /**
     * 客户id
     */
    private Long customerId;

    /**
     * 出库单状态 ， 跟订单状态一致
     */
    private Integer status;

    /**
     * 出库单类型  0 普通 1 热销 2 智选批次
     */
    private Integer orderType;

    /**
     * 冻结原因
     */
    private Short freezeReason;

    /**
     * 是否分配stockout_order_detail_position
     */
    private Integer isAllocated;

    /**
     * 出库状态1验货2称重4出库8物流同步,1073741824原始单已完成
     */
    private Integer consignStatus;

    /**
     * 电子面单,获取状态
     */
    private Integer ebillStatus;

    /**
     * 制单人
     */
    private Integer operatorId;

    private BigDecimal goodsCount;

    private Short goodsTypeCount;

    /**
     * MD5字符串值,用于单据排序
     */
    private String md5Str;

    /**
     * 预留字段002
     */
    private BigDecimal rawGoodsCount;

    /**
     * 其他出库自定义子类别0，1，2，3，4
     */
    private Short customType;

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
     * 京东三环内
     */
    private String receiverRing;

    /**
     * 大头笔
     */
    private String receiverDtb;

    /**
     * 配送时间，如周末
     */
    private String toDeliverTime;

    /**
     * 配送中心
     */
    private String preChargeTime;

    /**
     * 币种
     */
    private String currency;

    /**
     * 物流公司ID
     */
    private Short logisticsId;

    /**
     * 货品总售价
     */
    private BigDecimal goodsTotalAmount;

    /**
     * 未知成本销售总额
     */
    private BigDecimal unknownGoodsAmount;

    /**
     * 货品总成本价
     */
    private BigDecimal goodsTotalCost;

    /**
     * 预估邮费成本
     */
    private BigDecimal calcPostCost;

    /**
     * 邮费成本
     */
    private BigDecimal postCost;

    /**
     * 预估重量
     */
    private BigDecimal calcWeight;

    /**
     * 实际称得重量kg
     */
    private BigDecimal weight;

    /**
     * 快递给的重量
     */
    private BigDecimal postWeight;

    /**
     * 包装
     */
    private Integer packageId;

    /**
     * 包装成本
     */
    private BigDecimal packageCost;

    /**
     * 是否包含发票
     */
    private Integer hasInvoice;

    /**
     * 打单员
     */
    private Integer printerId;

    /**
     * 拣货出错次数
     */
    private Integer pickErrorCount;

    /**
     * 拣货操作员ID
     */
    private Integer pickerId;

    /**
     * 包裹分拣员id
     */
    private Integer sorterId;

    /**
     * 验货操作员id
     */
    private Integer examinerId;

    /**
     * 发货人id
     */
    private Integer consignerId;

    /**
     * 打包员
     */
    private Integer packagerId;

    /**
     * 打包积分
     */
    private Integer packScore;

    /**
     * 拣货积分
     */
    private Integer pickScore;

    /**
     * 签出员工id
     */
    private Integer checkouterId;

    /**
     * 监视员
     */
    private Integer watcherId;

    /**
     * 打印批次
     */
    private String batchNo;

    /**
     * 物流单编号
     */
    private String logisticsNo;

    /**
     * 分拣单编号
     */
    private String picklistNo;

    /**
     * 订单在分拣单中序号
     */
    private Short picklistSeq;

    /**
     * 分拣单打印状态 0未打印 1打印中 2已打印 3无需打印
     */
    private Integer picklistPrintStatus;

    /**
     * 物流单打印状态，0未打印 1打印中 2已打印 3无需打印
     */
    private Integer logisticsPrintStatus;

    /**
     * 发货单打印状态，0未打印 1待打中 2已打印 3无需打印
     */
    private Integer sendbillPrintStatus;

    /**
     * 发票打印状态
     */
    private Integer invoicePrintStatus;

    /**
     * 颜色
     */
    private Short flagId;

    /**
     * 出库时间
     */
    private LocalDateTime consignTime;

    /**
     * 截停原因1申请退款2已退款4地址被修改8发票被修改16物流被修改32仓库变化64备注修改128更换货品256取消退款
     */
    private Integer blockReason;

    /**
     * 物流单模板ID
     */
    private Short logisticsTemplateId;

    /**
     * 发货单模板id
     */
    private Short sendbillTemplateId;

    /**
     * 货位分配方式：0系统自动分配, 1手动指定货位分配 2手动指定有效期批次货位 3其它出库优化使用出库货位 4按批次出库
     */
    private Integer posAllocateMode;

    /**
     * 便签条数
     */
    private Short noteCount;

    /**
     * 其他出库原因
     */
    private Integer reasonId;

    /**
     * 锁定策略id
     */
    private Integer lockId;

    /**
     * 保留
     */
    private String reserve;

    /**
     * 备注
     */
    private String remark;

    private LocalDateTime modified;

    private LocalDateTime created;

 /*   private String srcTids;

    *//**
     * 交易单id
     *//*
    private Long tradeId;

    private Integer shopId;
*/
/*    private String batchTime;*/

    private static final long serialVersionUID = 1L;
}