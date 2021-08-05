package com.chenfan.finance.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author lr
 */
@Data
public class ChargeInQuantityMatchVO {

    /**
     * 款号
     */
    private String productCode;
    /**
     * 到货数量
     */
    private Integer arrivalQty;

    /**
     * 拒收数量
     */
    private Integer rejectionQty;

    /**
     * 实收数量
     */
    private Integer actualQty;

    /**
     *  wdt 扫码数量
     */
    private Integer quantity;

}
