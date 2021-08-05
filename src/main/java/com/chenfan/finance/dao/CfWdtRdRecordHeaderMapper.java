package com.chenfan.finance.dao;

import com.chenfan.finance.model.CfWdtRdRecordHeader;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  财务_业务单据_入库单 (cf_wdt_rd_record_header)  Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Repository
public interface CfWdtRdRecordHeaderMapper extends BaseMapper<CfWdtRdRecordHeader> {

    /**
     * batchUpdateChargeFlagByIds
     * @param wdtRdRecordIds wdtRdRecordIds
     */
    void batchUpdateChargeFlagByIds(@Param("list") List<Long> wdtRdRecordIds);
}
