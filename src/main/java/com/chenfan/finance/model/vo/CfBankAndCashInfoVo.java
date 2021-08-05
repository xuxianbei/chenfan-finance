package com.chenfan.finance.model.vo;

import com.chenfan.finance.model.CfBankAndCash;
import com.chenfan.finance.model.CfBsOperationLog;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liran
 */
@Data
@ApiModel(value = "财务-实收付款详情实体")
public class CfBankAndCashInfoVo extends CfBankAndCash {
    /**
     * 收入
     */
    private BigDecimal inputAmount;

    /**
     * 支出
     */
    private BigDecimal outputAmount;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 核销号集合
     */
    private List<String> clearNos;

    /**
     * 全部审批流
     */
    private List<Long> flowIds;

    /**
     * 当前审批流
     */
    private Long flowId;


    /**
     * 操作日字记录
     */
    private List<CfBsOperationLog> cfBsOperationLogList;
}
