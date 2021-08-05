package com.chenfan.finance.server;


import com.chenfan.common.vo.Response;
import com.chenfan.finance.model.MqMsg;
import com.chenfan.finance.server.fallback.BaseRemoteServerFallBack;
import com.chenfan.finance.server.remote.model.InventoryCategoryNew;
import com.chenfan.finance.server.remote.model.InventoryGetInfoModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author mbji
 * @date 2018/11/14.
 */
@Component
@FeignClient(name = "chenfan-cloud-base", fallback = BaseRemoteServerFallBack.class)
public interface BaseRemoteServer {


    /**
     * 品牌列表 弃用
     *
     * @param brands
     * @return
     */
    //@PostMapping({"brand/list"})
    //Response<List<BrandDetailResModelRpc>> list(@RequestBody BrandDetailResModelsDto brands);

    /**
     * 单号
     *
     * @param module
     * @return
     */
    @RequestMapping(value = "/numberGenerator/getNumber/{module}", method = RequestMethod.GET)
    Response getPayNum(@PathVariable("module") String module);

    /**
     * 工艺单尺码
     *
     * @param craftsId
     * @return
     */
    @RequestMapping(value = "/craftsApp/view_new_crafts_position/?craftsId={craftsId}", method = RequestMethod.GET)
    Response getNewCraftPosition(@PathVariable("craftsId") String craftsId);

    /**
     * 获取工艺单
     *
     * @param productCode
     * @param vendorId
     * @param status
     * @return
     */
    @RequestMapping(value = "/crafts/getCraftsLists?productCode={productCode}&vendorId={vendorId}&status={status}", method = RequestMethod.GET)
    Response getCraftsList(@PathVariable("productCode") String productCode
            , @PathVariable("vendorId") String vendorId, @PathVariable("status") String status);

    /**
     * 根据用户ID查找关联上的品牌ID列表 弃用
     *
     * @param userId   用户ID
     * @return 用户具备的品牌ID列表
     */
    //@GetMapping(value = "brand/user/{userId}")
    //Response<List<Integer>> queryBrandIdsByUserId(@PathVariable("userId") Long userId);

//    /**
//     * 根据用户ID查找关联上的品牌ID列表
//     *
//     * @param brand   可选参数 brandName 品牌名,brandCode 品牌编码,status 品牌状态
//     * @return 用户具备的品牌ID列表
//     */
//    @GetMapping(value = "brand/getList")
//    Response<PageInfo<BrandDetailResModel>> getList(BrandGetListReqModel brand);

    /**
     * 根据品牌Id获取品牌信息 弃用
     *
     * @param brandId 品牌ID
     * @return
     */
    //@GetMapping(value = "base/getBrandInfo/{brandId}")
    //Response<BaseGetBrandInfoList> getBrandInfo(@PathVariable("brandId") Integer brandId);

    /**
     * 根据品牌Id获取品牌信息  弃用
     *
     * @param brandId 品牌ID
     * @return
     */
    //@GetMapping(value = "base/getBrandInfo/{brandId}")
    //Response<BaseGetBrandInfoList> getBrandDetail(@PathVariable("brandId") Long brandId);

    /**
     * 获取品牌详情 弃用
     *
     * @param brand 品牌
     * @return Response<List < BrandDetailResModel>>
     */
/*
    @GetMapping(
            value = "getList",
            produces = {"application/json;charset=UTF-8"})
    Response<List<BrandDetailResModel>> getList(BrandGetListReqModel brand);
*/

    /**
     * 获取单号
     *
     * @param module module
     * @return com.chenfan.common.vo.Response<java.lang.String>
     * @author zq
     * @date 2020/7/15 11:17
     */
    @RequestMapping("numberGenerator/getNumber/{module}")
    Response<String> getNumber(@PathVariable("module") String module);


    /**
     * 获取字典类型下的值集合 弃用
     *
     * @param dictProfileCode dictProfileCode
     * @return Response<List < BaseDicts>>
     */
/*
    @ApiOperation(value = "字典类型下的值集合", notes = "字典类型下的值集合", produces = "application/json")
    @GetMapping(value = "dict/value/list", produces = MediaType.APPLICATION_JSON_VALUE)
    Response<List<BaseDicts>> getDictList(@RequestParam("dictProfileCode") String dictProfileCode);
*/

    /**
     * 保存消息
     *
     * @param ms MqMsg
     * @return Response<Object>
     */
    @PostMapping(value = "mqMsg/send", produces = {"application/json;charset=UTF-8"})
    Response<Object> send(MqMsg ms);

    /**
     * 保存消息
     *
     * @param ms MqMsg
     * @return Response<Object>
     */
    @PostMapping(value = "mqMsg/consume", produces = {"application/json;charset=UTF-8"})
    Response<Object> consume(MqMsg ms);

    /**
     * parentId 不传递时可获取所有大类
     * @param parentId
     * @return
     */
    @ApiOperation(value = "获取存货档案品类表信息", notes = "获取存货档案品类表信息")
    @GetMapping(value = "/inventory/getInventoryCategoryNewList")
    Response<List<InventoryCategoryNew>> getInventoryCategoryNewList(@RequestParam(value = "parentId", required=false) Integer parentId);

    /**
     * 查看存货档案详情
     *
     * @param productCode 请求参数
     * @return 成功/失败状态
     */
    @ApiOperation(value = "查看存货档案详情", notes = "查看存货档案详情", produces = "application/json")
    @GetMapping(
            value = "/inventory/getInfo",
            produces = {"application/json;charset=UTF-8"})
    Response<InventoryGetInfoModel> getInfo(@RequestParam("productCode") String productCode, @RequestParam(value = "state", required=false)Integer state);
}
