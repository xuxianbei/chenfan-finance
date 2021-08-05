package com.chenfan.finance.model.bo;

import java.io.Serializable;
import lombok.Data;

/**
 * toc_brand_mapping
 * @author 
 */
@Data
public class TocBrandMapping implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 名称
     */
    private String brandName;

    /**
     * 对应的brand_id
     */
    private Integer brandId;

    /**
     * 支付宝账户
     */
    private String shopAlipayAccount;

    /**
     * 状态：0：不可用，1：可用
     */
    private Byte state;

    private static final long serialVersionUID = 1L;
}