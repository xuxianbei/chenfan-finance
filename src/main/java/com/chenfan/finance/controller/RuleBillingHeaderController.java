package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.exception.FinanceException;
import com.chenfan.finance.enums.ErrorMessageEnum;
import com.chenfan.finance.model.bo.RuleBillingHeaderBo;
import com.chenfan.finance.model.bo.RuleBillingHeaderListBo;
import com.chenfan.finance.model.vo.RuleBillingHeaderListVo;
import com.chenfan.finance.model.vo.RuleBillingHeaderVo;
import com.chenfan.finance.service.RuleBillingHeaderService;
import com.chenfan.privilege.common.config.SearchAuthority;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * @Description:  计费方案管理controller
 * @author 2062
 */
@RestController
@RequestMapping("ruleBill")
@Api(tags = "计费方案管理controller")
public class RuleBillingHeaderController {

    private static Logger logger = LoggerFactory.getLogger(RuleBillingHeaderController.class);

    @Autowired
    private RuleBillingHeaderService ruleBillingHerderService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "计费方案列表", notes = "计费方案列表", produces = "application/json")
    @GetMapping(value = "list", produces = {"application/json;charset=UTF-8"})
    public Response<List<RuleBillingHeaderListVo>> ruleBillList(@RequestHeader(Constant.AUTHORIZATION_TOKEN) String token, @SearchAuthority RuleBillingHeaderListBo ruleBillingHeaderBo) {
        logger.info("进入计费方案列表>>");
        try {
            return new Response(ResponseCode.SUCCESS, ruleBillingHerderService.list(ruleBillingHeaderBo));
        } catch (Exception e) {
            return new Response<>(ResponseCode.NOT_FIND_DATABASE_CONN);
        }
    }
    @ApiOperation(value = "计费方案-启用1/停用2/删除0", notes = "计费方案-启用/停用/删除", produces = "application/json")
    @GetMapping(value = "updateState", produces = {"application/json;charset=UTF-8"})
    public Response updateState(@Param("state") Integer state,@Param("ruleBillingId")  Long ruleBillingId)throws FinanceException,ParseException {
        logger.info("进入计费方案-启用/停用>>");
        int i = ruleBillingHerderService.updateState(state, ruleBillingId);
        if(i <= 0){
            return new Response(ResponseCode.SAVE_ERROR);
        }
        return new Response(ResponseCode.SUCCESS,i);
    }

    @ApiOperation(value = "计费方案详情", notes = "计费方案详情", produces = "application/json")
    @GetMapping(value = "detail", produces = {"application/json;charset=UTF-8"})
    public Response<RuleBillingHeaderVo> detail(Long ruleBillingId) {
        logger.info("进入计费方案详情>>");
        try {
            return new Response(ResponseCode.SUCCESS,ruleBillingHerderService.detail(ruleBillingId));
        } catch (Exception e) {
            return new Response<>(ResponseCode.NOT_FIND_DATABASE_CONN);
        }
    }
    @ApiOperation(value = "计费方案新建", notes = "计费方案新建")
    @PostMapping(value = "addRule")
    public Response<Object> addRule(@RequestBody RuleBillingHeaderBo headerBo, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token)throws FinanceException, ParseException {
        logger.info("进入计费方案新建>>");
        UserVO user = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
        if(null == user){
            return new Response(ResponseCode.LOGIN_ERROR);
        }

        try {
            Long aLong = ruleBillingHerderService.addRule(headerBo, user);
            return new Response(ResponseCode.SUCCESS,aLong);
        } catch (Exception e) {
            logger.error(ErrorMessageEnum.CREATE_FAIL.getMsg(), e);
            return new Response(e);
        }
    }

    @ApiOperation(value = "计费方案更新", notes = "计费方案更新", produces = "application/json")
    @PostMapping(value = "updateRule", produces = {"application/json;charset=UTF-8"})
    public Response updateRule(@RequestBody RuleBillingHeaderBo headerBo,@RequestHeader(Constant.AUTHORIZATION_TOKEN) String token){
        logger.info("进入计费方案修改>>");
        UserVO user = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
        if(null == user){
            return new Response(ResponseCode.LOGIN_ERROR);
        }
        try {
            Long ruleBillId = ruleBillingHerderService.updateRule(headerBo, user);
            return new Response(ResponseCode.SUCCESS,ruleBillId);
        } catch (Exception e) {
            logger.error(ErrorMessageEnum.UPDATE_ERROR.getMsg(), e);
            return new Response(e);
        }
    }
}
