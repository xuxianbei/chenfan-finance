package com.chenfan.finance.constant;

/**
 * @author 2062
 */
public class CfFinanceConstant {

    /**
     * 计费方案业务类型
     * 货品采购
     */
    public static final Integer RULE_BILLING_TYPE_FOR_PO = 1;

    /**
     * 计费方案扣费类型
     * RULE_TYPE_FOR_DELAY 延期
     * RULE_TYPE_FOR_QUALITY_TEST 质检
     */
    public static final String RULE_TYPE_FOR_DELAY = "1";
    public static final String RULE_TYPE_FOR_QUALITY_TEST = "2";

    /**
     * 费用来源类型
     * CHARGE_SOURCE_FOR_PO 货品采购
     * CHARGE_SOURCE_FOR_RD 普通采购
     */
    public static final String CHARGE_SOURCE_FOR_PO = "1";
    public static final String CHARGE_SOURCE_FOR_RD = "7";

    /**
     * 日期格式
     */
    public static final String FORMAT_YM = "yyyyMM";

    /**
     * 采购单定金结算状态
     * PO_HS_STATUS_FOR_SETTLED 已结算
     */
    public static final Integer PO_HS_STATUS_FOR_NO_SETTLED = 0;
    public static final Integer PO_HS_STATUS_FOR_SETTLED = 1;

    /**
     * 预付款申请单审核状态
     * ADVANCEPAY_STATE_FOR_PAID 已打款
     */
    public static final Integer ADVANCEPAY_STATE_FOR_PAID = 8;

}
