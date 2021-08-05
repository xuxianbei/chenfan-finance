package com.chenfan.finance.server.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * admin
 */
@Data
public class CompanyNameReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("公司name集合")
    private List<String> names;

}