package com.chenfan.finance.model.dto;

import com.chenfan.common.dto.PagerDTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 开票条件查询
 *
 * @author: xuxianbei
 * Date: 2021/3/5
 * Time: 16:58
 * Version:V1.0
 */
@Data
public class TaxInvoiceCommonListDto extends PagerDTO {

    /**
     * 发票号
     */
    private String invoiceNo;


    /**
     * 结算主体
     */
    private String balance;

    /**
     * 开票流水号
     */
    private String taxInvoiceNo;

    /**
     * 开票抬头
     */
    private String invoiceTitle;

    /**
     * 开票状态： 1待提交、2审批中、3审批拒绝、4待开票、5已开票、6已核销、7已撤回、8已作废
     */
    private Integer taxInvoiceStatus;

    /**
     * 核销状态，(1=未核销， 2=已核销,0=已删除)新建都是1，选择实收付款单后变为2。1状态下可以直接删除，变为0
     */
    private Integer clearStatus;

    /**
     * 开始时间
     */
    private LocalDateTime inVoiceBeginDate;

    /**
     * 结束时间
     */
    private LocalDateTime invoiceEndDate;

    /**
     * 创建开始
     */
    private LocalDateTime createBeginDate;

    /**
     * 创建结束
     */
    private LocalDateTime createEndDate;

    /**
     * 来源单号
     */
    private String chargeSourceCode;


}
