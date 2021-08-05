package com.chenfan.finance.service.common.state;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.dao.CfClearBankDetailMapper;
import com.chenfan.finance.dao.CfClearDetailMapper;
import com.chenfan.finance.dao.CfClearHeaderMapper;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.CfChargeCommon;
import com.chenfan.finance.model.CfClearBankDetail;
import com.chenfan.finance.model.CfClearDetail;
import com.chenfan.finance.model.CfClearHeader;
import com.chenfan.finance.server.McnRemoteServer;
import com.chenfan.finance.service.common.BankAndCashCommonService;
import com.chenfan.finance.service.common.ChargeCommonService;
import com.chenfan.finance.service.common.state.AbstractStateService;
import com.chenfan.finance.utils.OperateUtil;
import com.chenfan.finance.utils.RpcUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 核销状态
 * @author: xuxianbei
 * Date: 2021/4/6
 * Time: 10:41
 * Version:V1.0
 */
@Service
public class ClearStateService extends AbstractStateService {


    /**
     * 审批流
     */
    public static final Long APPROVAL_PROCESS_ID = 1371280567689019392L;

    @Resource
    private CfClearHeaderMapper cfClearHeaderMapper;

    @Resource
    private CfClearDetailMapper cfClearDetailMapper;

    @Resource
    private CfClearBankDetailMapper clearBankDetailMapper;

    @Autowired
    private BankAndCashCommonService bankAndCashCommonService;

    public static final String BUSINESS_LOCK = "ClearState:";

    @Autowired
    private McnRemoteServer mcnRemoteServer;

    @Autowired
    private ChargeCommonService chargeCommonService;

    /**
     * 实收付流程
     */
    private List<List<GetCode>> clearStatesTaxInvoice = new ArrayList<>();

    {
        clearStatesTaxInvoice.add(Arrays.asList(ClearHeaderEnum.BEFORE_CLEAR, ClearHeaderEnum.CLEAR_STATUS_FIVE,ClearHeaderEnum.CLEAR_STATUS_SEVEN));
        clearStatesTaxInvoice.add(Arrays.asList(ClearHeaderEnum.CLEAR_STATUS_THREE, ClearHeaderEnum.CLEAR_STATUS_FOUR));
        clearStatesTaxInvoice.add(Arrays.asList(ClearHeaderEnum.AFTER_CLEAR));
    }

