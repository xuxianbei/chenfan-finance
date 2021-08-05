package com.chenfan.finance.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * <p>
 *  财务_业务单据_入库单 (cf_wdt_rd_record_header) 
 * </p>
 *
 * @author zhaoganlin
 * @since 2020-08-20
 */
@Setter
@Getter
@Builder
@TableName("cf_wdt_rd_record_header")
@ApiModel("财务业务单据入库表")
public class CfWdtRdRecordHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *  主键id 
     */
    @TableId(value = "wdt_rd_record_id")
    private Long wdtRdRecordId;

    /**
     *  旺店通入库单号
     */
    private String wdtRdRecordCode;

    /**
     *  仓库名称
     */
    private String warehouseName;

    /**
     *  品牌id
     */
    private Long brandId;

    /**
     *  品牌名称
     */
    private String brandName;

    /**
     *  审核状态
     */
    private Boolean state;

    /**
     *  备注
     */
    private String remark;

    /**
     *  删除状态
     */
    private Boolean isDelete;

    /**
     *  审核日期
     */
    private LocalDateTime auditDate;

    /**
     *  审核人
     */
    private Long auditBy;

    /**
     *  审核人名称
     */
    private String auditName;

    /**
     *  公司
     */
    private Long companyId;

    /**
     *  创建人
     */
    private Long createBy;

    /**
     *  创建人名称
     */
    private String createName;

    /**
     *  创建时间
     */
    private LocalDateTime createDate;

    /**
     *  更新人
     */
    private Long updateBy;

    /**
     *  更新人名称
     */
    private String updateName;

    /**
     *  更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 是否生成过费用,0未生成 1 已生成
     */
    private Boolean createChargeFlag;


}
