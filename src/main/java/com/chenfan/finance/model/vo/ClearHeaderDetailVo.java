package com.chenfan.finance.model.vo;

import com.chenfan.finance.model.CfBsOperationLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 核销明细
 *
 * @author: xuxianbei
 * Date: 2020/8/25
 * Time: 15:47
 * Version:V1.0
 */
@Data
@ApiModel(value = "财务-核销明细")
public class ClearHeaderDetailVo {

    /**
     * 核销信息
     */
    private ClearHeaderDetailBaseVo clearHeaderDetailBase = new ClearHeaderDetailBaseVo();

    /**
     * 实收付款单
     */
    private List<ClearHeaderDetailBankAndCashVo> clearHeaderDetailBankAndCashVos = new ArrayList<>();

    /**
     * 费用明细
     */
    private List<ClearHeaderDetailInvoiceDetailVo> detailInvoiceDetailVos = new ArrayList<>();

    /**
     * 核销费用总计
     */
    private ClearHeaderDetailTotalVo clearHeaderDetailTotalVo = new ClearHeaderDetailTotalVo();

    /**
     * MCN核销日志
     */
    private List<CfBsOperationLog> cfBsOperationLogList;

    /**
     * 全部审批流
     */
    private List<Long> flowIds;

    /**
     * 当前审批流
     */
    private Long flowId;
}