    /**
     * 更新状态
     *
     * @param clearId
     * @param status
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateState(Long clearId, Integer status) {
        pageInfoUtil.tryLockBusinessTip(BUSINESS_LOCK, clearId);
        try {
            CfClearHeader cfClearHeader = cfClearHeaderMapper.selectById(clearId);
            switch (status) {
                default:
                    Assert.isTrue(judgeStatus(cfClearHeader.getClearStatus(), status, 0), ModuleBizState.DATE_ERROR.message());
            }
            //盲猜审批流中的实体不易过长
            //修改cfClearHeader 是因为字段有可以set null
            CfClearHeader cfClearHeaderUpdate = new CfClearHeader();
            cfClearHeaderUpdate.setClearId(clearId);
            cfClearHeaderUpdate.setClearStatus(status);
            cfClearHeaderUpdate.setUpdateDate(LocalDateTime.now());
            cfClearHeader.setClearStatus(cfClearHeaderUpdate.getClearStatus());
            cfClearHeader.setUpdateDate(cfClearHeaderUpdate.getUpdateDate());
            if (ClearHeaderEnum.AFTER_CLEAR.getCode().equals(status)) {
                cfClearHeaderUpdate.setClearDate(LocalDateTime.now());
                cfClearHeader.setClearDate(cfClearHeaderUpdate.getClearDate());
            }
            //状态之后
            afterStatus(status, cfClearHeaderUpdate);
            Integer result = 1;
            //
            if (!ClearHeaderEnum.CLEAR_STATUS_FOUR.getCode().equals(status)) {
                result = cfClearHeaderMapper.updateById(cfClearHeader);
            }
            //核销流程通过 MCN 回调收入合同 更新回款状态及金额
            if (result > 0 && ClearHeaderEnum.AFTER_CLEAR.getCode().equals(status)
                    && ClearHeaderEnum.JOB_TYPE_MCN.getCode().equals(cfClearHeader.getJobType())) {
                callbackMcnWithPayback(clearId);
            }
            //日志
            OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_Clear,
                                           OperationTypeEnum.getTypeByClearEnum(ClearHeaderEnum.getByCode(status)),
                                           cfClearHeader.getClearNo(), cfClearHeader.getClearId());
            return result;
        } finally {
            pageInfoUtil.tryUnLock(BUSINESS_LOCK, clearId);
        }
    }


    @Override
    protected boolean judgeStatus(Integer oldInvoiceStatus, Integer invoiceStatus, Integer customerInvoiceWay) {
        super.judgeStatus(oldInvoiceStatus, invoiceStatus, customerInvoiceWay, clearStatesTaxInvoice);
        Integer newStatus = getInvoiceStatusEnumFromList(oldInvoiceStatus, clearStatesTaxInvoice, invoiceStatus);
        return newStatus.equals(invoiceStatus);
    }

    @Transactional(rollbackFor = Exception.class)
    public void backUpClear(Long clearId,Integer status) {
        String rejectReason=CFRequestHolder.getInvalidThreadLocal();
        if (!ClearHeaderEnum.CLEAR_STATUS_FOUR.getCode().equals(status)){
            return;
        }
        CfClearHeader cfClearHeader = cfClearHeaderMapper.selectById(clearId);
        //驳回后回写核销详情费用
        List<CfClearDetail> cfClearDetails =
                cfClearDetailMapper.selectList(Wrappers.lambdaQuery(CfClearDetail.class).eq(CfClearDetail::getClearId,
                                                                                          cfClearHeader.getClearId()));
        cfClearDetails.forEach(x -> {
            x.setNowBalance(BigDecimal.ZERO);
            chargeCommonService.backUpByClear(x.getChargeId(), cfClearHeader.getNowClearBalance(),cfClearHeader.getClearNo());
            cfClearDetailMapper.updateById(x);
        });
        //驳回后删除核销详情实收付
        List<CfClearBankDetail> cfClearBankDetails =
                clearBankDetailMapper.selectList(Wrappers.lambdaQuery(CfClearBankDetail.class).eq(CfClearBankDetail::getClearId,
                                                                                                  clearId));
        List<Long> bankAndCashIds =
                cfClearBankDetails.stream().map(CfClearBankDetail::getBankAndCashId).collect(Collectors.toList());
        clearBankDetailMapper.delete(Wrappers.lambdaQuery(CfClearBankDetail.class).eq(CfClearBankDetail::getClearId,
                                                                                      clearId));
        bankAndCashCommonService.backClearBacth(bankAndCashIds,
                                                cfClearHeader.getClearNo(), cfClearHeader.getNowClearBalance());
        //驳回后回写核销表
        CFRequestHolder.setInvalidThreadLocal(rejectReason);
        backUpClearHeader(cfClearHeader);
        cfClearHeaderMapper.updateById(cfClearHeader);
    }

    private void backUpClearHeader(CfClearHeader cfClearHeader) {
        cfClearHeader.setClearMethod(Strings.EMPTY);
        cfClearHeader.setClearStatus(ClearHeaderEnum.BEFORE_CLEAR.getCode());
        cfClearHeader.setActualArApDate(null);
        cfClearHeader.setBankAmount(BigDecimal.ZERO);
        cfClearHeader.setFiUser(Strings.EMPTY);
        cfClearHeader.setBank(Strings.EMPTY );
        cfClearHeader.setBankNo(Strings.EMPTY);
        cfClearHeader.setCheckNo(Strings.EMPTY);
        cfClearHeader.setNowClearDebit(cfClearHeader.getLastBalanceBalance());
        cfClearHeader.setNowClearBalance(cfClearHeader.getLastBalanceBalance());
        cfClearHeader.setUpdateDate(LocalDateTime.now());
        cfClearHeader.setRejectReason(CFRequestHolder.getInvalidThreadLocal());
    }

    private void afterStatus(Integer status, CfClearHeader cfClearHeaderUpdate) {
        //开启审批流
        if (ClearHeaderEnum.CLEAR_STATUS_THREE.getCode().equals(status)) {
            pageInfoUtil.startProcess(cfClearHeaderUpdate,cfClearHeaderUpdate.getClass(), APPROVAL_PROCESS_ID,
                    cfClearHeaderUpdate.getClearId());
        }
        //如果是驳回(审批拒绝)
        if (ClearHeaderEnum.CLEAR_STATUS_FOUR.getCode().equals(status)){
            backUpClear(cfClearHeaderUpdate.getClearId(),status);
        }

    }

    /**
     * 回款回调MCN
     * @param clearId
     */
    private void callbackMcnWithPayback(Long clearId) {
        //目前1个核销单只会关联1个收入合同
        List<CfClearDetail> cfClearDetails = cfClearDetailMapper.selectList(Wrappers.lambdaQuery(CfClearDetail.class)
                .eq(CfClearDetail::getChargeType, ChargeCommonEnum.CHARGE_SOURCE_TYPE_MCN.getCode())
                .eq(CfClearDetail::getClearId,clearId));
        if(CollectionUtils.isEmpty(cfClearDetails)){
            return;
        }
        List<String> sourceCodes = cfClearDetails.stream().map(CfClearDetail::getChargeSourceCode).distinct().collect(Collectors.toList());
        Map<String,List<Long>> chargeIdMap = new HashMap<>(cfClearDetails.size());
        for(String sourceCode : sourceCodes) {
            List<CfChargeCommon> chargeCommonList = chargeCommonService.selectCfChargesBySourceCode(sourceCode);
            if(CollectionUtils.isEmpty(chargeCommonList)){
                continue;
            }
            List<Long> chargeIds = chargeCommonList.stream().map(CfChargeCommon::getChargeId).distinct().collect(Collectors.toList());
            chargeIdMap.put(sourceCode,chargeIds);
        }
        if(MapUtils.isEmpty(chargeIdMap)){
            return;
        }
        for(Map.Entry<String,List<Long>> entry : chargeIdMap.entrySet()){
            List<CfClearDetail> clearDetailList = cfClearDetailMapper.getClearedDetailsByChargeIds(entry.getValue());
            if(CollectionUtils.isEmpty(clearDetailList)){
                continue;
            }
            //汇总核销金额
            BigDecimal totalActualAmount = BigDecimal.ZERO;
            for (CfClearDetail detail : clearDetailList) {
                totalActualAmount = totalActualAmount.add(detail.getActualClearAmount());
            }
            RpcUtil.getObjNoException(mcnRemoteServer.payBackCallBack(totalActualAmount, entry.getKey()));
        }
    }
}
