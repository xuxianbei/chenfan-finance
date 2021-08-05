package com.chenfan.finance.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 费压公共更新
 * @author: xuxianbei
 * Date: 2021/3/1
 * Time: 10:30
 * Version:V1.0
 */
@Data
public class ChargeCommonUpdateDto {

    /**
     * 费用内部编号id
     */
    @NotNull
    @Size(min = 1)
    private List<Long> chargeIds;

    /**
     * 费用审核状态 1草稿、2已提交、3已审核、4已驳回、5已作废、0已删除
     */
    private Integer checkStatus;

}
