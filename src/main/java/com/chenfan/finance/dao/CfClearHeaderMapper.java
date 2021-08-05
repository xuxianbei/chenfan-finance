package com.chenfan.finance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.CfClearHeader;
import com.chenfan.finance.model.vo.CfClearDetailU8VO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 财务_核销(cf_clear_header） Mapper 接口
 * </p>
 *
 * @author lywang
 * @since 2020-08-22
 */
@Repository
public interface CfClearHeaderMapper extends BaseMapper<CfClearHeader> {

    /**
     * getDetailsByClearNo
     * @param clearNo
     * @return
     */
    List<CfClearDetailU8VO> getDetailsByClearNo(String clearNo);
}
