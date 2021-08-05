package com.chenfan.finance.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 2062
 */
@Data
public class Advancepay {
    private Integer advancePayId;

    private String receiptDepartment;

    private String bank;

    private String payment;

    private String bankAccount;

    private Integer isArrive;

    private Integer enclosure;

    private String paymentUse;

    private String moneyCapital;

    private BigDecimal money;

    private String updateName;

    private Long updateBy;

    private Date updateDate;

    private String accname;
}
