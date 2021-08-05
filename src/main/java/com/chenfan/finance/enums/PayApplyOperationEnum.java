package com.chenfan.finance.enums;

/**
 * @author mbji
 */

public enum PayApplyOperationEnum {
    /**
     * confirm
     * */
    CONFIRM("confirm"),
    /**
     *close
     * */
    CLOSE("close"),
    /**
     *submit
     * */
    SUBMIT("submit"),
    /**
     *financeAudit
     * */
    FINANCE_AUDIT("financeAudit"),
    /**
     *recheck
     * */
    RECHECK("recheck"),
    /**
     *paid
     * */
    PAID("paid"),
    /**
     *audit
     * */
    AUDIT("audit"),
    /**
     *reject
     * */
    REJECT("reject");

    private String operation;

    PayApplyOperationEnum(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public static PayApplyOperationEnum getEnumByOperation(String operation) {
        for (PayApplyOperationEnum value : PayApplyOperationEnum.values()) {
            if (value.operation.equals(operation)) {
                return value;
            }
        }
        return null;
    }
}
