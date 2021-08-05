package com.chenfan.finance.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 创建实收付核销
 *
 * @author: xuxianbei
 * Date: 2021/3/11
 * Time: 14:39
 * Version:V1.0
 */
@Data
public class CreateAndClearDto {

    /**
     * id
     */
    private Integer bankAndCashId;


    /**
     * 收付流水号
     */
    @NotEmpty(groups = {Clear.class, Add.class})
    @Length(groups = {Clear.class, Add.class}, max = 20)
    private String recordSeqNo;

    /**
     * 业务类型：1货品采购; 2销售订单;3MCN
     */
    @NotNull(groups = {Clear.class, Add.class})
    private Integer jobType;

    /**
     * 记录类型 1支付宝，2微信支付，3现金，4支票，5汇款
     */
    @NotEmpty(groups = {Clear.class, Add.class})
    private String recordType;

    /**
     * 单据收付类型 1实收，2实付，3预收，4预付
     */
    @NotNull(groups = {Clear.class, Add.class})
    private String arapType;

    /**
     * 收/付日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @NotNull(groups = {Clear.class, Add.class})
    private Date arapDate;

    /**
     * 经办人
     */
    @NotEmpty(groups = {Clear.class, Add.class})
    private String recordUser;

    /**
     * 总金额
     */
    @NotNull(groups = {Clear.class, Add.class})
    private BigDecimal amount;

    /**
     * 结算主体
     */
    @NotNull(groups = {Clear.class, Add.class})
    private String balance;


    /**
     * 交易方-公司名称
     */
    @NotEmpty(groups = {Clear.class, Add.class})
    private String paymentBranch;

    /**
     * 银行名称
     */
    private String bank;

    /**
     * 银行账号
     */
    private String bankNo;

    /**
     * 备注
     */
    private String remark;


    /**
     * 打款-入账公司
     */
    @NotEmpty(groups = {Clear.class, Add.class})
    private String payCompany;

    /**
     * 打款-入账公司-id
     */
    private String payCompanyId;


    /**
     * 打款-入账银行名称
     */
    @NotEmpty(groups = {Clear.class, Add.class})
    private String payBank;

    /**
     * 打款-入账银行账号
     */
    @NotEmpty(groups = {Clear.class, Add.class})
    private String payBankNo;

    /**
     * 开票流水号
     */
    @Null
    private String taxInvoiceNo;

    /**
     * 账单流水号
     */
    @NotEmpty(groups = {Clear.class})
    private String invoiceNo;

    public interface Add {
    }

    public interface Clear {
    }

}
