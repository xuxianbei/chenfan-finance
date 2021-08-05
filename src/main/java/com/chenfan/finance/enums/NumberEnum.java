package com.chenfan.finance.enums;

/**
 * @Author Wen.Xiao
 * @Description  常用的数字
 * @Date 2020/12/4  15:35
 * @Version 1.0
 */
public enum  NumberEnum {
    ZERO(0),
    /**1*/
    ONE(1),
    /**2*/
    TWO(2),
    /**3*/
    THREE(3),
    /**4*/
    FOUR(4),
    /**5*/
    FIVE(5),
    /**6*/
    SIX(6),
    /**7*/
    SEVEN(7),
    /**8*/
    EIGHT(8),
    /**9*/
    NINE(9),
    /**10*/
    TEN(10),;
    private int code;
    private String msg;

    NumberEnum(int code) {
        this.code = code;
    }

    NumberEnum(int code, String msg) {
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
}
