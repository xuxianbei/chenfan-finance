package com.chenfan.finance.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 发票填写
 * @author: xuxianbei
 * Date: 2021/3/4
 * Time: 19:18
 * Version:V1.0
 */
@Data
public class TaxInvoiceFillDto {

    /**
     * 普通帐单内部编号
     */
    @NotNull
    private Long invoiceId;

    /**
     * 备注
     */
    @Length(max = 500, message = "输入长度不能超过500个字符")
    private String remark;

    /**
     * 对方发票号
     */
    @NotEmpty
    @Length(max = 20, message = "发票号不能超过20")
    private String customerInvoiceNo;

    /**
     * 对方发票日期
     */
    @NotNull
    private LocalDateTime customerInvoiceDate;

    /**
     * 财务账期；精确到月
     */
    @NotEmpty
    private String paymentDays;

}
