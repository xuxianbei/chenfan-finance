package com.chenfan.finance.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 费收_费用(cf_charge）
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CfChargeListVO {

    /**
     * 主键id
     */
    private Long chargeId;

    /**
     * 费用号
     */
    private String chargeCode;
    /**
     * 费用集合
     */
    private String chargeIds;
    private String chargeIdsDelay;
    private String chargeIdsQc;
    private String payIds;
    /**
     * 结算主体集合
     */
    private String balances;
    private String brands;

    /**
     * 费用种类
     */
    private String chargeType;

    /**
     * 费用审核状态
     */
    private Integer checkStatus;


    /**
     * 费用来源类型
     */
    private String chargeSource;


    /**
     * 品牌
     */
    private Long brandId;

    /**
     * 费用来源id
     */
    private String chargeSourceCode;

    /**
     * 费用来源明细id
     */
    private String chargeSourceDetailCode;

    /**
     * 数量
     */
    private Integer chargeQty;


    /**
     * 总价(pp)
     */
    private BigDecimal amountPp;

    @Excel(name = "结算主体",width = 20)
    private String balance;

    @Excel(name = "品牌名称",width = 15)
    private String brandName;

    @Excel(name = "款号",width = 15)
    private String productCode;

    @Excel(name = "商品名称",width = 35)
    private String goodsName;

    @Excel(name = "销售类型")
    private String salesTypeAll;

    @Excel(name = "收/付")
    private String arapType;

    @Excel(name = " 成本价")
    private BigDecimal pricePp;

    @Excel(name = " 采购税率(%)")
    private BigDecimal taxRate;
    /**
     * 前端需要
     */
    private String balanceCode;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    //// 核销页面用
    /**
     * 核销流水号
     */
    private String clearNo;

    /**
     * 实收金额(核销后反写)
     */
    private BigDecimal actualAmount;


    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 余额 总价-实收金额
     */
    private BigDecimal balanceAmount;
    /**
     * 账单列表 显示 应收
     */
    private BigDecimal arAmount;
    /**
     * 账单列表 显示 应付
     */
    private BigDecimal apAmount;

    /**
     * 同一spu、同一来源单号、同一费用种类的sku
     */
    List<CfChargeSkuListVO> ChargeSkuListVO;


    /**
     * 销售类型(1. 直播; 2. 正常)
     */
    private Integer salesType;


    /**
     * 账单日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime invoiceDate;

    /**
     * 核销日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualDate;


    /**
     * 到货数量
     */
    @Excel(name = "到货数量")
    private Integer arrivalQty;

    /**
     * 拒收数量
     */
    @Excel(name = "拒收数量")
    private Integer rejectionQty;

    /**
     * 实收数量
     */
    @Excel(name = "实收数量")
    private Integer actualQty;

    /**
     * 退次数量
     */
    @Excel(name = "退次数量")
    private Integer defectiveRejectionQty;

    /**
     * 本期结算数量
     */
    @Excel(name = "本期结算数量")
    private Integer settlementQty;

    @Excel(name = "货款总金额")
    private BigDecimal settlementAmount;

    /**
     * 延期明细
     */
    private String postponeDetail;

    /**
     * 延期扣款总金额
     */
    @Excel(name = "延期扣款总金额")
    private BigDecimal postponeDeductionsTotal;


    /**
     * 红字扣款
     */
    private BigDecimal redDeductions;

    /**
     * 税差
     */
    private BigDecimal taxDiff;


    /**
     * 延期明细
     */
    private List<Map<String, Integer>> postponeDetailArray;

    @Excel(name = "延期3-5天")
    private String postponeDetail35;
    @Excel(name = "延期6-7天")
    private String postponeDetail67;
    @Excel(name = "延期7天以上")
    private String postponeDetail79;

    @Excel(name = "质检扣款")
    private BigDecimal qaDeductions;


    /**
     * 预付款预付金额(采购单里已结算 尾款+定金)
     */
    @Excel(name = "预付款合同金额")
    private BigDecimal advancepayAmount;

    /**
     * 预付款实付金额 （预付款申请单“已打款”金额和）
     */
    @Excel(name = "预付款实付金额")
    private BigDecimal advancepayAmountActual;


    @Excel(name = "本期应付金额")
    private BigDecimal settlementAmountActual;

    private Boolean export;

    private Set<String> chargeIdAll;
    @Excel(name = "采购类型（0成品 1辅料）", replace = {"成品_0", "辅料_1", " _null"})
    private Integer rdRecordType;

    private Integer chargeSourceType = 0;

    private Integer isOffset;
}
