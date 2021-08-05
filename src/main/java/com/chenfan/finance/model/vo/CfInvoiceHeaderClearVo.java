package com.chenfan.finance.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: xuxianbei
 * Date: 2020/9/9
 * Time: 20:36
 * Version:V1.0
 */
@ApiModel(value = "账单-核销关联信息")
@Data
public class CfInvoiceHeaderClearVo {
    @ApiModelProperty(value = "核销流水号")
    private String clearNo;

    @ApiModelProperty(value = "本次核销总计")
    private BigDecimal nowClearBalance;

    @ApiModelProperty(value = "核销时间")
    private LocalDateTime clearDate;
}
