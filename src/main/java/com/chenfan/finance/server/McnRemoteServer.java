package com.chenfan.finance.server;

import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.Response;
import com.chenfan.finance.server.dto.AccountInfoQueryDTO;
import com.chenfan.finance.server.dto.ExcutionSetPaidDTO;
import com.chenfan.finance.server.remote.model.IncomeContract;
import com.chenfan.finance.server.remote.request.CommentAddDTO;
import com.chenfan.finance.server.remote.vo.ExcutionSettleInfoVO;
import com.chenfan.finance.server.remote.vo.FinanceAccountInfoVO;
import com.chenfan.finance.server.remote.vo.InvoiceAccountInfoVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MCN 合同执行单
 *
 * @author: xuxianbei
 * Date: 2021/3/1
 * Time: 18:17
 * Version:V1.0
 */
@Component
@FeignClient(name = "chenfan-cloud-mcn")
public interface McnRemoteServer {


    /**
     * 财务结算单的执行单信息
     *
     * @param chargeType
     * @param chargeSourceCode
     * @return
     */
    @GetMapping("/excutionOrder/settleInfo")
    Response<ExcutionSettleInfoVO> getSettleInfo(@RequestParam @ApiParam("1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费") Integer chargeType,
                                                 @RequestParam String chargeSourceCode);


    @ApiOperation("财务结算单的执行单信息（新：费用使用）")
    @GetMapping("/excutionOrder/settleInfoForCharge")
    Response<ExcutionSettleInfoVO> getSettleInfoForCharge(@RequestParam @ApiParam("1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费") Integer chargeType, @RequestParam String chargeSourceCode);


    /**
     * 财务查询账户信息
     *
     * @param chargeType
     * @param chargeSourceCode
     * @param chargeSourceDetail
     * @return
     */
    @GetMapping("/excutionOrder/accountInfo")
    Response<FinanceAccountInfoVO> getFinanceAccount(@RequestParam @ApiParam("1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费") Integer chargeType, @RequestParam String chargeSourceCode, @RequestParam String chargeSourceDetail);


    /**
     * 打款
     *
     * @param excutionSetPaidDTOS
     * @return
     */
    @GetMapping("/excutionOrder/setPaid")
    Response<Object> setPaid(@RequestBody List<ExcutionSetPaidDTO> excutionSetPaidDTOS);

    /**
     * 财务批量查询关联单据账户信息
     *
     * @param accountInfoQueryList
     * @return
     */
    @PostMapping("/excutionOrder/batchInvoice/accountInfo")
    Response<List<InvoiceAccountInfoVO>> getAccountInfo(@RequestBody List<AccountInfoQueryDTO> accountInfoQueryList);


    /**
     * 财务修改年度返点审批单状态
     *
     * @param rebateContractCodes {@link List<String>}
     * @param status              {@link Integer}
     * @return {@link Response}
     */
    @GetMapping(value = "/annual/rebate/finance/status")
    Response<Object> financeStatusChange(@RequestParam List<String> rebateContractCodes, @RequestParam Integer status);

    /**
     * 根据合同编号获取收入合同
     *
     * @param contractCode 来源单号
     * @return IncomeContract
     */
    @GetMapping("/incomeContract/incomeContract/getByCode")
    Response<IncomeContract> getByCode(@RequestParam String contractCode);

    /**
     * 修改回款状态
     *
     * @param payBackStatus 回款状态
     * @param contractCode  收入合同编号
     * @return
     */
    @Deprecated
    @GetMapping("/incomeContract/payBackStatus/change")
    Response<Integer> changePayBackStatus(@ApiParam("0未回款；1部分回款；2已回款") @RequestParam Integer payBackStatus,
                                          @RequestParam String contractCode);

    /**
     * 核销后回款回调
     *
     * @param payBackAmount 回款金额
     * @param contractCode  收入合同编号
     * @return
     */
    @GetMapping("/incomeContract/payBack/callback")
    Response<Boolean> payBackCallBack(@RequestParam BigDecimal payBackAmount,
                                          @RequestParam String contractCode);


    /**
     * 新增评论
     * @param commentAddDTO
     * @return
     */
    @PostMapping("/commentLog/add")
    Response<Boolean> addComment(@RequestBody CommentAddDTO commentAddDTO);

    /**
     * 开票发票回调 （增量）
     *
     * @param taxInvoiceNo   开票流水号 用于请求幂等去重
     * @param invoicedAmount 开票金额
     * @param contractCode   收入合同编号
     * @return
     */
    @GetMapping("/incomeContract/invoiced/callback")
    Response<Boolean> invoicedCallBack(@RequestParam String taxInvoiceNo,
                                       @RequestParam BigDecimal invoicedAmount,
                                       @RequestParam String contractCode);
}
