package com.chenfan.finance.service.common;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.finance.config.BillNoConstantClassField;
import com.chenfan.finance.dao.CfClearDetailMapper;
import com.chenfan.finance.dao.CfClearHeaderMapper;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.*;
import com.chenfan.finance.model.dto.CreateAndClearDto;
import com.chenfan.finance.service.common.state.InvoiceStateService;
import com.chenfan.finance.service.common.state.JudgeStateExt;
import com.chenfan.finance.service.common.state.TaxInvoiceStateService;
import com.chenfan.finance.service.impl.CfBankAndCashServiceImpl;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 实收付 账单 核销 组合服务
 *
 * @author: xuxianbei
 * Date: 2021/5/13
 * Time: 20:00
 * Version:V1.0
 */
@Service
public class CombinationService {

    @Autowired
    private PageInfoUtil pageInfoUtil;

    @Autowired
    private BankAndCashCommonService bankAndCashCommonService;

    @Autowired
    private CfClearHeaderMapper cfClearHeaderMapper;

    @Resource
    private CfClearDetailMapper cfClearDetailMapper;

    @Autowired
    private InvoiceStateService invoiceStateService;
    @Autowired
    private TaxInvoiceStateService taxInvoiceStateService;

    @Autowired
    private InvoiceCommonService invoiceCommonService;


    @Autowired
    private ChargeCommonService chargeCommonService;

    @Autowired
    private TaxInvoiceCommonService taxInvoiceCommonService;

    @Data
    class CreateAndClearContext {
        /**
         * 核销金额
         */
        private BigDecimal clearSum = BigDecimal.ZERO;

        private CfClearHeader cfClearHeader;

        /**
         * 收付时间
         */
        private CfBankAndCash cfBankAndCash;

