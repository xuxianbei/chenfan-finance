package com.chenfan.finance.dao;

import com.chenfan.finance.model.TocReportRp;
import com.chenfan.finance.model.bo.TocU8Detail;
import com.chenfan.finance.model.dto.TocReportDto;
import com.chenfan.finance.model.vo.TocReportByWeekExportVo;


import java.math.BigDecimal;
import java.util.List;

/**
 * ss
 */
public interface TocU8DetailMapper {


    /**
     * 批量插入数据
     * @param list
     * @return
     */
    int insertList(List<TocU8Detail> list);


    /**
     * 查询当前关联的oid
     * @param detailId
     * @return
     */
    TocU8Detail getOidsByMappingDetailId(Integer detailId);

    /**
     * 根据查询
     * @param tocReportByWeek
     * @return
     */
    List<TocReportRp.TocReportRpByWeek> getDataOfWeek(TocReportDto.TocReportByWeek tocReportByWeek);

    /**
     *
     * @param tocReportByWeek
     * @return
     */
    List<TocReportByWeekExportVo> getDataOfWeekExportVo(TocReportDto.TocReportByWeek tocReportByWeek);
    /**
     * 统计一个品牌的数据是否正确
     * @param mappingIds
     * @return
     */
    BigDecimal selectAmountSum(List<Long> mappingIds);

    /**
     * 根据对应的mapping数据查找对应的详情列表
     * @param mappingId
     * @return
     */
    List<TocU8Detail> selectByMappingId(Long mappingId);
}