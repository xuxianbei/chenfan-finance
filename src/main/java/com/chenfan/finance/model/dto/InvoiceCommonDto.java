package com.chenfan.finance.model.dto;

import com.chenfan.common.dto.PagerDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 列表Dto
 *
 * @author: xuxianbei
 * Date: 2021/3/3
 * Time: 9:48
 * Version:V1.0
 */
@Data
public class InvoiceCommonDto extends PagerDTO {

    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 账单状态（1待提交；2待结算；3部分结算；4全部结算；7已作废，0已删除,5 业务待审核,8 待提交财务,9已核销,10审批中,11待打款,12已开票，13已撤回
     */
    private Integer invoiceStatus;

    /**
     * 应收/应付类型  AR=收；AP=付；
     */
    private String invoiceType;

    /**
     * 业务类型(货品采购1; 销售订单2; 3:MCN)
     */
    private String jobType;

    /**
     * 开始日期
     */
    private LocalDateTime beginDate;

    /**
     * 结束日期
     */
    private LocalDateTime endDate;

    /**
     * 收付日期开始
     */
    private LocalDateTime beginArapDate;

    /**
     * 收付日期结束
     */
    private LocalDateTime endArapDate;

    /**
     * 财务主体
     */
    private List<String> financeEntitys;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 打款平台ID
     */
    private List<Long> accountIds;

}
