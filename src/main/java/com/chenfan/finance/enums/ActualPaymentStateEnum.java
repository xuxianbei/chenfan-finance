package com.chenfan.finance.enums;

/**
 * 财务-实收付款状态枚举
 * @author zq
 * @date 2020/8/21 9:45
 * @param
 * @return
 */
public enum ActualPaymentStateEnum {

    /**
     * 草稿
     */
    DRAFT(1),
    /**
     * 已入账(未核销)
     */
    NOTSTARTED(2),
    /**
     * 部分核销
     */
    PART(3),
    /**
     * 全部核销
     */
    FINISHED(4),
    /**
     * 作废
     */
    CANCELLED(5),
    /**
     * 已删除
     */
    DELETED(0)
    ;

    private int state;

    ActualPaymentStateEnum(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public static ActualPaymentStateEnum getEnumByState(Integer state) {
        for (ActualPaymentStateEnum value : ActualPaymentStateEnum.values()) {
            if (value.state == state) {
                return value;
            }
        }
        return null;
    }
}
