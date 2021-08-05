package com.chenfan.finance.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 账单更新-状态
 *
 * @author: xuxianbei
 * Date: 2021/3/4
 * Time: 16:49
 * Version:V1.0
 */
@Data
public class InvoiceUpdateStateDto {

    /**
     * 普通帐单内部编号
     */
    @NotNull
    private Long invoiceId;

    /**
     * 账单状态（1待提交；2待结算；3部分结算；4全部结算；7已作废，0已删除,5 业务待审核,8 待提交财务,9已核销,10审批中,11待打款,12已开票，13已撤回
     */
    @NotNull
    @Range(min = 1, max = 15)
    private Integer invoiceStatus;


    /**
     * 备注
     */
    private String operationContent;
}
