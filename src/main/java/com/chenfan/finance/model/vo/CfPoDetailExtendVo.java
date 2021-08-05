package com.chenfan.finance.model.vo;
import com.chenfan.finance.model.CfPoDetail;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhaoganlin
 * @since 2020-08-25
 */

@Data
@ApiModel("财务采购单明细扩展类")
public class CfPoDetailExtendVo extends CfPoDetail {

    /**
     * 采购类型
     */
    private Integer poType;

    /**
     *  定金
     */
    private BigDecimal bargain;

    /**
     *  尾款
     */
    private BigDecimal retainage;

    /**
     *  定金结算状态
     */
    private Integer hsStatus;

    /**
     * 品牌
     */
    private Long brandId;
    /**
     * 供应商
     */
    private Long vendorId;

}
