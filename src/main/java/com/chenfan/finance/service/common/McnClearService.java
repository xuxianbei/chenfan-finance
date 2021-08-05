package com.chenfan.finance.service.common;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.common.vo.Response;
import com.chenfan.finance.config.BillNoConstantClassField;
import com.chenfan.finance.convertor.ClearConvertor;
import com.chenfan.finance.dao.CfChargeCommonMapper;
import com.chenfan.finance.dao.CfClearBankDetailMapper;
import com.chenfan.finance.dao.CfClearDetailMapper;
import com.chenfan.finance.dao.CfClearHeaderMapper;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.exception.FinanceBizState;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.*;
import com.chenfan.finance.model.dto.CfClearAddAndUpdateDto;
import com.chenfan.finance.model.vo.*;
import com.chenfan.finance.server.McnRemoteServer;
import com.chenfan.finance.server.remote.model.IncomeContract;
import com.chenfan.finance.service.common.state.ClearStateService;
import com.chenfan.finance.service.common.state.InvoiceStateService;
import com.chenfan.finance.service.common.state.TaxInvoiceStateService;
import com.chenfan.finance.service.impl.CfBankAndCashServiceImpl;
import com.chenfan.finance.utils.OperateUtil;
import com.chenfan.finance.utils.RpcUtil;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: xuxianbei
 * Date: 2021/3/17
 * Time: 11:39
 * Version:V1.0
 */
@Slf4j
@Service
public class McnClearService implements ClearDetail {

    @Autowired
    private ChargeCommonService chargeCommonService;

    @Autowired
    private CfBankAndCashServiceImpl cfBankAndCashService;

    @Resource
    private CfClearHeaderMapper clearHeaderMapper;

    @Resource
    private CfClearDetailMapper clearDetailMapper;

    @Resource
    private CfClearBankDetailMapper clearBankDetailMapper;

    @Autowired
    private McnConfiguration mcnConfiguration;

    @Autowired
    private PageInfoUtil pageInfoUtil;

    @Autowired
    private TaxInvoiceStateService taxInvoiceStateService;

    @Autowired
    private BankAndCashCommonService bankAndCashCommonService;

    @Autowired
    private ClearStateService clearStateService;

    //解决（一个类中的方法调用时，被调用的方法如果有事务管理，那么事务管理会失效。）
    @Autowired
    private McnClearService mcnClearService;

    @Autowired
    private McnRemoteServer mcnRemoteServer;


    private final String FAIL_REASON_ON = "匹配失败;";

    @Deprecated
    public boolean support(ClearContext clearContext) {
        List<CfChargeCommon> cfChargeCommons = clearContext.getWaitCfChargeCommons();
        List<CfChargeCommon> chargeCommonsMcn =
                cfChargeCommons.stream().filter(t -> t.getChargeSourceType().equals(ChargeCommonEnum.CHARGE_SOURCE_TYPE_MCN.getCode())).collect(Collectors.toList());
        boolean result = CollectionUtils.isNotEmpty(cfChargeCommons) && cfChargeCommons.size() == chargeCommonsMcn.size();
        CfChargeCommon cfChargeCommonBase = cfChargeCommons.get(0);
        cfChargeCommons.forEach(cfChargeCommon ->
                Assert.isTrue(cfChargeCommonBase.getArapType().equals(cfChargeCommon.getArapType()) &&
                                cfChargeCommonBase.getInvoiceTitle().equals(cfChargeCommon.getInvoiceTitle()) &&
                                cfChargeCommonBase.getBalance().equals(cfChargeCommon.getBalance()) &&
                                cfChargeCommonBase.getChargeSourceCode().equals(cfChargeCommon.getChargeSourceCode()) &&
                                ChargeEnum.ARAP_TYPE_AR.getCode().equals(cfChargeCommonBase.getArapType())
                                && cfChargeCommon.getAmountPp().compareTo(BigDecimal.ZERO) >= 0,
                        ModuleBizState.DATE_ERROR_CLEAR_VERITY.message()));
        clearContext.getWaitCfBankAndCashs().forEach(waitCfBankAndCash -> {
            Assert.isTrue(waitCfBankAndCash.getPaymentBranch().equals(cfChargeCommonBase.getInvoiceTitle()) &&
                            cfChargeCommonBase.getBalance().equals(waitCfBankAndCash.getBalance()) &&
                            "1".equals(waitCfBankAndCash.getArapType()),
                    ModuleBizState.DATE_ERROR_BUSINESS.format("实收付的收付类型、结算主体和【交易方公司名称】需跟所选费用一致！"));
        });

        return result;
    }

