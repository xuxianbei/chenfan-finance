package com.chenfan.finance.server;

import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.Response;
import com.chenfan.finance.model.BaseGetBrandInfoListReqModel;
import com.chenfan.finance.model.BaseGetBrandInfoListResModel;
import com.chenfan.finance.model.dto.BaseGetBrandInfoList;
import com.chenfan.finance.server.remote.model.BaseCustomerBilling;
import com.chenfan.finance.server.remote.model.BaseDicts;
import com.chenfan.finance.server.remote.model.ThirdPayAccount;
import com.chenfan.finance.server.remote.request.AccountPlatformReq;
import com.chenfan.finance.server.remote.request.BrandFeignRequest;
import com.chenfan.finance.server.remote.vo.BrandFeignVO;
import com.chenfan.finance.server.remote.vo.InvoiceTitleVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linghongshuai
 * @date 2020/11/13
 */
@FeignClient(name = "chenfan-cloud-baseinfo")
public interface BaseInfoRemoteServer {

    /**
     * 根据品牌ID查询品牌信息
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/api/brand/getBrandByBrandIdList")
    Response<List<BrandFeignVO>> getBrandByBrandIdList(@RequestBody BrandFeignRequest request);

    /**
     * 根据品牌Id获取品牌信息
     *
     * @param brandId 品牌ID
     * @return
     */
    @GetMapping(value = "/api/brand/getBrandInfo/{brandId}")
    Response<BaseGetBrandInfoList> getBrandInfo(@PathVariable("brandId") Integer brandId);


    /**
     * 获取字典类型下的值集合
     *
     * @param dictProfileCode dictProfileCode
     * @return Response<List < BaseDicts>>
     */
    @ApiOperation(value = "字典类型下的值集合", notes = "字典类型下的值集合", produces = "application/json")
    @GetMapping(value = "dict/value/list", produces = MediaType.APPLICATION_JSON_VALUE)
    Response<List<BaseDicts>> getDictList(@RequestParam("dictProfileCode") String dictProfileCode);


    /**
     * sendTestMessage
     *
     * @param id
     * @param content
     * @return
     */
    @GetMapping(value = "/util/sendTestMessage")
    Response sendTestMessage(@RequestParam String id, @RequestParam String content);


    /**
     * 获取品牌信息_带权限控制（根据用户id）
     *
     * @param reqModel
     * @return
     */
    @PostMapping("brand/getBranInfoListPer")
    Response<List<BaseGetBrandInfoListResModel>> getBranInfoListPer(@RequestBody BaseGetBrandInfoListReqModel reqModel);

    /**
     * 获取所有品牌信息
     * @param reqModel
     * @return
     */
    @PostMapping("brand/getBranInfoList")
    Response<List<BaseGetBrandInfoListResModel>> getBranInfoList(@RequestBody BaseGetBrandInfoListReqModel reqModel);


    /**
     * 根据id获取mcn客户开票信息
     *
     * @param id
     * @return
     */
    @GetMapping("baseCustomer/getBillingsById")
    Response<BaseCustomerBilling> getBillingsInfoById(@RequestParam Long id, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token);


    /**
     * 获取开票信息
     *
     * @return
     */
    @GetMapping("baseCustomer/getInvoiceTitle")
    Response<List<InvoiceTitleVO>> getinvoiceTitle();

    /**
     * 获取第三方账户信息
     *
     * @param accountPlatform
     * @return
     */
    @PostMapping("/thirdPay/selectAccountByName")
    Response<List<ThirdPayAccount>> selectAccountByName(@RequestBody AccountPlatformReq accountPlatform);


    @GetMapping(value = "/brand/selectByBrandId/{brandId}")
    Response<BrandFeignVO> selectByBrandId(@PathVariable("brandId") Integer brandId);

}
