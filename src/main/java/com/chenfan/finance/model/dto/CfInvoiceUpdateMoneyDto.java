package com.chenfan.finance.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description // 手动调整账单的金额请求参数
 * @Date 2021/5/31  16:01
 * @Version 1.0
 */
@Data
public class CfInvoiceUpdateMoneyDto {
    @ApiModelProperty("调整后延期金额")
    private BigDecimal adjustDelayMoney;
    @ApiModelProperty("调整后其他扣款金额")
    private BigDecimal adjustOtherMoney;
    @ApiModelProperty("调整后质检扣款金额")
    private BigDecimal adjustQcMoney;
    @ApiModelProperty("调整后红字扣款金额")
    private BigDecimal adjustRedMoney;
    @ApiModelProperty("调整后税差")
    private BigDecimal adjustTaxMoney;
    @ApiModelProperty("蓝字账单Id")
    private Long  invoiceId;
    @ApiModelProperty("蓝字账单号")
    private String invoiceNo;

    @ApiModelProperty("红字抵充的账单编号列表")
    private List<Long> invoiceIdsOfAssociated;
}
