package com.chenfan.finance.enums;

import lombok.Getter;

/**
 * @author: xuxianbei
 * Date: 2021/3/23
 * Time: 14:47
 * Version:V1.0
 */
@Getter
public enum MultyImageEnum {
    INVOICE(1, "账单");
    private Integer code;
    private String msg;

    MultyImageEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
