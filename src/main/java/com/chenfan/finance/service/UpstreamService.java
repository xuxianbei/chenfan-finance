package com.chenfan.finance.service;

import com.chenfan.common.vo.Response;
import com.chenfan.finance.model.dto.CreateQcChargeDto;
import com.chenfan.finance.model.dto.UpdateInvDto;
import com.chenfan.finance.model.dto.UpdatePoDetailConDto;

import com.chenfan.finance.model.dto.UpdatePoDetailPriceDto;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

/**
 * @Author Wen.Xiao
 * @Description // 上游数据更改引发下游财务数据更改
 * @Date 2021/5/28  16:08
 * @Version 1.0
 */
public interface UpstreamService {


    /**
     * 更新采购单合同日期
     * @param updatePoDetailConDto
     * @return
     */
    Response<Boolean> updatePoCon( UpdatePoDetailConDto updatePoDetailConDto);


    /**
     * 更新商品档案
     * @param updateInvDto
     * @return
     */
    Response<Boolean>  updateInv(UpdateInvDto updateInvDto);


    /**
     * 批量修改已经推送过来的采购单的价格
     * @param updatePoDetailPriceDtoList
     * @return
     */
    Response<Boolean> updatePriceOfPo(List<UpdatePoDetailPriceDto> updatePoDetailPriceDtoList);



    /**
     * 检查当前采购单是否可以改价（申请时调用，审批时直接调用更新采购单价格）
     * @param poDetailIdList
     * @param chargeIds
     * @return
     */
    Response<Boolean>  checkPoStateOfPrice( List<Long> poDetailIdList,List<Long> chargeIds);

    /**
     * 质检扣款
     * @param createQcChargeDto
     * @return
     */
    Response<Boolean>  createQcChargeByQcTask(@RequestBody CreateQcChargeDto createQcChargeDto);
}
