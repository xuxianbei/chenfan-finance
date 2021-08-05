package com.chenfan.finance.dao;

import com.chenfan.finance.model.RuleBillingDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import feign.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Repository
public interface RuleBillingDetailMapper extends BaseMapper<RuleBillingDetail> {

    /**
     * deleteByPrimaryKey
     * @param ruleBillingDetailId ruleBillingDetailId
     * @return int
     */
    int deleteByPrimaryKey(Long ruleBillingDetailId);

    /**
     * insert
     * @param details details
     * @return int
     */
    int insert(@Param("details") List<RuleBillingDetail> details);

    /**
     * insertSelective
     * @param record record
     * @return int
     */
    int insertSelective(RuleBillingDetail record);

    /**
     * selectByPrimaryKey
     * @param ruleBillingId ruleBillingId
     * @param ruleType ruleType
     * @return List<RuleBillingDetail>
     */
    List<RuleBillingDetail> selectByPrimaryKey(@Param("ruleBillingId")Long ruleBillingId,@Param("ruleType") String ruleType);

    /**
     * updateByPrimaryKeySelective
     * @param record record
     * @return int
     */
    int updateByPrimaryKeySelective(RuleBillingDetail record);

    /**
     * updateByPrimaryKey
     * @param ruleBillingHeaderId ruleBillingHeaderId
     * @return int
     */
    int updateByPrimaryKey(Long ruleBillingHeaderId);

    /**
     * updateById
     * @param ruleBillingHeaderId ruleBillingHeaderId
     * @return int
     */
    int updateById(Long ruleBillingHeaderId);
}
