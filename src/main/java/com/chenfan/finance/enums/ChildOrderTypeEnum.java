package com.chenfan.finance.enums;

/**
 * @author mbji.
 * @date 2018/9/5.
 * @time 11:40.
 */
public enum ChildOrderTypeEnum {
    /**
     * 上新前加单
     * */
    BEFORE_NEW_ADD("上新前加单", 1);
    private String msg;

    private Integer code;

    ChildOrderTypeEnum(String msg, Integer code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static Integer getCodeByMsg(String msg) {
        for (ChildOrderTypeEnum childOrderTypeEnum : ChildOrderTypeEnum.values()) {
            if (childOrderTypeEnum.msg.equals(msg)) {
                return childOrderTypeEnum.code;
            }
        }
        return null;
    }

    public static String getMsgByCode(Integer code) {
        for (ChildOrderTypeEnum childOrderTypeEnum : ChildOrderTypeEnum.values()) {
            if (childOrderTypeEnum.code.equals(code)) {
                return childOrderTypeEnum.msg;
            }
        }
        return null;
    }
}
