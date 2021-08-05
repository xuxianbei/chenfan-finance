package com.chenfan.finance.dao;

import com.chenfan.finance.model.bo.TocIncomeOrderStockOut;

import java.util.List;
/**
 * @Author Wen.Xiao
 * @Description //
 * @Date 2021/4/9  16:12
 * @Version 1.0
 */
public interface TocIncomeOrderStockOutMapper {


    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertList(List<TocIncomeOrderStockOut> list);


    /**
     * 查询
     * @param oids
     * @return
     */
    List<TocIncomeOrderStockOut> selectListOfOids(List<String> oids);
}