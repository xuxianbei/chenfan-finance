package com.chenfan.finance.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Author Wen.Xiao
 * @Description // 修改采购单详情的价格
 * @Date 2021/6/11  15:09
 * @Version 1.0
 */
@Data
public class UpdatePoDetailPriceDto {
    /**
     * 采购单详情
     */
    @NotNull(message = "采购单详情id不能为空 ")
    private Long poDetailId;
    /**
     * 含税单价
     */
    @NotNull(message = "含税单价不能为空")
    private BigDecimal taxUnitPrice;
    /**
     * 不含税单价
     */
    @NotNull(message = "不含税单价不能为空")
    private BigDecimal unitPrice;
    /**
     * 含税税率
     */
    @NotNull(message = "含税税率不能为空")
    private BigDecimal taxRate;
    /**
     * 加价不含税单价
     */
    @NotNull(message = "加价不含税单价不能为空")
    private BigDecimal markupUnitPrice;

    /**
     * 税额
     * */
    private BigDecimal  taxMoney;

    /**
     * 不含税金额
     * */
    private BigDecimal freeTaxMoney;
    /**
     * 含税金额
     * */
    private BigDecimal includedTaxMoney;

}
