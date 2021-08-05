package com.chenfan.finance.dao;

import com.chenfan.finance.model.AdvancepayApplication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 预付款申请 Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Repository
public interface AdvancepayApplicationMapper extends BaseMapper<AdvancepayApplication> {

    /**
     * 查询审核状态为已打款的预付款申请单集合
     * @param poIds
     * @param state
     * @return
     */
    List<AdvancepayApplication> queryPaidAdvancepayList(@Param("poIds") List<Long> poIds, @Param("state") Integer state);

    /**
     * 预付款单费用记录反写
     * @param advancePayIds 预订单ids
     * @param chargeInId 费用id
     */
    void recordChargeInByAdvancePayIds(@Param("advancePayIds") List<Integer> advancePayIds, @Param("chargeInId") Long chargeInId);

}
