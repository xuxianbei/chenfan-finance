package com.chenfan.finance.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *  财务_业务单据_采购单 (cf_po_detail) 
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_po_detail")
public class CfPoDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *  主键id 
     */
    @TableId(value = "po_detail_id")
    private Long poDetailId;

    /**
     *  采购订单主表id
     */
    private Long poId;

    /**
     *  采购订单号
     */
    private String poCode;

    /**
     *  存货编码id
     */
    private Long inventoryId;

    /**
     *  存货编码
     */
    private String inventoryCode;

    /**
     *  货号
     */
    private String productCode;

    /**
     *  数量
     */
    private Integer quantity;

    /**
     *  含税单价
     */
    private BigDecimal taxUnitPrice;

    /**
     *  无税单价
     */
    private BigDecimal unitPrice;

    /**
     *  税率
     */
    private BigDecimal taxRate;

    /**
     *  无税金额
     */
    private BigDecimal freeTaxMoney;

    /**
     *  含税金额
     */
    private BigDecimal includedTaxMoney;

    /**
     *  税额
     */
    private BigDecimal taxMoney;

    /**
     *  币种
     */
    private String exchName;

    /**
     *  合同开始日期
     */
    private LocalDateTime conStartDate;

    /**
     *  合同结束日期 (arrivedate)
     */
    private LocalDateTime conEndDate;

    /**
     *  是否质检
     */
    private Boolean gsp;

    /**
     *  请购单子表id
     */
    private Long appvouchDetailId;

    /**
     *  备注
     */
    private String remark;

    /**
     *  明细状态
     */
    private Integer detailStatus;

    /**
     *  是否删除
     */
    private Boolean isDelete;

    /**
     *  加价不含税单价
     */
    private BigDecimal markupUnitPrice;

    /**
     *  加价倍率
     */
    private BigDecimal markupRate;

    /**
     *  退回单子表id
     */
    private Long returnDetailId;

    /**
     *  修改时间 (
     辅料采购特有，需要成衣采购
     )
     */
    private LocalDateTime updateDate;

    /**
     *  辅料需求单明细主键id
     */
    private Long accessoryRequisitionsInfoId;

    /**
     * 商品名称，对应 inv 存货名称
     */
    private String inventoryName;

    /**
     * 销售类型（1：直播款；2：正常款）
     */
    private Integer salesType;

    /**
     * 到货数量
     */
    private Integer arrivalQty;

    /**
     * 拒收数量
     */
    private Integer rejectionQty;

    /**
     * 实收数量
     */
    private Integer actualQty;

    /**
     * 退次数量
     */
    private Integer defectiveRejectionQty;
}
