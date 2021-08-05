package com.chenfan.finance.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 入库单明细
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_rd_record_detail")
public class CfRdRecordDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private Long rdRecordDetailId;

    /**
     * 入库单ID(rd_id)
     */
    private Long rdRecordId;

    /**
     * 到货子单ID（iarrs_id）
     */
    private Long puArrDetailId;

    /**
     * 退次子单ID
     */
    private Long rjRetiredDetailId;

    /**
     * 存货编码主键
     */
    private Integer inventoryId;

    /**
     * 存货编码(旧:cinvcode)
     */
    private String inventoryCode;

    /**
     * 货号(旧:procode)
     */
    private String productCode;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 开票状态0未开票1已开票
     */
    private Integer invoiceState;

    /**
     * 开票金额
     */
    private BigDecimal invoiceMoney;

    /**
     * 开票数量
     */
    private BigDecimal invoiceQuantity;

    /**
     * 发票号
     */
    private String invoiceCode;

    /**
     * 是否删除(0:有效；1:删除)
     */
    private Boolean isDelete;

    /**
     * 实际入库数量
     */
    private Integer actualQuantity;

    /**
     * 含税单价
     */
    private BigDecimal taxUnitPrice;

    /**
     * 无税单价
     */
    private BigDecimal unitPrice;

    /**
     * 税率
     */
    private BigDecimal taxRate;

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

    private Long poDetailId;

    private Long poId;

    private Integer mlQuantity;

    /**
     * 是否生成过费用,0未生成 1 已生成
     */
    private Boolean createChargeFlag;

    /**
     * 推送u8数量
     */
    private Integer pushQuantity;

    /**
     * 入库单
     */
    @TableField(exist = false)
    private CfRdRecord rdRecord;

    @Override
    public String toString() {
        return "CfRdRecordDetail{" +
                "id=" + id +
                ", rdRecordDetailId=" + rdRecordDetailId +
                ", rdRecordId=" + rdRecordId +
                ", puArrDetailId=" + puArrDetailId +
                ", rjRetiredDetailId=" + rjRetiredDetailId +
                ", inventoryId=" + inventoryId +
                ", inventoryCode='" + inventoryCode + '\'' +
                ", productCode='" + productCode + '\'' +
                ", quantity=" + quantity +
                ", invoiceState=" + invoiceState +
                ", invoiceMoney=" + invoiceMoney +
                ", invoiceQuantity=" + invoiceQuantity +
                ", invoiceCode='" + invoiceCode + '\'' +
                ", isDelete=" + isDelete +
                ", actualQuantity=" + actualQuantity +
                ", taxUnitPrice=" + taxUnitPrice +
                ", unitPrice=" + unitPrice +
                ", taxRate=" + taxRate +
                ", inventoryName='" + inventoryName + '\'' +
                ", salesType=" + salesType +
                ", arrivalQty=" + arrivalQty +
                ", rejectionQty=" + rejectionQty +
                ", actualQty=" + actualQty +
                ", poDetailId=" + poDetailId +
                ", poId=" + poId +
                ", mlQuantity=" + mlQuantity +
                ", createChargeFlag=" + createChargeFlag +
                ", pushQuantity=" + pushQuantity +
                ", rdRecord=" + rdRecord +
                '}';
    }
}
