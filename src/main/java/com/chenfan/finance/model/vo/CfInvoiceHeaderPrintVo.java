package com.chenfan.finance.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

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
public class CfInvoiceHeaderPrintVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 普通帐单内部编号
     */
    @TableId(value = "invoice_id", type = IdType.AUTO)
    private Long invoiceId;

    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 帐单状态(1=草稿, 2=已提交, 3=业务已审核,4=业务已驳回，5=账单已提交（即待财务审核），6=财务已审核，7=财务已驳回；8=作废；0=已删除)
     */
    private Integer invoiceStatus;

    /**
     * 发票状态(1=未填写、2=未校验、3=已校验)
     */
    private Integer customerInvoiceStatus;

    /**
     * 帐单收付类型
     */
    private String invoiceType;

    /**
     * 业务类型(采购1; 销售订单2)
     */
    private String jobType;

    /**
     * 品牌id
     */
    private Long brandId;
    private String brandName;

    /**
     * 结算主体
     */
    private String balance;
    /**
     * 供应商id
     */
    private Integer vendorId;
    /**
     * 结算主体简称
     */
    private String balanceName;
    /**
     * 帐单抬头
     */
    private String invoiceTitle;

    /**
     * 帐单抬头名称
     */
    private String invoiceTitleName;

    /**
     * 银行
     */
    private String bank;

    /**
     * 银行帐号
     */
    private String bankAccounts;

    /**
     * 账单币种
     */
    private String invoiceCurrencyCode;

    /**
     * 账单汇率
     */
    private BigDecimal invoiceExchangeRate;

    /**
     * 帐单金额-应收总金额
     */
    private BigDecimal invoicelDebit;

    /**
     * 帐单金额-应付总金额
     */
    private BigDecimal invoicelCredit;

    /**
     * 应收付日期
     */
    private LocalDateTime arapDate;

    /**
     * 帐单日期
     */
    private LocalDateTime invoiceDate;

    /**
     * 账单制作人id
     */
    private Long invoiceBy;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人登录名称
     */
    private Long createBy;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 更新人登录名称
     */
    private Long updateBy;

    /**
     * 更新日期
     */
    private LocalDateTime updateDate;

    /**
     * 更新人名称
     */
    private String updateName;

    /**
     * 公司
     */
    private Long companyId;

    /**
     * 核销状态(0=未核销,1=部分核销,2=全部核销)
     */
    private Integer clearStatus;

    /**
     * 核销金额
     */
    private BigDecimal clearAmount;
    /**
     * 调整后延期扣款金额
     */
    private BigDecimal adjustDelayMoney;
    /**
     * 调整后本期实付金额
     */
    private BigDecimal adjustRealMoney;

    /**
     * 推送u8状态
     */
    private Boolean u8State;


    /**
     * 付款申请单打印:日期
     */
    private Date paymentDate;

    /**
     * 付款申请单打印:付款金额
     */
    private BigDecimal paymentMoney;

    /**
     * 付款类型
     */
    private String paymentType;
    /**
     * 付款单号
     */
    private String paymentCode;

    /**
     * 申请人
     */
    private String proposer;
    /**
     * 申请部门
     */
    private String department;
    /**
     * 职位
     */
    private String duties;
    /**
     * 采购类型
     */
    private String poType;

    /**
     * 结算单号
     */
    private String invoiceSettlementNo;

    private Long invoiceSettlementId;

    /**
     * 付款方式(1:汇票,2:汇款,3:现金,4:银行转账,5:支付宝转账)
     */
    private Integer paymentMethod;

    /**
     * 发票是否已给到(0:是,1:否)
     */
    private Integer isInvoice;

    private String paymentRemark;

    private Integer accessory;

    /**
     * 户名
     */
    private String accname;
    /**
     * 收款单位
     */
    private String vendorLetterHead;
    /**
     * 1 直播 /2正常
     */
    private String salesType;

}
