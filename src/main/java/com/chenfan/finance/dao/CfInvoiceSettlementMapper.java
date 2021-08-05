package com.chenfan.finance.dao;

import com.chenfan.finance.model.CfInvoiceSettlement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 账单结算表 Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-10-20
 */
@Repository
public interface CfInvoiceSettlementMapper extends BaseMapper<CfInvoiceSettlement> {

    /**
     * 作废结算单数据置为空
     * @param invoiceSettlementId
     * @return
     */
    int  invalidSettlementById(Long invoiceSettlementId);


    /**
     * 查询关联的红字抵扣列表
     * @param associatedInvoiceNo
     * @param associatedInvoiceSettlementNo
     * @return
     */
    List<CfInvoiceSettlement> selectByAssociated(@Param("associatedInvoiceNo") String associatedInvoiceNo, @Param("associatedInvoiceSettlementNo") String  associatedInvoiceSettlementNo);
}