        /**
         * 额外状态判断
         */
        private JudgeStateExt judgeStateExt;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer createAndClear(CreateAndClearDto createAndClearDto, JudgeStateExt judgeStateExt) {
        vertify(createAndClearDto);

        CfBankAndCash cfBankAndCash = bankAndCashCommonService.add(createAndClearDto, BankAndCashEnum.BANK_AND_CASH_STATUS_TWO.getCode());

        CfClearHeader cfClearHeader = new CfClearHeader();

        String res1 = pageInfoUtil.generateBusinessNum(BillNoConstantClassField.CLEAR);
        cfClearHeader.setClearNo(res1);
        cfBankAndCash.setClearNo(cfClearHeader.getClearNo());
        cfClearHeader.setActualArApDate(PageInfoUtil.dateToLocalDateTime(createAndClearDto.getArapDate()));
        fillClearHeader(cfClearHeader, cfBankAndCash, createAndClearDto);
        cfClearHeaderMapper.insert(cfClearHeader);
        CreateAndClearContext createAndClearContext = new CreateAndClearContext();
        createAndClearContext.setCfClearHeader(cfClearHeader);
        createAndClearContext.setCfBankAndCash(cfBankAndCash);
        createAndClearContext.setJudgeStateExt(judgeStateExt);
        fillClearDetail(createAndClearDto, cfClearHeader.getClearId(), createAndClearContext);
        Assert.isTrue(createAndClearContext.getClearSum().compareTo(cfBankAndCash.getAmount()) == 0, ModuleBizState.DATE_ERROR_BUSINESS.format("实收付总金额必须和费用总金额相等"));

        //更新账单
        updateStateInvoice(createAndClearDto.getInvoiceNo(), cfClearHeader.getClearNo(), cfBankAndCash.getAmount(), createAndClearContext);

        //更新开票
        updateStateTaxInvoice(createAndClearDto.getTaxInvoiceNo(), cfClearHeader.getClearNo(), cfBankAndCash.getAmount());

        //反写实收付
        bankAndCashCommonService.updateByClear(cfBankAndCash.getBankAndCashId(), cfClearHeader.getClearNo(), createAndClearContext.getClearSum());

        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer createAndClear(CreateAndClearDto createAndClearDto) {
        return createAndClear(createAndClearDto, null);
    }

    private void fillClearDetail(CreateAndClearDto createAndClearDto, Long clearId, CreateAndClearContext createAndClearContext) {
        //账单
        fillClearDetailInvoice(createAndClearDto, clearId, createAndClearContext);
        //开票
        fillClearDetailTaxInvoice(createAndClearDto, clearId);
    }

    private void fillClearHeader(CfClearHeader cfClearHeader, CfBankAndCash cfBankAndCash, CreateAndClearDto createAndClearDto) {
        //如果是账单
        fillClearHeaderInvoice(cfClearHeader, createAndClearDto);
        //如果是开票
        fillClearHeaderTaxInvoice(cfClearHeader, createAndClearDto);

        pageInfoUtil.baseInfoFill(cfClearHeader);
        cfClearHeader.setClearMethod(createAndClearDto.getRecordType());
        cfClearHeader.setClearStatus(2);
        cfClearHeader.setClearDate(LocalDateTime.now());
        cfClearHeader.setBank(createAndClearDto.getBank());
        cfClearHeader.setBankNo(createAndClearDto.getBankNo());
        cfClearHeader.setBankAmount(createAndClearDto.getAmount());
        cfClearHeader.setFiUser(Strings.EMPTY);
        cfClearHeader.setNowClearBalance(createAndClearDto.getAmount());
        cfClearHeader.setCurrencyCode("RMB");
        cfClearHeader.setClearBy(pageInfoUtil.getUser().getUserId());
        cfClearHeader.setLastBalanceType(cfClearHeader.getNowClearType());
        cfClearHeader.setJobType(ClearHeaderEnum.JOB_TYPE_MCN.getCode());
        cfClearHeader.setNowBalanceType(cfClearHeader.getNowClearType());
        cfClearHeader.setClearMethod(cfBankAndCash.getRecordType());
    }

    private void updateStateTaxInvoice(String taxInvoicNo, String clearNo, BigDecimal amout) {
        if (StringUtils.isNotBlank(taxInvoicNo)) {
            CfClearHeader cfClearHeader =
                    cfClearHeaderMapper.selectOne(Wrappers.lambdaQuery(CfClearHeader.class).eq(CfClearHeader::getClearNo, clearNo));
            Assert.isTrue(Objects.nonNull(cfClearHeader), ModuleBizState.DATE_ERROR.message());
            InvoiceStateService.UpdateStateData updateStateData = new InvoiceStateService.UpdateStateData();
            updateStateData.setRemark(cfClearHeader.getRemark());
            updateStateData.setAmount(amout);
            taxInvoiceStateService.updateState(taxInvoicNo, TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SIX.getCode(), updateStateData);
        }
    }

    private void updateStateInvoice(String invoiceNo, String clearNo, BigDecimal amout, CreateAndClearContext createAndClearContext) {
        if (StringUtils.isNotBlank(invoiceNo)) {
            CfClearHeader cfClearHeader =
                    cfClearHeaderMapper.selectOne(Wrappers.lambdaQuery(CfClearHeader.class).eq(CfClearHeader::getClearNo, clearNo));
            Assert.isTrue(Objects.nonNull(cfClearHeader), ModuleBizState.DATE_ERROR.message());
            InvoiceStateService.UpdateStateData updateStateData = new InvoiceStateService.UpdateStateData();
            updateStateData.setRemark(cfClearHeader.getRemark());
            updateStateData.setAmount(amout);
            updateStateData.setCfBankAndCash(createAndClearContext.getCfBankAndCash());
            updateStateData.setJudgeStateExt(createAndClearContext.getJudgeStateExt());
            invoiceStateService.updateState(invoiceNo, InvoiceHeaderEnum.INVOICE_STATUS_NINE.getCode(), updateStateData);
        }
    }


    private void fillClearDetailInvoice(CreateAndClearDto createAndClearDto, Long clearId, CreateAndClearContext createAndClearContext) {
        if (StringUtils.isNotBlank(createAndClearDto.getInvoiceNo())) {
            CfInvoiceHeader cfInvoiceHeader = invoiceCommonService.getCfInvoiceHeader(createAndClearDto.getInvoiceNo());
            Assert.isTrue(Objects.nonNull(cfInvoiceHeader), ModuleBizState.DATE_ERROR.message());
            List<CfInvoiceDetail> cfInvoiceDetails = invoiceCommonService.getCfInvoiceDetails(cfInvoiceHeader.getInvoiceId());
            cfInvoiceDetails.forEach(cfInvoiceDetail -> {
                CfClearDetail cfClearDetail = new CfClearDetail();
                cfClearDetail.setClearId(clearId);
                cfClearDetail.setInvoiceNo(cfInvoiceHeader.getInvoiceNo());
                PageInfoUtil.copyProperties(cfInvoiceHeader, cfClearDetail);
                cfClearDetail.setChargeId(cfInvoiceDetail.getChargeId());
                cfClearDetail.setArapType(cfInvoiceDetail.getArapType());
                cfClearDetail.setChargeType(cfInvoiceDetail.getChargeType());
                cfClearDetail.setChargeSourceType(1);
                cfClearDetail.setLastBalance(cfInvoiceDetail.getInvoiceCredit());
                cfClearDetail.setClearedAmount(cfInvoiceDetail.getInvoiceCredit());
                cfClearDetail.setActualClearAmount(cfInvoiceDetail.getInvoiceCredit());
                cfClearDetailMapper.insert(cfClearDetail);
                createAndClearContext.setClearSum(createAndClearContext.getClearSum().add(cfInvoiceDetail.getInvoiceCredit()));
                //反写费用
                chargeCommonService.updateByClear(cfInvoiceDetail.getChargeId(), createAndClearContext.getCfClearHeader().getClearNo(),
                        cfInvoiceDetail.getInvoiceCredit(), LocalDateTime.now());
            });
        }
    }


    private void fillClearHeaderTaxInvoice(CfClearHeader cfClearHeader, CreateAndClearDto createAndClearDto) {
        if (StringUtils.isNotBlank(createAndClearDto.getTaxInvoiceNo())) {
            cfClearHeader.setInvoiceNo(createAndClearDto.getTaxInvoiceNo());
            CfTaxInvoiceHeader cfTaxInvoiceHeader = taxInvoiceCommonService.getCfTaxInvoiceHeader(createAndClearDto.getTaxInvoiceNo());
            Assert.isTrue(Objects.nonNull(cfTaxInvoiceHeader), ModuleBizState.DATE_ERROR.message());
            PageInfoUtil.copyProperties(cfTaxInvoiceHeader, cfClearHeader);
            cfClearHeader.setClearDebit(createAndClearDto.getAmount());
            cfClearHeader.setClearType("0");
            cfClearHeader.setClearBalance(createAndClearDto.getAmount());
            cfClearHeader.setNowClearCredit(createAndClearDto.getAmount());
            cfClearHeader.setNowClearType(ChargeEnum.ARAP_TYPE_AR.getCode());
        }
    }

    private void fillClearHeaderInvoice(CfClearHeader cfClearHeader, CreateAndClearDto createAndClearDto) {
        if (StringUtils.isNotBlank(createAndClearDto.getInvoiceNo())) {
            cfClearHeader.setInvoiceNo(createAndClearDto.getInvoiceNo());
            CfInvoiceHeader cfInvoiceHeader = invoiceCommonService.getCfInvoiceHeader(createAndClearDto.getInvoiceNo());
            Assert.isTrue(Objects.nonNull(cfInvoiceHeader), ModuleBizState.DATE_ERROR.message());
            PageInfoUtil.copyProperties(cfInvoiceHeader, cfClearHeader);
            List<CfInvoiceDetail> cfInvoiceDetails = invoiceCommonService.getCfInvoiceDetails(cfInvoiceHeader.getInvoiceId());
            BigDecimal sum = cfInvoiceDetails.stream().map(CfInvoiceDetail::getInvoiceCredit).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            cfClearHeader.setClearCredit(sum);
            cfClearHeader.setClearType(ChargeEnum.ARAP_TYPE_AP.getCode());
            cfClearHeader.setClearBalance(sum);
            cfClearHeader.setNowClearCredit(createAndClearDto.getAmount());
            cfClearHeader.setNowClearType(ChargeEnum.ARAP_TYPE_AP.getCode());
        }
    }


    private void fillClearDetailTaxInvoice(CreateAndClearDto createAndClearDto, Long clearId) {
        if (StringUtils.isNotBlank(createAndClearDto.getTaxInvoiceNo())) {
            CfTaxInvoiceHeader cfTaxInvoiceHeader = taxInvoiceCommonService.getCfTaxInvoiceHeader(createAndClearDto.getTaxInvoiceNo());
            Assert.isTrue(Objects.nonNull(cfTaxInvoiceHeader), ModuleBizState.DATE_ERROR.message());
            List<CfTaxInvoiceDetail> invoiceDetails = taxInvoiceCommonService.getCfTaxInvoiceDetail(cfTaxInvoiceHeader.getTaxInvoiceId());
            invoiceDetails.forEach(cfTaxInvoiceDetail -> {
                CfClearDetail cfClearDetail = new CfClearDetail();
                PageInfoUtil.copyProperties(cfTaxInvoiceHeader, cfClearDetail);
                cfClearDetail.setInvoiceNo(createAndClearDto.getTaxInvoiceNo());
                cfClearDetail.setClearId(clearId);
                cfClearDetail.setClearDebit(cfTaxInvoiceDetail.getInvoiceDebit());
                cfClearDetail.setChargeType(String.valueOf(cfTaxInvoiceDetail.getChargeType()));
                cfClearDetail.setActualClearAmount(cfTaxInvoiceDetail.getInvoiceDebit());
                cfClearDetail.setChargeId(cfTaxInvoiceDetail.getChargeId());
                cfClearDetail.setArapType(cfTaxInvoiceDetail.getArapType());
                cfClearDetailMapper.insert(cfClearDetail);
            });
        }
    }

    private void vertify(CreateAndClearDto createAndClearDto) {
        List<CfChargeCommon> chargeCommons = invoiceCommonService.getChargeCommonByInvoiceNo(createAndClearDto.getInvoiceNo());
        Assert.isTrue(CollectionUtils.isNotEmpty(chargeCommons), ModuleBizState.DATE_ERROR.message());
        Assert.isTrue(chargeCommons.get(0).getBalance().equals(createAndClearDto.getBalance()), ModuleBizState.DATE_ERROR.message());
    }
}
