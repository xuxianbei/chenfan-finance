package com.chenfan.finance.service.common;

import com.chenfan.finance.model.CfClearDetail;
import com.chenfan.finance.model.CfClearHeader;
import com.chenfan.finance.model.vo.ClearHeaderDetailInvoiceDetailVo;
import com.chenfan.finance.model.vo.ClearHeaderDetailVo;

import java.util.List;

/**
 * 核销详情
 * 依赖接口，不依赖实现，本质还是因为一个需求，有多种实现，而每种实现又有自己的特点
 *
 * @author: xuxianbei
 * Date: 2021/4/20
 * Time: 20:10
 * Version:V1.0
 */
public interface ClearDetail {

    /**
     * 详情
     *
     * @param clearHeaderDetailVo
     * @param cfClearHeader
     */
    void clearHeaderDetailBase(ClearHeaderDetailVo clearHeaderDetailVo, CfClearHeader cfClearHeader);

    /**
     * 详情-费用明细
     * @param clearHeaderDetailVo
     * @param cfClearHeader
     * @param cfClearDetails
     * @return
     */
    List<ClearHeaderDetailInvoiceDetailVo> detailInvoiceDetailVos(ClearHeaderDetailVo clearHeaderDetailVo, CfClearHeader cfClearHeader, List<CfClearDetail> cfClearDetails);

    /**
     * 明细汇总
     *
     * @param clearHeaderDetailVo
     * @param cfClearHeader
     */
    void clearHeaderDetailTotalVo(ClearHeaderDetailVo clearHeaderDetailVo, CfClearHeader cfClearHeader);
}
