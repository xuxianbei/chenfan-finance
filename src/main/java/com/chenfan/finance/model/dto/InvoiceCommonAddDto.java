package com.chenfan.finance.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chenfan.finance.utils.pageinfo.MultyImageVo;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账单新增
 *
 * @author: xuxianbei
 * Date: 2021/3/1
 * Time: 16:24
 * Version:V1.0
 */
@Data
public class InvoiceCommonAddDto {

    /**
     * 费用内部编号id
     */
    @NotNull
    private Long chargeId;

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
    @Valid
    private List<MultyImageVo> contractUrls;


}
