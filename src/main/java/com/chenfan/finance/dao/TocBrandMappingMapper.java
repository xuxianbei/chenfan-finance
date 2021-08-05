package com.chenfan.finance.dao;

import com.chenfan.finance.model.bo.TocBrandMapping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TocBrandMappingMapper {

    /**
     * 获取当前所有的绑定关系
     * @return
     */
    List<TocBrandMapping> selectOfList();

    /**
     * 根据账户查询品牌
     * @param shopAccount
     * @return
     */
    TocBrandMapping selectOne(String shopAccount);


    /**
     *  根据品牌查询对应账户信息
     * @param brandIds
     * @return
     */
    List<String> selectShopAccountByBrandIds(@Param("brandIds") List<Integer> brandIds);


    /**
     * 查询所有的账户信息
     * @return
     */
    List<String> selectShopAccountAll();
}