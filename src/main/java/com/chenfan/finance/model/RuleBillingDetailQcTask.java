package com.chenfan.finance.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RuleBillingDetailQcTask extends RuleBillingDetail {

    /**
     * 品牌ids
     */
    private Map<String, String> brandIdsMap;
    /**
     * 中类ids
     */
    private Map<String, String> middleClassIdsMap;
    /**
     * 商品季节
     */
    private Map<String, String> seasonMap;

}
