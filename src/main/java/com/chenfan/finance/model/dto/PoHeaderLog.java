package com.chenfan.finance.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author liulingqiong
 * @date 2020-6-24
 */
@Data
public class PoHeaderLog {
    private Integer poLogId;

    private String poCode;

    private Integer type;

    private Long createBy;

    private String createName;

    private Date createDate;

    private String remark;

}