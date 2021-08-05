package com.chenfan.finance.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.chenfan.finance.enums.OperationBsTypeEnum;
import com.chenfan.finance.enums.OperationTypeEnum;
import lombok.Data;

/**
 * cf_bs_operation_log
 * @author 
 */
@Data
public class CfBsOperationLog implements Serializable {
    /**
     * 住建
     */
    private Long id;

    /**
     * @see OperationTypeEnum
     * 操作类型：创建，编辑，审核，作废，删除 ，撤回，驳回
     */
    private Integer operationType;

    @TableField(exist = false)
    private String  operationTypeName;

    /**
     * @see OperationBsTypeEnum
     * 业务类型：201：实收付款，202：账单管理 203：开票管理
     */
    private Integer businessType;

    /**
     * 业务
     */
    private Long businessId;

    /**
     * 业务编码
     */
    private String businessCode;

    /**
     * 操作时间
     */
    private LocalDateTime operationDate;

    /**
     * 操作人
     */
    private Long operationUserId;

    /**
     * 操作人名称
     */
    private String operationUserName;

    /**
     * 备注
     */
    private String operationContent;

    private static final long serialVersionUID = 1L;
}