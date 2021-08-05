package com.chenfan.finance.server.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * author:   tangwei
 * Date:     2021/5/14 16:00
 * Description: 财务批量查询关联单据账户信息
 */
@Data
public class AccountInfoQueryDTO implements Serializable {

    private static final long serialVersionUID = 831443357803206644L;

    @ApiModelProperty(value = "1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费")
    private Integer chargeType;

    @ApiModelProperty(value = "来源单号")
    private String chargeSourceCode;

}