package com.chenfan.finance.scheduled.bo;

import com.chenfan.finance.model.bo.TocApiRefund;
import com.chenfan.finance.model.bo.TocApiTrade;
import com.chenfan.finance.model.bo.TocApiTradeOrder;
import com.chenfan.finance.model.bo.TocStockoutOrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description //
 * @Date 2021/4/7  16:55
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TocCacheBo {

    /**
     * 原始订单主单
     */
    private TocApiTrade tocApiTrade;


    /**
     * 原始子单信息
     */
    private List<TocDataCacheOfTrader> tocDataCacheOfTraderList;


    /**
     * 一个原始订单子单对应 <=1 退款单 ，关联>出库量=0
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TocDataCacheOfTrader{
        /**
         * 原始订单子单
         */
        private TocApiTradeOrder tocApiTradeOrder;


        /**
         * 关联的退款单，如果出库单
         */
        private TocApiRefund tocApiRefund;

        /**
         * 一个销售订单子单对应的出库单详情
         */
        private List<TocStockoutOrderDetail>  tocStockOutOrderDetailList;
    }
}
