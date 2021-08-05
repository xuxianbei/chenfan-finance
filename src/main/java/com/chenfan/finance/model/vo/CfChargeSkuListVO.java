package com.chenfan.finance.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * @author liran
 */
@Data
public class CfChargeSkuListVO {
    /**
     * 主键id
     */
    private Long chargeId;

    /**
     * 费用号
     */
    private String chargeCode;

    /**
     * 费用种类
     */
    private String chargeType;

    /**
     * 费用审核状态
     */
    private Integer checkStatus;

    /**
     * 应收/应付类型
     */
    private String arapType;

    /**
     * 费用来源类型
     */
    private String chargeSource;
    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 品牌
     */
    private Long brandId;

    /**
     * 费用来源id
     */
    private String chargeSourceCode;

    /**
     * 费用来源明细id
     */
    private String chargeSourceDetailCode;

    /**
     * 数量
     */
    private Integer chargeQty;

    /**
     * 单价(pp)
     */
    private BigDecimal pricePp;

    /**
     * 总价(pp)
     */
    private BigDecimal amountPp;

    /**
     * 结算主体
     */
    private String balance;
    /**
     * 前端需要
     */
    private String balanceCode;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    //// 核销页面用
    /**
     * 核销流水号
     */
    private String clearNo;

    /**
     * 实收金额(核销后反写)
     */
    private BigDecimal actualAmount;


    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 余额 总价-实收金额
     */
    private BigDecimal balanceAmount;
    /**
     * 账单列表 显示 应收
     */
    private BigDecimal arAmount;
    /**
     * 账单列表 显示 应付
     */
    private BigDecimal apAmount;

    /**
     * 销售类型(1. 直播; 2. 正常)
     */
    private Integer salesType;

    /**
     * 账单日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime invoiceDate;

    /**
     * 核销日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualDate;

    /**
     * 税率
     */
    private BigDecimal taxRate;
}
