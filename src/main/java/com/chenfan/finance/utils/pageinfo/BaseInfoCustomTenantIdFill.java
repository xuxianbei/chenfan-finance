package com.chenfan.finance.utils.pageinfo;

/**
 * @author: xuxianbei
 * Date: 2021/1/17
 * Time: 16:00
 * Version:V1.0
 */
public interface BaseInfoCustomTenantIdFill extends BaseInfoSet {
    /**
     * 设置租户名称
     *
     * @param tenantId
     */
    default void setTenantId(Long tenantId) {

    };

    /**
     * 公司
     *
     * @param companyId
     */
    void setCompanyId(Long companyId);

}
