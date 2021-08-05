package com.chenfan.finance.model.bo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * toc_u8_header
 * @author 
 */
@Data
public class TocU8Header implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long mappingId;

    /**
     * 月份 eg202001
     */
    private String month;

    /**
     * 当前月第几周
     */
    private Integer weeknOfMonth;

    /**
     * 类型：0：收入或者 1：支出
     */
    private Integer type;

    /**
     * 品牌id
     */
    private Integer brandId;

    /**
     * 到账时间（假的）
     */
    private LocalDateTime daozhangt;

    private String vtid;

    /**
     * 对应的U8账套
     */
    private String u8db;

    /**
     * U8订单编号
     */
    @JSONField(serialize=false)
    private String orderNo;

    /**
     * 统计开始时间
     */
    @JSONField(serialize=false)
    private LocalDateTime countStartTime;

    /**
     * 统计结束时间
     */
    @JSONField(serialize=false)
    private LocalDateTime countEndTime;

    /**
     * 推送时间
     */
    private LocalDateTime pushTime;

    /**
     * 推送状态
     */
    private Integer pushState;


    @TableField(exist = false)
    private List<TocU8Detail> skuInfos;

    private static final long serialVersionUID = 1L;
}