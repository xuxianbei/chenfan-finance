package com.chenfan.finance.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description // 计算当前蓝字账单选择了红字抵充后的红字实付调整后金额请求参数
 * @Date 2021/5/31  15:19
 * @Version 1.0
 */
@Data
public class CalculateRedRealMoneyDto {
    @ApiModelProperty("蓝字账单编号")
    private  Long invoiceId;
    @ApiModelProperty("红字抵充的账单编号列表")
    private List<Long> invoiceIdsOfAssociated;
}
