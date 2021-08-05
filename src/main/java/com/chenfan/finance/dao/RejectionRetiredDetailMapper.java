package com.chenfan.finance.dao;

import com.chenfan.finance.model.RejectionRetiredDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description // 退次
 * @Date 2021/4/23  16:07
 * @Version 1.0
 */
public interface RejectionRetiredDetailMapper {
    /**
     * 根据主键查询
     * @param rjRetiredDetailId
     * @return
     */
    RejectionRetiredDetail selectByPrimaryKey(Long rjRetiredDetailId);

    /**
     * 根据聚合
     * @param rjRetiredDetailIds
     * @return
     */
    List<RejectionRetiredDetail> selectListByPrimaryKeys(@Param("rjRetiredDetailIds") List<Long> rjRetiredDetailIds);
}
