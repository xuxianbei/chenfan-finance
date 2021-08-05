package com.chenfan.finance.controller.common;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.ccp.common.result.R;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.enums.BankAndCashEnum;
import com.chenfan.finance.enums.ClearHeaderEnum;
import com.chenfan.finance.enums.InvoiceHeaderEnum;
import com.chenfan.finance.enums.TaxInvoiceHeaderEnum;
import com.chenfan.finance.model.*;
import com.chenfan.finance.model.dto.ApprovalFlowDTO;
import com.chenfan.finance.service.ApprovalFlowService;
import com.chenfan.finance.service.CfBankAndCashService;
import com.chenfan.finance.service.CfClearHeaderService;
import com.chenfan.finance.service.CfInvoiceHeaderService;
import com.chenfan.finance.service.common.ApprovalJson;
import com.chenfan.finance.service.common.InvoiceCommonService;
import com.chenfan.finance.service.common.McnClearService;
import com.chenfan.finance.service.common.TaxInvoiceCommonService;
import com.chenfan.finance.service.common.state.BankAndCashStateService;
import com.chenfan.finance.service.common.state.InvoiceStateService;
import com.chenfan.finance.service.common.state.TaxInvoiceStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.chenfan.finance.enums.InvoiceHeaderEnum.CUSTOMER_INVOICE_WAY_TAX_INVOICE_LATER;
import javax.annotation.Resource;
import java.util.*;

/**
 * 审批回调
 *
 * @author: xuxianbei
 * Date: 2021/3/4
 * Time: 17:27
 * Version:V1.0
 */
@RestController
@RequestMapping
@Slf4j
public class CallbackController {

    /**
     * 业务不够复杂，未提炼接口
     */
    @Autowired
    private InvoiceStateService invoiceStateService;

    @Autowired
    private InvoiceCommonService invoiceCommonService;

    @Autowired
    private TaxInvoiceStateService taxInvoiceStateService;

    @Autowired
    private BankAndCashStateService bankAndCashStateService;

    @Autowired
    private McnClearService mcnClearService;

    @Resource
    private ApprovalFlowService approvalFlowService;

    @Autowired
    private CfInvoiceHeaderService invoiceHeaderService;

    @Autowired
    private TaxInvoiceCommonService taxInvoiceCommonService;

    @Autowired
    private CfBankAndCashService actualPaymentService;

    @Autowired
    private CfClearHeaderService cfClearHeaderService;

