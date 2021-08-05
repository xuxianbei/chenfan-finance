package com.chenfan.finance.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 状态更新
 *
 * @author: xuxianbei
 * Date: 2021/3/9
 * Time: 9:42
 * Version:V1.0
 */
@Data
public class TaxInvoiceCommonUpdateStatusDto {

    /**
     * 普通发票内部编号
     */
    @NotNull
    private Long taxInvoiceId;

    /**
     * 开票状态： 1待提交、2审批中、3审批拒绝、4待开票、5已开票、6已核销、7已撤回、8已作废
     */
    @NotNull
    @Range(min = 1, max = 8)
    private Integer taxInvoiceStatus;


    /**
     * 备注
     */
    private String operationContent;
}
