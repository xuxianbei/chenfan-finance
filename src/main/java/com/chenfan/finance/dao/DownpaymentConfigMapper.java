package com.chenfan.finance.dao;

import com.chenfan.finance.model.dto.DownpaymentConfig;

import java.util.List;

/**
 * @author admin
 * @date 2020/7/15 16:38
 */
public interface DownpaymentConfigMapper {
    /**
     * 删除
     * @param paymentConfId
     * @return
     */
    int deleteByPrimaryKey(Integer paymentConfId);

    /**
     * 新增
     * @param record
     * @return
     */
    int insert(DownpaymentConfig record);

    /**
     * 新增
     * @param record
     * @return
     */
    int insertSelective(DownpaymentConfig record);

    /**
     * 根据id查询
     * @param paymentConfId
     * @return
     */
    DownpaymentConfig selectByPrimaryKey(Integer paymentConfId);

    /**
     * 修改
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(DownpaymentConfig record);

    /**
     * 修改
     * @param record
     * @return
     */
    int updateByPrimaryKey(DownpaymentConfig record);

    /**
     * 查询全部
     * @return
     */
    List<DownpaymentConfig> selectAll();
}