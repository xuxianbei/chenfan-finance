package com.chenfan.finance.server.remote.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: weishili
 */
@Data
public abstract class BaseCreateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建人id
     */
    private Long createBy;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}