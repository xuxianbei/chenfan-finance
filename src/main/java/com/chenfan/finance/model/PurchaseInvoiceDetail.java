package com.chenfan.finance.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 发票表明细
 * </p>
 *
 * @author lizhejin
 * @since 2020-10-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("purchase_invoice_detail")
public class PurchaseInvoiceDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "purchase_invoice_detail_id")
    private Long purchaseInvoiceDetailId;

    /**
     * 发票主表id
     */
    private Long purchaseInvoiceId;

    /**
     * 存货编码主键
     */
    private Integer inventoryId;

    /**
     * 存货编码(旧:cinvcode)
     */
    private String inventoryCode;

    /**
     * 数量
     */
    private BigDecimal quantity;

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
     * 入库单号
     */
    private String recordCode;

    /**
     * 更新人名称
     */
    private String updateName;

    private Long updateBy;

    /**
     * 更新日期
     */
    private Date updateDate;

    /**
     * 是否删除(0:有效；1:删除)
     */
    private Boolean isDelete;

    /**
     * 入库子表id
     */
    private Long rdRecordDetailId;

    @TableField(exist = false)
    private Integer poId;

    @TableField(exist = false)
    private Long rdId;

    private String inventoryCategory;
}
