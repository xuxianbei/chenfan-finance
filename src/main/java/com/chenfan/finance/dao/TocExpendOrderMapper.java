package com.chenfan.finance.dao;

import com.chenfan.finance.model.bo.TocExpendOrder;
import com.chenfan.finance.model.bo.TocU8Detail;
import org.apache.ibatis.annotations.Param;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TocExpendOrderMapper {


    /**
     * 更新订单的出库单状态
     * @param oids
     * @return
     */
    int updateOrderNo(@Param("oids") List<String> oids,@Param("orderNo") String orderNo);
    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertList(List<TocExpendOrder> list);


    /**
     * 根据tid 找到已经被使用的退款单
     * @param tids
     * @return
     */
    List<String> getRefundNoOfUsedByTids(@Param("tids") Set<String> tids);

    /**
     * 根据oid  查询对应的详情
     * @param oids
     * @return
     */
    List<TocExpendOrder>  selectListByOids(@Param("oids") List<String> oids);



    /**
     * 获取有支付流水的支出
     * @param start
     * @param end
     * @param shopAccounts
     * @param checkTypes
     * @return
     */
    List<TocU8Detail> getExpend(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("shopAccounts") List<String> shopAccounts,@Param("checkTypes") List<Integer> checkTypes);

    /**
     * 退款数量不一致,退差价
     * @param start
     * @param end
     * @param shopAccounts
     * @param checkTypes
     * @return
     */
    List<TocU8Detail> getExpendOfOther(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("shopAccounts") List<String> shopAccounts,@Param("checkTypes") List<Integer> checkTypes);

    /**
     * 从正向收入中获取关联的退款的(有退还商品)
     * @param start
     * @param end
     * @param shopAccounts
     * @param checkTypes
     * @return
     */
    List<TocU8Detail> getExpendOfIncomeRefund(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("shopAccounts") List<String> shopAccounts,@Param("checkTypes") List<Integer> checkTypes);

    /**
     * 从正向收入中获取关联的权益金
     * @param start
     * @param end
     * @param shopAccounts
     * @param checkTypes
     * @return
     */
    List<TocU8Detail> getExpendOfIncomeEquity(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("shopAccounts") List<String> shopAccounts,@Param("checkTypes") List<Integer> checkTypes);

    /**
     * 从正向收入中获取关联的退款的(退差价等)
     * @param start
     * @param end
     * @param shopAccounts
     * @param checkTypes
     * @return
     */
    List<TocU8Detail> getExpendOfIncomeOther(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("shopAccounts") List<String> shopAccounts,@Param("checkTypes") List<Integer> checkTypes);

}