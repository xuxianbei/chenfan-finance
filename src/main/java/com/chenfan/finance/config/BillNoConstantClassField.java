package com.chenfan.finance.config;

import com.chenfan.finance.enums.ChargeEnum;

/**
 * finance装用单号类
 * @author
 * @date 2018-02-01
 */
public class BillNoConstantClassField {

    /**
     * 财务-实收付款单号
     */
    public static final String ACTUALPAYMENT = ChargeEnum.ARAP_TYPE_AP.getCode();

    /**
     * 计费方案
     */
    public static final String RULEBILL = "RB";

    /**
     * 核销单
     */
    public static final String CLEAR = "CL";


    /**
     * 费用
     */
    public static final String CRG = "CRG";

    /**
     * 账单
     */
    public static final String INVOICE = "INV";

    /**
     * 发票
     */
    public static final String SETTLE = "QFP";
    
    /**
     * 开票
     */
    public static final String TAX_INVOICE = "FP";
    
    /**
     * 结算付款
     */
    public static final String PAY_APPLY_BUSINESS = "JSFK";

    /**
     * 结算
     * */
    public static final String SETTLEMENT = "JS";


    private BillNoConstantClassField() {
    }
}
