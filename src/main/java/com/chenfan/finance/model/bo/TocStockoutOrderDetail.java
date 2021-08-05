package com.chenfan.finance.model.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * toc_stockout_order_detail
 * @author 
 */
@Data
@TableName("ldb.m_wdt_stockout_order_detail")
public class TocStockoutOrderDetail implements Serializable {
    private Long recId;

    private Integer stockoutId;

    /**
     * 源单据类别 1销售订单2,调拨出库3,采购出库4,生产出库5,盘亏出库6,其他出库
     */
    private Integer srcOrderType;

    /**
     * sales_trade_order表的主键
     */
    private Integer srcOrderDetailId;

    /**
     * 基本单位
     */
    private Short baseUnitId;

    /**
     * 辅助单位
     */
    private Short unitId;

    /**
     * 单位换算
     */
    private BigDecimal unitRatio;

    /**
     * 辅助数量
     */
    private BigDecimal num2;

    /**
     * 货品数量
     */
    private BigDecimal num;

    /**
     * 最终价格，share_price删掉了，取代share_price
     */
    private BigDecimal price;

    /**
     * 总货款，新加的，设计漏了
     */
    private BigDecimal totalAmount;

    /**
     * 成本价，开始按平均成本，拣货后重新计算
     */
    private BigDecimal costPrice;

    /**
     * 货品名称,冗余字段
     */
    private String goodsName;

    /**
     * 货品id,冗余字段
     */
    private Integer goodsId;

    /**
     * 货品编号,冗余字段
     */
    private String goodsNo;

    /**
     * 规格名,冗余字段
     */
    private String specName;

    /**
     * 规格id，冗余字段
     */
    private Integer specId;

    /**
     * 商家编码,冗余字段
     */
    private String specNo;

    /**
     * 规格码,冗余字段
     */
    private String specCode;

    /**
     * 估算总重量
     */
    private BigDecimal weight;

    /**
     * 货位，指出库货位
     */
    private Integer positionId;

    /**
     * 指定出库批次
     */
    private Integer batchId;

    /**
     * 指定出库货品有效期
     */
    private Date expireDate;

    /**
     * 是否验货，用于挂起，已标记为验货的不需要再验货
     */
    private Integer isExamined;

    /**
     * 是否包装
     */
    private Integer isPackage;

    /**
     * 是否允许0成本
     */
    private Integer isZeroCost;

    /**
     * 0未验货,1扫描验货,2手工验货
     */
    private Integer scanType;

    private String remark;

    private Date modified;

    private Date created;

/*    private String srcTid;*/



    private static final long serialVersionUID = 1L;
}