    public boolean supportV1(ClearContext clearContext) {
        List<CfChargeCommon> cfChargeCommons = clearContext.getWaitCfChargeCommons();
        List<CfChargeCommon> chargeCommonsMcn =
                cfChargeCommons.stream().filter(t -> t.getChargeSourceType().equals(ChargeCommonEnum.CHARGE_SOURCE_TYPE_MCN.getCode())).collect(Collectors.toList());
        boolean result = CollectionUtils.isNotEmpty(cfChargeCommons) && cfChargeCommons.size() == chargeCommonsMcn.size();
        CfChargeCommon cfChargeCommonBase = cfChargeCommons.get(0);
        cfChargeCommons.forEach(cfChargeCommon ->
                                        Assert.isTrue(cfChargeCommonBase.getArapType().equals(cfChargeCommon.getArapType()) &&
                                                              cfChargeCommonBase.getInvoiceTitle().equals(cfChargeCommon.getInvoiceTitle()) &&
                                                              cfChargeCommonBase.getBalance().equals(cfChargeCommon.getBalance()) &&
                                                              cfChargeCommonBase.getChargeSourceCode().equals(cfChargeCommon.getChargeSourceCode()) &&
                                                              ChargeEnum.ARAP_TYPE_AR.getCode().equals(cfChargeCommonBase.getArapType())
                                                              && cfChargeCommon.getAmountPp().compareTo(BigDecimal.ZERO) >= 0,
                                                      ModuleBizState.DATE_ERROR_CLEAR_VERITY.message()));
        return result;
    }

    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public void save(CfClearAddAndUpdateDto cfClearAddAndUpdateDto, ClearContext clearContext) {
        /**
         * 如果流程频繁变动还是用pipeline合适些
         * 获取实收付
         * 获取费用
         * 校验费用总计<=实收付总计， 校验费用是否被核销，
         * 计算费用
         * 创建核销主表
         * 创建核销明细
         * 反写费用
         * 反写开票
         * 反写实收付
         *
         */
        List<CfBankAndCash> cfBankAndCashs = clearContext.getWaitCfBankAndCashs();
        List<CfChargeCommon> cfChargeCommons = clearContext.getWaitCfChargeCommons();

        BigDecimal sum = cfChargeCommons.stream().map(CfChargeCommon::getAmountPp).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        cfChargeCommons.forEach(cfChargeCommon -> Assert.isTrue(StringUtils.isEmpty(cfChargeCommon.getClearNo()), ModuleBizState.DATE_ERROR_BUSINESS.format("费用核销余额为0不能核销")));

        BigDecimal balanceSum = cfBankAndCashs.stream().map(CfBankAndCash::getBalanceBalance).reduce(BigDecimal.ZERO, BigDecimal::add);

        Assert.isTrue(balanceSum.compareTo(sum) >= 0 && sum.compareTo(BigDecimal.ZERO) >= 0
                && balanceSum.compareTo(BigDecimal.ZERO) >= 0, ModuleBizState.BANK_AND_CASH_VERTIFY_ERROR.message());

        cfClearAddAndUpdateDto.setClearNo(pageInfoUtil.generateBusinessNum(BillNoConstantClassField.CLEAR));
        //核销主表
        CfClearHeader cfClearHeader = saveClearHeader(cfClearAddAndUpdateDto, cfBankAndCashs,cfChargeCommons, sum);
        //核销明细
        saveClearDetails(cfChargeCommons, cfClearHeader,null);

        Map<String, List<CfChargeCommon>> invoiceNoChargeCommonMap =
                cfChargeCommons.stream().collect(Collectors.groupingBy(CfChargeCommon::getInvoiceNo));

        invoiceNoChargeCommonMap.entrySet().stream().forEach(stringListEntry -> {
            BigDecimal sonSum = stringListEntry.getValue().stream().map(CfChargeCommon::getAmountPp).reduce(BigDecimal.ZERO, BigDecimal::add);
            InvoiceStateService.UpdateStateData updateStateData = new InvoiceStateService.UpdateStateData();
            updateStateData.setRemark(cfClearHeader.getRemark());
            updateStateData.setAmount(sonSum);
            updateStateData.setClearCharges(stringListEntry.getValue());
            stringListEntry.getValue().forEach(cfChargeCommon -> {
                //费用反写  MCN 业务 费用一定会被核销掉
                chargeCommonService.updateByClear(cfChargeCommon.getChargeId(), cfClearHeader.getClearNo(), cfChargeCommon.getAmountPp(), cfClearHeader.getCreateDate());
            });
            //反写开票
            taxInvoiceStateService.updateState(stringListEntry.getKey(), TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SIX.getCode(), updateStateData);
        });


        //反写实收付
        bankAndCashCommonService.updateByClearBatch(PageInfoUtil.lambdaToList(cfBankAndCashs, CfBankAndCash::getBankAndCashId),
                cfClearHeader.getClearNo(), sum);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveV1(CfClearAddAndUpdateDto cfClearAddAndUpdateDto, ClearContext clearContext) {
        /**
         * 如果流程频繁变动还是用pipeline合适些
         * 获取实收付
         * 获取费用
         * 计算费用
         * 创建核销主表
         * 创建核销明细
         *
         */
        List<CfBankAndCash> cfBankAndCashs = clearContext.getWaitCfBankAndCashs();
        List<CfChargeCommon> cfChargeCommons = clearContext.getWaitCfChargeCommons();

        BigDecimal sum = cfChargeCommons.stream().map(CfChargeCommon::getAmountPp).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal overageSum = cfChargeCommons.stream().map(CfChargeCommon::getOverage).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        cfChargeCommons.forEach(cfChargeCommon -> {
            Assert.isTrue(BigDecimal.ZERO.compareTo(cfChargeCommon.getOverage()) < 0,
                          ModuleBizState.DATE_ERROR_BUSINESS.format("费用核销余额为0不能核销"));
            Assert.isTrue(!ChargeCommonEnum.CHECK_STSTUS_ZERO.getCode().equals(cfChargeCommon.getCheckStatus()),
                          ModuleBizState.DATE_ERROR_BUSINESS.format("费用已经被删除,不能核销"));
            Assert.isTrue(cfChargeCommon.getOverage().compareTo(cfClearAddAndUpdateDto.getNowClearBalance())>=0,
                          ModuleBizState.DATE_ERROR_BUSINESS.format("核销金额大于费用核销余额"));
        } );

        cfClearAddAndUpdateDto.setClearNo(pageInfoUtil.generateBusinessNum(BillNoConstantClassField.CLEAR));
        //核销主表
        CfClearHeader cfClearHeader = saveClearHeader(cfClearAddAndUpdateDto, cfBankAndCashs,cfChargeCommons, sum);
        //核销明细
        saveClearDetails(cfChargeCommons, cfClearHeader, overageSum);
        //日志
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_Clear, OperationTypeEnum.OPERATION_CREATE, cfClearHeader.getClearNo(), cfClearHeader.getClearId());
    }

