package com.chenfan.finance.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author Wen.Xiao
 * @Description // 原到货单与财务入库单差异
 * @Date 2021/1/20  14:10
 * @Version 1.0
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CfMonitoringOrderVo {
    private Integer id;

    private String rdRecordCode;
    /**
     * 存货编码主键
     */
    private Integer inventoryId;

    /**
     * 存货编码(旧:cinvcode)
     */
    private String inventoryCode;

    /**
     * 货号(旧:procode)
     */
    private String productCode;

    /**
     * 数量
     */
    private Integer quantity;
    private Integer cfQuantity;
    private Integer plmQuantity;


    private Integer accountQuantity;
    private Integer cfAccountQuantity;
    private Integer plmAccountQuantity;

    /**
     * 含税单价
     */
    private BigDecimal taxUnitPrice;

    private BigDecimal cfTaxUnitPrice;

    private BigDecimal plmTaxUnitPrice;


    /**
     * 无税单价
     */
    private BigDecimal unitPrice;
    /**
     * 无税单价
     */
    private BigDecimal cfUnitPrice;
    /**
     * 无税单价
     */
    private BigDecimal plmUnitPrice;

    /**
     * 拒收数量
     */
    private Integer unqualifiedQuantity;
    private Integer cfUnqualifiedQuantity;
    private Integer plmUnqualifiedQuantity;


    /**
     * 税率
     */
    private BigDecimal taxRate;
    private BigDecimal cfTaxRate;
    private BigDecimal plmTaxRate;

}
