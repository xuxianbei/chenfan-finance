package com.chenfan.finance.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 业务工作流关联表
 * </p>
 *
 * @author lizhejin
 * @since 2021-03-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("approval_flow")
public class ApprovalFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 单据id
     */
    private Long srcId;

    /**
     * 流程id
     */
    private Long processId;

    /**
     * 审批id
     */
    private Long approvalId;

    /**
     * 执行状态,0已结束1进行中
     */
    private Integer activeStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;


}
