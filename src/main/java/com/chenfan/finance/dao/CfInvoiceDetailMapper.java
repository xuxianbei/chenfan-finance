package com.chenfan.finance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.CfInvoiceDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
public interface CfInvoiceDetailMapper extends BaseMapper<CfInvoiceDetail> {

    /**
     * selectInvoiceCountByChargeIds
     * @param chargeIds chargeIds
     * @return int
     */
    int selectInvoiceCountByChargeIds(List<Long> chargeIds );

    /**
     * 批量插入账单子表
     *
     * @param list list
     * @return int
     */
    int batchInsert(List<CfInvoiceDetail> list);

    /**
     * 编辑时，删除用户删除的列
     * @param invoiceId invoiceId
     * @param invoiceDetailIds invoiceDetailIds
     * @return int
     */
    int deleteByUpdate(@Param("invoiceId") Long invoiceId, @Param("invoiceDetailIds") List<Long> invoiceDetailIds);

    /**
     * updateInvoiceNo
     * @param invoiceDetail invoiceDetail
     * @return int
     */
    int updateInvoiceNo(CfInvoiceDetail invoiceDetail);

    /**
     * selectChargeIdsByInvoiceId
     * @param invoiceId invoiceId
     * @return List<Long>
     */
    List<Long> selectChargeIdsByInvoiceId(Long invoiceId);

    /**
     * updateEpricePpByChargeIds
     * @param delayChargeIds delayChargeIds
     * @param money money
     */
    void updateEpricePpByChargeIds(@Param("delayChargeIds") List<Long> delayChargeIds, @Param("money") BigDecimal money);
}