    private void saveClearBandkDetails(List<CfBankAndCash> cfBankAndCashs, CfClearHeader cfClearHeader) {
        //核销明细
        cfBankAndCashs.forEach(cfBankAndCash -> {
            CfClearBankDetail cfClearBankDetail = new CfClearBankDetail();
            PageInfoUtil.copyProperties(cfBankAndCash, cfClearBankDetail);
            cfClearBankDetail.setClearId(cfClearHeader.getClearId());
            cfClearBankDetail.setLastBalance(cfBankAndCash.getBalanceBalance());
            clearBankDetailMapper.insert(cfClearBankDetail);
        });
    }

    /**
     * 更新状态
     *
     * @param clearNo
     * @param state
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateState(String clearNo, Integer state) {
        CfClearHeader cfClearHeader =
                clearHeaderMapper.selectOne(Wrappers.lambdaQuery(CfClearHeader.class).eq(CfClearHeader::getClearNo, clearNo));
        Assert.isTrue(Objects.nonNull(cfClearHeader), ModuleBizState.DATE_ERROR.message());
        return clearStateService.updateState(cfClearHeader.getClearId(), state);
    }

    /**
     * 通过核销号获取核销列表
     *
     * @param clearNos
     */
    @Transactional(rollbackFor = Exception.class)
    public List<CfClearHeader> selectListByClearNos(List<String> clearNos) {
        List<CfClearHeader> cfClearHeaders = clearHeaderMapper.selectList(Wrappers.lambdaQuery(CfClearHeader.class).in(CfClearHeader::getClearNo, clearNos)
                .ne(CfClearHeader::getClearStatus, ClearHeaderEnum.IS_DELETE.getCode())
                .ne(CfClearHeader::getClearStatus, ClearHeaderEnum.CLEAR_STATUS_SIX.getCode()));
        Assert.isTrue(Objects.nonNull(cfClearHeaders), ModuleBizState.DATE_ERROR.message());
        return cfClearHeaders;
    }


    /**
     * 更新状态
     *
     * @param clearId
     * @param state
     */
    public Integer updateState(Long clearId, Integer state) {
        return clearStateService.updateState(clearId, state);
    }

    private void saveClearDetails(List<CfChargeCommon> cfChargeCommons, CfClearHeader cfClearHeader,BigDecimal overageSum ) {
        //核销明细
        cfChargeCommons.forEach(cfChargeCommon -> {
            CfClearDetail cfClearDetail = new CfClearDetail();
            PageInfoUtil.copyProperties(cfChargeCommon, cfClearDetail);
            cfClearDetail.setClearId(cfClearHeader.getClearId());
            cfClearDetail.setInvoiceNo(cfChargeCommon.getTaxInvoiceNo());
            cfClearDetail.setClearDebit(cfChargeCommon.getAmountPp());
            cfClearDetail.setChargeType(String.valueOf(cfChargeCommon.getChargeType()));
            cfClearDetail.setActualClearAmount(BigDecimal.ZERO);
            cfClearDetail.setArapType(cfChargeCommon.getArapType());
            cfClearDetail.setClearedAmount(cfChargeCommon.getAmountPp());
            cfClearDetail.setLastBalance(overageSum);
            clearDetailMapper.insert(cfClearDetail);
        });
    }

    private void updateClearDetails(List<CfChargeCommon> cfChargeCommons,CfClearHeader cfClearHeader,BigDecimal sum,
                                    List<CfBankAndCash> cfBankAndCashs) {
        List<CfClearDetail> cfClearDetails =
                clearDetailMapper.selectList(Wrappers.lambdaQuery(CfClearDetail.class).eq(CfClearDetail::getClearId,
                                                                                          cfClearHeader.getClearId()));
        saveClearBandkDetails(cfBankAndCashs, cfClearHeader);
        Map<Long, CfChargeCommon> map = cfChargeCommons.stream().collect(Collectors.toMap(CfChargeCommon::getChargeId
                , t -> t, (a, b) -> b));
        for (CfClearDetail cfClearDetail : cfClearDetails) {
            BigDecimal overage =  map.get(cfClearDetail.getChargeId()).getOverage();
            cfClearDetail.setLastBalance(overage);
            if (sum.compareTo(overage) < 0) {
                cfClearDetail.setActualClearAmount(sum);
                cfClearDetail.setNowBalance(overage.subtract(sum));
                sum = BigDecimal.ZERO;
            } else if (sum.compareTo(overage) >0){
                cfClearDetail.setActualClearAmount(overage);
                cfClearDetail.setNowBalance(BigDecimal.ZERO);
                sum=sum.subtract(overage);
            }else {
                cfClearDetail.setActualClearAmount(sum);
                cfClearDetail.setNowBalance(sum.subtract(overage));
            }
            clearDetailMapper.updateById(cfClearDetail);
        }
    }



