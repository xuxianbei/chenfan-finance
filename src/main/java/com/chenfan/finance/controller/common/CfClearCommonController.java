package com.chenfan.finance.controller.common;

import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.Response;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.model.dto.CfClearAddAndUpdateDto;
import com.chenfan.finance.model.dto.ClearCommonUpdateStateDto;
import com.chenfan.finance.model.vo.McnClearVo;
import com.chenfan.finance.service.common.McnClearService;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核销
 *
 * @author: xuxianbei
 * Date: 2021/4/4
 * Time: 10:31
 * Version:V1.0
 */
@RestController
@RequestMapping(value = "cfClear/common/")
public class CfClearCommonController {

    @Autowired
    private McnClearService mcnClearService;

    /**
     * 新增
     *
     * @param cfClearAddAndUpdateDto
     * @param token
     * @return
     */
    @Deprecated
    @PostMapping("add")
    public Response<String> add(@Validated(CfClearAddAndUpdateDto.Mcn.class) @RequestBody CfClearAddAndUpdateDto cfClearAddAndUpdateDto, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        return PageInfoUtil.smartSuccess(mcnClearService.add(cfClearAddAndUpdateDto));
    }

    /**
     * 新增(v1.4.0核销功能重构)
     *
     * @param cfClearAddAndUpdateDto
     * @param token
     * @return
     */
    @PostMapping("v1/add")
    public Response<String> addV1(@Validated(CfClearAddAndUpdateDto.Mcn.class) @RequestBody CfClearAddAndUpdateDto cfClearAddAndUpdateDto, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        return PageInfoUtil.smartSuccess(mcnClearService.addV1(cfClearAddAndUpdateDto));
    }

    /**
     * 批量提交(v1.4.0核销功能重构)
     *
     * @param cfClearAddAndUpdateDto
     * @return
     */
    @PostMapping("batchCommit")
    public Response<String> batchCommit(@Validated(CfClearAddAndUpdateDto.batchCommit.class) @RequestBody CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
       return Response.success(mcnClearService.batchCommit(cfClearAddAndUpdateDto));
    }

    /**
     * 编辑(v1.4.0)
     *
     * @param cfClearAddAndUpdateDto
     * @return
     */
    @PostMapping("v1/update")
    public Response<String> update(@Validated(CfClearAddAndUpdateDto.McnUpdate.class)@RequestBody CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
        return  PageInfoUtil.smartSuccess(mcnClearService.update(cfClearAddAndUpdateDto));
    }


    /**
     * 新增并提交
     *
     * @param cfClearAddAndUpdateDto
     * @param token
     * @return
     */
    @Deprecated
    @PostMapping("commit")
    public Response<String> commit(@Validated(CfClearAddAndUpdateDto.Mcn.class) @RequestBody CfClearAddAndUpdateDto cfClearAddAndUpdateDto, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        return PageInfoUtil.smartSuccess(mcnClearService.commit(cfClearAddAndUpdateDto));
    }


    /**
     * 状态更新
     *
     * @param cfClearAddAndUpdateDto
     * @return
     */
    @PostMapping("update/state")
    public Response<Integer> updateState(@RequestBody ClearCommonUpdateStateDto cfClearAddAndUpdateDto) {
        CFRequestHolder.setInvalidThreadLocal(cfClearAddAndUpdateDto.getOperationContent());
        return PageInfoUtil.smartSuccess(mcnClearService.updateState(cfClearAddAndUpdateDto.getClearId(), cfClearAddAndUpdateDto.getClearStatus()));
    }


    /**
     * 查询mcn收入合同关联核销
     *
     * @param contractCode
     * @return Response<List<McnClearVo>>
     */
    @GetMapping("getMcnClear")
    public Response<List<McnClearVo>> getMcnClear(@RequestParam String contractCode) {
        return Response.success(mcnClearService.getMcnClear(contractCode));
    }


    /**
     * 批量审核同意
     * @param clearIds
     * @return
     */
    @PostMapping("batchAgree")
    public Response<Integer> batchAgree(@RequestBody List<Long> clearIds) {
        return Response.success(mcnClearService.batchAgree(clearIds));
    }

    /**
     * 获取核销字典（目前只获取结算主体）
     * @return
     */
    @GetMapping("getAllBalance")
    public Response getAllBalance() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> allBalances = mcnClearService.getAllBalance();
        result.put("allBalances", allBalances);
        return Response.success(result);
    }

}
