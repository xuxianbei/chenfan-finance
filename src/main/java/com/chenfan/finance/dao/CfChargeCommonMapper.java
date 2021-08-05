package com.chenfan.finance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.CfChargeCommon;
import com.chenfan.finance.model.vo.McnChargeCommonVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 费收_费用(cf_charge_common） Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2021-02-27
 */
public interface CfChargeCommonMapper extends BaseMapper<CfChargeCommon> {

    List<McnChargeCommonVO> getMcnCharge(@Param("chargeSourceCode") String chargeSourceCode);

    List<McnChargeCommonVO> getBalanceByClearids(@Param("clearids")List<Long> clearids );

}
