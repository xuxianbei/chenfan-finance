package com.chenfan.finance.dao;

import com.chenfan.finance.model.CfPoDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.vo.CfPoDetailExtendVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  财务_业务单据_采购单 (cf_po_detail)  Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Repository
public interface CfPoDetailMapper extends BaseMapper<CfPoDetail> {

    /**
     * 查询对应采购单基本以及明细数据
     * @param poDetailIds
     * @return
     */
    List<CfPoDetailExtendVo> queryCfPoDetailExtends(@Param("poDetailIds") List<Long> poDetailIds);

    /**
     * 根据税率，成本价和spu获取采购单信息(不含税单价和加价不含税单价)
     * @param productCode
     * @param taxUnitPrice
     * @param taxRate
     * @return
     */
    List<CfPoDetail> queryPoDetailByGroupConditions(@Param("productCode")String productCode, @Param("taxUnitPrice")BigDecimal taxUnitPrice,@Param("taxRate")BigDecimal taxRate);


    /**
     * 检查原始单据的不同
     * @param startTime
     * @param endTime
     * @return
     */
    List<CfPoDetail> selectDiffOfOriginal(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
