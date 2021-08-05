package com.chenfan.finance.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author: xuxianbei
 * Date: 2021/3/9
 * Time: 10:31
 * Version:V1.0
 */
@Data
public class TaxInvoiceCommonUpdateInvoiceDto {

    /**
     * 普通发票内部编号
     */
    @NotNull
    private Long taxInvoiceId;

    /**
     * 开票流水号
     */
    private String taxInvoiceNo;


    /**
     * 发票号
     */
    @NotEmpty
    private String invoiceNo;

    /**
     * 发票日期
     */
    @NotNull
    private LocalDateTime invoiceDate;

    /**
     * 财务账期；精确到月
     */
    @NotEmpty
    private String paymentDays;

    /**
     * 发票备注
     */
    @Length(max = 500, message = "输入长度不能超过500个字符")
    private String invoiceRemark;

}
