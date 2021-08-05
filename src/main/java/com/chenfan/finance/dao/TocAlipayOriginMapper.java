package com.chenfan.finance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.TocReportRp;
import com.chenfan.finance.model.bo.TocAlipayOrigin;
import com.chenfan.finance.model.dto.TocReportDto;
import com.chenfan.finance.model.vo.AlipayOriginExportVo;
import com.chenfan.finance.model.vo.TimeRange;
import com.chenfan.finance.model.vo.TocReportBySuccessExportVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description //
 * @Date 2021/4/9  16:12
 * @Version 1.0
 */
public interface TocAlipayOriginMapper  extends BaseMapper<TocAlipayOrigin> {

    /**
     * 批量插入
     * @param subList
     * @param shopAccount
     * @return
     */
    int insertList(@Param("subList") List<TocAlipayOrigin> subList, @Param("shopAccount") String shopAccount);

    /**
     * 根据时间获取
     * @param timeRange
     * @return
     */
    List<TocAlipayOrigin> getAllDataByMonth(@Param("timeRange") TimeRange timeRange);

    /**
     * 根据条件获取
     * @param tocAlipayOrigin
     * @return
     */
    List<TocAlipayOrigin> selectListByBean(TocAlipayOrigin tocAlipayOrigin);



    /**
     * 根据主键获取
     * @param financeNo
     * @return
     */
    List<TocAlipayOrigin> selectListByBeanKey(String financeNo);

    /**
     * 获取订单未拆分，流水拆分的流水
     * @return
     */
    List<TocAlipayOrigin> selectListForGroup(@Param("failTids") List<String> failTids);


    /**
     * 查询支出的支付宝流水
     * @param failTids
     * @param sqlString
     * @return
     */
    List<TocAlipayOrigin> selectListOfExpend(@Param("failTids") List<String> failTids, @Param("sqlString") String sqlString);


    /**
     * 查询失败的流水（未处理或者失败）
     * @param tocReportByFailure
     * @return
     */
    List<TocAlipayOrigin> selectListOfFail(TocReportDto.TocReportByFailure tocReportByFailure);


    /**
     * 查询失败的流水
     * @param tocReportByFailure
     * @return
     */
    List<AlipayOriginExportVo> selectListOfFailExportVo(TocReportDto.TocReportByFailure tocReportByFailure);
    /**
     * 查询匹配的数据
     * @param tocReportBySuccess
     * @return
     */
    List<TocReportRp.TocReportRpBySuccess> selectDataOfSuccess(TocReportDto.TocReportBySuccess tocReportBySuccess);


    /**
     * 查询匹配的数据
     * @param tocReportBySuccess
     * @return
     */
    List<TocReportBySuccessExportVo> selectDataOfSuccessExportVo(TocReportDto.TocReportBySuccess tocReportBySuccess);
    /**
     * 检查一段时间的流水匹配金额
     * @param shopAccountList
     * @param start
     * @param end
     * @return
     */
    BigDecimal selectBySum(List<String> shopAccountList, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}