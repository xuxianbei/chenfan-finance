package com.chenfan.finance.model.dto;

import com.chenfan.common.dto.PagerDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/3/17
 * Time: 10:12
 * Version:V1.0
 */
@Data
public class BankAndCashDto extends PagerDTO {


    /**
     * 实收付Ids
     */
    private List<Long> bankAndCashIds;

    /**
     * 收付流水号
     */
    private String recordSeqNo;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 交易方公司名称
     */
    private String paymentBranch;

    /**
     * 实收付状态 状态1=草稿（待提交）, 2=已入账（待核销） 3=部分核销  4=已核销,5=作废，0=已删除, 6审批中，7审批拒绝，8已撤回
     */
    private List<Integer> bankAndCashStatus;

    /**
     * 业务类型 1货品采购; 2销售订单;3MCN
     */
    private Integer jobType;

    /**
     * 收付日期开始
     */
    private LocalDateTime beginArapDate;

    /**
     * 收付日期结束
     */
    private LocalDateTime endArapDate;

    /**
     * v4.9.1
     * 记录类型
     */
    private List<Integer> recordTypes;


}
