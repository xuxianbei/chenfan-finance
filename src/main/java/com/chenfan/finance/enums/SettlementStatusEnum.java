package com.chenfan.finance.enums;

/**
 * 结算单结算状态 1=待提交财务 5=业务已审  8=作废 ;0=已删除 9, "已核销"
 * //发起结算后，结算明细的初始状态为待开票，
 * 用户点作废按钮后，该条结算明细状态为作废，
 * 财务针对该条结算明细填写发票信息后，该条记录状态变为已核销
 * @author lr
 */
public enum SettlementStatusEnum {
    /**
     * 待提交财务
     */
    FTBS(1,"待提交财务"),
    /**
     * 已删除
     */
    SC(0, "已删除"),
    /**
     * 业务已审核
     */
    DKP(5, "待开票"),

    ZF(8, "已作废"),
    YHX(9, "已核销"),
    YFK(11, "已付款"),
    YSQFK(12,"已申请付款"),
    YZ(-1,"未知"),
    ;

    private int code;
    private String msg;

    SettlementStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据code值获取枚举
     *
     * @param code
     * @return
     */
    public static SettlementStatusEnum getMsgByCode(Integer code) {
        for (SettlementStatusEnum value : SettlementStatusEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return SettlementStatusEnum.YZ;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
