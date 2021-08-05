package com.chenfan.finance.convertor;

import com.chenfan.finance.model.CfTaxInvoiceHeader;
import com.chenfan.finance.model.vo.McnTaxInvoiceVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 发票实体类转换器
 *
 * @author wulg
 * @date 2021-07-02
 **/
@Mapper
public interface TaxInvoiceConvertor {

    TaxInvoiceConvertor INSTANCE = Mappers.getMapper(TaxInvoiceConvertor.class);

    List<McnTaxInvoiceVo> asVoList(List<CfTaxInvoiceHeader> list);


}
