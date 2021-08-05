package com.chenfan.finance.utils.pageinfo;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 基础信息填充
 *
 * @author: xuxianbei
 * Date: 2021/1/17
 * Time: 15:31
 * Version:V1.0
 */
public interface BaseInfoSet {

    /**
     * 设置创建用户id
     *
     * @param userId
     */
    void setCreateBy(Long userId);

    /**
     * 设置创建用户名称
     *
     * @param createName
     */
    void setCreateName(String createName);

    /**
     * 设置创建时间
     *
     * @param localDateTime
     */
    default void setCreateDate(LocalDateTime localDateTime) {

    }


    /**
     * 设置创建时间
     *
     * @param createDate
     */
    default void setCreateDate(Date createDate) {

    }

    /**
     * 更新用户ID
     *
     * @param userId
     */
    default void setUpdateBy(Long userId) {

    }

    /**
     * 更新用户人
     *
     * @param updateName
     */
    default void setUpdateName(String updateName) {

    }

    /**
     * 更新时间
     *
     * @param updateDate
     */
    default void setUpdateDate(LocalDateTime updateDate) {

    }

}
