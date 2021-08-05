package com.chenfan.finance.dao;

import com.chenfan.finance.model.bo.TocExpendOrderStockOut;
import com.chenfan.finance.model.bo.TocIncomeOrderStockOut;


import java.util.List;

public interface TocExpendOrderStockOutMapper {


    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertList(List<TocExpendOrderStockOut> list);


    /**
     * 查询
     * @param oids
     * @return
     */

    List<TocExpendOrderStockOut> selectListOfOids(List<String> oids);

}