package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.annotation.Login;
import com.chenfan.finance.commons.annotation.MultiLockInt;

import com.chenfan.finance.enums.OperateLockEnum;
import com.chenfan.finance.model.CfInvoiceSettlement;
import com.chenfan.finance.model.bo.CfInvoiceSettlementBO;
import com.chenfan.finance.model.dto.InvoiceSettlementPercentDTO;
import com.chenfan.finance.model.vo.CfInvoiceHeaderDetailVO;
import com.chenfan.finance.model.vo.CfInvoiceHeaderPrintVo;
import com.chenfan.finance.model.vo.CfInvoiceSettlementInfoVo;
import com.chenfan.finance.model.vo.ChargeInVO;

import com.chenfan.finance.service.CfInvoiceSettlementService;

import com.chenfan.finance.utils.OperateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


/**
 * @author lqliu
 * @Description: 结算单管理controller
 * @date 2020/10/20 16:00
 */
@Slf4j
@RestController
@RequestMapping("settlement")
@Api(tags = "结算单管理controller")
public class CfSettlementController {

    @Autowired
    private CfInvoiceSettlementService cfInvoiceSettlementService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Login
    @ApiOperation(value = "新增结算单", notes = "新增结算单", produces = "application/json")
    @PostMapping(value = "createSettlement", produces = {"application/json;charset=UTF-8"})
    @MultiLockInt(paramNames={"invoiceNo"},paramClass =String.class,unlockEnum = OperateLockEnum.SETTLEMENT_CREATE,isCheck = true,isCollections = false)
    public Response<Integer> createSettlement(@RequestBody CfInvoiceSettlement cfInvoiceSettlement, @ApiIgnore UserVO userVO, @RequestHeader(value = Constant.AUTHORIZATION_TOKEN) String token) {
        log.info("进入方法>>addSettlement --新增结算单");
        cfInvoiceSettlementService.addSettlement(cfInvoiceSettlement, userVO);
        return new Response<>(ResponseCode.SUCCESS);
    }

    @Login
    @ApiOperation(value = "结算审批流")
    @PostMapping(value = "updateSettlement")
    public Response<String> updateSettlement(@RequestBody List<CfInvoiceSettlementBO> cfInvoiceSettlement, @ApiIgnore UserVO userVO, @RequestHeader(value = Constant.AUTHORIZATION_TOKEN) String token) {
        log.info("进入方法>>updateSettlement --审批流结算单");
        cfInvoiceSettlementService.auditSettlement(cfInvoiceSettlement, userVO);
        return new Response<>(ResponseCode.SUCCESS);
    }

    @Login
    @ApiOperation("账单详情-付款申请单打印")
    @PostMapping("paymentReqPrint/{invoiceSettlementId}")
    public Response<CfInvoiceHeaderPrintVo> getPaymentReqPrint(@PathVariable Long invoiceSettlementId, @ApiIgnore UserVO userVO, @RequestHeader(value = Constant.AUTHORIZATION_TOKEN) String token) {
        log.info("进入方法>>>--paymentReqPrint--付款申请单打印");
        return new Response<>(ResponseCode.SUCCESS, cfInvoiceSettlementService.paymentReqPrint(invoiceSettlementId, userVO));
    }

    @ApiOperation("账单详情-打印")
    @PostMapping("invoicePrint/{invoiceSettlementId}/{type}")
    public Response<CfInvoiceHeaderDetailVO> getInvoicePrint(@PathVariable(value = "invoiceSettlementId") Long invoiceSettlementId,@PathVariable(value = "type",required = false) Integer type) {
        CfInvoiceHeaderDetailVO print = cfInvoiceSettlementService.print(invoiceSettlementId);
        List<ChargeInVO> chargeInList = print.getChargeInList();
        Boolean a=Objects.isNull(type)||type==0;
        for (ChargeInVO cf: chargeInList) {
            OperateUtil.onConvertedDecimal(cf,"actualAmount","settlementAmount","postponeDeductionsTotal","taxMoney","unitAmount");
            if(a){
                cf.setTaxMoney(null);
            }
        }
        OperateUtil.onConvertedDecimal(print,"adjustRealMoney");
        return new Response<>(ResponseCode.SUCCESS,print);
    }
    @Login
    @ApiOperation(value = "编辑结算单", notes = "编辑结算单", produces = "application/json")
    @MultiLockInt(paramNames={"invoiceSettlementId"},isCheck = true,isCollections = false)
    @PostMapping(value = "updateSettle", produces = {"application/json;charset=UTF-8"})
    public Response<Integer> updateSettle(@RequestBody CfInvoiceSettlement cfInvoiceSettlement, @ApiIgnore UserVO userVO, @RequestHeader(value = Constant.AUTHORIZATION_TOKEN) String token) {
        log.info("进入方法>>updateSettle --编辑结算单");
        cfInvoiceSettlementService.updateSettle(cfInvoiceSettlement, userVO);
        return new Response<>(ResponseCode.SUCCESS);
    }

    @ApiOperation(value = "获取结算单的结算金额，结算余额")
    @PostMapping(value = "getSettlementAmount", produces = {"application/json;charset=UTF-8"})
    public Response<Object> getSettlementAmount(@RequestBody CfInvoiceSettlement cfInvoiceSettlement) {
        log.info("进入方法>>getSettlementAmount --获取结算单的结算金额，结算余额");
        return new Response<>(ResponseCode.SUCCESS, cfInvoiceSettlementService.getSettlementAmount(cfInvoiceSettlement));
    }

    @ApiOperation("账单详情-结算明细导出")
    @PostMapping("invoiceDetailSettlementExport")
    public Response<Object> invoiceDetailSettlementExport(@RequestBody InvoiceSettlementPercentDTO invoiceSettlementPercentDTO, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token, HttpServletResponse response) {
        log.info("进入方法>>>--invoiceDetailSettlementExport--账单详情结算明细导出");
        UserVO user = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
        return new Response<>(ResponseCode.SUCCESS, cfInvoiceSettlementService.invoiceDetailSettlementExport(invoiceSettlementPercentDTO,user,response));
    }

    @ApiOperation("账单详情-结算单明细详情列表")
    @GetMapping("invoiceDetailSettlementList/{invoiceSettlementId}")
    public Response<CfInvoiceSettlementInfoVo> getInvoiceSettlementList(@PathVariable Long invoiceSettlementId) {
        return new Response<>(ResponseCode.SUCCESS, cfInvoiceSettlementService.getInvoiceSettlementList(invoiceSettlementId));
    }

    @ApiOperation("账单详情-修改打印付款申请状态")
    @PostMapping("updateSettleStatus")
    public Response<Object> updateSettleStatus(@RequestBody CfInvoiceSettlement cfInvoiceSettlement) {
        log.info("进入方法>>updateSettleStatus --账单详情-修改打印付款申请状态");
        cfInvoiceSettlementService.updateSettleStatus(cfInvoiceSettlement);
        return new Response<>(ResponseCode.SUCCESS);
    }


}
