package com.chenfan.finance.model.dto;


import com.chenfan.finance.model.CfRdRecordDetail;
import com.chenfan.finance.model.RuleBillingDetailQcTask;
import com.chenfan.finance.model.vo.CfPoDetailExtendVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description // 创建质检扣费
 * @Date 2021/7/20  16:32
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class CreateQcChargeDto {


    /**
     * qcTaskDetails
     */
    @JsonProperty("qc_task_details")
    @Size(min = 1)
    @NotNull
    private List<QcTaskDetailsBean> qcTaskDetails;
    /**
     * qcTask
     */
    @JsonProperty("qc_task")
    private QcTaskBean qcTask;
    /**
     * businessCode
     */
    @JsonProperty("business_code")
    private String businessCode;

    /**
     * QcTaskBean
     */
    @NoArgsConstructor
    @Data
    public static class QcTaskBean {
        /**
         * brandId
         */
        @JsonProperty("brandId")
        @NotNull
        private Integer brandId;
        /**
         * brandName
         */
        @JsonProperty("brandName")
        @NotNull
        private String brandName;
        /**
         * companyId
         */
        @JsonProperty("companyId")
        private Integer companyId;
        /**
         * createBy
         */
        @NotNull
        @JsonProperty("createBy")
        private Long createBy;
        /**
         * createDate
         */
        @NotNull
        @JsonProperty("createDate")
        private LocalDateTime createDate;
        /**
         * createName
         */
        @NotNull
        @JsonProperty("createName")
        private String createName;
        /**
         * qcTaskCode
         */
        @NotNull
        @JsonProperty("qcTaskCode")
        private String qcTaskCode;
        /**
         * qcTaskId
         */
        @NotNull
        @JsonProperty("qcTaskId")
        private Integer qcTaskId;
        /**
         * rdRecordCode
         */
        @NotNull
        @JsonProperty("rdRecordCode")
        private String rdRecordCode;
        /**
         * rdRecordId
         */
        @NotNull
        @JsonProperty("rdRecordId")
        private Long rdRecordId;
        /**
         * vendorCode
         */
        @NotNull
        @JsonProperty("vendorCode")
        private String vendorCode;
        /**
         * vendorId
         */
        @NotNull
        @JsonProperty("vendorId")
        private Integer vendorId;
    }

    /**
     * QcTaskDetailsBean
     */
    @NoArgsConstructor
    @Data
    public static class QcTaskDetailsBean {
        /**
         * goodsName
         */
        @NotNull
        @JsonProperty("goodsName")
        private String goodsName;
        /**
         * inventoryCode
         */
        @NotNull
        @JsonProperty("inventoryCode")
        private String inventoryCode;
        /**
         * middleClassId
         */
        @NotNull
        @JsonProperty("middleClassId")
        private Integer middleClassId;
        /**
         * middleClassName
         */
        @NotNull
        @JsonProperty("middleClassName")
        private String middleClassName;
        /**
         * productCode
         */
        @NotNull
        @JsonProperty("productCode")
        private String productCode;
        /**
         * qcChargingId
         */
        @NotNull
        @JsonProperty("qcChargingId")
        private Long qcChargingId;
        /**
         * qcChargingQty
         */
        @NotNull
        @JsonProperty("qcChargingQty")
        private Integer qcChargingQty;
        /**
         * qcChargingType
         */
        @NotNull
        @JsonProperty("qcChargingType")
        private String qcChargingType;
        /**
         * qcTaskId
         */
        @NotNull
        @JsonProperty("qcTaskId")
        private Integer qcTaskId;
        /**
         * salesType
         */
        @NotNull
        @JsonProperty("salesType")
        private Integer salesType;
        /**
         * season
         */
        @NotNull
        @JsonProperty("season")
        private String season;
    }

}
