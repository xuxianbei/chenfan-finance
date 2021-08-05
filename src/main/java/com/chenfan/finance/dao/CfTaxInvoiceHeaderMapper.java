package com.chenfan.finance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.CfTaxInvoiceHeader;
import com.chenfan.finance.model.dto.TaxInvoiceCommonListDto;
import com.chenfan.finance.model.vo.TaxInvoiceHeaderExportVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 发票主表 Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2021-03-05
 */
public interface CfTaxInvoiceHeaderMapper extends BaseMapper<CfTaxInvoiceHeader> {
    /**
     * 导出发票
     *
     * @param dto {@link TaxInvoiceCommonListDto}
     * @return {@link TaxInvoiceHeaderExportVO}
     */
    List<TaxInvoiceHeaderExportVO> exportList(@Param("dto") TaxInvoiceCommonListDto dto);
}
