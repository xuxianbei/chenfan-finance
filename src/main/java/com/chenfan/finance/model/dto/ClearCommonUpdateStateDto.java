package com.chenfan.finance.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @author: xuxianbei
 * Date: 2021/4/6
 * Time: 11:25
 * Version:V1.0
 */
@Data
public class ClearCommonUpdateStateDto {

    /**
     * 主键id
     */
    @NotNull
    private Long clearId;


    /**
     * 状态，(1=未核销， 2=已核销,0=已删除)3：审批中，4：审批拒绝，5：已撤回，6：已作废
     */
    @NotNull
    @Range(min = 1, max = 6)
    private Integer clearStatus;


    /**
     * v4.9.0
     * 备注
     */
    private String operationContent;
}
