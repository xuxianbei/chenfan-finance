package com.chenfan.finance.model.dto;

import com.chenfan.common.dto.PagerDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 费收_费用(cf_charge）
 * </p>
 *
 * @author lr
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CfChargeListQuery extends PagerDTO {
    /**
     * 状态
     */
    private Integer checkStatus;

    /**
     * 费用号
     */
    private String chargeCode;

    /**
     * 费用种类
     */
    private String chargeType;

    /**
     * 应收/应付类型
     */
    private String arapType;

    /**
     * 费用来源类型
     */
    private Integer chargeSource;

    /**
     * 帐单抬头
     */
    private String invoiceTitle;

    /**
     * 主体
     */
    private String balance;

    /**
     * 品牌
     */
    private Long brandId;

    /**
     * 费用来源明细
     */
    private String chargeSourceDetailCode;

    private String invoiceNo;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date timeBegin;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date timeEnd;


    private boolean invoiceNeed = false;
    /**
     * 账单新增时使用，传递true表示账单新增，可选费用状态为 费用状态为已审核且未被创建账单
     */
    private boolean invoiceCreate = false;

    /**
     * 是否生成账单(0-未生成，1-已生成)
     */
    private Integer isInvoice;

    /**
     * 详情页账单编号
     */
    private String detailInvoiceNo;

    /**
     * 费用id集合
     */
    private List<Long> chargeIds;

    /**
     * 税率（多选）
     */
    private List<BigDecimal> taxRates;

    /**
     * 销售类型
     */
    private Integer salesType;

    /**
     * 费用来源单号
     */
    private String chargeSourceCode;

    /**
     * 发票号
     */
    private String taxInvoiceNo;

    /**
     * 是否填写发票 1: 是； 0：否；
     */
    private Integer taxInvoiceNoFilled;

    private Boolean groupDetail;

    private Boolean export;


    private Integer hk;
    private Integer delay;
    private Integer red;
    private Integer qc;
    /**
     * 入库通知单类型（0成品 1辅料）
     */
    @ApiModelProperty("入库通知单类型（0成品 1辅料）")
    private Integer rdRecordType;

    @ApiModelProperty("是否延期 1：延期，0不延期")
    private Integer havingDelay;


    private List<Long> rdRecordDetailIdList;
}
