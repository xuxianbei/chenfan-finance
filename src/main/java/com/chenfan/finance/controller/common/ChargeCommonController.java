package com.chenfan.finance.controller.common;

import com.chenfan.common.vo.Response;
import com.chenfan.finance.model.dto.CfChargeCommonDto;
import com.chenfan.finance.model.dto.CfChargeListQuery;
import com.chenfan.finance.model.dto.ChargeCommonUpdateDto;
import com.chenfan.finance.model.dto.SplitChargeCommonDto;
import com.chenfan.finance.model.vo.CfChargeCommonVo;
import com.chenfan.finance.model.vo.McnChargeCommonVO;
import com.chenfan.finance.service.common.ChargeCommonService;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.chenfan.privilege.common.config.SearchAuthority;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 费用
 *
 * @author: xuxianbei
 * Date: 2021/2/27
 * Time: 14:21
 * Version:V1.0
 */
@Slf4j
@RestController
@RequestMapping("charge/common")
public class ChargeCommonController {

    @Autowired
    private ChargeCommonService chargeCommonService;

    /**
     * 列表
     *
     * @return
     */
    @PostMapping("/list")
    public Response<PageInfo<CfChargeCommonVo>> list(@SearchAuthority  CfChargeCommonDto cfChargeCommonDto) {
        log.info("获取的关联的brandIds:{},companyIds:{},userIds:{}", cfChargeCommonDto.getBrandIds(),
                 cfChargeCommonDto.getCompanyIds(), cfChargeCommonDto.getUserIds());
        return PageInfoUtil.smartSuccess(chargeCommonService.list(cfChargeCommonDto));
    }


    /**
     * 详情
     * @param chargeId
     * @return
     */
    @GetMapping("/detail")
    public Response<CfChargeCommonVo> detail(@RequestParam Long chargeId) {
        return PageInfoUtil.smartSuccess(chargeCommonService.detail(chargeId));
    }


    /**
     * 删除
     *
     * @param chargeCommonUpdateDto
     * @return
     */
    @PostMapping("/delete")
    public Response<Integer> updateDelete(@Validated @RequestBody ChargeCommonUpdateDto chargeCommonUpdateDto) {
        return PageInfoUtil.smartSuccess(chargeCommonService.updateDelete(chargeCommonUpdateDto));
    }

    /**
     * 作废
     * @param chargeId
     * @return
     */
    @PostMapping(value = "/invalid/{chargeId}",produces = {"application/json;charset=UTF-8"})
    public Response updateInvalid(@PathVariable("chargeId") Long chargeId){
        return chargeCommonService.updateInvalid(chargeId);
    }

    /**
     * 作废前的检查
     * @param chargeId
     * @return
     */
    @PostMapping(value = "/invalid/check/{chargeId}",produces = {"application/json;charset=UTF-8"})
    public Response invalidCheck(@PathVariable("chargeId") Long chargeId){
        return chargeCommonService.invalidCheck(chargeId,new ArrayList<>(),new ArrayList<>());
    }

    /**
     * 结算信息列表
     *
     * @return
     */
    @GetMapping("/balance")
    public Response<List<String>> getBalance(String balance) {
        return PageInfoUtil.smartSuccess(chargeCommonService.getBalance(balance));
    }

    /**
     * 财务主体列表
     *
     * @return
     */
    @GetMapping("/finance/entity")
    public Response<List<String>> getFinanceEntity(String entity) {
        return PageInfoUtil.smartSuccess(chargeCommonService.getFinanceEntity(entity));
    }

    /**
     * 费用账单抬头列表
     * @param value
     * @return
     */
    @GetMapping("/fields/invoiceTitle")
    public Response<List<String>> fieldsInvoiceTitle(String value) {
        return PageInfoUtil.smartSuccess(chargeCommonService.fieldsInvoiceTitle(value));
    }


    /**
     * 选择费用
     * @param cfChargeListQuery
     * @return
     */
    @PostMapping("/select")
    public Response<PageInfo<CfChargeCommonVo>> select(@RequestBody CfChargeListQuery cfChargeListQuery) {
        return PageInfoUtil.smartSuccess(chargeCommonService.select(cfChargeListQuery));
    }


    /**
     * 拆分费用
     * @param splitChargeCommonDto
     * @return
     */
    @PostMapping("/split")
    public Response<Long> splitChargeCommon(@Validated @RequestBody SplitChargeCommonDto splitChargeCommonDto){
        return PageInfoUtil.smartSuccess(chargeCommonService.splitChargeCommon(splitChargeCommonDto));
    }


    /**
     * 根据来源单号、来源明细查询是否有未删除状态费用
     *
     * @param chargeSourceDetail
     * @param chargeSourceCode
     * @return Response<Integer>
     */
    @Deprecated
    @GetMapping("/getRelatedCount")
    public Response<Integer> getRelatedCount(@RequestParam(required = false) String chargeSourceCode, @RequestParam(required = false) String chargeSourceDetail) {
        return Response.success(chargeCommonService.getRelatedCount(chargeSourceCode, chargeSourceDetail));
    }

    /**
     * 根据来源单号、来源明细查询是否有未作废未删除状态费用
     *
     * @return Response<Integer>
     */
    @GetMapping("/getInvalid")
    public Response<Integer> getInvalid(@RequestParam(required = false) String chargeSourceCode, @RequestParam(required = false) String chargeSourceDetail) {
        return Response.success(chargeCommonService.getInvalid(chargeSourceCode, chargeSourceDetail));
    }

    /**
     * 查询mcn收入合同关联费用
     *
     * @param chargeSourceCode
     * @return Response<PageInfo < McnChargeCommonVO>>
     */
    @GetMapping("/getMcnCharge")
    public Response<List<McnChargeCommonVO>> getMcnCharge(@RequestParam String chargeSourceCode) {
        return Response.success(chargeCommonService.getMcnCharge(chargeSourceCode));
    }

}
