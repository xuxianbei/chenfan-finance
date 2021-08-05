package com.chenfan.finance.model.vo;

import com.chenfan.finance.utils.pageinfo.BaseInfoSet;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 费用-公共
 *
 * @author: xuxianbei
 * Date: 2021/3/1
 * Time: 9:43
 * Version:V1.0
 */
@Data
public class CfChargeCommonVo implements BaseInfoSet {

    /**
     * 费用内部编号id
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
     * 费用种类 1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费
     */
    private Integer chargeType;

    /**
     * AR=收、AP=付；
     */
    private String arapType;

    /**
     * 金额(pp)
     */
    private BigDecimal amountPp;

    /**
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    private String financeEntity;

    /**
     * 帐单抬头
     */
    private String invoiceTitle;

    /**
     * 1= MCN
     */
    private Integer chargeSourceType;

    /**
     * 费用来源单号:1.MCN收入取收入合同编号SR20201231675;2.红人分成费,客户返点费,红人采购费:取执行单号：ZXD20201231675;3.年度返点费 取年度返点申请单号
     */
    private String chargeSourceCode;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 发票号-费用开票后反写；
     */
    private String taxInvoiceNo;

    /**
     * 发票日期
     */
    private LocalDateTime taxInvoiceDate;

    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 帐单日期
     */
    private LocalDateTime invoiceDate;

    /**
     * 已核销金额
     */
    private BigDecimal actualAmount;

    /**
     * 核销余额
     */
    private BigDecimal amountBalance;

    /**
     * 拆分类型： 0；全费用；1：拆分费用
     */
    private Integer splitType;

    /**
     * 分批开票信息
     */
    private List<SplitVo> splitVos;

    /**
     * 兄弟费用信息
     */
    private List<CfChargeCommonBrotherVo> brothercharges;

    /**
     * 0：未核销；1：已核销；2：部分核销
     */
    private Integer clearState;

    /**
     * version:V1.1.2
     * 开票形式：0无需开票；1开票待定；2开票
     */
    @ApiModelProperty("开票形式：0无需开票；1开票待定；2开票")
    private Integer invoiceForm;

    /**
     * version:V1.1.2
     * 开票类型：普票；专票
     */
    @ApiModelProperty("开票类型：普票；专票")
    private String invoiceType;

    /**
     * version:V1.1.2
     * 发票内容
     */
    @ApiModelProperty("发票内容")
    private String invoiceContent;

    /**
     * version:V1.1.2
     * 发票备注
     */
    @ApiModelProperty("发票备注")
    private String invoiceRemark;

    /**
     * version:V1.4.0
     * 费用余额
     */
    @ApiModelProperty("费用余额")
    private BigDecimal overage;
}
