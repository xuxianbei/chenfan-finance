package com.chenfan.finance.dao;

import com.chenfan.finance.model.RuleBillingHeader;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.bo.RuleBillingHeaderBo;
import com.chenfan.finance.model.bo.RuleBillingHeaderListBo;
import com.chenfan.finance.model.vo.RuleBillingHeaderListVo;
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
public interface RuleBillingHeaderMapper extends BaseMapper<RuleBillingHeader> {

    /**
     * 查询所有
     * @param ruleBillingHeaderBo ruleBillingHeaderBo
     * @return List<RuleBillingHeaderListVo>
     */
    List<RuleBillingHeaderListVo> selectAll(RuleBillingHeaderListBo ruleBillingHeaderBo);

    /**
     * 根据id修改状态
     * @param ruleBillingId  ruleBillingId
     * @param ruleBillingStatus  ruleBillingStatus
     * @return  int
     * */
    int updateState(@Param("ruleBillingStatus") Integer ruleBillingStatus,@Param("ruleBillingId") Long ruleBillingId);

    /**
     * 根据id和未删除查询计费方案
     * @param ruleBillingId ruleBillingId
     * @return  RuleBillingHeader
     * */
    RuleBillingHeader selectByRuleBillingId(Long ruleBillingId);

   /**
    * 根据id查询计费方案
    * @param ruleBillingId ruleBillingId
    * @return RuleBillingHeader
    * */
    RuleBillingHeader selectById(Long ruleBillingId);

    /**
     * 根据业务类型查询启动的计费方案(去除当前id单据)
     * @param ruleBillingId  ruleBillingId
     * @param businessType  businessType
     * @return List<RuleBillingHeader>
     * */
    List<RuleBillingHeader> selectByState(@Param("businessType") String businessType,@Param("ruleBillingId") Long ruleBillingId);

    /**
     * ruleBillingId
     * @param ruleBillingId ruleBillingId
     * @return int
     */
    int deleteByPrimaryKey(Long ruleBillingId);

    /**
     * insert
     * @param record record
     * @return int
     */
    @Override
    int insert(RuleBillingHeader record);

    /**
     * insertSelective
     * @param record record
     * @return int
     */
    int insertSelective(RuleBillingHeaderBo record);

    /**
     * record
     * @param record record
     * @return int
     */
    int updateByPrimaryKeySelective(RuleBillingHeader record);

    /**
     * updateByPrimaryKey
     * @param record record
     * @return int
     */
    int updateByPrimaryKey(RuleBillingHeader record);
}
