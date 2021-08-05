package com.chenfan.finance.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.bo.TocApiTrade;
/**
 * @Author Wen.Xiao
 * @Description //
 * @Date 2021/4/9  16:12
 * @Version 1.0
 */
@DS("tocdb")
public interface TocApiTradeMapper extends BaseMapper<TocApiTrade> {

}