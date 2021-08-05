package com.chenfan.finance.server;

import com.chenfan.common.vo.Response;
import com.chenfan.finance.server.dto.BaseVendorSettlemet;
import com.chenfan.vendor.api.VendorCenterRemoteServer;
import com.chenfan.vendor.response.VendorResModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author weishili
 */
@FeignClient(name = "chenfan-cloud-vendor-center")
public interface VendorCenterServer extends VendorCenterRemoteServer {

    /**
     * 根据codes 查询供应商名称 集合
     */
    /**
     * 根据codes 查询供应商名称 集合
     * @param codes
     * @return
     */
    @PostMapping(value = "vendor/getVendorNamesByCodes")
    Response<List<VendorResModel>> getVendorNamesByCodes(@RequestBody List<String> codes);

    @GetMapping(value = "vendor/getVendorSettlementInfo")
    Response<List<BaseVendorSettlemet>> getVendorSettlementInfo(@RequestParam("vendorId") Long vendorId, @RequestParam("vendorCode") String vendorCode);
}