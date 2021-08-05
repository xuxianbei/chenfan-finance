package com.chenfan.finance.model.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * toc_expend_order_stock_out
 * @author 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TocExpendOrderStockOut implements Serializable {
    /**
     * 出库单详情id
     */
    private Long stockOutDetailId;

    /**
     * 出库单id
     */
    private Integer stockOutId;

    /**
     * 出库单号
     */
    private String stockOutNo;

    /**
     * 出库数量
     */
    private BigDecimal stockOutNum;

    /**
     * 出库价格
     */
    private BigDecimal stockOutPrice;

    /**
     * 出库商品总价stock_out_price*stock_out_total_amount
     */
    private BigDecimal stockOutTotalAmount;

    /**
     * 成本价
     */
    private BigDecimal stockOutCostPrice;

    /**
     * goods_name
     */
    private String goodsName;

    /**
     * goods_no
     */
    private String goodsNo;

    /**
     * spec_name
     */
    private String specName;

    /**
     * spec_id
     */
    private Integer specId;

    /**
     * spec_no
     */
    private String specNo;

    /**
     * spec_code
     */
    private String specCode;

    /**
     * 子订单号
     */
    private String oid;

    private static final long serialVersionUID = 1L;
}