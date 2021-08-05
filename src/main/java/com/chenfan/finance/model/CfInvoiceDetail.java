package com.chenfan.finance.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@TableName("cf_invoice_detail")
public class CfInvoiceDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 普通帐单明细内部编号
     */
    @TableId(value = "invoice_detail_id", type = IdType.AUTO)
    private Long invoiceDetailId;

    /**
     * 账单id
     */
    private Long invoiceId;

    /**
     * 费用id
     */
    private Long chargeId;

    /**
     * 费用名称
     * 费用种类 1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费
     */
    private String chargeType;

    /**
     * (0=ar-收入 1=ap-支出)
     */
    private String arapType;

    /**
     * 费用来源类型
     */
    private Integer chargeSource;

    /**
     * 费用来源id(费用来源单号)
     */
    private String chargeSourceCode;

    /**
     * 费用来源明细id
     */
    private String chargeSourceDetailCode;

    /**
     * 费用号
     */
    private String chargeCode;

    /**
     * 1= MCN
     */
    private Integer chargeSourceType;

    private String productCode;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 单价
     */
    private BigDecimal pricePp;

    /**
     * 计费单位
     */
    private String chargeUnit;

    /**
     * 数量
     */
    private Integer invoiceQty;

    private BigDecimal invoiceDebit;


    /**
     * 支出
     */
    private BigDecimal invoiceCredit;

    /**
     * 备注
     */
    private String remark;

    /**
     * 对方发票号
     */
    private String customerInvoiceNo;

    /**
     * 对方发票日期
     */
    private LocalDateTime customerInvoiceDate;

    /**
     * 财务账期；精确到月
     */
    private String paymentDays;

    /**
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    private String financeEntity;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 创建人名称
     */
    private String createName;
}
