package com.chenfan.finance.controller;

import com.chenfan.common.vo.Response;
import com.chenfan.finance.commons.annotation.MultiLockInt;
import com.chenfan.finance.enums.OperateLockEnum;
import com.chenfan.finance.model.dto.CreateQcChargeDto;
import com.chenfan.finance.model.dto.UpdateInvDto;
import com.chenfan.finance.model.dto.UpdatePoDetailConDto;
import com.chenfan.finance.model.dto.UpdatePoDetailPriceDto;
import com.chenfan.finance.service.UpstreamService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 上游数据更新同步接口(提供给其他服务，更新财务数据的业务接口)
 * @Author Wen.Xiao
 * @Description 上游数据更新同步接口(提供给其他服务，更新财务数据的业务接口)
 * @Date 2021/6/11  17:41
 * @Version 1.0
 */
@Slf4j
@Api(tags = "上游数据更新同步接口(提供给其他服务，更新财务数据的业务接口)")
@RestController
@RequestMapping("/upstream")
public class UpstreamUpdateController {
    @Resource
    private UpstreamService upstreamService;

    /**
     * 更新采购单详情对应的合同日期,不传日期是检查，传递日期的话是更改日期
     * @param updatePoDetailConDto
     * @return
     */
    @MultiLockInt(paramNames={"poId"},isCheck = true,isCollections = false)
    @PostMapping("updatePoCon")
    public Response<Boolean>  updatePoCon(@RequestBody UpdatePoDetailConDto updatePoDetailConDto){
        return upstreamService.updatePoCon(updatePoDetailConDto);
    }

    /**
     * 更新商品档案的信息
     * @param updateInvDto
     * @return
     */
    @PostMapping("updateInv")
    public Response<Boolean>  updateInv(@Validated @RequestBody UpdateInvDto updateInvDto){
        return upstreamService.updateInv(updateInvDto);
    }

    /**
     * 更新采购单价格
     * @param updatePoDetailPriceDtoList
     * @return
     */
    @PostMapping("updatePoPrice")
    public Response<Boolean>  updatePoPrice(@Validated @RequestBody List<UpdatePoDetailPriceDto> updatePoDetailPriceDtoList){
        return upstreamService.updatePriceOfPo(updatePoDetailPriceDtoList);
    }


    /**
     * 检查当前采购单是否可以改价（申请时调用，审批时直接调用更新采购单价格）
     * @param poDetailIdList
     * @return
     */
    @PostMapping("checkPoStateOfPrice")
    public Response<Boolean>  checkPoStateOfPrice(@RequestBody List<Long> poDetailIdList){
        return upstreamService.checkPoStateOfPrice(poDetailIdList,null);
    }

    /**
     * 根据质检任务创建质检费用
     *
     * 由于财务这边的入库信息同步较晚，所以存在数量小于质检数量的情况，所以财务这边没法限制登记的数量，需质检入库那边限制单个质检的总数量要小于商品入库数量
     * @param createQcChargeDto
     * @return
     */
    @PostMapping("createQcChargeByQcTask")
    public Response<Boolean>  createQcChargeByQcTask(@Validated @RequestBody CreateQcChargeDto createQcChargeDto){
        return upstreamService.createQcChargeByQcTask(createQcChargeDto);
    }
}
