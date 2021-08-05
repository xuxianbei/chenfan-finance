package com.chenfan.finance.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.chenfan.finance.enums.PayTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *  财务_业务单据_采购单 (cf_po_header) 
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_po_header")
public class CfPoHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *  主键id 
     */
    @TableId(value = "po_id", type = IdType.AUTO)
    private Long poId;

    /**
     *  采购订单号 
     */
    private String poCode;

    /**
     *  上新计划下单表下单主表id 
     */
    private Long newPlanOrderMainId;

    /**
     *  供应商主键id 
     */
    private Long vendorId;

    /**
     *  供应商编码 
     */
    private String vendorCode;

    /**
     *  品牌id 
     */
    private Long brandId;

    /**
     *  品牌名称 
     */
    private String brandName;

    /**
     *  审核状态 
     */
    private Boolean state;

    /**
     *  首返单 
     */
    private Integer orderType;

    /**
     *  首返单子类型 
     */
    private Integer childOrderType;

    /**
     *  打印次数 
     */
    private Integer printCount;

    /**
     *  辅料需求单主键id 
     */
    private Long accessoryRequisitionsId;

    /**
     *  辅料需求单编号 
     */
    private String accessoryRequisitionsCode;

    /**
     *  结算单号 
     */
    private String accountBillCode;

    /**
     *  定金结算状态 
     */
    private Integer hsStatus;

    /**
     *  尾款 
     */
    private BigDecimal retainage;

    /**
     *  尾款比 
     */
    private BigDecimal retainageRatio;

    /**
     *  定金 
     */
    private BigDecimal bargain;

    /**
     *  定金比 
     */
    private BigDecimal bargainRatio;

    /**
     *  审核人 
     */
    private Long verifyBy;

    /**
     *  审核人名称 
     */
    private String verifyName;

    /**
     *  审核日期 
     */
    private LocalDateTime verifyDate;

    /**
     *  关闭人 
     */
    private Long closeBy;

    /**
     *  关闭人名称 
     */
    private String closeName;

    /**
     *  关闭日期 
     */
    private LocalDateTime closeDate;

    /**
     *  备注 
     */
    private String remark;

    /**
     *  推送旺店通仓库编码 
     */
    private String cwhCode;

    /**
     *  所属部门 
     */
    private String sysOrgCode;

    /**
     *  所属公司 
     */
    private String sysCompanyCode;

    /**
     *  是否删除 
     */
    private Boolean isDelete;

    /**
     *  八期付款总预付款金额 
     */
    private BigDecimal hirePurchase;

    /**
     *  退回单主键id 
     */
    private Long returnId;

    /**
     *  退回单号 
     */
    private String returnCode;

    /**
     *  采购类型 
     */
    private Integer poType;

    /**
     *  订单号 
     */
    private String sourceNumber;

    /**
     *  订单类型 
     */
    private Boolean sourceType;

    /**
     *  来源订单 
     */
    private Long sourceOrderId;

    /**
     *  公司 
     */
    private Long companyId;

    /**
     *  创建人名称 
     */
    private String createName;

    /**
     *  创建人 
     */
    private Long createBy;

    /**
     *  创建日期 
     */
    private LocalDateTime createDate;

    /**
     *  更新人名称 
     */
    private String updateName;

    /**
     *  更新人 
     */
    private Long updateBy;

    /**
     *  更新日期 
     */
    private LocalDateTime updateDate;

    public CfPoHeader() {

    }
    public CfPoHeader(Long poId, BigDecimal money, BigDecimal bargainRatio,BigDecimal retainageRatio, PayTypeEnum payTypeEnum) {
        this.poId = poId;
        if (PayTypeEnum.FIRST_PAYMENT.equals(payTypeEnum)) {
            this.bargain = money;
            this.bargainRatio = bargainRatio;
        }
        if (PayTypeEnum.FINAL_PAYMENT.equals(payTypeEnum)) {
            this.retainage = money;
            this.retainageRatio = retainageRatio;
        }
    }

}
