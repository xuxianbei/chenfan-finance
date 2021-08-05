package com.chenfan.finance.model.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/3/22
 * Time: 13:54
 * Version:V1.0
 */
@Data
public class TaxInvoiceCommonPreAddViewDto {
    /**
     * 费用Id列表
     */
    @NotNull
    @Size(min = 1)
    private List<Long> chargeIds;
}
