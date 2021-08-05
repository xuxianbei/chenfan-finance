package com.chenfan.finance.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *  财务_业务单据_入库单明细（cf_wdt_rd_record_detail） 
 * </p>
 *
 * @author lizhejin
 * @since 2020-09-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_wdt_rd_record_detail")
public class CfWdtRdRecordDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "wdt_rd_record_detail_id")
    private Long wdtRdRecordDetailId;

    /**
     * 旺店通入库单主单id
     */
    private Long wdtRdRecordId;

    /**
     * 入库单号
     */
    private Long rdRecordId;

    /**
     * 旺店通入库单号
     */
    private String rdRecordCode;

    /**
     * imp入库单子单id
     */
    private Long rdRecordDetailId;

    /**
     *  存货编码id 
     */
    private Long inventoryId;

    /**
     *  存货编码code 
     */
    private String inventoryCode;

    /**
     *  供应商id 
     */
    private Long vendorId;

    /**
     *  供应商code 
     */
    private String vendorCode;

    /**
     *  税率 
     */
    private BigDecimal taxRate;

    /**
     *  tax含税单价 
     */
    private BigDecimal taxPrice;

    /**
     *  不含税单价 
     */
    private BigDecimal unitPrice;

    /**
     *  数量 
     */
    private Integer quantity;

    /**
     *  箱号 
     */
    private String boxCode;

    /**
     *  删除状态 
     */
    private Boolean isDelete;

    /**
     *  公司 
     */
    private Long companyId;

    /**
     *  创建人 
     */
    private Long createBy;

    /**
     *  创建人名称 
     */
    private String createName;

    /**
     *  创建时间 
     */
    private LocalDateTime createDate;

    /**
     *  更新人 
     */
    private Long updateBy;

    /**
     *  更新人名称 
     */
    private String updateName;

    /**
     *  更新时间 
     */
    private LocalDateTime updateDate;

    /**
     * 采购订单明细表id
     */
    private Long poDetailId;

    /**
     * 采购单号
     */
    private String poCode;

    /**
     * 主键
     */
    private Integer puArrDetailId;

    /**
     * 到货主单ID
     */
    private Integer puArrivalId;

    /**
     * 到货单号
     */
    private String puArrivalCode;

    /**
     * 到货数量(到货单子表 收货数量 )
     */
    private Integer arrivalQty;

    /**
     * 拒收数量(到货单子表 不合格数量 )
     */
    private Integer rejectionQty;

    /**
     * 实收数量(到货单子表 合格数量 )
     */
    private Integer actualQty;


}
