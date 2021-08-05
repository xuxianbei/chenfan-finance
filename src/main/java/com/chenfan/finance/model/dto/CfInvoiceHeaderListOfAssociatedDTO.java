package com.chenfan.finance.model.dto;

import com.chenfan.common.dto.PagerDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description // 红字抵充请求参数
 * @Date 2021/5/31  14:21
 * @Version 1.0
 */
@Data
public class CfInvoiceHeaderListOfAssociatedDTO  extends PagerDTO {
    /**
     * 帐单号
     */
    @ApiModelProperty("帐单号")
    private String invoiceNo;

    /**
     * 结算单号
     */
    @ApiModelProperty("结算单号")
    private String invoiceSettlementNo;

    /**
     * 结算主体
     */
    @ApiModelProperty("结算主体")
    @NotNull(message = "结算主体不能为空")
    private String balance;

    /**
     * 入库通知单类型（0成品 1辅料）
     */
    @ApiModelProperty("入库通知单类型（0成品 1辅料）")
    @NotNull(message ="采购类型不能为空" )
    private Integer rdRecordType;

    @ApiModelProperty("品牌ID")
    @NotNull(message = "品牌不能为空")
    private Integer brandId;

    @ApiModelProperty(hidden = true)
    private List<String> jobTypes;

    /**
     * 销售类型
     */
    @ApiModelProperty("销售类型")
    @NotNull(message = "请求参数：销售类型不能为空")
    private Integer salesType;

    @ApiModelProperty("蓝字账单编号")
    @NotBlank(message = "请求参数：蓝字账单编号不能为空")
    private String associatedInvoiceNo;
}