    private CfClearHeader saveClearHeader(CfClearAddAndUpdateDto cfClearAddAndUpdateDto, List<CfBankAndCash> cfBankAndCashs,List<CfChargeCommon> cfChargeCommons, BigDecimal sum) {
        CfClearHeader cfClearHeader = createClearHeader(cfClearAddAndUpdateDto.getClearNo());
        fillClearHeaderBankAndCash(cfBankAndCashs.get(0), cfClearHeader);
        cfClearHeader.setClearDebit(sum);
        cfClearHeader.setClearType(ChargeEnum.ARAP_TYPE_AR.getCode());
        cfClearHeader.setClearBalance(sum);
        cfClearHeader.setNowClearDebit(cfClearAddAndUpdateDto.getNowClearBalance());
        cfClearHeader.setNowClearType(ChargeEnum.ARAP_TYPE_AR.getCode());
        cfClearHeader.setNowClearBalance(cfClearAddAndUpdateDto.getNowClearBalance());
        cfClearHeader.setRemark(cfClearAddAndUpdateDto.getRemark());
        cfClearHeader.setClearStatus(ClearHeaderEnum.BEFORE_CLEAR.getCode());
        cfClearHeader.setRecordSeqNo(cfClearAddAndUpdateDto.getRecordSeqNo().trim());
        cfClearHeader.setReceiptScreenshot(cfClearAddAndUpdateDto.getReceiptScreenshot());
        cfClearHeader.setCollectionAndPayRemark(cfClearAddAndUpdateDto.getCollectionAndPayRemark());
        cfClearHeader.setBalance(cfChargeCommons.get(0).getBalance());
        cfClearHeader.setLastBalanceBalance(cfClearAddAndUpdateDto.getNowClearBalance());
        pageInfoUtil.baseInfoFillOld(cfClearHeader);
        cfClearHeader.setFiUser(Strings.EMPTY);
        clearHeaderMapper.insert(cfClearHeader);
        cfClearAddAndUpdateDto.setClearId(cfClearHeader.getClearId());
        return cfClearHeader;
    }

    private void fillClearHeaderBankAndCash(CfBankAndCash cfBankAndCash, CfClearHeader cfClearHeader) {
        cfClearHeader.setFiUser(Objects.isNull(cfBankAndCash.getRecordUser()) ? Strings.EMPTY : cfBankAndCash.getRecordUser());
        cfClearHeader.setBank(Objects.isNull(cfBankAndCash.getBank()) ? Strings.EMPTY : cfBankAndCash.getBank());
        cfClearHeader.setBankNo(Objects.isNull(cfBankAndCash.getBankNo()) ? Strings.EMPTY : cfBankAndCash.getBankNo());
        cfClearHeader.setBankAmount(Objects.isNull(cfBankAndCash.getAmount()) ? new BigDecimal(0) :
                                            cfBankAndCash.getAmount());
        cfClearHeader.setCheckNo(Objects.isNull(cfBankAndCash.getCheckNo()) ? Strings.EMPTY: cfBankAndCash.getCheckNo());
        cfClearHeader.setActualArApDate(Objects.isNull(cfBankAndCash.getArapDate()) ? null :
                                                PageInfoUtil.dateToLocalDateTime(cfBankAndCash.getArapDate()));
        cfClearHeader.setClearMethod(Objects.isNull(cfBankAndCash.getRecordType())?Strings.EMPTY:cfBankAndCash.getRecordType());
    }

    private CfClearHeader createClearHeader(String clearNo) {
        CfClearHeader cfClearHeader = PageInfoUtil.initEntityAndFill(CfClearHeader.class);
        cfClearHeader.setClearNo(clearNo);
        cfClearHeader.setInvoiceNo(Strings.EMPTY);
        cfClearHeader.setJobType(ClearHeaderEnum.JOB_TYPE_MCN.getCode());
        cfClearHeader.setBrandId(Long.valueOf(mcnConfiguration.getBrandIdMcn()));
        cfClearHeader.setCurrencyCode("RMB");
        cfClearHeader.setExchangeRate(BigDecimal.ONE);
        cfClearHeader.setClearBy(pageInfoUtil.getUser().getUserId());
        return cfClearHeader;
    }

    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public String add(CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
        doAdd(cfClearAddAndUpdateDto);
        return cfClearAddAndUpdateDto.getClearNo();
    }

    @Transactional(rollbackFor = Exception.class)
    public String addV1(CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
        doAddV1(cfClearAddAndUpdateDto);
        return cfClearAddAndUpdateDto.getClearNo();
    }

