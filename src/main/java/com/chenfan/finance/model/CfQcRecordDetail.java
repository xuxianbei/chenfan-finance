package com.chenfan.finance.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * cf_qc_record_detail
 * @author 
 */
@Data
public class CfQcRecordDetail implements Serializable {
    @TableId(value = "qc_charging_id")
    private Long qcChargingId;

    private Integer qcTaskId;

    private String inventoryCode;

    private String productCode;

    private String goodsName;

    private String season;

    private Integer middleClassId;

    private String middleClassName;

    private String qcChargingType;

    /**
     * 总数量
     */
    private Integer qcChargingQty;

    private Integer salesType;

    /**
     * 已处理数量
     */
    private Integer qcQty;

    private static final long serialVersionUID = 1L;
}