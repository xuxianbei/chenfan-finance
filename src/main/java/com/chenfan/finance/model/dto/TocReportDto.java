package com.chenfan.finance.model.dto;

import com.chenfan.common.dto.PagerDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description // toc 报表数据请求参数
 * @Date 2021/4/26  11:10
 * @Version 1.0
 */
public class TocReportDto {


    public  static  class  TocReportDtoBase extends PagerDTO{
        @ApiModelProperty("起始时间")
        private LocalDateTime startTime;
        @ApiModelProperty("结束时间")
        private LocalDateTime endTime;
        @ApiModelProperty(hidden = true)
        private List<String> shopAccountList;

        public TocReportDtoBase() {
        }

        public TocReportDtoBase(LocalDateTime startTime, LocalDateTime endTime, List<String> shopAccountList) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.shopAccountList = shopAccountList;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }

        public List<String> getShopAccountList() {
            return shopAccountList;
        }

        public void setShopAccountList(List<String> shopAccountList) {
            this.shopAccountList = shopAccountList;
        }
    }
    /**
     * toc 报表数据请求参数(按周汇总推送的数据)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TocReportByWeek extends TocReportDtoBase{
        /**
         * U8单据号
         */
        @ApiModelProperty("U8单据号")
        private String orderNo;
        @ApiModelProperty("sku")
        private String skuNo;

    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TocReportBySuccess extends TocReportDtoBase{
        /**
         * U8单据号
         */
        @ApiModelProperty("U8单据号")
        private String orderNo;
        @ApiModelProperty("sku")
        private String skuNo;
        @ApiModelProperty("流水编号")
        private String financeNo;
        @ApiModelProperty("是否推送到U8")
        private Boolean havingPush;

    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TocReportByFailure extends TocReportDtoBase {
        /*
         * 流水编号
         */
        @ApiModelProperty("流水编号")
        private String financeNo;
        /**
         * 是失败还是没配（对应含订单数据）
         */
        @ApiModelProperty("是否含有订单数据")
        private Boolean isFail;

    }

}
