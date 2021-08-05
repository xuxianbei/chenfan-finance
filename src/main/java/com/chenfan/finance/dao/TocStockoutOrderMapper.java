package com.chenfan.finance.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.bo.TocStockoutOrder;
import org.apache.ibatis.annotations.Mapper;
/**
 * @Author Wen.Xiao
 * @Description //
 * @Date 2021/4/9  16:12
 * @Version 1.0
 */
@Mapper
@DS("tocdb")
public interface TocStockoutOrderMapper extends BaseMapper<TocStockoutOrder> {



}