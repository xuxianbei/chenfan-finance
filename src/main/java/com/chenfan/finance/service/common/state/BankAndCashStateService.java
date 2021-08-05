package com.chenfan.finance.service.common.state;

import com.chenfan.finance.dao.CfBankAndCashMapper;
import com.chenfan.finance.enums.BankAndCashEnum;
import com.chenfan.finance.enums.GetCode;
import com.chenfan.finance.enums.OperationBsTypeEnum;
import com.chenfan.finance.enums.OperationTypeEnum;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.CfBankAndCash;
import com.chenfan.finance.utils.OperateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 实收付
 *
 * @author: xuxianbei
 * Date: 2021/3/20
 * Time: 17:47
 * Version:V1.0
 */
@Service
public class BankAndCashStateService extends AbstractStateService {

    /**
     * 实收付审批流
     */
    public static final Long APPROVAL_PROCESS_ID = 1371280443780890624L;

    public static final String BUSINESS_LOCK = "BankAndCashState:";

    @Resource
    private CfBankAndCashMapper cfBankAndCashMapper;


    /**
     * 实收付流程
     */
    private List<List<GetCode>> bankAndCashStatussTaxInvoice = new ArrayList<>();

    {
        bankAndCashStatussTaxInvoice.add(Arrays.asList(BankAndCashEnum.BANK_AND_CASH_STATUS_ONE, BankAndCashEnum.BANK_AND_CASH_STATUS_SEVEN));
        bankAndCashStatussTaxInvoice.add(Arrays.asList(BankAndCashEnum.BANK_AND_CASH_STATUS_SIX, BankAndCashEnum.BANK_AND_CASH_STATUS_SEVEN));
        bankAndCashStatussTaxInvoice.add(Arrays.asList(BankAndCashEnum.BANK_AND_CASH_STATUS_TWO,BankAndCashEnum.BANK_AND_CASH_STATUS_FOUR, BankAndCashEnum.BANK_AND_CASH_STATUS_THREE));
        //这个尴尬了  没有想到可以跳状态
    }

    /**
     * 更新状态
     *
     * @param bankAndCashId
     * @param status
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateState(Long bankAndCashId, Integer status) {
        pageInfoUtil.tryLockBusinessTip(BUSINESS_LOCK, bankAndCashId);
        try {
            CfBankAndCash cfBankAndCash = cfBankAndCashMapper.selectById(bankAndCashId);
            switch (status) {

                default:
                    Assert.isTrue(judgeStatus(cfBankAndCash.getBankAndCashStatus(), status, 0), ModuleBizState.DATE_ERROR.message());
            }
            CfBankAndCash cfBankAndCashUpdate = new CfBankAndCash();
            cfBankAndCashUpdate.setBankAndCashId(bankAndCashId);
            cfBankAndCashUpdate.setBankAndCashStatus(status);
            cfBankAndCashUpdate.setUpdateDate(new Date());
            //状态之后
            afterStatus(status, cfBankAndCashUpdate, cfBankAndCash);
            return cfBankAndCashMapper.updateById(cfBankAndCashUpdate);
        } finally {
            pageInfoUtil.tryUnLock(BUSINESS_LOCK, bankAndCashId);
        }
    }

    private void afterStatus(Integer status, CfBankAndCash cfBankAndCashUpdate, CfBankAndCash cfBankAndCash) {
        //开启审批流
        if (BankAndCashEnum.BANK_AND_CASH_STATUS_SIX.getCode().equals(status)) {
            pageInfoUtil.startProcess(cfBankAndCashUpdate, cfBankAndCashUpdate.getClass(), APPROVAL_PROCESS_ID,
                    cfBankAndCashUpdate.getBankAndCashId());
        }
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_CASH,
                OperationTypeEnum.getTypeByBankAndCashEnum(BankAndCashEnum.getByCode(status)),cfBankAndCash.getRecordSeqNo(),cfBankAndCashUpdate.getBankAndCashId());
    }

    @Override
    protected boolean judgeStatus(Integer oldInvoiceStatus, Integer invoiceStatus, Integer customerInvoiceWay) {
        super.judgeStatus(oldInvoiceStatus, invoiceStatus, customerInvoiceWay, bankAndCashStatussTaxInvoice);
        Integer newStatus = getInvoiceStatusEnumFromList(oldInvoiceStatus, bankAndCashStatussTaxInvoice, invoiceStatus, true);
        return newStatus.equals(invoiceStatus);
    }
}
