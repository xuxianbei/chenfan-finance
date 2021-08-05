package com.chenfan.finance.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MCN收入合同关联核销
 *
 * @author wulg
 * @date 2021-07-02
 **/
@Data
@ApiModel(value = "MCN收入合同关联核销VO")
public class McnClearVo {

    @ApiModelProperty(value = "核销ID")
    private Long clearId;

    @ApiModelProperty(value = "核销状态 1=未核销， 2=已核销,0=已删除")
    private Integer clearStatus;

    @ApiModelProperty(value = "核销单号")
    private String clearNo;

    @ApiModelProperty(value = "核销类型：AR：收，AP：付")
    private String clearType;

    @ApiModelProperty(value = "核销方式(0=转帐; 1=现金; 2=支票")
    private String clearMethod;

    /**
     * 业务类型(1货品采购; 2销售订单;3MCN)
     */
    private Integer jobType;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "结算主体")
    private String balance;

    @ApiModelProperty(value = "结算主体名称")
    private String balanceName;

    @ApiModelProperty(value = "本次核销总计")
    private BigDecimal nowClearBalance;

    @ApiModelProperty(value = "本次核销余额")
    private BigDecimal nowBalanceBalance;

    @ApiModelProperty(value = "核销人")
    private String clearName;

    @ApiModelProperty("财务人员")
    private String fiUser;

    /**
     * 账单抬头
     */
    @ApiModelProperty(value = "账单抬头")
    private String invoiceTitle;


    /**
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    @ApiModelProperty(value = "财务主体")
    private String financeEntity;

    @ApiModelProperty(value = "实收付流水号")
    private String recordSeqNo;


    @ApiModelProperty(value = "核销时间")
    private LocalDateTime clearDate;

    @ApiModelProperty(value = "创建日期")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "创建人")
    private String createName;

    @ApiModelProperty(value = "驳回理由")
    private String rejectReason;
}
