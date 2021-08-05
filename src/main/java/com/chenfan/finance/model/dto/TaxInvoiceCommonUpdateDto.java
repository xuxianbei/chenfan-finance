package com.chenfan.finance.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 开票更新
 * @author: xuxianbei
 * Date: 2021/3/5
 * Time: 17:02
 * Version:V1.0
 */
@Data
public class TaxInvoiceCommonUpdateDto {

    /**
     * 普通发票内部编号
     */
    @NotNull
    @ApiModelProperty("普通发票内部编号")
    private Long taxInvoiceId;

    /**
     * 开票内容
     */
    @ApiModelProperty("开票内容")
    private String billingContent;

    /**
     * 开票类型：1普票，2专票
     */
    @ApiModelProperty("开票类型：普票；专票")
    private String taxInvoiceType;

    /**
     * 开票方式(1=开票、2=无票、)
     */
    @NotNull
    @ApiModelProperty("开票方式：1=开票；2=无票")
    private Integer taxInvoiceWay;

    /**
     * 备注
     */
    @Length(max = 500, message = "输入长度不能超过500个字符")
    @ApiModelProperty("备注")
    private String remark;


}
