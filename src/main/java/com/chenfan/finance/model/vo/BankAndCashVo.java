package com.chenfan.finance.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 实收付详情
 * @author: xuxianbei
 * Date: 2021/3/17
 * Time: 10:11
 * Version:V1.0
 */
@Data
public class BankAndCashVo {

    /**
     * 实收付款单内部编号
     */
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
     * 状态1=草稿（待提交）, 2=已入账（待核销） 3=部分核销  4=已核销,5=作废，0=已删除, 6审批中，7审批拒绝，8已撤回
     */
    private Integer bankAndCashStatus;

    /**
     * 收/付日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date arapDate;

    /**
     * 单据收付类型 1实收，2实付，3预收，4预付
     */
    private String arapType;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 核销状态
     */
    private Integer clearStatus;

    /**
     * 总金额
     */
    private BigDecimal amount;


    /**
     * 已核销金额
     */
    private BigDecimal clearAmout;

    /**
     * 余额 未核销金额	 金额-已核销金额（已核销金额取核销号）
     */
    private BigDecimal balanceBalance;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 银行名称
     */
    private String bank;

    /**
     * 银行账号
     */
    private String bankNo;


    /**
     * 交易方公司名称
     */
    private String paymentBranch;

    /**
     * 经办人
     */
    private String recordUser;

    /**
     * 核销人
     */
    private String clearName;

    /**
     * 打款-入账公司--出入账公司信息
     */
    private String payCompany;

    /**
     * 全部审批流
     */
    private List<Long> flowIds;

    /**
     * 当前审批流
     */
    private Long flowId;
}
