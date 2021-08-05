package com.chenfan.finance.model.dto;

import com.chenfan.finance.model.CfMultyImage;
import com.chenfan.finance.utils.pageinfo.MultyImageVo;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账单更新
 * @author: xuxianbei
 * Date: 2021/3/4
 * Time: 9:45
 * Version:V1.0
 */
@Data
public class InvoiceCommonUpdateDto {

    /**
     * 普通帐单内部编号
     */
    @NotNull
    private Long invoiceId;

    /**
     * 帐单日期
     */
    @NotNull
    private LocalDateTime invoiceDate;

    /**
     * 备注
     */
    @Length(max = 500, message = "输入长度不能超过500个字符")
    private String remark;


    /**
     * 开票方式(1=开票、2=无票、3=后补票)
     */
    @NotNull
    @Range(min = 1, max = 3)
    private Integer customerInvoiceWay;

    /**
     * 合同url
     */
    @Size(max = 4)
    private List<MultyImageVo> contractUrls;
}
