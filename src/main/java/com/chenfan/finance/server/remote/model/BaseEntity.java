package com.chenfan.finance.server.remote.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: weishili
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseEntity extends BaseCreateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 修改人id
     */
    private Long updateBy;

    /**
     * 修改人
     */
    private String updateName;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;


    /**
     * 公司id
     */
    private Long companyId;


    /**
     * 租户id
     */
    private Long tenantId;

}