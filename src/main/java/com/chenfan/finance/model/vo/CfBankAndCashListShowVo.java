package com.chenfan.finance.model.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * @author liran
 */
@Data
@ApiModel(value = "财务-实收付款请求实体")
public class CfBankAndCashListShowVo {
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
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private String arapDate;

    /**
     * 品牌
     */
    private Integer brandId;

    /**
     * 1草稿,2已入账（待核销),3部分核销,4已核销,5作废,0已删除
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
     * 未核销金额	 金额-已核销金额（已核销金额取核销号）
     */
    private BigDecimal balanceBalance;

    /**
     * 出票日期
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
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
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private Date accountInDate;

    /**
     * 入账确认人
     */
    private String accountInUser;

    /**
     * 出账公司
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
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
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
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    /**
     * 核销状态 0未核销 1部分核销 2全部核销
     */
    private String clearStatus;

    /**
     * 结算主体简称
     */
    private String balanceName;

    /**
     * 业务类型(1货品采购; 2销售订单;3MCN)
     */
    private Integer jobType;
}
