package com.chenfan.finance.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rule_billing_detail")
public class RuleBillingDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计费方案明细id
     */
    @TableId(value = "rule_billing_detail_id", type = IdType.AUTO)
    private Long ruleBillingDetailId;

    /**
     * 计费方案id
     */
    private Long ruleBillingId;

    /**
     * 扣费类型（1.延期扣款;2.质检扣款）
     */
    private String ruleType;

    /**
     * 商品范围（0.成衣; 1.辅料）对应页面适用范围
     */
    private Integer goodsRange;

    /**
     * 策略级别对应页面级别
     */
    private String ruleLevel;

    /**
     * 扣费比例
     */
    private BigDecimal deductionRate;

    /**
     * 级别条件对应页面延期天数
     */
    private String ruleLevelCondition;

    /**
     * 状态
     */
    private Integer isDelete;

    /**品牌ids*/
    private String brandIds;
    /**质检项目*/
    private String qcDeductionType;
    /**中类ids*/
    private String  middleClassIds;
    /**商品季节*/
    private String  season;
    /**扣款金额*/
    private BigDecimal  deductionAmount;
}
