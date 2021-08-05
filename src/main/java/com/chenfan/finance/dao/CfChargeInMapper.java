package com.chenfan.finance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.CfChargeIn;
import com.chenfan.finance.model.vo.ChargeInVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 财务_费用生成记录（cf_charge_in） Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Repository
public interface CfChargeInMapper extends BaseMapper<CfChargeIn> {

	/**
	 * chargeIds
	 * @param chargeIds chargeIds
	 * @return List<ChargeInVO>
	 */
	List<ChargeInVO> queryChargeInByChargeIds(@Param("chargeIds") List<Long> chargeIds);

	/**
	 * selectChargeInDetail
	 * @param invoiceId
	 * @return
	 */
    List<ChargeInVO> selectChargeInDetail(Long invoiceId);

	/**
	 * 统计当前chargeId 下每个product_code 的结算数量
	 * @param chargeIds
	 * @return
	 */
	List<ChargeInVO> checkQtyByChargeIds(@Param("chargeIds") List<Long> chargeIds);


	/**
	 * 根据
	 * @param rdRecodeDetailId
	 * @return
	 */
	CfChargeIn selectChargeInByRdRecodeDetailId(@Param("rdRecodeDetailId") long rdRecodeDetailId);
}
