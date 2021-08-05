package com.chenfan.finance.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 财务_费用生成记录（cf_charge_in）
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Data
public class ChargeInVO implements Serializable {


    private static final long serialVersionUID = -9182576691368007242L;
    /**
     * 主键id
     */
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
    private Integer salesType;

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
     * 延期明细
     */
    private List<Map<String, Integer>> postponeDetailArray;

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
     * 本期结算数量
     */
    private Integer settlementQty;
    /**
     *  本期实付金额
     */
    private BigDecimal settlementAmount;
    /**
     * 本期账单金额/元
     */
    private BigDecimal actualAmount;


    /**
     * 预付款预付金额
     */
    private BigDecimal advancepayAmount;

    /**
     * 预付款实付金额
     */
    private BigDecimal advancepayAmountActual;


    private Integer invoiceQty;
    /**
     * 货款总金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 结算总件数 （实收-退次）
     */
    private Integer settlementAllQty = 0;

    /**
     * 历史结算数量
     */
    private Integer historySettlementAllQty = 0;

    /**
     * 历史未结算数量  （结算总件数 - 历史结算数量）
     */
    private Integer historyNotSettlementAllQty = 0;


    /**
     *  无税单价
     */
    private BigDecimal unitPrice;
    /**
     *  税额
     */
    private BigDecimal taxMoney;
    /**
     *  加价不含税单价
     */
    private BigDecimal markupUnitPrice;

    /**
     *  加价倍率
     */
    private BigDecimal markupRate;


    /**
     * 不含税总金额/元'
     */
    private BigDecimal unitAmount;

}
