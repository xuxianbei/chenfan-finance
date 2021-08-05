package com.chenfan.finance.enums;

import com.chenfan.finance.model.CfClearHeader;

import java.util.Objects;

/**
 * @Author Wen.Xiao
 * @Description // 财务操作记录的操作类型枚举
 * 操作类型：创建，编辑，审核，作废，删除 ，撤回，驳回
 * @Date 2021/7/1  15:50
 * @Version 1.0
 */
public enum OperationTypeEnum {

    OPERATION_CREATE(1,"创建"),
    OPERATION_UPDATE(2,"编辑"),
    OPERATION_AUDIT(3,"审批通过"),
    OPERATION_INVALID(4,"作废"),
    OPERATION_DELETE(5,"删除"),
    OPERATION_WITHDRAW(6,"撤回"),
    OPERATION_REJECTED(7,"审批拒绝"),
    OPERATION_MAKE_INVOICE(8,"开票"),
    OPERATION_CLEAR(9,"核销"),
    OPERATION_SUBMIT(10,"提交"),
    OPERATION_FINANCIAL_DISMISSAL(11,"财务驳回"),
    OPERATION_FORCE_BACK(12,"强制驳回")
    ;

    private int code;
    private String msg;

    OperationTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static String getMsgByCode(int code){
        for (OperationTypeEnum op: values()) {
            if(Objects.equals(code,op.getCode())){
                return op.getMsg();
            }
        }
        return null;
    }
    public static OperationTypeEnum getEnumByCode(int code){
        for (OperationTypeEnum op: values()) {
            if(Objects.equals(code,op.getCode())){
                return op;
            }
        }
        return null;
    }
    public static OperationTypeEnum getTypeByTaxInvoiceHeaderEnum(TaxInvoiceHeaderEnum taxInvoiceHeaderEnum){
        switch (taxInvoiceHeaderEnum){
            case TAX_INVOICE_STATUS_ONE:
                return OPERATION_CREATE;
            case TAX_INVOICE_STATUS_TWO:
                return OPERATION_SUBMIT;
            case TAX_INVOICE_STATUS_THREE:
                return OPERATION_REJECTED;
            case TAX_INVOICE_STATUS_FOUR:
                return OPERATION_AUDIT;
            case TAX_INVOICE_STATUS_FIVE:
                return OPERATION_MAKE_INVOICE;
            case TAX_INVOICE_STATUS_SIX:
                return  OPERATION_CLEAR;
            case TAX_INVOICE_STATUS_SEVEN:
                return OPERATION_WITHDRAW;
            case TAX_INVOICE_STATUS_EIGHT:
                return OPERATION_INVALID;
        }
        return null;
    }
    public static OperationTypeEnum getTypeByInvoiceHeaderEnum(InvoiceHeaderEnum invoiceHeaderEnum){
        switch (invoiceHeaderEnum){
            case INVOICE_STATUS_ZERO:
                return OPERATION_DELETE;
            case INVOICE_STATUS_ONE:
                return OPERATION_CREATE;
            case INVOICE_STATUS_SIX:
            case INVOICE_STATUS_ELEVNE:
                return OPERATION_AUDIT;
            case INVOICE_STATUS_SEVEN:
                return OPERATION_INVALID;
            case INVOICE_STATUS_NINE:
                return OPERATION_CLEAR;
            case INVOICE_STATUS_TEN:
                return  OPERATION_SUBMIT;

            case INVOICE_STATUS_TWELVE:
                return OPERATION_MAKE_INVOICE;
            case INVOICE_STATUS_THIRTEEN:
                return OPERATION_WITHDRAW;
            case INVOICE_STATUS_FOUTTEEN:
                return OPERATION_REJECTED;

        }
        return null;
    }

    public static OperationTypeEnum getTypeByBankAndCashEnum(BankAndCashEnum bankAndCashEnum){
        switch (bankAndCashEnum){
            case BANK_AND_CASH_STATUS_ONE:
                return OPERATION_CREATE;
            case BANK_AND_CASH_STATUS_TWO:
                return OPERATION_AUDIT;
            case BANK_AND_CASH_STATUS_THREE:
            case BANK_AND_CASH_STATUS_FOUR:
                return OPERATION_CLEAR;
            case BANK_AND_CASH_STATUS_FIVE:
                return OPERATION_INVALID;
            case BANK_AND_CASH_STATUS_ZERO:
                return OPERATION_DELETE;
            case BANK_AND_CASH_STATUS_SIX:
                return OPERATION_SUBMIT;
            case BANK_AND_CASH_STATUS_SEVEN:
                return OPERATION_REJECTED;
            case BANK_AND_CASH_STATUS_EIGHT:
                return OPERATION_WITHDRAW;

        }
        return null;
    }

    public static OperationTypeEnum getTypeByClearEnum(ClearHeaderEnum clearHeaderEnum){
        switch (clearHeaderEnum){
            case BEFORE_CLEAR:
                return OPERATION_CREATE;
            case CLEAR_STATUS_THREE:
                return OPERATION_AUDIT;
            case CLEAR_STATUS_FOUR:
                return OPERATION_REJECTED;
            case CLEAR_STATUS_SEVEN:
                return  OPERATION_FINANCIAL_DISMISSAL;
            case AFTER_CLEAR:
                return OPERATION_CLEAR;
        }
        return null;
    }
}
