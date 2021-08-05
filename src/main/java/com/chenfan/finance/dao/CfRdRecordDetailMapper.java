package com.chenfan.finance.dao;

import com.chenfan.finance.model.CfRdRecordDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.vo.CfMonitoringOrderVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 入库单明细 Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-31
 */
@Repository
public interface CfRdRecordDetailMapper extends BaseMapper<CfRdRecordDetail> {

    /**
     * 查询当前财务入库单和原始数据库中的到货单差异性数据
     * @param startTime
     * @param endTime
     * @return
     */
    List<CfMonitoringOrderVo> selectRdOrderAndArrivalOrderDifferences(LocalDateTime startTime,LocalDateTime endTime);



    /**
     * selectRdOrderAndArrivalOrderDifferencesForRefund
     * @param startTime
     * @param endTime
     * @return
     */
    List<CfMonitoringOrderVo> selectRdOrderAndArrivalOrderDifferencesForRefund(LocalDateTime startTime,LocalDateTime endTime);


    /**
     * 获取所有新财务的品牌id
     * @return
     */
    List<Integer> selectBrandIds();


    /**
     * 根据poId 查询入库通知单no
     * @param poId
     * @return
     */
    List<String> selectRkNoByPoId(Long poId);


    /**
     * 查询尚未同步的到货单
     * @param startTime
     * @param endTime
     * @return
     */
    List<String> selectRdInfoDifferences(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);


    /**
     * 检查详情
     * @param startTime
     * @param endTime
     * @return
     */
    List<HashMap<String, Object>> selectRdDetailInfoDifferences(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);




    /**
     * 根据po_id 查找所有的入库单详情id
     * @param poId
     * @return
     */
    List<Long> selectRdDetailIdsByPoId(@Param("poId") Long poId);


}
