package com.chenfan.finance.model.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 费用(toc_charge）
 * </p>
 *
 * @author lizhejin
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("toc_charge")
public class TocCharge implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 款号
     */
    private String productCode;

    /**
     * 费用来源类型
     */
    private String chargeSource;

    /**
     * 费用来源单号
     */
    private String chargeSourceCode;

    /**
     * 费用来源明细
     */
    private String chargeSourceDetailCode;

    /**
     * 品牌
     */
    private Long brandId;

    /**
     * 店铺id
     */
    private Integer shopId;

    /**
     * 店铺
     */
    private String shopNickname;

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
     * 帐单号
     */
    private String invoiceNo;

    private LocalDateTime receivedDate;


}
