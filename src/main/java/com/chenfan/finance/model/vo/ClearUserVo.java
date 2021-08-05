package com.chenfan.finance.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2020/8/31
 * Time: 15:29
 * Version:V1.0
 */
@ApiModel(value = "财务-核销人")
@Data
public class ClearUserVo {

    /**
     * 核销人id
     */
    @ApiModelProperty(value = "核销人id")
    private Long clearBy;

    /**
     * 核销人名
     */
    @ApiModelProperty(value = "核销人名")
    private String clearName;

}
