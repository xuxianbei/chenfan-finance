package com.chenfan.finance.server.remote.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author liran
 */
@Data
public class BrandFeignVO implements Serializable {
    private static final long serialVersionUID = 5077982699937684047L;

    /**
     *品牌ID
    */
    private Integer brandId;

    /**
     *中文前缀
    */
    private String prefixCh;

    /**
     *品牌名称
    */
    private String brandName;

    /**
     *是否入库新流程（1是  0否）
    */
    private Boolean inboundType;

    /**
     *入库存货类型（0成品 1辅料 2所有）
    */
    private Integer inbInventoryType;

    /**
     *客户ID
    */
    private Long customerId;

    /**
     *加价倍率
    */
    private BigDecimal markupRate;

    /**
     *销售客户
    */
    private String customerName;

    /**
     *店铺类型
    */
    private String brandType;
    /**
     * 收货联系人
     */
    private String receiveName;
    /**
     * 联系电话
     */
    private String receiveTel;
    /**
     * 收货地址
     */
    private String receiveAddress;
    /**
     * 财务主体
     */
    private String financialBody;
    /**
     * 销售税率
     */
    private BigDecimal salesTaxRate;

    /**
     * 状态(0禁用；1启用)
     */
    private Integer state;

    /**
     *品牌code
    */
    private String brandCode;

    /**
     *最低供应商等级
    */
    private String lowestVendorLevel;

    /**
     *英文前缀
    */
    private String prefixEn;
    /**
     * 可设置的到货加急sku数量
     */
    private Integer urgentSKUNum;
}