    /**
     * 我希望这里提供一个标准可以扩展的方式，
     * 简单的说我希望这里的实现可以在其他关联的地方实现。而不是在这里实现
     * 第一阶段思路：采用concurrentHashMap实现，存储回调方法。但是有一个问题，随着系统长时间运行这里面内存消耗会异常的大
     * 那么就需要写定时清理concurrentHashMap。缺点：增加代码复杂性，增加了数据异常的风险。此方案作废
     * 第二阶段思路：采用C++的指针函数的概念。
     *
     * @param callback
     * @return
     */
    @PostMapping("/callback")
    public R<String> callback(@RequestBody String callback) {
        log.info("callback-> " + callback);
        ApprovalCallBack approvalCallBack = JSONObject.parseObject(callback, ApprovalCallBack.class);
        ApprovalJson approvalJson = JSONObject.parseObject(approvalCallBack.getParam(), ApprovalJson.class);
        Boolean status=getStatus(approvalCallBack);
        ApprovalFlowDTO dto = new ApprovalFlowDTO();
        dto.setApprovalId(approvalCallBack.getApprovalId());
        if (Objects.nonNull(approvalCallBack.getNextApprovalUserId())) {
            dto.setTargetUserId(Arrays.asList(approvalCallBack.getNextApprovalUserId().split(",")));
            if(!StringUtils.isEmpty(approvalCallBack.getNextApprovalUserName())){
                dto.setTargetUserName(Arrays.asList(approvalCallBack.getNextApprovalUserName().split(",")));
            } else {
                dto.setTargetUserName(Collections.emptyList());
            }
        }
        //放入备注
        CFRequestHolder.setInvalidThreadLocal(approvalCallBack.getRemark());
        if (approvalJson.getClassName().equals(CfInvoiceHeader.class.getSimpleName())) {
            CfInvoiceHeader cfInvoiceHeader = JSONObject.parseObject(approvalJson.getJson(), CfInvoiceHeader.class);
            if (!approvalCallBack.getApprovalFlag()) {
                invoiceStateService.updateState(cfInvoiceHeader.getInvoiceId(), InvoiceHeaderEnum.INVOICE_STATUS_FOUTTEEN.getCode());
            }
            if (success(approvalCallBack)) {
                //这个代码就有点恶心了。这里又涉及了一部分业务，而且还是零散的业务
                cfInvoiceHeader = invoiceCommonService.selectById(cfInvoiceHeader.getInvoiceId());
                //如果是后补票 直接跳到已开票
                if (InvoiceHeaderEnum.valueOfCustomerInvoiceStatus(cfInvoiceHeader.getCustomerInvoiceWay()) == CUSTOMER_INVOICE_WAY_TAX_INVOICE_LATER) {
                    invoiceStateService.updateState(cfInvoiceHeader.getInvoiceId(), InvoiceHeaderEnum.INVOICE_STATUS_TWELVE.getCode());
                } else {
                    invoiceStateService.updateState(cfInvoiceHeader.getInvoiceId(), InvoiceHeaderEnum.INVOICE_STATUS_ELEVNE.getCode());
                }
            }
            invoiceHeaderService.approvalCallback(cfInvoiceHeader,dto,status);
        } else if (approvalJson.getClassName().equals(CfTaxInvoiceHeader.class.getSimpleName())) {
            CfTaxInvoiceHeader cfInvoiceHeader = JSONObject.parseObject(approvalJson.getJson(), CfTaxInvoiceHeader.class);
            if (!approvalCallBack.getApprovalFlag()) {
                taxInvoiceStateService.updateState(cfInvoiceHeader.getTaxInvoiceId(), TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_THREE.getCode());
            }
            if (success(approvalCallBack)) {
                taxInvoiceStateService.updateState(cfInvoiceHeader.getTaxInvoiceId(), TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FOUR.getCode());
            }
            taxInvoiceCommonService.approvalCallback(cfInvoiceHeader,dto,status);
        } else if (approvalJson.getClassName().equals(CfBankAndCash.class.getSimpleName())) {
            CfBankAndCash cfBankAndCash = JSONObject.parseObject(approvalJson.getJson(), CfBankAndCash.class);
            if (!approvalCallBack.getApprovalFlag()) {
            }
            if (success(approvalCallBack)) {
                bankAndCashStateService.updateState(cfBankAndCash.getBankAndCashId(), BankAndCashEnum.BANK_AND_CASH_STATUS_TWO.getCode());
            }
            actualPaymentService.approvalCallback(cfBankAndCash,dto,status);
        } else if (approvalJson.getClassName().equals(CfClearHeader.class.getSimpleName())) {
            CfClearHeader cfClearHeader = JSONObject.parseObject(approvalJson.getJson(), CfClearHeader.class);
            if (!approvalCallBack.getApprovalFlag()) {
                mcnClearService.updateState(cfClearHeader.getClearId(), ClearHeaderEnum.CLEAR_STATUS_FOUR.getCode());
            }
            if (success(approvalCallBack)) {
                mcnClearService.updateState(cfClearHeader.getClearId(), ClearHeaderEnum.AFTER_CLEAR.getCode());
            }
            cfClearHeaderService.approvalCallback(cfClearHeader,dto,status);
        }


        return R.data("success");
    }

    private boolean success(ApprovalCallBack approvalCallBack) {
        return approvalCallBack.getApprovalFlag() && approvalCallBack.getApprovalFinished();
    }

    private Boolean getStatus(ApprovalCallBack approval){
        Boolean status=null;
        if(Objects.nonNull(approval.getApprovalFlag())){
            if (!Boolean.TRUE.equals(approval.getApprovalFlag())) {
                status = false;
            } else if (Boolean.TRUE.equals(approval.getApprovalFlag()) && approval.getApprovalFinished()) {
                status = true;
            }
        }
        return  status;
    }

    private ApprovalFlow getApprovalFlow(Long approvalId, int index) {
        int times = 5;
        ApprovalFlow approvalFlow = approvalFlowService.getOne(Wrappers.<ApprovalFlow>lambdaQuery().eq(ApprovalFlow::getApprovalId, approvalId));
        if(Objects.isNull(approvalFlow) && index < times) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error(String.format("自旋获取审批流异常！approvalId=%s,retry=%s",approvalId,index),e);
                if(Thread.currentThread().isInterrupted()){
                    Thread.currentThread().interrupt();
                }
            } finally {
                approvalFlow = getApprovalFlow(approvalId, ++index);
            }
        }
        return approvalFlow;
    }

}
