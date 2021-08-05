package com.chenfan.finance.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Wen.Xiao
 * @Description // 退次单
 * @Date 2021/4/23  16:05
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RejectionRetiredDetail {
    private Long rjRetiredDetailId;

    private Integer rjRetiredId;

    private String rjRetiredCode;

    private Integer inventoryId;

    private String inventoryCode;

    private String productCode;

    private BigDecimal quantity;

    private Integer accountState;

    private BigDecimal taxUnitPrice;

    private BigDecimal unitPrice;

    private BigDecimal taxRate;

    private BigDecimal freeTaxMoney;

    private BigDecimal includedTaxMoney;

    private BigDecimal taxMoney;

    private String exchName;

    private String accountCode;

    private String updateName;

    private Long updateBy;

    private Date updateDate;

    private Integer isDelete;

    private BigDecimal markupRate;

    private BigDecimal markupUnitPrice;
}
