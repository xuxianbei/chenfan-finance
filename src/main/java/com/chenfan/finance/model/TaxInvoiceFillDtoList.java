package com.chenfan.finance.model;

import com.chenfan.finance.model.dto.TaxInvoiceFillDto;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/3/16
 * Time: 10:44
 * Version:V1.0
 */
@Data
public class TaxInvoiceFillDtoList {

    @Valid
    @NotNull
    @Size(min = 1)
    private List<TaxInvoiceFillDto> list;
}
