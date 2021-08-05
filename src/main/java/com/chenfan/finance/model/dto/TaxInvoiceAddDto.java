package com.chenfan.finance.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 新增开票
 * @author: xuxianbei
 * Date: 2021/3/5
 * Time: 14:15
 * Version:V1.0
 */
@Data
public class TaxInvoiceAddDto {

    @NotNull
    @Size(min = 1)
    @ApiModelProperty("费用id集合")
    private List<Long> chargeIds;

    /**
     * 开票抬头
     */
    @NotEmpty
    @ApiModelProperty("开票抬头")
    private String invoiceTitle;

    /**
     * 结算主体
     */
    @NotEmpty
    @ApiModelProperty("结算主体")
    private String balance;

    /**
     * 纳税人识别号
     */
    @NotEmpty
    @ApiModelProperty("纳税人识别号")
    private String taxpayerIdentificationNumber;

    /**
     * 开票地址
     */
    @NotEmpty
    @ApiModelProperty("开票地址")
    private String billingAddress;

    /**
     * 开票电话
     */
    @NotEmpty
    @ApiModelProperty("开票电话")
    private String billingTel;

    /**
     * 开户银行
     */
    @NotEmpty
    @ApiModelProperty("开票银行")
    private String billingBank;

    /**
     * 开票账户
     */
    @NotEmpty
    @ApiModelProperty("开票账户")
    private String billingAccount;

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
     * 开票方式(1=开票、2=无票)
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

    /**
     * 发票状态： 开票状态： 1待提交、2审批中、3审批拒绝、4待开票、5已开票、6已核销、7已撤回、8已作废
     */
    private Integer taxInvoiceStatus;
}
