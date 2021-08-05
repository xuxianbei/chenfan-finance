package com.chenfan.finance.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;

import com.chenfan.finance.utils.pageinfo.BaseInfoCustomTenantIdFill;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * <p>
 *
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_bank_and_cash")
public class CfBankAndCash implements Serializable, BaseInfoCustomTenantIdFill {

    private static final long serialVersionUID = 1L;

    /**
     * 实收付款单内部编号
     */
    @TableId(value = "bank_and_cash_id", type = IdType.AUTO)
    private Long bankAndCashId;

    /**
     * 收付流水号
     */
    private String recordSeqNo;

    /**
     * 记录类型 1支付宝，2微信支付，3现金，4支票，5汇款
     */
    private String recordType;

    /**
     * 单据收付类型 1实收，2实付，3预收，4预付
     */
    private String arapType;

    /**
     * 收/付日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date arapDate;

    /**
     * 品牌
     */
    private Integer brandId;

    /**
     * 状态1=草稿（待提交）, 2=已入账（待核销） 3=部分核销  4=已核销,5=作废，0=已删除, 6审批中，7审批拒绝，8已撤回
     */
    private Integer bankAndCashStatus;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 收款单位
     */
    private String collectionUnit;

    /**
     * 银行名称
     */
    private String bank;

    /**
     * 银行账号
     */
    private String bankNo;

    /**
     * 总金额
     */
    private BigDecimal amount;

    /**
     * 余额 未核销金额	 金额-已核销金额（已核销金额取核销号）
     */
    private BigDecimal balanceBalance;

    /**
     * 出票日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date checkDate;

    /**
     * 核销号
     */
    private String clearNo;

    /**
     * 支票号
     */
    private String checkNo;

    /**
     * 经办人
     */
    private String recordUser;

    /**
     * 用途
     */
    private String chargesRemark;

    /**
     * 入账日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date accountInDate;

    /**
     * 入账确认人
     */
    private String accountInUser;

    /**
     * 出账公司  交易方-公司名称
     */
    private String paymentBranch;

    /**
     * 出账银行
     */
    private String outgoBank;

    /**
     * 出账银行账号
     */
    private String outgoBankno;

    /**
     * 出账确认人
     */
    private String outgoConfirmUser;

    /**
     * 备注
     */
    private String remark;

    /**
     * 公司
     */
    private Long companyId;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 更新人名称
     */
    private String updateName;

    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    /**
     * 结算主体简称
     */
    private String balanceName;

    @TableField(exist = false)
    private Integer clearStatus;

    @TableField(exist = false)
    private Long invoiceId;

    private String sourceCode;

    /**
     * 打款-入账公司--出入账公司信息
     */
    private String payCompany;

    /**
     * 打款-入账公司--出入账公司信息
     */
    private String payCompanyId;

    /**
     * 打款-入账银行名称--出入账公司信息
     */
    private String payBank;

    /**
     * 打款-入账银行账号--出入账公司信息
     */
    private String payBankNo;

    /**
     * 打款-入账确认人--出入账公司信息
     */
    private String payAccountInUser;

    /**
     * 业务类型(1货品采购; 2销售订单;3MCN)
     */
    private Integer jobType;


}
