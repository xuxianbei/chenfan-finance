package com.chenfan.finance.dao;

import com.chenfan.finance.model.CfRdRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 入库单 Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-31
 */

@Repository
public interface CfRdRecordMapper extends BaseMapper<CfRdRecord> {

    /**
     * batchUpdateChargeFlagByIds
     * @param rdRecordIds rdRecordIds
     */
    void batchUpdateChargeFlagByIds(@Param("list") List<Long> rdRecordIds);

    /**
     * 子表修改为已收
     * @param rdRecordIds
     */
    void batchUpdateChargeFlagByDetailIds(@Param("list") List<Integer> rdRecordIds);

}
