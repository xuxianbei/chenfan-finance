package com.chenfan.finance.enums;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: xuxianbei
 * Date: 2021/3/1
 * Time: 10:34
 * Version:V1.0
 */
@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
@Getter
public enum ChargeCommonEnum {

    CHECK_STATUS_ONE(1, "草稿"),
    CHECK_STATUS_TWO(2, "已提交"),
    CHECK_STATUS_THREE(3, "已审核"),
    CHECK_STATUS_FOUR(4, "已驳回"),
    CHECK_STSTUS_ZERO(0, "已删除"),

    CHARGE_SOURCE_TYPE_HUGE_GOODS(1, "大货采购"),
    CHARGE_SOURCE_TYPE_MCN(3, "MCN"),
    UNDEFINE(999, "未定义"),

    CHARGE_TYPE_HOT_MAN(3, "MCN收入, 收入合同"),
    CHARGE_TYPE_FOUR(4, "红人采购费"),
    CHARGE_TYPE_SIX(6, "平台手续费");

    private Integer code;
    private String desc;

    ChargeCommonEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ChargeCommonEnum valueOfChargeSourceType(int code) {
        for (ChargeCommonEnum value : values()) {
            if (value.name().contains("CHARGE_SOURCE_TYPE")) {
                if (value.getCode() == code) {
                    return value;
                }
            }
        }
        return UNDEFINE;
    }
}
