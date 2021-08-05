package com.chenfan.finance.convertor;

import com.chenfan.finance.model.CfClearHeader;
import com.chenfan.finance.model.vo.McnClearVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 核销实体类转换器
 *
 * @author wulg
 * @date 2021-07-02
 **/
@Mapper
public interface ClearConvertor {

    ClearConvertor INSTANCE = Mappers.getMapper(ClearConvertor.class);

    List<McnClearVo> asVoList(List<CfClearHeader> list);

    McnClearVo asVo(CfClearHeader entity);
}
