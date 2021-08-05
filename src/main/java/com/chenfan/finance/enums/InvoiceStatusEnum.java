package com.chenfan.finance.enums;

/**
 * 账单结算状态（1草稿；2待结算；3部分结算；4全部结算；7已作废，0已删除,5 业务待审核)
 *
 * @author lr
 */
public enum InvoiceStatusEnum {
    /**
     * 已删除
     */
    SC(0, "已删除"),
    /**
     * 草稿
     */
    CG(1, "草稿"),
    /**
     * 业务待审核
     */
    YWDS(5, "业务待审核"),

    TJCW(8, "待提交财务"),
    /**
     * 待结算
     */
    DJS(2, "待结算"),
    /**
     * 部分结算
     */
    BJS(3, "部分结算"),
    /**
     * 全部结算
     */
    QJS(4, "全部结算"),

    ZF(7, "已作废"),
    YHX(9, "已核销"),
    QBFK(12, "全部付款"),
    ;

    private int code;
    private String msg;

    InvoiceStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据code值获取枚举
     *
     * @param code
     * @return
     */
    public static InvoiceStatusEnum getMsgByCode(Integer code) {
        for (InvoiceStatusEnum value : InvoiceStatusEnum.values()) {
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
