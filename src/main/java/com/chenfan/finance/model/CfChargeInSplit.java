package com.chenfan.finance.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 财务_费用生成记录（cf_charge_in）
 * </p>
 *
 * @author lizhejin
 * @since 2020-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_charge_in_split")
public class CfChargeInSplit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "charge_in_id")
    private Long chargeInId;

    /**
     * 费用id
     */
    private Long chargeId;

    /**
     * 费用方案id
     */
    private Long ruleBillingId;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 供应商id
     */
    private Long vendorId;

    /**
     * sku
     */
    private String inventoryCode;

    /**
     * 款号
     */
    private String productCode;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 销售类型(1. 直播; 2. 正常)
     */
    private Boolean salesType;

    /**
     * 成本价
     */
    private BigDecimal costsPrice;

    /**
     * 采购税率
     */
    private BigDecimal taxRate;

    /**
     * 到货数量
     */
    private Integer arrivalQty;

    /**
     * 拒收数量
     */
    private Integer rejectionQty;

    /**
     * 实收数量
     */
    private Integer actualQty;

    /**
     * 退次数量
     */
    private Integer defectiveRejectionQty;

    /**
     * 延期明细
     */
    private String postponeDetail;

    /**
     * 延期扣款总金额
     */
    private BigDecimal postponeDeductionsTotal;

    /**
     * 质检扣款
     */
    private BigDecimal qaDeductions;

    /**
     * 红字扣款
     */
    private BigDecimal redDeductions;

    /**
     * 税差
     */
    private BigDecimal taxDiff;

    /**
     * 其他扣费
     */
    private BigDecimal othersDeductions;

    /**
     * 备注
     */
    private String remark;

    /**
     * 公司
     */
    private Long companyId;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 更新人名称
     */
    private String updateName;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 预付款预付金额(采购单里已结算 尾款+定金)
     */
    private BigDecimal advancepayAmount;

    /**
     * 预付款实付金额 （预付款申请单“已打款”金额和）
     */
    private BigDecimal advancepayAmountActual;

    private Long parentId;


}