    public ClearHeaderCommitVo batchCommit(CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
        List<Long> list = cfClearAddAndUpdateDto.getClearIds();
        List<CfClearHeader> CfClearHeaders = clearHeaderMapper.selectBatchIds(list);
        CfClearHeaders.forEach(cfClearHeader->{
            if (!ClearHeaderEnum.BEFORE_CLEAR.getCode().equals(cfClearHeader.getClearStatus())){
                throw FinanceBizState.CF_CLEAR_HEAD_DONT_COMMIT;
            }
        });
        List<CfClearHeader> failList = new ArrayList<>();
        List<CfClearHeader> succssList = new ArrayList<>();
        List<CfClearHeader> rejectList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (Long clearId : list) {
                try {
                    mcnClearService.commitCfClear(failList, succssList, rejectList, clearId);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
        return new ClearHeaderCommitVo(failList.size(), succssList.size(), rejectList.size());
    }

    @Transactional(rollbackFor = Exception.class)
    public String update(CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
        CfClearHeader cfClearHeader = clearHeaderMapper.selectById(cfClearAddAndUpdateDto.getClearId());
        if (!ClearHeaderEnum.BEFORE_CLEAR.getCode().equals(cfClearHeader.getClearStatus()) && !ClearHeaderEnum.CLEAR_STATUS_SEVEN.getCode().equals(cfClearHeader.getClearStatus())) {
            throw FinanceBizState.CF_CLEAR_HEAD_ERROR;
        }
        cfClearHeader.setNowClearBalance(cfClearAddAndUpdateDto.getNowClearBalance());
        cfClearHeader.setNowClearDebit(cfClearAddAndUpdateDto.getNowClearBalance());
        cfClearHeader.setLastBalanceBalance(cfClearAddAndUpdateDto.getNowClearBalance());
        cfClearHeader.setRecordSeqNo(cfClearAddAndUpdateDto.getRecordSeqNo());
        cfClearHeader.setReceiptScreenshot(cfClearAddAndUpdateDto.getReceiptScreenshot());
        cfClearHeader.setCollectionAndPayRemark(cfClearAddAndUpdateDto.getCollectionAndPayRemark());
        cfClearHeader.setRemark(cfClearAddAndUpdateDto.getRemark());
        cfClearHeader.setClearStatus(ClearHeaderEnum.BEFORE_CLEAR.getCode());
        cfClearHeader.setRejectReason(null);
        if (!ClearHeaderEnum.BEFORE_CLEAR.getCode().equals(cfClearHeader.getClearStatus())){
            updateState(cfClearHeader.getClearId(), ClearHeaderEnum.BEFORE_CLEAR.getCode());
        }
        clearHeaderMapper.updateById(cfClearHeader);
        //日志
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_Clear, OperationTypeEnum.OPERATION_UPDATE, cfClearHeader.getClearNo(), cfClearHeader.getClearId());
        return cfClearHeader.getClearNo();
    }

    @Transactional(rollbackFor = Exception.class)
    public void commitCfClear(List<CfClearHeader> failList, List<CfClearHeader> succssList,
                              List<CfClearHeader> rejectList, Long clearId) {
        CfClearHeader cfClearHeader = clearHeaderMapper.selectById(clearId);
        CfBankAndCashInfoVo infoByCode = cfBankAndCashService.getInfoByCode(cfClearHeader.getRecordSeqNo());
        List<CfClearDetail> cfClearDetails =
                clearDetailMapper.selectList(Wrappers.lambdaQuery(CfClearDetail.class).eq(CfClearDetail::getClearId,
                                                                                          clearId));
        List<Long> chargeIds =
                cfClearDetails.stream().map(CfClearDetail::getChargeId).collect(Collectors.toList());
        CfChargeCommon cfChargeCommon = chargeCommonService.selectOne(chargeIds.get(0));
        //实收付为空，费用已经被删除，实收付不是MCN，实收付不是待核销合部分核销，实收付不是实收
        String msg1 = check1(infoByCode, cfChargeCommon,cfClearHeader.getNowClearBalance());
        if (org.apache.commons.lang3.StringUtils.isNotBlank(msg1)) {
            cfClearHeader.setRejectReason(msg1);
            failList.add(cfClearHeader);
            clearHeaderMapper.updateById(cfClearHeader);
            return;
        }
        //财务驳回
        String msg = check(infoByCode, cfChargeCommon);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(msg)) {
            if (org.apache.commons.lang3.StringUtils.isBlank(cfClearHeader.getCollectionAndPayRemark())) {
                cfClearHeader.setRejectReason(msg);
                cfClearHeader.setClearStatus(ClearHeaderEnum.CLEAR_STATUS_SEVEN.getCode());
                rejectList.add(cfClearHeader);
                clearHeaderMapper.updateById(cfClearHeader);
                return;
            }
        }
        //成功的处理方式
        ClearContext clearContext = ClearContext.getInstance();
        clearContext.setWaitCfBankAndCashs(Arrays.asList(infoByCode));
        clearContext.setWaitCfChargeCommons(chargeCommonService.selectList(chargeIds));
        update(cfClearHeader, clearContext);
        //日志
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_Clear, OperationTypeEnum.OPERATION_SUBMIT, cfClearHeader.getClearNo(), cfClearHeader.getClearId());
        succssList.add(cfClearHeader);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(CfClearHeader cfClearHeader, ClearContext clearContext) {
        /**
         * 如果流程频繁变动还是用pipeline合适些
         * 获取实收付
         * 获取费用
         * 校验费用总计<=实收付总计， 校验费用是否被核销，
         * 计算费用
         * 创建核销主表
         * 创建核销明细
         * 反写费用
         * 反写开票
         * 反写实收付
         *
         */
        List<CfBankAndCash> cfBankAndCashs = clearContext.getWaitCfBankAndCashs();
        List<CfChargeCommon> cfChargeCommons = clearContext.getWaitCfChargeCommons();

//        BigDecimal sum =
//                cfChargeCommons.stream().map(CfChargeCommon::getOverage).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        cfChargeCommons.forEach(cfChargeCommon -> Assert.isTrue(BigDecimal.ZERO.compareTo(cfChargeCommon.getOverage()) <= 0, ModuleBizState.DATE_ERROR_BUSINESS.format("费用核销余额为0不能核销")));


//        BigDecimal balanceSum = cfBankAndCashs.stream().map(CfBankAndCash::getBalanceBalance).reduce(BigDecimal.ZERO,
//                                                                                                     BigDecimal::add);
        //核销金额
        BigDecimal nowClearBalance = cfClearHeader.getNowClearBalance();
//        cfClearHeader.setNowClearBalance(balanceSum.compareTo(sum) < BigDecimal.ZERO.intValue() ? balanceSum : sum);
        cfClearHeader.setNowClearDebit(cfClearHeader.getNowClearBalance());
        cfClearHeader.setRejectReason(null);
        //核销详情
        updateClearDetails(cfChargeCommons, cfClearHeader, cfClearHeader.getNowClearBalance(), cfBankAndCashs);
        //核销主表
        //补充实收付的字段
        fillClearHeaderBankAndCash(cfBankAndCashs.get(0), cfClearHeader);
        clearHeaderMapper.updateById(cfClearHeader);
        Map<String, List<CfChargeCommon>> invoiceNoChargeCommonMap =
                cfChargeCommons.stream().collect(Collectors.groupingBy(CfChargeCommon::getInvoiceNo));
        //核销详情
        List<CfClearDetail> cfClearDetails =
                clearDetailMapper.selectList(Wrappers.lambdaQuery(CfClearDetail.class).eq(CfClearDetail::getClearId,
                                                                                          cfClearHeader.getClearId()));
        Map<Long, CfClearDetail> map = cfClearDetails.stream().collect(Collectors.toMap(CfClearDetail::getChargeId
                , t -> t, (a, b) -> b));
        invoiceNoChargeCommonMap.entrySet().stream().forEach(stringListEntry -> {
            BigDecimal sonSum =
                    stringListEntry.getValue().stream().map(CfChargeCommon::getAmountPp).reduce(BigDecimal.ZERO,
                                                                                                BigDecimal::add);
            InvoiceStateService.UpdateStateData updateStateData = new InvoiceStateService.UpdateStateData();
            updateStateData.setRemark(cfClearHeader.getRemark());
            updateStateData.setAmount(sonSum);
            updateStateData.setClearCharges(stringListEntry.getValue());
            stringListEntry.getValue().forEach(cfChargeCommon -> {
                //
                //费用反写  MCN 业务 费用一定会被核销掉
                chargeCommonService.updateByClear(cfChargeCommon.getChargeId(), cfClearHeader.getClearNo(),
                                                  cfClearHeader.getCreateDate(),
                                                  map.get(cfChargeCommon.getChargeId()).getNowBalance());
            });
            //反写开票
//            taxInvoiceStateService.updateState(stringListEntry.getKey(),
//                                               TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SIX.getCode(), updateStateData);
        });
        //反写实收付
        bankAndCashCommonService.updateByClearBatch(PageInfoUtil.lambdaToList(cfBankAndCashs,
                                                                              CfBankAndCash::getBankAndCashId),
                                                    cfClearHeader.getClearNo(), nowClearBalance);
        //修改审批状态
        clearStateService.updateState(cfClearHeader.getClearId(), ClearHeaderEnum.CLEAR_STATUS_THREE.getCode());
    }


