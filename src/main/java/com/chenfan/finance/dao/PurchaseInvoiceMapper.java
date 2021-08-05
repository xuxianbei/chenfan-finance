package com.chenfan.finance.dao;

import com.chenfan.finance.model.PurchaseInvoice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * 发票表 Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-10-13
 */
@Repository
public interface PurchaseInvoiceMapper extends BaseMapper<PurchaseInvoice> {

    int insertAuto(PurchaseInvoice record);
}
