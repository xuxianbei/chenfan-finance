package com.chenfan.finance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 2062
 */
@AllArgsConstructor
@Getter
public enum MessageEnum {

    /**
     * 待办消息 枚举
     */
    TO_DO_MESSAGE(1, "待办"),
    NOTIFY_MESSAGE(2, "通知");

    private final int notifyType;
    private final String notifyName;

    public int getNotifyType() {
        return this.notifyType;
    }
}
