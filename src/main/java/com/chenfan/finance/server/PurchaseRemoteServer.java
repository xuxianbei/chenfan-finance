package com.chenfan.finance.server;

import com.chenfan.common.vo.Response;
import com.chenfan.finance.model.dto.PoHeader;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linghongshuai
 * @date 2020/11/13
 */
@FeignClient(name = "chenfan-cloud-purchase")
public interface PurchaseRemoteServer {

    /**
     * 更新采购单的定金，尾款，分期付款信息
     *
     * @param poHeader
     * @return
     */
    @PostMapping(value = "/pomain/updatePoHeaderFromFinance")
    Response updatePoHeaderFromFinance(@RequestBody PoHeader poHeader);

    /**
     * 根据采购单id获取主单信息
     * @param poId
     * @return
     */
    @GetMapping(value = "/pomain/getPoHeaderInfoByPoId")
    Response<PoHeader> getPoHeaderInfoByPoId(@RequestParam Integer poId);



}
