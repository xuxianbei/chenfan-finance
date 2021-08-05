package com.chenfan.finance.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.bo.TocStockoutOrderDetail;

import java.util.List;
/**
 * @Author Wen.Xiao
 * @Description //
 * @Date 2021/4/9  16:12
 * @Version 1.0
 */
@DS("tocdb")
public interface TocStockoutOrderDetailMapper extends BaseMapper<TocStockoutOrderDetail> {

    /**
     * 根据tid 查询对应的出库单（已弃用，数据太大，关联查询太慢）
     * @param tid
     * @return
     */
    @Deprecated
    List<TocStockoutOrderDetail> selectListByTid(String tid);
}