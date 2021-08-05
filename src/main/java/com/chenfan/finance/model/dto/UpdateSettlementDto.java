package com.chenfan.finance.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 更新结算
 *
 * @author: xuxianbei
 * Date: 2021/3/5
 * Time: 10:51
 * Version:V1.0
 */
@Data
public class UpdateSettlementDto {

    /**
     * 普通帐单内部编号
     */
    @NotNull
    private Long invoiceId;

    /**
     * 打款方式
     */
    private Long accountId;

    /**
     * 打款方式
     */
    private String accountName;

    /**
     * 打款类型：1, "红人收款账户" 2, "客户收款账户" 3, "公司账户" 4, "第三方账户"
     */
    private Integer accountType;


    /**
     * 平台手续费金额
     */
    private BigDecimal pricePp;

    /**
     * 结算单模板(1, "内部红人执行单模板";2, "外部红人执行单模板";3, "红人采购费/年度返点/客户返点模板")
     */
    @NotNull
    private Integer settleTemplate;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 标题
     */
    @Length(max = 20, message = "标题内容不能超过20个")
    private String title;

    /**
     * 描述
     */
    @Length(max = 500, message = "描述内容不能超过500个")
    private String description;
}
