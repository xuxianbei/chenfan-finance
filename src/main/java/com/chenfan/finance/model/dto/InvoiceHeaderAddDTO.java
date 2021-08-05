package com.chenfan.finance.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description // 费用生成账单的请求参数
 * @Date 2021/4/25  16:07
 * @Version 1.0
 */
@Data
public class InvoiceHeaderAddDTO {
    @ApiModelProperty("关联的费用编号")
    private List<Long> chargeIds;

    /**
     * 结算开始时间
     */
    @ApiModelProperty("结算开始时间")
    private LocalDateTime dateStart;
    /**
     * 结算结束时间
     */
    @ApiModelProperty("结算结束时间")
    private  LocalDateTime dateEnd;



}
