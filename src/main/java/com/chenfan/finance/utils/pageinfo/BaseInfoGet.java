package com.chenfan.finance.utils.pageinfo;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author: xuxianbei
 * Date: 2021/3/30
 * Time: 18:29
 * Version:V1.0
 */
public interface BaseInfoGet {

    /**
     * 创建用户id
     */
    Long getCreateBy();

    /**
     * 设置创建用户名称
     */
    String getCreateName();

    /**
     * 设置创建时间
     */
    default LocalDateTime getCreateDate() {
        return LocalDateTime.now();
    }


    /**
     * 更新用户ID
     */
    default Long getUpdateBy() {
        return getCreateBy();
    }

    /**
     * 更新用户人
     */
    default String getUpdateName() {
        return getCreateName();
    }

    /**
     * 更新时间
     */
    default LocalDateTime getUpdateDate() {
        return LocalDateTime.now();
    }


}
