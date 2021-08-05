package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.model.dto.CfClearAddAndUpdateDto;
import com.chenfan.finance.model.dto.CfClearListDto;
import com.chenfan.finance.model.vo.ClearHeaderDetailVo;
import com.chenfan.finance.model.vo.ClearHeaderVo;
import com.chenfan.finance.model.vo.ClearUserVo;
import com.chenfan.finance.service.CfClearHeaderService;
import com.chenfan.privilege.common.config.SearchAuthority;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 核销-废弃
 * @Description: 财务核销controller
 * @Author:lywang
 * @Date:2020/8/22
 */
@RestController
@RequestMapping(value = "cfClear/")
@Api(tags = "财务-核销相关接口")
public class CfClearController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CfClearController.class);
    @Autowired
    private CfClearHeaderService cfClearHeaderService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @ApiOperation(value = "财务-核销单新建(选择费用后保存)", notes = "核销单新建(选择费用后保存)")
    @PostMapping(value = "saveAfterCharge")
    public Response<String> saveAfterCharge(@Validated(CfClearAddAndUpdateDto.Mcn.class) @RequestBody CfClearAddAndUpdateDto cfClearAddAndUpdateDto, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        LOGGER.info("进入方法>>>--saveAfterCharge--生成报销单");
        String clearNo;
        UserVO user = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
        if (null == user) {
            return new Response(ResponseCode.LOGIN_ERROR);
        }
        clearNo = cfClearHeaderService.create(cfClearAddAndUpdateDto, user);
        if (StringUtils.isNotBlank(clearNo)) {
            LOGGER.info("退出方法>>>--saveAfterCharge--核销单新建");
            return new Response<>(ResponseCode.SUCCESS, clearNo);
        }
        LOGGER.info("退出方法>>>--saveAfterCharge--核销单新建");
        return new Response<>(ResponseCode.SAVE_ERROR);
    }

    @ApiOperation(value = "财务-核销单新建(选择费用后保存)", notes = "核销单新建(选择费用后保存)")
    @PostMapping(value = "update")
    public Response<Object> update(@Validated(value = CfClearAddAndUpdateDto.update.class) @RequestBody CfClearAddAndUpdateDto cfClearAddAndUpdateDto, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        LOGGER.info("进入方法>>>--update--生成报销单");
        String clearNo;
        UserVO user = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
        if (null == user) {
            return new Response(ResponseCode.LOGIN_ERROR);
        }
        clearNo = cfClearHeaderService.update(cfClearAddAndUpdateDto, user);

        LOGGER.info("退出方法>>>--update--核销单新建");
        return new Response<>(ResponseCode.SUCCESS, clearNo);
    }

    @ApiOperation(value = "财务-核销单列表", notes = "核销列表展示")
    @PostMapping(value = "list")
    public Response<PageInfo<ClearHeaderVo>> list(@SearchAuthority  CfClearListDto cfClearListDto) {
        LOGGER.info("获取的关联的brandIds:{},companyIds:{},userIds:{}", cfClearListDto.getBrandIds(),
                    cfClearListDto.getCompanyIds(), cfClearListDto.getUserIds());
        PageInfo<ClearHeaderVo> list = cfClearHeaderService.customList(cfClearListDto);
        return new Response(ResponseCode.SUCCESS, list);
    }

    @ApiOperation(value = "财务-核销单列表", notes = "核销列表展示")
    @GetMapping(value = "detail")
    public Response<ClearHeaderDetailVo> detail(String clearNo) {
        ClearHeaderDetailVo clearHeaderDetailVo = cfClearHeaderService.detail(clearNo);
        return new Response(ResponseCode.SUCCESS, clearHeaderDetailVo);
    }


    @ApiOperation(value = "财务-核销单删除", notes = "核销列表删除")
    @GetMapping(value = "delete")
    public Response<Integer> delete(String clearNos) {
        LOGGER.info("进入方法>>>--detail--财务-核销单详情");
        Integer result = cfClearHeaderService.delete(Lists.newArrayList(clearNos.split(",")));
        LOGGER.info("退出方法>>>--detail--财务-核销单详情");
        return new Response(ResponseCode.SUCCESS, result);
    }

    @ApiOperation(value = "财务-核销人列表", notes = "核销人列表")
    @GetMapping(value = "clearUsers")
    public Response<List<ClearUserVo>> clearUsers() {
        LOGGER.info("进入方法>>>--clearUsers--财务-核销人列表");
        List<ClearUserVo> clearUserVos = cfClearHeaderService.clearUsers();
        LOGGER.info("退出方法>>>--clearUsers--财务-核销人列表");
        return new Response(ResponseCode.SUCCESS, clearUserVos);
    }


    /**
     * 导出
     *
     * @return com.chenfan.common.vo.Response<java.lang.Object>
     * @author zq
     * @date 2020/8/19 16:53
     */
    @ApiOperation(value = "财务-实收付款单导出", notes = "财务-实收付款单导出")
    @PostMapping(value = "export")
    public Response<Object> export(@RequestHeader(Constant.AUTHORIZATION_TOKEN) String token, @SearchAuthority @RequestBody CfClearListDto cfClearListDto,
                                   HttpServletResponse response) {
        LOGGER.info("进入方法>>实收付款导出...");
        UserVO userVO = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
        if (null == userVO) {
            return new Response<>(Constant.UNAUTHORIZED, "用户登录失效");
        }
        try {
            cfClearHeaderService.export(cfClearListDto, userVO, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("退出方法>>实收付款导出...");
        return new Response(ResponseCode.SUCCESS);
    }

}
