package com.chenfan.finance.model.bo;

import com.chenfan.common.dto.PagerDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 2062
 */
@Data
@ApiModel
public class CfPoHeaderBo extends PagerDTO {

    /**
     * 采购单号
     */
    @ApiModelProperty(value = "采购单号")
    private Long poId;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String sourceNumber;

    /**
     * 订单类型
     */
    @ApiModelProperty(value = "订单类型")
    private Integer sourceType;

    /**
     * 首/返单
     */
    @ApiModelProperty(value = "首/返单")
    private Integer orderType;

    /**
     * 供应商
     */
    @ApiModelProperty(value = "供应商")
    private Long vendorId;

    /**
     * 下单日期开始
     */
    @ApiModelProperty(value = "下单日期start")
    private String createDateBegin;

    /**
     * 下单日期结束
     */
    @ApiModelProperty(value = "下单日期end")
    private String createDateEnd;

    @ApiModelProperty(value = "采购订单号")
    private String poCode;

    @ApiModelProperty(value = "采购类型(0-成衣，1-辅料)")
    private Integer poType;

    /**
     * 首返单子类型
     */
    @ApiModelProperty(value = "首返单子类型")
    private Integer childOrderType;
}
