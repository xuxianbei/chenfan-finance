package com.chenfan.finance.server.fallback;

import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.finance.model.MqMsg;
import com.chenfan.finance.server.BaseRemoteServer;
import com.chenfan.finance.server.remote.model.InventoryCategoryNew;
import com.chenfan.finance.server.remote.model.InventoryGetInfoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 降级处理
 *
 * @author: xuxianbei
 * Date: 2020/9/11
 * Time: 16:21
 * Version:V1.0
 */
@Slf4j
@Component
public class BaseRemoteServerFallBack implements BaseRemoteServer {
    /*@Override
    public Response<List<BrandDetailResModelRpc>> list(BrandDetailResModelsDto brands) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }*/

    @Override
    public Response getPayNum(String module) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    @Override
    public Response getNewCraftPosition(String craftsId) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    @Override
    public Response getCraftsList(String productCode, String vendorId, String status) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    /*@Override
    public Response<List<Integer>> queryBrandIdsByUserId(Long userId) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }*/

    /*@Override
    public Response<BaseGetBrandInfoList> getBrandInfo(Integer brandId) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }*/

    /*@Override
    public Response<BaseGetBrandInfoList> getBrandDetail(Long brandId) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }*/

    /*@Override
    public Response<List<BrandDetailResModel>> getList(BrandGetListReqModel brand) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }*/

    @Override
    public Response<String> getNumber(String module) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    /*@Override
    public Response<List<BaseDicts>> getDictList(String dictProfileCode) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }*/

    @Override
    public Response<Object> send(MqMsg ms) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    @Override
    public Response<Object> consume(MqMsg ms) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    @Override
    public Response<List<InventoryCategoryNew>> getInventoryCategoryNewList(Integer parentId) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    @Override
    public Response<InventoryGetInfoModel> getInfo(String productCode, Integer state) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }
}
