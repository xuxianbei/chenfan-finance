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
 * 费收_费用(cf_charge）
 * </p>
 *
 * @author lizhejin
 * @since 2020-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_charge_split")
public class CfChargeSplit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "charge_id")
    private Long chargeId;

    /**
     * 费用号
     */
    private String chargeCode;

    /**
     * 费用种类  1货品采购费，2销售货款,3入库费,4出库费,5 仓储费,6行政费用,7其他 ,8延期9质检
     */
    private String chargeType;

    /**
     * 费用审核状态 1草稿、2已提交、3已审核、4已驳回、5已作废、0已删除
     */
    private Integer checkStatus;

    /**
     * 应收/应付类型
     */
    private String arapType;

    /**
     * 应收/应付审核日期
     */
    private LocalDateTime arapCheckDate;

    /**
     * 款号
     */
    private String productCode;

    /**
     * 费用来源类型
     */
    private String chargeSource;

    /**
     * 费用来源单号
     */
    private String chargeSourceCode;

    /**
     * 费用来源明细
     */
    private String chargeSourceDetailCode;

    /**
     * 入库通知单明细
     */
    private Long chargeSourceDetailId;

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
     * 税率
     */
    private BigDecimal taxRate;

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
     * 费用所属年月
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
     * 销售类型(1. 直播; 2. 正常)
     */
    private Integer salesType;

    /**
     * 是否是抵消的费用  0否 /1是
     */
    private Integer isOffset;

    private Long parentId;


}
