package com.chenfan.finance.enums;

import java.util.Objects;

/**
 * @Author Wen.Xiao
 * @Description // 财务的业务类型
 * @Date 2021/7/1  15:56
 * @Version 1.0
 */
public enum  OperationBsTypeEnum {
    OPERATION_BS_MCN_CASH(201,"MCN实收付","financialSettlemen/amountPayment","page"),
    OPERATION_BS_MCN_INVOICE(202,"MCN账单","financialSettlemen/mcnAccountant","page"),
    OPERATION_BS_MCN_TAX(203,"MCN开票","invoiceManage","page"),
    OPERATION_BS_MCN_Clear(204,"MCN核销","cfClear","page"),
    ;
    private int code;
    private String msg;
    private String messageUrl;
    private String jumpUrlType;


    OperationBsTypeEnum(int code, String msg, String messageUrl, String jumpUrlType) {
        this.code = code;
        this.msg = msg;
        this.messageUrl = messageUrl;
        this.jumpUrlType = jumpUrlType;
    }

    public String getJumpUrlType() {
        return jumpUrlType;
    }

    public void setJumpUrlType(String jumpUrlType) {
        this.jumpUrlType = jumpUrlType;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

    public static OperationBsTypeEnum getEnumByCode(int code){
        for (OperationBsTypeEnum op:values()) {
            if(Objects.equals(op.getCode(),code)){
                return op;
            }
        }
        return null;
    }
}
