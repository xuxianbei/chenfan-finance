package com.chenfan.finance.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * @author liran
 */
@Data
@ApiModel(value = "财务-实收付款列表展示实体")
public class CfBankAndCashShowResponseVo {
    @ApiModelProperty(value = "实收付款单主键id")
    private Long bankAndCashId;

    @ApiModelProperty(value = "收付流水号")
    private String recordSeqNo;

    @ApiModelProperty(value = "记录类型 1支付宝，2微信支付，3现金，4支票，5汇款")
    private String recordType;

    @ApiModelProperty(value = "单据收付类型 1实收，2实付，3预收，4预付")
    private String arapType;

    @ApiModelProperty(value = "收/付日期")
    private Date arapDate;

    @ApiModelProperty(value = "结算主体")
    private String balance;

    @ApiModelProperty(value = "收/付款单位")
    private String collectionUnit;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "支票号")
    private String checkNo;

    @ApiModelProperty(value = "经办人")
    private String recordUser;

    @ApiModelProperty(value = "状态值 1草稿,2已入账(待核销),3部分核销,4已核销,5作废,0已删除")
    private Integer bankAndCashStatus;

    @ApiModelProperty(value = "核销状态 未核销_0 部分核销_1 全部核销_2")
    private Integer clearStatus;

    @ApiModelProperty(value = "未核销金额")
    private BigDecimal balanceBalance;
}
