package com.chenfan.finance.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description // 结算单详情
 * @Date 2021/4/23  18:28
 * @Version 1.0
 */
@Data
public class CfInvoiceSettlementInfoVo  extends  CfInvoiceSettlementVo{
    private List<ChargeInVO> chargeInList;
}
