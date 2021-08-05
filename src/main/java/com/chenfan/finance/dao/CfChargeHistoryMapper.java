package com.chenfan.finance.dao;

import com.chenfan.finance.model.CfChargeHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 费收_费用(cf_charge） Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-28
 */
@Repository
public interface CfChargeHistoryMapper extends BaseMapper<CfChargeHistory> {

    /**
     * cfChargeHistoryList
     *
     * @param cfChargeHistoryList cfChargeHistoryList
     */
    void batchInsertCfChargeHistory(@Param("list") List<CfChargeHistory> cfChargeHistoryList);

}
