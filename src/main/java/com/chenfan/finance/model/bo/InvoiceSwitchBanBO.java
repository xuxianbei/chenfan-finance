package com.chenfan.finance.model.bo;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author xiongbin
 * @date 2020-08-24
 */
@ToString
@Data
public class InvoiceSwitchBanBO {

    @NotNull(message = "账单id不能为空")
    private Long invoiceId;
    /**
     * 账单结算状态（1草稿；2待结算；3部分结算；4全部结算；7已作废，0已删除,5 业务待审核)
     */
    @NotNull(message = "账单状态不可为空")
    private Integer invoiceStatus;

}
