package com.chenfan.finance.scheduled.bo;

import com.chenfan.finance.enums.TocMappingTypeEnum;

import com.chenfan.finance.model.bo.TocAlipayOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author Wen.Xiao
 * @Description //
 * @Date 2021/4/7  18:44
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TocOriginBo {

    /**
     * 原始支付宝流水
     */
    private TocAlipayOrigin tocAlipayOrigin;

    private BigDecimal postAmount;

    /**
     *  数据结构
     */
    private TocCacheBo tocCacheBo;

    /**
     * @see TocMappingTypeEnum
     */
    private TocMappingTypeEnum tocMappingTypeEnum;
}
