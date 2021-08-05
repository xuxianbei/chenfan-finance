package com.chenfan.finance.model.vo;

import com.chenfan.finance.model.bo.AdvancePayBo;
import com.chenfan.finance.model.dto.AdvancepayApply;
import lombok.Data;

import java.util.List;

/**
 * @author
 */
@Data
public class AdvancePayToNewFinanceVO {

    /**
     * 包装好的预付款数据
     */
    private List<AdvancepayApply> advancepayApplies;

    /**
     * 预付款新增的入参实体
     */
    private AdvancePayBo bo;
}
