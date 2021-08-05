package com.chenfan.finance.enums;

/**
 * 费收费用 审核状态枚举类
 * 1草稿、2已提交、3已审核、4已驳回、5已作废、0已删除
 * @author 2062
 */
public enum ChargeCheckStatusEnum {
    /**
     * 已删除
     */
    SC(0, "已删除"),
    /**
     * 草稿
     */
    CG(1, "草稿"),
    /**
     * 已提交
     */
    TJ(2, "已提交"),
    /**
     * 已审核
     */
    SH(3, "已审核"),
    /**
     * 已驳回
     */
    BH(4, "已驳回"),
    /**
     * 已作废
     */
    ZF(5, "已作废"),
    ;

    private int code;
    private String msg;

    ChargeCheckStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据code值获取枚举
     *
     * @param code
     * @return
     */
    public static ChargeCheckStatusEnum getMsgByCode(Integer code) {
        for (ChargeCheckStatusEnum value : ChargeCheckStatusEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
