package com.chenfan.finance.enums;

/**
 * @author llq
 * @date 2020/8/20 14:02
 * @param
 * @return
 */
public enum StateEnum {

    /**
     * 已删除
     * */
    IS_DELETE(0, "已删除"),
    /**
     * 正常
     * */
    CHILD_OPEN(1, "正常"),
    /**
     * 已停用
     * */
    CHILD_CLOSE(2, "已停用"),

    /**
     * 扣费类型1.延期扣款;2.质检扣款
     * */
    DELAY(1, "1"),
    QC(2, "2");

    private int code;
    private String msg;

    StateEnum(int code, String msg) {
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
