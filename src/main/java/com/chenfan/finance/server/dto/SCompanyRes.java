package com.chenfan.finance.server.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: xuxianbei
 * Date: 2021/3/24
 * Time: 14:33
 * Version:V1.0
 */
@Data
@ApiModel("公司组织-出参")
public class SCompanyRes {

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("上级code，顶级为 -1")
    private String parentCode;

    @ApiModelProperty("上级公司名称")
    private String parentCompanyName;

    @ApiModelProperty("公司code")
    private String companyCode;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("公司类型")
    private String companyType;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建人")
    private String createName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改人")
    private String updateName;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("状态 0：正常 1：禁用")
    private Integer status;

}
