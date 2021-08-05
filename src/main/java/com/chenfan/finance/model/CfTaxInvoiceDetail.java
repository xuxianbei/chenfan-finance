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
 * 发票明细
 * </p>
 *
 * @author lizhejin
 * @since 2021-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_tax_invoice_detail")
public class CfTaxInvoiceDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 普通发票明细内部编号
     */
    @TableId(value = "tax_invoice_detail_id", type = IdType.AUTO)
    private Long taxInvoiceDetailId;

    /**
     * 发票id
     */
    private Long taxInvoiceId;

    /**
     * 费用id
     */
    private Long chargeId;

    /**
     * 费用号
     */
    private String chargeCode;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费
     */
    private Integer chargeType;

    /**
     * arap类型(0=ar-收入 1=ap-支出)
     */
    private String arapType;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 单价
     */
    private BigDecimal pricePp;

    /**
     * 数量
     */
    private Integer invoiceQty;

    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 收入
     */
    private BigDecimal invoiceDebit;

    /**
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    private String financeEntity;

    /**
     * 1= MCN
     */
    private Integer chargeSourceType;

    /**
     * 费用来源单号:1.MCN收入取收入合同编号SR20201231675;2.红人分成费,客户返点费,红人采购费:取执行单号：ZXD20201231675;3.年度返点费 取年度返点申请单号
     */
    private String chargeSourceCode;

    /**
     * 备注
     */
    private String remark;

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
    private LocalDateTime createDate;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;


}
