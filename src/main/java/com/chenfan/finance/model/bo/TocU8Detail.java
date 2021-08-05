package com.chenfan.finance.model.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * toc_u8_detail
 * @author 
 */
@Data
public class TocU8Detail implements Serializable {
    /**
     * 主键
     */
    private Long detailId;

    /**
     * spu_code
     */
    private String spuCode;

    /**
     * spu_name
     */
    private String spuName;

    /**
     * sku_code
     */
    private String skuCode;

    /**
     * sku_name
     */
    private String skuName;

    /**
     * 本周此sku 销售或退款汇总
     */
    private BigDecimal skuCount;

    /**
     * 此sku 对应的均价（总金额/数量）
     */
    private BigDecimal skuPrice;

    /**
     * 总金额
     */
    private BigDecimal totalMoney;

    private LocalDateTime daozhangt;


    /**
     * toc_u8_header 主键
     */
    private Long mappingId;

    /**
     * 关联的原始销售单子单
     */
    private String oids;

    /**
     * 是否有对应的退款流水
     */
    private Boolean havaRefundFinance;

    private static final long serialVersionUID = 1L;
}