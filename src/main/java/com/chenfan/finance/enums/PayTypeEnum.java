package com.chenfan.finance.enums;
/**
 * @author liran
 */
public enum PayTypeEnum {
    /**
     * 订金
     * */
    FIRST_PAYMENT(0, "订金"),
    /**
     *尾款
     * */
    FINAL_PAYMENT(1, "尾款"),
    /**
     *一期付款
     * */
    ONE_PAYMENT(2, "一期付款"),
    /**
     *二期付款
     * */
    TWO_PAYMENT(3, "二期付款"),
    /**
     *三期付款
     * */
    THREE_PAYMENT(4, "三期付款"),
    /**
     *四期付款
     * */
    FOUR_PAYMENT(5, "四期付款"),
    /**
     *五期付款
     * */
    FIVE_PAYMENT(6, "五期付款"),
    /**
     *六期付款
     * */
    SIX_PAYMENT(7, "六期付款"),
    /**
     *七期付款
     * */
    SEVEN_PAYMENT(8, "七期付款"),
    /**
     *八期付款
     * */
    EIGHT_PAYMENT(9, "八期付款");

    private Integer value;
    private String msg;

    PayTypeEnum(Integer value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static PayTypeEnum getEnumByValue(Integer value) {
        for (PayTypeEnum payTypeEnum : PayTypeEnum.values()) {
            if (payTypeEnum.getValue().equals(value)) {
                return payTypeEnum;
            }
        }
        return null;
    }

    public static PayTypeEnum getEnumByMsg(String msg) {
        for (PayTypeEnum payTypeEnum : PayTypeEnum.values()) {
            if (payTypeEnum.getMsg().equals(msg)) {
                return payTypeEnum;
            }
        }
        return null;
    }
}