    private void doAddV1(CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
        ClearContext clearContext = ClearContext.getInstance();
        prepareDateV1(clearContext, cfClearAddAndUpdateDto);
        Assert.isTrue(supportV1(clearContext), ModuleBizState.DATE_ERROR.message());
        saveV1(cfClearAddAndUpdateDto, clearContext);
    }

    @Deprecated
    private void doAdd(CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
        ClearContext clearContext = ClearContext.getInstance();
        prepareDate(clearContext, cfClearAddAndUpdateDto);
        Assert.isTrue(support(clearContext), ModuleBizState.DATE_ERROR.message());
        save(cfClearAddAndUpdateDto, clearContext);
    }

    private void prepareDateV1(ClearContext clearContext, CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
        clearContext.setWaitCfChargeCommons(chargeCommonService.selectList(cfClearAddAndUpdateDto.getChargeIds()));
        List<CfBankAndCash> cfBankAndCashes=Arrays.asList(new CfBankAndCash());
        clearContext.setWaitCfBankAndCashs(cfBankAndCashes);
    }

    @Deprecated
    private void prepareDate(ClearContext clearContext, CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
        clearContext.setWaitCfChargeCommons(chargeCommonService.selectList(cfClearAddAndUpdateDto.getChargeIds()));
        List<CfBankAndCash> cfBankAndCashes = cfBankAndCashService.selectListActive(cfClearAddAndUpdateDto.getRecordSeqNos());
        Assert.isTrue(CollectionUtils.isNotEmpty(cfBankAndCashes) && cfBankAndCashes.size() <= 10, ModuleBizState.DATE_ERROR.message());
        clearContext.setWaitCfBankAndCashs(cfBankAndCashes);
    }

    @Transactional(rollbackFor = Exception.class)
    public String commit(CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
        if (StringUtils.isEmpty(cfClearAddAndUpdateDto.getClearNo())) {
            doAdd(cfClearAddAndUpdateDto);
        }
        updateState(cfClearAddAndUpdateDto.getClearNo(), ClearHeaderEnum.CLEAR_STATUS_THREE.getCode());

        return cfClearAddAndUpdateDto.getClearNo();
    }

    @Override
    public void clearHeaderDetailBase(ClearHeaderDetailVo clearHeaderDetailVo, CfClearHeader cfClearHeader) {

    }

    private boolean notSupport(CfClearHeader cfClearHeader) {
        if (!cfClearHeader.getJobType().equals(ChargeCommonEnum.CHARGE_SOURCE_TYPE_MCN.getCode())) {
            return true;
        }
        return false;
    }

    @Override
    public List<ClearHeaderDetailInvoiceDetailVo> detailInvoiceDetailVos(ClearHeaderDetailVo clearHeaderDetailVo, CfClearHeader cfClearHeader, List<CfClearDetail> cfClearDetails) {
        List<ClearHeaderDetailInvoiceDetailVo> clearHeaderDetailInvoiceDetailVos = new ArrayList<>();
        if (notSupport(cfClearHeader)) {
            return clearHeaderDetailInvoiceDetailVos;
        }
        return getClearHeaderDetailInvoiceDetailVosMcn(clearHeaderDetailVo, cfClearDetails);
    }

    @Override
    public void clearHeaderDetailTotalVo(ClearHeaderDetailVo clearHeaderDetailVo, CfClearHeader cfClearHeader) {
        if (notSupport(cfClearHeader)) {
            return;
        }
    }


