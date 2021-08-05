package com.chenfan.finance.dao;

import com.chenfan.finance.model.CfClearDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 财务_核销费用明细（cf_clear_detail） Mapper 接口
 * </p>
 *
 * @author lywang
 * @since 2020-08-22
 */
public interface CfClearDetailMapper extends BaseMapper<CfClearDetail> {

    /**
     * 根据费用ID集合获取已核销的明细
     * @param chargeIds
     * @return
     */
    List<CfClearDetail> getClearedDetailsByChargeIds(List<Long> chargeIds);
}
