package com.chenfan.finance.dao;

import com.chenfan.finance.model.CfBankAndCash;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.dto.AdvancepayApply;
import com.chenfan.finance.model.dto.AdvancepayApplyInvoiceToBank;
import com.chenfan.finance.model.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Repository
public interface CfBankAndCashMapper extends BaseMapper<CfBankAndCash> {
    /**
     * 新增
     * @author zq
     * @date 2020/8/19 18:14
     * @param acVo acVo
     * @return Integer
     */
    Integer create(CfBankAndCash acVo);

    /**
     * 编辑
     * @author zq
     * @date 2020/8/19 18:15
     * @param acVo acVo
     * @return int
     */
    int updateByPrimaryKey(CfBankAndCash acVo);

    /**
     * 删除
     * @author zq
     * @date 2020/8/19 19:59
     * @param apVo
     * @return int
     */
    int delete(CfBankAndCashShowRequestVo apVo);

    /**
     * 获取列表
     * @author zq
     * @date 2020/8/19 21:02
     * @param apsVo apsVo
     * @return java.util.List<com.chenfan.finance.model.ActualPaymentShowResponseVo>
     */
    List<CfBankAndCashListShowVo> getList(CfBankAndCashShowRequestVo apsVo);

    /**
     * 获取列表count
     * @author zq
     * @date 2020/8/20 10:19
     * @param apsVo apsVo
     * @return java.lang.Integer
     */
    Integer count(CfBankAndCashShowRequestVo apsVo);

    /**
     * 详情
     * @author zq
     * @date 2020/8/20 1:38
     * @param bankAndCashId bankAndCashId
     * @return com.chenfan.finance.model.vo.ActualPaymentInfoVo
     */
    CfBankAndCashInfoVo info(Long bankAndCashId);

    /**
     * 获取导出数据
     * @author zq
     * @date 2020/8/20 10:18
     * @param apsVo apsVo
     * @return com.chenfan.finance.model.vo.ActualPaymentListExportVo
     */
    List<CfBankAndCashListExportVo> export(CfBankAndCashShowRequestVo apsVo);

    /**
     * 根据code获取info
     * @author zq
     * @date 2020/8/20 14:56
     * @param recordSeqNo recordSeqNo
     * @return com.chenfan.finance.model.vo.ActualPaymentInfoVo
     */
    CfBankAndCashInfoVo getInfoByCode(String recordSeqNo);

    /**
     * 获取可核销no
     * @param poIds poIds
     * @return List<AdvancepayApply>
     */
    List<AdvancepayApplyInvoiceToBank> getAllCanClearNos(@Param("ids") List<Long> poIds);
}
