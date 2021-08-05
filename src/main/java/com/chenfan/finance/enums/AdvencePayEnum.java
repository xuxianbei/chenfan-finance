package com.chenfan.finance.enums;

/**
 * @author liran
 */
public enum AdvencePayEnum {
    /**
     * 未确认
     * */
    NOT_CONFIRM(0, "未确认"),
    /**
     * 已确认
     * */
    CONFIRM(1, "已确认"),
    /**
     * 组长已审核
     * */
    AUDIT(2, "组长已审核"),
    /**
     * 组长已审核
     * */
    SUBMIT(3, "已提交"),
    /**
     * 财务已审核
     * */
    FINANCE_AUDIT(4, "财务已审核"),
    /**
     * 待打款
     * */
    COMPLETE(5, "待打款"),
    /**
     * 已打款
     * */
    PAID(8, "已打款"),
    /**
     * 已驳回
     * */
    REJECT(6, "已驳回"),
    /**
     * 已关闭
     * */
    CLOSE(7, "已关闭");

    private Integer code;
    private String msg;

    AdvencePayEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据code值获取枚举
     *
     * @param code
     * @return
     */
    public static AdvencePayEnum getEnumByCodeAndState(Integer code) {
        for (AdvencePayEnum value : AdvencePayEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
