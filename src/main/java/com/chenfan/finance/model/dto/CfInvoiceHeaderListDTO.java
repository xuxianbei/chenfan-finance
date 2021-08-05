package com.chenfan.finance.model.dto;

import com.chenfan.common.dto.PagerDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author xiongbin
 * @date 2020-08-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class CfInvoiceHeaderListDTO extends PagerDTO {
    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 业务类型(采购1; 销售订单2)
     */
    private String jobType;

    private List<String> jobTypes;

    /**
     * 帐单收付类型
     */
    private String invoiceType;

    /**
     * 帐单状态(1=草稿, 2=已提交, 3=业务已审核,4=业务已驳回，5=账单已提交（即待财务审核），6=财务已审核，7=财务已驳回；8=作废；0=已删除)
     * <p>
     * 账单结算状态（1草稿；2待结算；3部分结算；4全部结算；7已作废，0已删除)--现在
     */
    private Integer invoiceStatus;

    /**
     * 核销状态(0=未核销,1=部分核销,2=全部核销)
     */
    private Integer clearStatus;

    private String balance;

    private String createDateBegin;

    private String createDateEnd;

    /**
     * 品牌id多选
     */
    private List<Integer> brandIdz;

    /**
     * 结算单号
     */
    private String invoiceSettlementNo;

    /**
     * 结算开始时间
     */
    private String balanceDateStart;

    /**
     * 结算结束时间
     */
    private String balanceDateEnd;

    /**
     * 入库通知单类型（0成品 1辅料）
     */
    @ApiModelProperty("入库通知单类型（0成品 1辅料）")
    private Integer rdRecordType;

    @ApiModelProperty("商品款号")
    private String productCode;


}
