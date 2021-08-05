package com.chenfan.finance.model.vo;

import com.chenfan.finance.model.Pay;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 2062
 */
@Data
public class AdvanceVo {

    private BigDecimal bargainRatio;

    private BigDecimal bargain;

    private BigDecimal retainage;

    private BigDecimal retainageRatio;

    private List<Pay> pays;

    private Integer paymentConfigId;

    private Long poId;

    private Integer poType;

    private String poCode;

    private String taskPerson;

    private Date createDate;

    private String receiptDepartment;

    private String bank;

    private String payment;

    private String bankAccount;

    private Integer isArrive;

    private Integer enclosure;

    private String paymentUse;

    private BigDecimal money;

    private String moneyCapital;

    private String department;

    private String managDirector;

    private String accountant;

    private String financechief;

    private String generalmanager;

    private String cashier;

    private Integer advancePayId;

    private String paymentType;

    /**
     * 一期付款金额
     */
    private BigDecimal firstPayment;

    private String departmentName;

    private String account;

    private String roleName;

    private String accname;
}
