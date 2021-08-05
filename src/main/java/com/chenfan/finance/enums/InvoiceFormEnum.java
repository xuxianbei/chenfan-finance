package com.chenfan.finance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author:   tangwei
 * Date:     2021/5/10 16:22
 * Description: 开票形式
 */
@AllArgsConstructor
public enum InvoiceFormEnum {
    NO_NEED_INVOICE(0, "无需开票"),
    UNCERTAIN_INVOICE(1, "开票待定"),
    NEED_INVOICE(2, "开票"),
    ;

    @Getter
    private Integer type;

    @Getter
    private String desc;

    public static InvoiceFormEnum getByType(Integer type) {
        for (InvoiceFormEnum invoiceFormEnum : InvoiceFormEnum.values()) {
            if (invoiceFormEnum.getType().equals(type)) {
                return invoiceFormEnum;
            }
        }
        return null;
    }
}