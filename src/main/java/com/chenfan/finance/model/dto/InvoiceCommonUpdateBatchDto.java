package com.chenfan.finance.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/5/12
 * Time: 15:53
 * Version:V1.0
 */
@Data
public class InvoiceCommonUpdateBatchDto {

    /**
     * 账单IDs
     */
    @Size(min = 1)
    @NotNull
    private List<Long> invoiceIds;

    /**
     * 账单状态 1待提交；2待结算；3部分结算；4全部结算；7已作废，0已删除,5 业务待审核,8 待提交财务,9已核销,10审批中,11待打款待打款,12已开票，13已撤回, 14审批拒绝, 15打款中
     */
    @NotNull
    @Range(min = 1, max = 15)
    private Integer invoiceStatus;
}
