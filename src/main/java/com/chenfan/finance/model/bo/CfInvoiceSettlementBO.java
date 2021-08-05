package com.chenfan.finance.model.bo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 账单结算表
 * </p>
 *
 * @author llq
 * @since 2020-10-20
 */
@Data
public class CfInvoiceSettlementBO {

    @NotNull(message = "结算id不能为空")
    private Long invoiceSettlementId;

    /**
     * 要修改的结算状态 5=业务已审  8=作废 ;0=已删除 11 已付款 1:待提交财务
     *      * 结算状态
     *       * : params.row.invoiceSettlementStatus === 1 ? '待提交财务'
     *       * : params.row.invoiceSettlementStatus === 5 ? '待开票'
     *       * : params.row.invoiceSettlementStatus === 8 ? '作废'
     *       * : params.row.invoiceSettlementStatus === 0 ? '已删除'
     *       * : params.row.invoiceSettlementStatus === 9 ? '已核销'
     *       * : params.row.invoiceSettlementStatus === 11 ? '已付款'
     *
     */
    private Integer invoiceSettlementStatus;

}
