package com.chenfan.finance.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MCN收入合同关联开票
 *
 * @author wulg
 * @date 2021-07-02
 **/
@ApiModel(value = "MCN收入合同关联开票VO")
@Data
public class McnTaxInvoiceVo {

    @ApiModelProperty(value = "普通发票内部编号")
    private Long taxInvoiceId;

    @ApiModelProperty(value = "开票流水号")
    private String taxInvoiceNo;

    @ApiModelProperty(value = "结算主体")
    private String balance;

    @ApiModelProperty(value = "发票状态： 1待提交、2审批中、3待收款、4已开票、5已核销、6已撤回")
    private Integer taxInvoiceStatus;

    @ApiModelProperty(value = "开票抬头")
    private String invoiceTitle;

    @ApiModelProperty(value = "财务主体")
    private String financeEntity;

    @ApiModelProperty(value = "发票号")
    private String invoiceNo;

    @ApiModelProperty(value = "开票金额-应收总金额")
    private BigDecimal invoicelDebit;

    @ApiModelProperty(value = "发票日期")
    private LocalDateTime invoiceDate;

    @ApiModelProperty(value = "核销状态(0=未核销,1=部分核销,2=全部核销)")
    private Integer clearStatus;

    @ApiModelProperty(value = "开票类型：1普票，2专票")
    private String taxInvoiceType;

    @ApiModelProperty(value = "创建日期")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "创建人名称")
    private String createName;
}
