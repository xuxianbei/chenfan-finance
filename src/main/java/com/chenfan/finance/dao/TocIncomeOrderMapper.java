package com.chenfan.finance.dao;

import com.chenfan.finance.model.bo.TocIncomeOrder;
import com.chenfan.finance.model.bo.TocU8Detail;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @Author Wen.Xiao
 * @Description //
 * @Date 2021/4/9  16:12
 * @Version 1.0
 */
public interface TocIncomeOrderMapper {

    /**
     * 更改推送u8的单号回写
     * @param oids
     * @param orderNo
     * @return
     */
    int updateOrderNo(@Param("oids") List<String> oids,@Param("orderNo") String orderNo);
    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertList(List<TocIncomeOrder> list);


    /**
     * 根据tid  检查当前是否使用过邮费
     * @param tid
     * @return
     */
    int checkPostUsedByTid(String tid);

    /**
     * 根据tids  检查当前是否使用过邮费
     * @param tids
     * @return
     */
    List<TocIncomeOrder> checkPostUsedByTids(Set<String>  tids);

    /**
     * 根据tid 找到已经使用过的Oid
     * @param tid
     * @return
     */
    List<String> getUsedOidByTid(String tid);

    /**
     * 根据tid 找到已经使用过的Oids
     * @param tids
     * @return
     */
    List<TocIncomeOrder> getUsedOidByTids(Set<String> tids);

    /**
     * 获取收入
     * @param start
     * @param end
     * @param shopAccounts
     * @return
     */
    List<TocU8Detail> getIncome(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("shopAccounts") List<String> shopAccounts);

    /**
     * 获取收入-所有邮费
     * @param start
     * @param end
     * @param shopAccounts
     * @return
     */
    List<TocU8Detail> getIncomeOfPost(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("shopAccounts") List<String> shopAccounts,@Param("checkTypes") List<Integer> checkTypes);

    /**
     * 根据tid 找到已经被使用的退款单
     * @param tids
     * @return
     */
    List<String> getRefundNoOfUsedByTids(@Param("tids") Set<String> tids, @Param("checkTypes") List<Integer> checkTypes);

    /**
     * 根据oids查找
     * @param oids
     * @return
     */
    List<TocIncomeOrder> selectListByOids(@Param("oids") List<String> oids);
}