package com.chenfan.finance.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 实收付批量删除
 * @author: xuxianbei
 * Date: 2021/6/17
 * Time: 16:27
 * Version:V1.0
 */
@Data
public class BankAndCashBatchDeleteDto {

    /**
     * 实收付Ids
     */
    @Size(min = 1)
    @NotNull
    private List<Long> bankAndCashIds;
}
