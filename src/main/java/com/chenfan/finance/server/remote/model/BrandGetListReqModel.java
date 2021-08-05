package com.chenfan.finance.server.remote.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author SXR
 * @date 2018/7/10
 */
@Data
@Builder
public class BrandGetListReqModel {

    /**
     * 页码 默认为1
     */
    private Integer pageNum = 1;
    /**
     * 每页条数 默认15
     */
    private Integer pageSize = 15;
    /**
     * 品牌id，逗号分隔
     */
    private String brandId;
    /**
     * 品牌编码
     */
    private String brandCode;
    /**
     * 状态(0禁用；1启用)，逗号分隔
     */
    private String state;
    /**
     * 登记人id，逗号分隔
     */
    private String createBy;
    /**
     * 登记时间-开始
     */
    private String createDateBegin;
    /**
     * 登记时间-结束
     */
    private String createDateEnd;
}
