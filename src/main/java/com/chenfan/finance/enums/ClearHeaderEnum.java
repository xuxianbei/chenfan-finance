package com.chenfan.finance.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @author liran
 */
@Getter
public enum ClearHeaderEnum implements GetCode {

    JOB_TYPE_MCN(3, "MCN"),

    CLEAR_STATUS_THREE(3, "审批中"),
    CLEAR_STATUS_FOUR(4, "审批拒绝,驳回"),
    CLEAR_STATUS_FIVE(5, "已撤回"),
    CLEAR_STATUS_SIX(6, "已作废"),
    CLEAR_STATUS_SEVEN(7, "财务驳回"),
    /**
     * 已删除
     */
    IS_DELETE(0, "已删除"),
    /**
     * 未核销
     */
    BEFORE_CLEAR(1, "未核销, 待提交"),
    /**
     * 已核销
     */
    AFTER_CLEAR(2, "已核销");

    private Integer code;
    private String msg;

    ClearHeaderEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ClearHeaderEnum getByCode(Integer code) {
        for (ClearHeaderEnum clearHeaderEnum : values()) {
            if(clearHeaderEnum.getCode().equals(code)) {
                return clearHeaderEnum;
            }
        }
        return null;
    }

}
