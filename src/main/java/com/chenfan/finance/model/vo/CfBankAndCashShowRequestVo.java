package com.chenfan.finance.model.vo;

import com.chenfan.common.dto.PagerDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 2062
 */
@Data
@ApiModel(value = "财务-实收付款请求实体")
public class CfBankAndCashShowRequestVo extends PagerDTO{

    @ApiModelProperty(value = "状态值 1草稿，2已提交，3已审核，4已驳回，0已删除")
    private List<Integer> states;

    @ApiModelProperty(value = "结算主体")
    private List<String> balances;

    @ApiModelProperty(value = "收付类型 1实收，2实付，3预收，4预付")
    private List<String> arApTypes;

    @ApiModelProperty(value = "核销状态 0全部核销，1部分核销，2未核销")
    private Integer clearStatus;

    @ApiModelProperty(value = "收付款单号")
    private List<String> recordSeqNos;

    @ApiModelProperty(value = "核销标识，1未核销金额大于0")
    private Integer verificationFlag;

    @ApiModelProperty(value = "记录类型 1支付宝，2微信支付，3现金，4支票InvoiceCommonService，5汇款")
    private List<String> recordTypes;

    @ApiModelProperty(value="收付日期")
    private String arapDate;

    @ApiModelProperty(value = "支票号")
    private List<Integer> checkNos;

    @ApiModelProperty(value="收付日期开始")
    private String arapDateStart;

    @ApiModelProperty(value="收付日期结束")
    private String arapDateEnd;

    @ApiModelProperty(value="品牌Id")
    private Long brandId;

    /**
     * 业务类型(采购1; 销售订单2)
     */
    private Integer jobType;

    private List<Integer> jobTypes;
}
