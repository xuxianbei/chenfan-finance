package com.chenfan.finance.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 费收_费用(cf_charge）
 * </p>
 *
 * @author lr
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CfChargeSaveQuery {

    /**
     * 主键id
     */
    private Long chargeId;
    /**
     * 费用种类
     */
    private String chargeType;

    /**
     * 应收/应付类型
     */
    private String arapType;

    /**
     * 品牌
     */
    private Long brandId;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 备注
     */
    private String remark;


    /**
     * 费用所属年月
     */
    private String chargeMonthBelongTo;


    /**
     * 费用来源类型
     */
    private String chargeSource;

    /**
     * 费用来源code
     */
    private String chargeSourceCode;

    /**
     * sku code
     */
    private String chargeSourceDetailCode;

    /**
     * 数量
     */
    private Integer chargeQty;

    /**
     * 单价(pp)
     */
    private BigDecimal pricePp;

    /**
     * 总价(pp)
     */
    private BigDecimal amountPp;
    /**
     * 单位
     */
    private String chargeUnit;

    /**
     * 对方帐单号
     */
    private String customerInvoiceNo;

    /**
     * 合同号
     */
    private String contractNo;

    /**
     * 应收日期
     */
    private LocalDateTime chargeDate;

    /**
     * 商品款号
     */
    private String productCode;


    /**
     * 销售类型(1. 直播; 2. 正常)
     */
    private Integer salesType;

    /**
     * 税率
     */
    private BigDecimal taxRate;


    /**
     * 是否是抵消的费用  0否 / 1是
     */
    private Integer isOffset;
}
