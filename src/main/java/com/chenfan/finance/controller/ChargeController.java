package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.annotation.Login;
import com.chenfan.finance.commons.exception.FinanceException;
import com.chenfan.finance.model.dto.*;
import com.chenfan.finance.model.vo.CfChargeDetailVO;
import com.chenfan.finance.model.vo.CfChargeRelevanceVO;
import com.chenfan.finance.service.CfChargeService;
import com.chenfan.finance.service.UpstreamService;
import com.chenfan.privilege.common.config.SearchAuthority;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 费用管理controller
 * @author mbji
 */
@Slf4j
@Api(tags = "费用管理相关接口")
@RestController
@RequestMapping("charge")
@Login(check = false)
public class ChargeController {

    @Autowired
    private CfChargeService cfChargeService;
    @ApiOperation("对账列表")
    @GetMapping("verifyChargeList")
    public Response<Object> verifyChargeList(@SearchAuthority CfChargeListQuery query, @ApiIgnore HttpServletResponse response) throws Exception {
        log.info("获取的关联的brandIds:{}",query.getBrandIds());
        return new Response<>(ResponseCode.SUCCESS, cfChargeService.verifyChargeList(query, response));
    }



    @ApiOperation("查询所有费用列表")
    @GetMapping("queryChargeList")
    public Response<Object> queryChargeList(@SearchAuthority CfChargeListQuery query) {
        log.info("获取的关联的brandIds:{}",query.getBrandIds());
        return new Response<>(ResponseCode.SUCCESS, cfChargeService.queryChargeList(query));
    }

    @ApiOperation("spu维度查询所有费用列表,费用来源按钮专用")
    @GetMapping("chargeListGroupByChargeSource")
    public Response<Object> chargeListGroupByChargeSource(@SearchAuthority CfChargeListQuery query, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        query.setGroupDetail(true);
        log.info("获取的关联的brandIds:{}",query.getBrandIds());
        return new Response<>(ResponseCode.SUCCESS, cfChargeService.chargeListGroupByChargeSource(query));
    }

    @ApiOperation("spu维度查询所有费用列表")
    @GetMapping("chargeList")
    public Response<Object> chargeList(@SearchAuthority CfChargeListQuery query, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        query.setGroupDetail(false);
        return new Response<>(ResponseCode.SUCCESS, cfChargeService.chargeListGroupByChargeSource(query));
    }

    @ApiOperation("spu维度查询所有费用列表")
    @PostMapping("chargeList")
    public Response<Object> chargeListPost(@SearchAuthority @RequestBody CfChargeListQuery query, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        query.setGroupDetail(false);
        return new Response<>(ResponseCode.SUCCESS, cfChargeService.chargeListGroupByChargeSource(query));
    }

    @ApiOperation("手动创建费用")
    @PutMapping("saveCharge")
    @Login
    public Response<Object> saveCharge(@RequestBody CfChargeSaveQuery saveQuery, @ApiIgnore UserVO userVO, @RequestHeader(value = Constant.AUTHORIZATION_TOKEN) String token) {
        return new Response<>(ResponseCode.SUCCESS, cfChargeService.saveCharge(saveQuery, userVO));
    }

    @ApiOperation("修改费用")
    @PostMapping("updateCharge")
    @Login
    public Response<Object> updateCharge(@RequestBody CfChargeSaveQuery saveQuery, @ApiIgnore UserVO userVO, @RequestHeader(value = Constant.AUTHORIZATION_TOKEN) String token) {
        cfChargeService.updateCharge(saveQuery, userVO);
        return new Response<>(ResponseCode.SUCCESS);
    }


    @ApiOperation("费用详情")
    @GetMapping("detailCharge/{chargeId}")
    public Response<CfChargeDetailVO> detailCharge(@PathVariable Long chargeId) {
        CfChargeDetailVO o = cfChargeService.detailCharge(chargeId);
        return new Response<>(ResponseCode.SUCCESS, o);
    }

    @ApiOperation("费用状态修改")
    @GetMapping("updateChargeCheckStatus/{chargeId}")
    @Login
    public Response<Object> updateChargeCheckStatus(@PathVariable Long chargeId, int status,
                                                    @ApiIgnore UserVO userVO, @RequestHeader(value = Constant.AUTHORIZATION_TOKEN) String token) {
        cfChargeService.updateChargeCheckStatus(chargeId, status, userVO);
        return new Response<>(ResponseCode.SUCCESS);
    }

    @ApiOperation("批量费用状态修改")
    @PostMapping("updateChargeListCheckStatus")
    @Login
    public Response<Object> updateChargeListCheckStatus(@RequestBody UpdateChargeStatusDTO dto, @ApiIgnore UserVO userVO, @RequestHeader(value = Constant.AUTHORIZATION_TOKEN) String token) {
        cfChargeService.updateChargeListCheckStatus(dto.getChargeIds(), dto.getStatus(), userVO);
        return new Response<>(ResponseCode.SUCCESS);
    }

    @ApiOperation("批量费用状态修改")
    @PostMapping("updateCheckStatus")
    @Login
    public Response<Object> updateCheckStatus(@RequestBody UpdateChargeStatusDTO dto, @ApiIgnore UserVO userVO, @RequestHeader(value = Constant.AUTHORIZATION_TOKEN) String token) {
        cfChargeService.updateCheckStatus(dto.getChargeIds(), dto.getStatus(), userVO);
        return new Response<>(ResponseCode.SUCCESS);
    }

    @ApiOperation("删除费用和账单得到关联关系")
    @PostMapping("delRelevance")
    public Response<Object> delRelevance(@RequestBody CfChargeRelevanceVO vo) {
        try {
            int i = cfChargeService.delRelevance(vo.getInvoiceNo(), vo.getChargeIds());
            return new Response<>(ResponseCode.SUCCESS, i);
        } catch (FinanceException e) {
            e.printStackTrace();
            return new Response(e);
        }
    }



}
