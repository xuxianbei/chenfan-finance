package com.chenfan.finance.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 财务_核销实收付明细
 * </p>
 *
 * @author lizhejin
 * @since 2021-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_clear_bank_detail")
public class CfClearBankDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 核销单明细id
     */
    @TableId(value = "clear_bank_detail_id", type = IdType.AUTO)
    private Long clearBankDetailId;

    /**
     * 核销单id
     */
    private Long clearId;

    /**
     * 实收付款单内部编号
     */
    private Long bankAndCashId;

    /**
     * 收付流水号
     */
    private String recordSeqNo;

    /**
     * 取值 字典表，待配置（1支付宝，2，微信支付，3现金，4=支票，5汇款）
     */
    private String recordType;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 初始=金额；核销收反写=金额-已核销金额（已核销金额取核销号）
     */
    private BigDecimal balanceBalance;

    /**
     * 上次核销余额
     */
    private BigDecimal lastBalance;

    /**
     * 单据收付类型 1实收，2实付，3预收，4预付
     */
    private String arapType;

    /**
     * 收/付日期
     */
    private LocalDateTime arapDate;

    /**
     * 出账公司
     */
    private String paymentBranch;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;


}