    public List<Map<String,String>> getAllBalance() {
        List<Map<String, String>> result = new ArrayList<>();
        List<CfClearHeader> list =
                clearHeaderMapper.selectList(Wrappers.lambdaQuery(CfClearHeader.class).eq(CfClearHeader::getJobType,
                                                                                          ChargeCommonEnum.CHARGE_SOURCE_TYPE_MCN.getCode()).groupBy(CfClearHeader::getBalance).select(CfClearHeader::getBalance, CfClearHeader::getBalance));
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(x -> {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(x.getBalance())) {
                    Map<String, String> map = new HashMap<>();
                    map.put("vendorCode", x.getBalance());
                    map.put("venAbbName", x.getBalance());
                    result.add(map);
                }
            });
        }
        return result;
    }

    private List<ClearHeaderDetailInvoiceDetailVo> getClearHeaderDetailInvoiceDetailVosMcn(ClearHeaderDetailVo clearHeaderDetailVo, List<CfClearDetail> cfClearDetails) {
        List<ClearHeaderDetailInvoiceDetailVo> clearHeaderDetailInvoiceDetailVos=new ArrayList<>();
        List<CfChargeCommon> cfCharges = chargeCommonService.selectList(Wrappers.lambdaQuery(CfChargeCommon.class).in(CfChargeCommon::getChargeId,
                cfClearDetails.stream().map(CfClearDetail::getChargeId).collect(Collectors.toList())));

        if (CollectionUtils.isEmpty(cfCharges)){
            return  clearHeaderDetailInvoiceDetailVos;
        }
        //前面已经校验了，这里直接取
        clearHeaderDetailVo.getClearHeaderDetailBase().setInvoiceNo(cfCharges.get(0).getInvoiceNo());

        clearHeaderDetailInvoiceDetailVos = cfClearDetails.stream().map(cfClearDetail -> {
            ClearHeaderDetailInvoiceDetailVo clearHeaderDetailInvoiceDetailVo = new ClearHeaderDetailInvoiceDetailVo();
            BeanUtils.copyProperties(cfClearDetail, clearHeaderDetailInvoiceDetailVo);

            CfChargeCommon cfCharge =
                    cfCharges.stream().filter(t ->
                            t.getChargeId().equals(cfClearDetail.getChargeId())).findFirst().get();
            //合同金额可能被拆分，所以如果有父级的，取父级的
            if(!Objects.isNull(cfCharge.getParentId())&&!cfCharge.getParentId().equals(-1) ) {
                CfChargeCommon parentCfCharge = chargeCommonService.selectById(cfCharge.getParentId());
                clearHeaderDetailInvoiceDetailVo.setBusinessAmount(parentCfCharge.getAmountPp());
            }else {
                clearHeaderDetailInvoiceDetailVo.setBusinessAmount(cfCharge.getAmountPp());
            }
            Response<IncomeContract> incomeContractResponse = mcnRemoteServer.getByCode(cfCharge.getChargeSourceCode());
            IncomeContract incomeContract = RpcUtil.getObjNoException(incomeContractResponse);

            //加入平台订单号
            clearHeaderDetailVo.getClearHeaderDetailBase().setPlatformOrderNumber(Objects.isNull(incomeContract)?null:incomeContract.getPlatformOrderNumber());
            clearHeaderDetailVo.getClearHeaderDetailBase().setFinanceEntity(cfCharge.getFinanceEntity());
            //这行报错，肯定数据异常
            String chargeCode = cfCharge.getChargeCode();
            clearHeaderDetailInvoiceDetailVo.setChargeCode(chargeCode);
            if (ChargeEnum.ARAP_TYPE_AP.getCode().equals(cfCharge.getArapType())) {
                clearHeaderDetailInvoiceDetailVo.setClearCredit(cfCharge.getAmountPp());
            } else {
                clearHeaderDetailInvoiceDetailVo.setClearDebit(cfCharge.getAmountPp());
            }
            clearHeaderDetailInvoiceDetailVo.setChargeSourceCode(cfCharge.getChargeSourceCode());
            clearHeaderDetailInvoiceDetailVo.setFinanceEntity(cfCharge.getFinanceEntity());
            clearHeaderDetailInvoiceDetailVo.setChargeSourceType(cfCharge.getChargeSourceType());
            clearHeaderDetailInvoiceDetailVo.setCreateDate(cfCharge.getCreateDate());
            clearHeaderDetailInvoiceDetailVo.setBalanceName(cfCharge.getBalance());
            clearHeaderDetailInvoiceDetailVo.setInvoiceNo(cfCharge.getInvoiceNo());
            clearHeaderDetailInvoiceDetailVo.setInvoiceTitle(cfCharge.getInvoiceTitle());
            clearHeaderDetailInvoiceDetailVo.setLastBalance(cfClearDetail.getLastBalance());
            clearHeaderDetailInvoiceDetailVo.setCreateBy(cfCharge.getCreateBy());
            clearHeaderDetailInvoiceDetailVo.setCreateName(cfCharge.getCreateName());
            clearHeaderDetailInvoiceDetailVo.setTaxInvoiceNo(cfCharge.getTaxInvoiceNo());
            clearHeaderDetailInvoiceDetailVo.setContractCode(cfCharge.getChargeSourceCode());
            clearHeaderDetailInvoiceDetailVo.setChargeType(String.valueOf(cfCharge.getChargeType()));
            return clearHeaderDetailInvoiceDetailVo;
        }).collect(Collectors.toList());
        return clearHeaderDetailInvoiceDetailVos;
    }


    /**
     * 核销提交匹配失败得具体原因
     * @param infoByCode
     * @param cfChargeCommon
     * @param nowClearBalance 核销金额
     * @return
     */
    private String check1(CfBankAndCashInfoVo infoByCode,CfChargeCommon cfChargeCommon,BigDecimal nowClearBalance) {
        StringBuilder checkResult = new StringBuilder();
        if (Objects.isNull(infoByCode)) {
            checkResult.append(ModuleBizState.BANKANDCASH_INFO_IS_NULL.message());
            return checkResult.toString();
        }
        if (ChargeCommonEnum.CHECK_STSTUS_ZERO.getCode().equals(cfChargeCommon.getCheckStatus())) {
            checkResult.append(FAIL_REASON_ON);
        }
        if (!infoByCode.getJobType().equals(BankAndCashEnum.JOB_TYPE_MCN.getCode())) {
            checkResult.append(ModuleBizState.BANKANDCASH_TYPE_IS_NOT_MCN.message());
        }
        if (!BankAndCashEnum.BANK_AND_CASH_STATUS_TWO.getCode().equals(infoByCode.getBankAndCashStatus()) &&
                !BankAndCashEnum.BANK_AND_CASH_STATUS_THREE.getCode().equals(infoByCode.getBankAndCashStatus())) {
            checkResult.append(ModuleBizState.BANKANDCASH_STATUS_ERROR.message());
        }
        if (!"1".equals(infoByCode.getArapType())) {
            checkResult.append(ModuleBizState.BANKANDCASH_TYPE_IS_NOT_AR.message());
        }
        if (cfChargeCommon.getOverage().compareTo(nowClearBalance)<0){
            checkResult.append(ModuleBizState.NOWCLEARBALANCE_MORE_THAN_OVERAGE.message());
        }
        if (infoByCode.getBalanceBalance().compareTo(nowClearBalance)<0){
            checkResult.append(ModuleBizState.NOWCLEARBALANCE_MORE_THAN_BALANCEBALANCE.message());
        }
        return checkResult.toString();
    }

    /**
     * 匹配核销时的校验
     * @param infoByCode
     * @param cfChargeCommon
     * @return
     */
    private String check(CfBankAndCashInfoVo infoByCode,CfChargeCommon cfChargeCommon) {
        StringBuilder checkResult = new StringBuilder();
        if (!org.apache.commons.lang3.StringUtils.equals(infoByCode.getPaymentBranch(),
                                                         cfChargeCommon.getInvoiceTitle())) {
            checkResult.append(ModuleBizState.PAYMENT_BRANCH_NOT_EQ_INVOICE_TITLE.message());
        }
        if (!org.apache.commons.lang3.StringUtils.equals(cfChargeCommon.getBalance(), infoByCode.getBalance())) {
            checkResult.append(ModuleBizState.CF_COMMON_BALANCE_NOT_EQ_BK_BALANCE.message());
        }
        if (!org.apache.commons.lang3.StringUtils.equals(cfChargeCommon.getFinanceEntity(),
                                                         infoByCode.getPayCompany())) {
            checkResult.append(ModuleBizState.CF_COMMON_NOT_FINANCEENTITY_EQ_PAY_COMPANY.message());
        }
        return checkResult.toString();
    }


    /**
     * 查询mcn收入合同关联核销
     * @param contractCode
     * @return List<McnClearVo>
     */
    public List<McnClearVo> getMcnClear(String contractCode) {
        //根据收入合同编号查询未删除的费用
        List<CfChargeCommon> chargeList = chargeCommonService.selectCfChargesBySourceCode(contractCode);
        if(CollectionUtils.isEmpty(chargeList)){
            return null;
        }
        List<Long> chargeIds = chargeList.stream().map(CfChargeCommon::getChargeId).distinct().collect(Collectors.toList());
        List<CfClearDetail> clearDetails = clearDetailMapper.selectList(
                Wrappers.lambdaQuery(CfClearDetail.class).eq(CfClearDetail::getChargeType,ChargeCommonEnum.CHARGE_SOURCE_TYPE_MCN.getCode())
                        .in(CfClearDetail::getChargeId,chargeIds));
        if(CollectionUtils.isEmpty(clearDetails)){
            return null;
        }
        List<Long> clearIds = clearDetails.stream().map(CfClearDetail::getClearId).distinct().collect(Collectors.toList());
        //查询除了已删除之外所有的核销单据
        List<CfClearHeader> clearHeaders = clearHeaderMapper.selectList(Wrappers.lambdaQuery(CfClearHeader.class)
                .in(CfClearHeader::getClearId, clearIds)
                .ne(CfClearHeader::getClearStatus, ClearHeaderEnum.IS_DELETE)
        );
        if(CollectionUtils.isEmpty(clearHeaders)){
            return null;
        }
        Map<Long,CfChargeCommon> clearIdMap = new HashMap<>(clearIds.size());
        clearDetails.forEach(detail -> {
            Optional<CfChargeCommon> optional = chargeList.stream().filter(t -> t.getChargeId().equals(detail.getChargeId())).findFirst();
            if(optional.isPresent()){
                clearIdMap.putIfAbsent(detail.getClearId(),optional.get());
            }
        });

        List<McnClearVo> clearList = ClearConvertor.INSTANCE.asVoList(clearHeaders);
        clearList.forEach(clear -> {
            CfChargeCommon cfChargeCommon = clearIdMap.get(clear.getClearId());
            if(cfChargeCommon != null){
                clear.setInvoiceTitle(cfChargeCommon.getInvoiceTitle());
                clear.setFinanceEntity(cfChargeCommon.getFinanceEntity());
            }
        });
        return clearList;
    }


    public ClearHeaderCommitVo batchAgree(List<Long> clearIds){
       List<CfClearHeader> cfClearHeaders= clearHeaderMapper.selectBatchIds(clearIds);
        cfClearHeaders.forEach(x->Assert.isTrue(x.getClearStatus()==ClearHeaderEnum.CLEAR_STATUS_THREE.getCode(),"请选择单据为审核中状态"));
        List<CfClearHeader> failList = new ArrayList<>();
        List<CfClearHeader> succssList = new ArrayList<>();
        for (CfClearHeader cfClearHeader: cfClearHeaders){
            try {
                mcnClearService.updateState(cfClearHeader.getClearId(),ClearHeaderEnum.AFTER_CLEAR.getCode());
                succssList.add(cfClearHeader);
            }catch (Exception e){
                failList.add(cfClearHeader);
                log.error("核销单审核通过报错,报错信息{}",e.getMessage(),e);
            }
        }
        return new ClearHeaderCommitVo(failList.size(), succssList.size());
    }
}
