package com.chenfan.finance.enums;

/**
 * @author mbji
 *         订单类型
 */
public enum OrderTypeEnum {
    /**
     * 首单
     * */
    FIRST(1, "首单"),
    /**
     *返单
     * */
    SECOND(2, "返单"),
    /**
     *超时补录
     * */
    TIME_OUT(3, "超时补录"),
    /**
     *代售
     * */
    PROXY_SELL(4, "代售"),
    /**
     *代修
     * */
    PROXY_FIX(5, "代修"),
    /**
     *溢装
     * */
    OVER_SIZE(6, "溢装"),
    /**
     *首单-上新前加单
     * */
    FIRST_ADD(7, "首单-上新前加单"),
    /**
     *补单
     * */
    ADD(8, "补单"),
    /**
     *返修
     * */
    RETURN_FIX(9, "返修");
    private Integer code;
    private String msg;

    OrderTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static String getMsgByCode(Integer code) {
        for (OrderTypeEnum orderTypeEnum : OrderTypeEnum.values()) {
            if (orderTypeEnum.code.equals(code)) {
                return orderTypeEnum.msg;
            }
        }
        return null;
    }
    public static Integer getValueByMsg(String msg){
        OrderTypeEnum[] values = OrderTypeEnum.values();
        for (OrderTypeEnum typeEnum : values) {
            if (typeEnum.msg.equals(msg)){
                return typeEnum.code;
            }
        }
        return null;
    }
}
