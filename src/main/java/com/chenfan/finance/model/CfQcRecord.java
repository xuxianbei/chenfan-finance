package com.chenfan.finance.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * cf_qc_record
 * @author 
 */
@Data
public class CfQcRecord implements Serializable {
    @TableId(value = "qc_task_id")
    private Integer qcTaskId;

    private String qcTaskCode;

    private Integer brandId;

    private String brandName;

    private Integer vendorId;

    private String vendorCode;

    private Long createBy;

    private String createName;

    private LocalDateTime createDate;

    private Long companyId;

    private Long rdRecordId;

    private String rdRecordCode;

    private static final long serialVersionUID = 1L;
}