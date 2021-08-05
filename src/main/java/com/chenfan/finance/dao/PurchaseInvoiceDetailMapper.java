package com.chenfan.finance.dao;

import com.chenfan.finance.model.PurchaseInvoiceDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 发票表明细 Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-10-13
 */
@Repository
public interface PurchaseInvoiceDetailMapper extends BaseMapper<PurchaseInvoiceDetail> {


    int insertAuto(PurchaseInvoiceDetail purchaseInvoiceDetail);
}
