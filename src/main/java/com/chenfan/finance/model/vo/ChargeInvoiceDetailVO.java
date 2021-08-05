package com.chenfan.finance.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 费收_费用(cf_charge）
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-27
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("费收费用表")
@ToString
public class ChargeInvoiceDetailVO implements Serializable {


    private static final long serialVersionUID = -5643067668142261504L;


    /**
     * 主键id
     */
    private Long chargeId;

    private Long invoiceDetailId;

    /**
     * 费用号
     */
    private String chargeCode;

    /**
     * 费用种类
     */
    private String chargeType;

    /**
     * 费用审核状态 1草稿、2已提交、3已审核、4已驳回、5已作废、0已删除
     */
    private Integer checkStatus;

    /**
     * 应收/应付类型  AR=收；AP=付；
     */
    private String arapType;

    /**
     * 应收/应付审核日期
     */
    private LocalDateTime arapCheckDate;

    /**
     * 费用来源类型
     */
    private Integer chargeSource;
    /**
     * 费用来源类型
     */
    private String chargeSourceName;

    /**
     * 费用来源单号
     */
    private String chargeSourceCode;

    /**
     * 费用来源明细
     */
    private String chargeSourceDetailCode;

    /**
     * 品牌
     */
    private Long brandId;

    /**
     * 合同号
     */
    private String contractNo;

    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 计费单位
     */
    private String chargeUnit;

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
     * 结算主体-对应前端账单费用详情的结算主体
     */
    private String balanceName;

    /**
     * 费用期间
     */
    private String chargeMonthBelongTo;

    /**
     * 发票号
     */
    private String taxInvoiceNo;

    /**
     * 发票日期
     */
    private LocalDateTime taxInvoiceDate;

    /**
     * 对方帐单号
     */
    private String customerInvoiceNo;

    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 帐单抬头
     */
    private String invoiceTitle;

    /**
     * 帐单抬头名称
     */
    private String invoiceTitleName;

    /**
     * 帐单日期
     */
    private LocalDateTime invoiceDate;

    /**
     * 核销流水号
     */
    private String clearNo;

    /**
     * 实收金额(核销后反写)
     */
    private BigDecimal actualAmount;

    /**
     * 实收日期
     */
    private LocalDateTime actualDate;

    /**
     * 实收历史日期
     */
    private String actualHistoryDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 应收日期
     */
    private LocalDateTime chargeDate;

    /**
     * 是否费用审核过(1=未审过,2=已审过)
     */
    private Integer chargeHistoryChecked;

    /**
     * 是否费用修改过(1=未修改过,2=已修改过)
     */
    private Integer chargeEdited;

    /**
     * 入账时间
     */
    private LocalDateTime invoiceEntranceDate;

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
    private LocalDateTime createDate;

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
    private LocalDateTime updateDate;

    /**
     * 款号
     */
    private String productCode;

    /**
     * 同一spu、同一来源单号、同一费用种类的sku
     */
    private List<ChargeInvoiceSkuDetailVO> chargeInvoiceDetailVOList;

    /**
     * 销售类型(1. 直播; 2. 正常)
     */
    private Integer salesType;


}
