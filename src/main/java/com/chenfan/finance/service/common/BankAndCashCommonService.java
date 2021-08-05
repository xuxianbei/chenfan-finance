package com.chenfan.finance.service.common;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.finance.dao.CfBankAndCashMapper;
import com.chenfan.finance.enums.BankAndCashEnum;
import com.chenfan.finance.enums.InvoiceHeaderEnum;
import com.chenfan.finance.enums.OperationBsTypeEnum;
import com.chenfan.finance.enums.OperationTypeEnum;
import com.chenfan.finance.exception.FinanceBizState;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.*;
import com.chenfan.finance.model.dto.BankAndCashBatchDeleteDto;
import com.chenfan.finance.model.dto.CreateAndClearDto;
import com.chenfan.finance.model.vo.CfBankAndCashBatchOutput;
import com.chenfan.finance.server.BaseInfoRemoteServer;
import com.chenfan.finance.server.PrivilegeUserServer;
import com.chenfan.finance.server.dto.CompanyNameReq;
import com.chenfan.finance.server.dto.SCompanyRes;
import com.chenfan.finance.server.remote.model.SCompanyBank;
import com.chenfan.finance.server.remote.vo.ExcutionSettleInfoVO;
import com.chenfan.finance.server.remote.vo.InvoiceTitleVO;
import com.chenfan.finance.service.common.state.BankAndCashStateService;
import com.chenfan.finance.utils.FileUtil;
import com.chenfan.finance.utils.OperateUtil;
import com.chenfan.finance.utils.RpcUtil;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.chenfan.finance.utils.pageinfo.model.CreateVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 实收付
 *
 * @author: xuxianbei
 * Date: 2021/3/18
 * Time: 10:17
 * Version:V1.0
 */
@RefreshScope
@Service
@Slf4j
public class BankAndCashCommonService implements InitializingBean {


    @Autowired
    private InvoiceCommonService invoiceCommonService;

    @Autowired
    private CombinationService combinationService;

    @Autowired
    private BankAndCashStateService bankAndCashStateService;

    @Autowired
    private PrivilegeUserServer privilegeUserServer;

    @Autowired
    private BaseInfoRemoteServer baseInfoRemoteServer;

    @Autowired
    private PageInfoUtil pageInfoUtil;

    @Resource
    private CfBankAndCashMapper cfBankAndCashMapper;

    @Autowired
    private McnConfiguration mcnConfiguration;

    @Autowired
    private McnClearService mcnClearService;

    @Autowired
    @Qualifier("sheduleClearMemory")
    private ThreadPoolTaskScheduler sheduleClearMemoryThreadPool;

    public static final String BUSINESS_LOCK = "BankAndCashCommon:";

    public static final String BUSINESS_BATCH = "BankAndCashCommonBatch:";

    @Value("${bank.and.cash.max.error.excel.count:100}")
    private Integer maxErrorExcelCount;

    @Value("${bank.and.cash.max.error.time.out:48}")
    private Integer maxErrorTimeOut;

    /**
     * 错误EXCEL
     * 不用ConcurrentHashMap；因为在put，get这个之间，无法保证原子性，改用读写锁实现保证原子性
     */
    private Map<Long, ErrorExcelInfo> errorExcel;

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    private AtomicInteger errorExcelSum = new AtomicInteger(0);

    @Override
    public void afterPropertiesSet() {
        errorExcel = new HashMap<>(maxErrorExcelCount);
        sheduleClearMemoryThreadPool.scheduleWithFixedDelay(() -> {
            if (errorExcelSum.get() > 0) {
                try {
                    readWriteLock.writeLock().lockInterruptibly();
                    //防止删除时候，被更新了
                    try {
                        Iterator<Map.Entry<Long, ErrorExcelInfo>> iterator = errorExcel.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Long, ErrorExcelInfo> entry = iterator.next();
                            if (entry.getValue().getTimeOut().compareTo(LocalDateTime.now()) <= 0) {
                                iterator.remove();
                                errorExcelSum.decrementAndGet();
                            }
                        }
                    } finally {
                        readWriteLock.writeLock().unlock();
                    }
                } catch (InterruptedException e) {
                    log.error("BankAndCashCommonService schedule clear error", e);
                }
            }
        }, Instant.ofEpochSecond(10L), Duration.ofMinutes(5L));
    }

    private void errorExcelRemove(Long userId) {
        try {
            readWriteLock.writeLock().lock();
            errorExcel.remove(userId);
            errorExcelSum.decrementAndGet();
            if (errorExcelSum.get() < 0) {
                errorExcelSum.set(0);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void errorExcelGet(Long userId) {
        try {
            readWriteLock.readLock().lock();
            errorExcel.get(userId);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }


    private void errorExcelPut(List<CfBankAndCashOutput> outputs) {
        try {
            readWriteLock.writeLock().lockInterruptibly();
            try {
                if (errorExcelSum.get() >= maxErrorExcelCount) {
                    throw new RuntimeException("今日导入文件次数已达上限，请明日再试");
                }
                ErrorExcelInfo errorExcelInfo = new ErrorExcelInfo();
                errorExcelInfo.setCfBankAndCashOutputs(outputs);
                errorExcelInfo.setTimeOut(LocalDateTime.now().plusHours(maxErrorTimeOut));
                errorExcel.put(pageInfoUtil.getUser().getUserId(), errorExcelInfo);
                errorExcelSum.set(errorExcel.size());
            } finally {
                readWriteLock.writeLock().unlock();
            }
        } catch (InterruptedException e) {
            log.error("BankAndCashCommonService errorExcelPut error", e);
        }
    }

    public void excelOutput(HttpServletResponse response) throws Exception {
        ErrorExcelInfo errorExcelInfo = errorExcel.get(pageInfoUtil.getUser().getUserId());
        if (Objects.isNull(errorExcelInfo)) {
            throw FinanceBizState.BANK_AND_CASH_EXCEL_OUTPUT_ERROR;
        }
        Assert.isTrue(!CollectionUtils.isEmpty(errorExcelInfo.getCfBankAndCashOutputs()), ModuleBizState.DATE_ERROR.message());

        FileUtil.exportExcel(PageInfoUtil.copyPropertiesList(errorExcelInfo.getCfBankAndCashOutputs(), CfBankAndCashOutput.class, null),
                null, "实收记录", CfBankAndCashOutput.class,
                "实收记录.xls", response);
    }

    public Integer addCommit(CreateAndClearDto createAndClearDto) {
        CfBankAndCash cfBankAndCash = add(createAndClearDto);
        return commit(cfBankAndCash.getBankAndCashId(), BankAndCashEnum.BANK_AND_CASH_STATUS_SIX.getCode());
    }

    public Integer commit(Long bankAndCashId, Integer status) {
        return bankAndCashStateService.updateState(bankAndCashId, status);
    }

    public List<String> getPaymentBranch(String paymentBranch) {
        List<CfBankAndCash> chargeCommons = cfBankAndCashMapper.selectList(Wrappers.lambdaQuery(CfBankAndCash.class)
                .like(StringUtils.isNotBlank(paymentBranch), CfBankAndCash::getPaymentBranch, paymentBranch)
                .groupBy(CfBankAndCash::getPaymentBranch).select(CfBankAndCash::getPaymentBranch));
        return chargeCommons.stream().filter(t -> Objects.nonNull(t)).map(CfBankAndCash::getPaymentBranch).collect(Collectors.toList());
    }

    public List<CreateVo> createNameList() {
        List<CfBankAndCash> list = cfBankAndCashMapper.selectList(Wrappers.lambdaQuery(CfBankAndCash.class).select(CfBankAndCash::getCreateBy, CfBankAndCash::getCreateName)
                .groupBy(CfBankAndCash::getCreateName));
        return list.stream().map(cfInvoiceHeader -> {
            CreateVo createVo = new CreateVo();
            BeanUtils.copyProperties(cfInvoiceHeader, createVo);
            return createVo;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer batchCreateAndClear(MultipartFile multipartFile) {
        final int importSize = 1000;
        List<CfBankAndCashBatchImport> cfBankAndCashImportList = PageInfoUtil.importExcel(multipartFile, 0, 1, CfBankAndCashBatchImport.class,
                t -> StringUtils.isNotEmpty(t.getInvoiceNo()));
        if (cfBankAndCashImportList.size() >= importSize || CollectionUtils.isEmpty(cfBankAndCashImportList)) {
            throw FinanceBizState.IMPORT_SIZE_ERROR;
        }
        List<String> invoiceNos = cfBankAndCashImportList.stream().map(CfBankAndCashBatchImport::getInvoiceNo).collect(Collectors.toList());
        List<CfInvoiceHeader> invoiceHeaders = invoiceCommonService.selectList(Wrappers.lambdaQuery(CfInvoiceHeader.class).in(CfInvoiceHeader::getInvoiceNo, invoiceNos));
        if (CollectionUtils.isEmpty(invoiceHeaders)) {
            throw FinanceBizState.format(ModuleBizState.DATE_ERROR_BUSINESS, "账单编号不存在");
        }

        Map<String, CfInvoiceHeader> invoiceNoHeaderMap = invoiceHeaders.stream().collect(Collectors.toMap(CfInvoiceHeader::getInvoiceNo, value -> value));

        int verifyResult = verifyBatchData(cfBankAndCashImportList, invoiceNoHeaderMap, invoiceHeaders);
        if (verifyResult == 0) {
            return verifyResult;
        }

        cfBankAndCashImportList.forEach(cfBankAndCashBatchImport -> {
            CreateAndClearDto createAndClearDto = createAndClearDtoAdapter(cfBankAndCashBatchImport, invoiceNoHeaderMap);
            combinationService.createAndClear(createAndClearDto, (oldState, newState) ->
                    (oldState == InvoiceHeaderEnum.INVOICE_STATUS_TWELVE.getCode() || oldState == InvoiceHeaderEnum.INVOICE_STATUS_FIVETEEN.getCode()) &&
                            (newState == InvoiceHeaderEnum.INVOICE_STATUS_NINE.getCode())
            );
        });

        return 1;
    }

    private CreateAndClearDto createAndClearDtoAdapter(CfBankAndCashBatchImport cfBankAndCashBatchImport, Map<String, CfInvoiceHeader> invoiceNoHeaderMap) {
        CfInvoiceHeader cfInvoiceHeader = invoiceNoHeaderMap.get(cfBankAndCashBatchImport.getInvoiceNo());
        CreateAndClearDto createAndClearDto = new CreateAndClearDto();
        createAndClearDto.setInvoiceNo(cfBankAndCashBatchImport.getInvoiceNo());
        createAndClearDto.setArapType("2");
        createAndClearDto.setJobType(3);
        createAndClearDto.setRecordSeqNo("SYS" + cfBankAndCashBatchImport.getInvoiceNo());
        createAndClearDto.setRecordType("5");
        createAndClearDto.setArapDate(new Date());
        createAndClearDto.setRecordUser(pageInfoUtil.getUser().getRealName());
        createAndClearDto.setAmount(cfInvoiceHeader.getInvoicelCredit());
        createAndClearDto.setBalance(cfInvoiceHeader.getBalance());
        ExcutionSettleInfoVO excutionSettleInfoVO = cfBankAndCashBatchImport.getExcutionSettleInfoVO();
        createAndClearDto.setPaymentBranch(excutionSettleInfoVO.getAccountName());
        createAndClearDto.setBank(excutionSettleInfoVO.getAccountBank());
        createAndClearDto.setBankNo(excutionSettleInfoVO.getAccountNumber());
        createAndClearDto.setPayCompanyId(cfBankAndCashBatchImport.getPayCompanyId());
        createAndClearDto.setPayCompany(cfBankAndCashBatchImport.getPayCompany());
        createAndClearDto.setPayBank(cfBankAndCashBatchImport.getPayBank());
        createAndClearDto.setPayBankNo(cfBankAndCashBatchImport.getPayBankNo());
        createAndClearDto.setRemark(cfBankAndCashBatchImport.getRemark());
        return createAndClearDto;
    }

    private int verifyBatchData(List<CfBankAndCashBatchImport> cfBankAndCashImportList, Map<String, CfInvoiceHeader> invoiceNoHeaderMap,
                                List<CfInvoiceHeader> invoiceHeaders) {

        CompanyNameReq companyNameReq = new CompanyNameReq();
        companyNameReq.setNames(PageInfoUtil.lambdaToList(invoiceHeaders, CfInvoiceHeader::getFinanceEntity));
        List<SCompanyBank> sCompanyBanks = RpcUtil.getObjException(privilegeUserServer.getByCompanyName(companyNameReq), "privilegeUserServer 异常");

        // 财务主体， 公司名称
        Map<String, SCompanyBank> balanceCompanyMap = invoiceCommonService.getCompanyInfo(PageInfoUtil.lambdaToList(invoiceHeaders, CfInvoiceHeader::getFinanceEntity));

        List<CfBankAndCashBatchOutput> cfBankAndCashOutputs = new ArrayList<>();
        Set<String> invoiceNoSet = new HashSet<>();
        cfBankAndCashImportList.forEach(cfBankAndCashBatchImport -> {
            CfBankAndCashBatchOutput cfBankAndCashBatchOutput = new CfBankAndCashBatchOutput();
            BeanUtils.copyProperties(cfBankAndCashBatchImport, cfBankAndCashBatchOutput);
            if (!invoiceNoSet.add(cfBankAndCashBatchImport.getInvoiceNo())) {
                cfBankAndCashBatchOutput.setError("账单编号重复");
            } else {
                CfInvoiceHeader cfInvoiceHeader = invoiceNoHeaderMap.get(cfBankAndCashBatchImport.getInvoiceNo());
                if (Objects.isNull(cfInvoiceHeader)) {
                    cfBankAndCashBatchOutput.setError("账单编号错误");
                } else {
                    if (StringUtils.isNotEmpty(cfBankAndCashBatchImport.getPayCompany())) {
                        Optional<SCompanyBank> optionalsCompanyBank =
                                sCompanyBanks.stream().filter(t -> t.getCompanyName().equals(cfBankAndCashBatchImport.getPayCompany())).findFirst();
                        if (!optionalsCompanyBank.isPresent()) {
                            cfBankAndCashBatchOutput.setError("我司打款户名错误");
                        } else {
                            cfBankAndCashBatchImport.setPayCompanyId(String.valueOf(optionalsCompanyBank.get().getCompanyId()));
                        }
                    }
                    if (StringUtils.isNotEmpty(cfBankAndCashBatchImport.getPayBank())) {
                        if (!sCompanyBanks.stream().filter(t -> t.getBankName().equals(cfBankAndCashBatchImport.getPayBank())).findFirst().isPresent()) {
                            cfBankAndCashBatchOutput.setError("我司打款银行错误");
                        }
                    }
                    if (StringUtils.isNotEmpty(cfBankAndCashBatchImport.getPayBankNo())) {
                        if (!sCompanyBanks.stream().filter(t -> t.getCardNumber().equals(cfBankAndCashBatchImport.getPayBankNo())).findFirst().isPresent()) {
                            cfBankAndCashBatchOutput.setError("我司打款账号错误");
                        }
                    }

                    if (!(InvoiceHeaderEnum.INVOICE_STATUS_TWELVE.getCode().equals(cfInvoiceHeader.getInvoiceStatus())
                            || InvoiceHeaderEnum.INVOICE_STATUS_FIVETEEN.getCode().equals(cfInvoiceHeader.getInvoiceStatus()))) {
                        cfBankAndCashBatchOutput.setError("账单状态为已开票或者打款中才可以被核销！");
                    }

                    vertifyFillDefault(balanceCompanyMap, cfBankAndCashBatchImport, cfBankAndCashBatchOutput, cfInvoiceHeader);
                }
            }
            if (StringUtils.isNotEmpty(cfBankAndCashBatchOutput.getError())) {
                cfBankAndCashOutputs.add(cfBankAndCashBatchOutput);
            }
        });
        if (cfBankAndCashOutputs.size() > 0) {
            //这里可能会出问题，但是不影响使用
            pageInfoUtil.cacheSet(BUSINESS_BATCH, pageInfoUtil.getUser().getUserId(),
                    JSONObject.toJSONString(cfBankAndCashOutputs), maxErrorTimeOut, TimeUnit.HOURS);
            return 0;
        }
        return 1;

    }

    private void vertifyFillDefault(Map<String, SCompanyBank> balanceCompanyMap, CfBankAndCashBatchImport cfBankAndCashBatchImport, CfBankAndCashBatchOutput cfBankAndCashBatchOutput, CfInvoiceHeader cfInvoiceHeader) {
        SCompanyBank sCompanyBank = balanceCompanyMap.get(cfInvoiceHeader.getFinanceEntity());

        if (Objects.isNull(sCompanyBank)) {
            cfBankAndCashBatchOutput.setError("找不到打款户名！");
        } else {
            if (StringUtils.isEmpty(cfBankAndCashBatchImport.getPayCompany())) {
                cfBankAndCashBatchImport.setPayCompanyId(String.valueOf(sCompanyBank.getCompanyId()));
                cfBankAndCashBatchImport.setPayCompany(sCompanyBank.getCompanyName());
            }

            if (StringUtils.isEmpty(cfBankAndCashBatchImport.getPayBank())) {
                cfBankAndCashBatchImport.setPayBank(sCompanyBank.getBankName());
            }

            if (StringUtils.isEmpty(cfBankAndCashBatchImport.getPayBankNo())) {
                cfBankAndCashBatchImport.setPayBankNo(sCompanyBank.getCardNumber());
            }
        }


        ExcutionSettleInfoVO excutionSettleInfoVO = invoiceCommonService.getExcutionSettleInfoVO(cfInvoiceHeader.getInvoiceId());
        if (Objects.isNull(excutionSettleInfoVO) || StringUtils.isEmpty(excutionSettleInfoVO.getAccountName()) || StringUtils.isEmpty(excutionSettleInfoVO.getAccountNumber())
                || StringUtils.isEmpty(excutionSettleInfoVO.getAccountBank())) {
            cfBankAndCashBatchOutput.setError("交易方公司信息获取失败");
        }

        cfBankAndCashBatchImport.setExcutionSettleInfoVO(excutionSettleInfoVO);
    }

    public void batchExcelOutput(HttpServletResponse response) throws Exception {
        String getvalue = pageInfoUtil.cacheGet(BUSINESS_BATCH, pageInfoUtil.getUser().getUserId());
        if (StringUtils.isEmpty(getvalue)) {
            throw FinanceBizState.BANK_AND_CASH_EXCEL_OUTPUT_ERROR;
        }
        List<CfBankAndCashBatchOutput> cfBankAndCashBatchOutputs = JSONObject.parseArray(getvalue, CfBankAndCashBatchOutput.class);

        Assert.isTrue(!CollectionUtils.isEmpty(cfBankAndCashBatchOutputs), ModuleBizState.DATE_ERROR.message());

        FileUtil.exportExcel(cfBankAndCashBatchOutputs, null, "账单打款核销", CfBankAndCashBatchOutput.class, "账单打款核销.xls", response);
    }

    /**
     * 批量删除
     *
     * @param bankAndCashBatchDeleteDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer batchDelete(BankAndCashBatchDeleteDto bankAndCashBatchDeleteDto) {
        List<CfBankAndCash> cfBankAndCashes =
                cfBankAndCashMapper.selectBatchIds(bankAndCashBatchDeleteDto.getBankAndCashIds());

        List<String> clearNos = PageInfoUtil.lambdaToList(cfBankAndCashes, CfBankAndCash::getClearNo);

        if (!CollectionUtils.isEmpty(clearNos)) {
            List<CfClearHeader> cfClearHeaders = mcnClearService.selectListByClearNos(clearNos);
            Map<String, CfClearHeader> clearMap =
                    cfClearHeaders.stream().collect(Collectors.toMap(CfClearHeader::getClearNo, Function.identity()));

            cfBankAndCashes.forEach(cfBankAndCash -> {
                if (Objects.nonNull(clearMap.get(cfBankAndCash.getClearNo()))) {
                    throw FinanceBizState.BANK_CASH_BATCH_DELETE_ERROR;
                }
            });
        }

        cfBankAndCashes.forEach(cfBankAndCash -> {
            CfBankAndCash cfBankAndCashUpdate = new CfBankAndCash();
            cfBankAndCashUpdate.setBankAndCashId(cfBankAndCash.getBankAndCashId());
            cfBankAndCashUpdate.setBankAndCashStatus(BankAndCashEnum.BANK_AND_CASH_STATUS_ZERO.getCode());
            updateSynchronize(cfBankAndCashUpdate);
            OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_CASH, OperationTypeEnum.OPERATION_INVALID,cfBankAndCash.getRecordSeqNo(),cfBankAndCash.getBankAndCashId());
        });
        return 1;
    }

    /**
     * 批量作废
     *
     * @param bankAndCashBatchDeleteDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer batchInvalid(BankAndCashBatchDeleteDto bankAndCashBatchDeleteDto) {
        List<CfBankAndCash> cfBankAndCashes =
                cfBankAndCashMapper.selectBatchIds(bankAndCashBatchDeleteDto.getBankAndCashIds());
        List<String> clearNos = PageInfoUtil.lambdaToList(cfBankAndCashes, CfBankAndCash::getClearNo);
        if (!CollectionUtils.isEmpty(clearNos)) {
            List<CfClearHeader> cfClearHeaders = mcnClearService.selectListByClearNos(clearNos);
            Map<String, CfClearHeader> clearMap =
                    cfClearHeaders.stream().collect(Collectors.toMap(CfClearHeader::getClearNo, Function.identity()));

            cfBankAndCashes.forEach(cfBankAndCash -> {
                if (Objects.nonNull(clearMap.get(cfBankAndCash.getClearNo()))) {
                    throw FinanceBizState.BANK_CASH_BATCH_DELETE_ERROR;
                }
            });
        }

        cfBankAndCashes.forEach(cfBankAndCash -> {
            CfBankAndCash cfBankAndCashUpdate = new CfBankAndCash();
            cfBankAndCashUpdate.setBankAndCashId(cfBankAndCash.getBankAndCashId());
            cfBankAndCashUpdate.setBankAndCashStatus(BankAndCashEnum.BANK_AND_CASH_STATUS_FIVE.getCode());
            updateSynchronize(cfBankAndCashUpdate);
        });
        return 1;
    }
    @Data
    static class ErrorExcelBatchInfo {
        private LocalDateTime timeOut;
        private List<CfBankAndCashBatchOutput> cfBankAndCashBatchOutputs;
    }

    @Data
    static class ErrorExcelInfo {
        private LocalDateTime timeOut;
        private List<CfBankAndCashOutput> cfBankAndCashOutputs;
    }


    /**
     * 核销反写
     *
     * @param bankAndCashId
     * @param clearNo
     * @param clearAmout
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateByClear(Long bankAndCashId, String clearNo, BigDecimal clearAmout) {
        CfBankAndCash cfBankAndCashOld = cfBankAndCashMapper.selectById(bankAndCashId);
        Assert.isTrue(Objects.nonNull(cfBankAndCashOld) && cfBankAndCashOld.getBalanceBalance().compareTo(BigDecimal.ZERO) >= 0, ModuleBizState.DATE_ERROR.message());
        Assert.isTrue(cfBankAndCashOld.getBalanceBalance().compareTo(clearAmout) >= 0 &&
                clearAmout.compareTo(BigDecimal.ZERO) >= 0, ModuleBizState.DATE_ERROR_BUSINESS.format("实收付金额必须大于等于费用总金额"));
        CfBankAndCash cfBankAndCashUpdate = new CfBankAndCash();
        cfBankAndCashUpdate.setBankAndCashId(bankAndCashId);
        cfBankAndCashUpdate.setClearNo(PageInfoUtil.stringAdd(cfBankAndCashOld.getClearNo(), clearNo));
        cfBankAndCashUpdate.setBalanceBalance(cfBankAndCashOld.getBalanceBalance().subtract(clearAmout));
        updateSynchronize(cfBankAndCashUpdate);
        bankAndCashStateService.updateState(bankAndCashId,
                cfBankAndCashUpdate.getBalanceBalance().compareTo(BigDecimal.ZERO) == 0 ? BankAndCashEnum.BANK_AND_CASH_STATUS_FOUR.getCode() :
                        BankAndCashEnum.BANK_AND_CASH_STATUS_THREE.getCode());
    }

    /**
     * 批量核销反写
     *
     * @param bankAndCashIds
     * @param clearNo
     * @param clearAmout
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateByClearBatch(List<Long> bankAndCashIds, String clearNo, BigDecimal clearAmout) {
        List<CfBankAndCash> cfBankAndCashOlds = cfBankAndCashMapper.selectBatchIds(bankAndCashIds);
        cfBankAndCashOlds.sort(Comparator.comparing(CfBankAndCash::getBalanceBalance));
        BigDecimal sum = cfBankAndCashOlds.stream().map(CfBankAndCash::getBalanceBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        Assert.isTrue(!CollectionUtils.isEmpty(cfBankAndCashOlds) &&
                sum.compareTo(BigDecimal.ZERO) >= 0, ModuleBizState.DATE_ERROR.message());
        //性能忽略不计
        for (CfBankAndCash cfBankAndCash : cfBankAndCashOlds) {
            CfBankAndCash cfBankAndCashUpdate = new CfBankAndCash();
            cfBankAndCashUpdate.setBankAndCashId(cfBankAndCash.getBankAndCashId());
            cfBankAndCashUpdate.setClearNo(PageInfoUtil.stringAdd(cfBankAndCash.getClearNo(), clearNo));
            clearAmout = cfBankAndCash.getBalanceBalance().subtract(clearAmout);
            /**
             * 1.刚好核销掉
             * 2.大于费用
             * 3.小于费用
             */
            if (clearAmout.compareTo(BigDecimal.ZERO) < 0) {
                cfBankAndCashUpdate.setBalanceBalance(BigDecimal.ZERO);
                clearAmout = clearAmout.abs();
            } else {
                cfBankAndCashUpdate.setBalanceBalance(clearAmout);
            }

            updateSynchronize(cfBankAndCashUpdate);
            bankAndCashStateService.updateState(cfBankAndCash.getBankAndCashId(),
                    cfBankAndCashUpdate.getBalanceBalance().compareTo(BigDecimal.ZERO) == 0 ? BankAndCashEnum.BANK_AND_CASH_STATUS_FOUR.getCode() :
                            BankAndCashEnum.BANK_AND_CASH_STATUS_THREE.getCode());
        }
    }

    /**
     * 驳回后反写实收付
     * @param bankAndCashIds
     * @param clearNo
     * @param clearAmout
     */
    public void backClearBacth(List<Long> bankAndCashIds, String clearNo, BigDecimal clearAmout){
        List<CfBankAndCash> cfBankAndCashOlds = cfBankAndCashMapper.selectBatchIds(bankAndCashIds);
        cfBankAndCashOlds.sort(Comparator.comparing(CfBankAndCash::getBalanceBalance));
        BigDecimal sum = cfBankAndCashOlds.stream().map(CfBankAndCash::getBalanceBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        Assert.isTrue(!CollectionUtils.isEmpty(cfBankAndCashOlds) &&
                              sum.compareTo(BigDecimal.ZERO) >= 0, ModuleBizState.DATE_ERROR.message());
        for (CfBankAndCash cfBankAndCash : cfBankAndCashOlds) {
            CfBankAndCash cfBankAndCashUpdate = new CfBankAndCash();
            cfBankAndCashUpdate.setBankAndCashId(cfBankAndCash.getBankAndCashId());
            cfBankAndCashUpdate.setClearNo(PageInfoUtil.stringSubtract(cfBankAndCash.getClearNo(), clearNo));
            clearAmout = cfBankAndCash.getBalanceBalance().add(clearAmout);
            cfBankAndCashUpdate.setBalanceBalance(clearAmout);

            updateSynchronize(cfBankAndCashUpdate);
            bankAndCashStateService.updateState(cfBankAndCash.getBankAndCashId(),
                                                cfBankAndCashUpdate.getBalanceBalance().compareTo(cfBankAndCash.getAmount()) == 0 ? BankAndCashEnum.BANK_AND_CASH_STATUS_TWO.getCode() :
                                                        BankAndCashEnum.BANK_AND_CASH_STATUS_THREE.getCode());
        }
    }



    /**
     * 同步更新
     */
    private void updateSynchronize(CfBankAndCash cfBankAndCash) {
        if (pageInfoUtil.tryLock(BUSINESS_LOCK, cfBankAndCash.getBankAndCashId())) {
            try {
                cfBankAndCashMapper.updateById(cfBankAndCash);
            } finally {
                pageInfoUtil.tryUnLock(BUSINESS_LOCK, cfBankAndCash.getBankAndCashId());
            }
        } else {
            throw FinanceBizState.SYSTEM_BUSY;
        }
    }


    public Integer update(CreateAndClearDto createAndClearDto) {
        CfBankAndCash cfBankAndCash = cfBankAndCashMapper.selectById(createAndClearDto.getBankAndCashId());
        BeanUtils.copyProperties(createAndClearDto, cfBankAndCash);
        //正常的应该去判断当前是否进入锁状态
        cfBankAndCash.setBankAndCashStatus(null);
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_CASH, OperationTypeEnum.OPERATION_UPDATE,cfBankAndCash.getRecordSeqNo(),cfBankAndCash.getBankAndCashId());
        return cfBankAndCashMapper.updateById(cfBankAndCash);
    }

    @Transactional(rollbackFor = Exception.class)
    public CfBankAndCash add(CreateAndClearDto createAndClearDto, Integer status) {
        CfBankAndCash cfBankAndCashSelect =
                cfBankAndCashMapper.selectOne(Wrappers.lambdaQuery(CfBankAndCash.class).eq(CfBankAndCash::getRecordSeqNo, createAndClearDto.getRecordSeqNo()));
        Assert.isTrue(Objects.isNull(cfBankAndCashSelect), ModuleBizState.BANK_AND_CASH_RECORDSEQNO_ERROR.message());

        CfBankAndCash cfBankAndCash = new CfBankAndCash();
        //去空格
        createAndClearDto.setRecordSeqNo(StringUtils.isNotBlank(createAndClearDto.getRecordSeqNo()) ?
                                                 createAndClearDto.getRecordSeqNo().trim() :
                                                 createAndClearDto.getRecordSeqNo());
        PageInfoUtil.copyProperties(createAndClearDto, cfBankAndCash);
        Assert.isTrue(cfBankAndCash.getAmount().compareTo(BigDecimal.ZERO) >= 0, ModuleBizState.DATE_ERROR_BIGDECIMAL_ZERO.message());
        cfBankAndCash.setBalanceBalance(cfBankAndCash.getAmount());
        cfBankAndCash.setBrandId(mcnConfiguration.getBrandIdMcn());
        cfBankAndCash.setBankAndCashStatus(status);
        pageInfoUtil.baseInfoFillOld(cfBankAndCash);
        cfBankAndCash.setRecordUser(cfBankAndCash.getCreateName());
        cfBankAndCash.setJobType(BankAndCashEnum.JOB_TYPE_MCN.getCode());
        cfBankAndCashMapper.insert(cfBankAndCash);
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_CASH,
                OperationTypeEnum.OPERATION_CREATE,cfBankAndCash.getRecordSeqNo(),cfBankAndCash.getBankAndCashId());

        return cfBankAndCash;
    }

    @Transactional(rollbackFor = Exception.class)
    public CfBankAndCash add(CreateAndClearDto createAndClearDto) {
        return add(createAndClearDto, BankAndCashEnum.BANK_AND_CASH_STATUS_ONE.getCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer excelImport(MultipartFile multipartFile) {
        List<CfBankAndCashImport> cfBankAndCashImportList;
        try {
            cfBankAndCashImportList = PageInfoUtil.importExcel(multipartFile, 0, 1, CfBankAndCashImport.class);
        } catch (Exception e) {
            log.error("excelImport", e);
            return 0;
        }
        List<CfBankAndCash> cfBankAndCashes = bankAndCashImportToCfBankAndCash(cfBankAndCashImportList);
        //清洗无效数据
        cfBankAndCashes = clearNoAvail(cfBankAndCashes);

        ExcelContext excelContext = ExcelContext.getInstance();
        prepareDate(excelContext);
        if (verify(cfBankAndCashes, excelContext)) {
            cfBankAndCashes.forEach(cfBankAndCash -> {
                pageInfoUtil.baseInfoFillOld(cfBankAndCash);
                cfBankAndCash.setRecordUser(cfBankAndCash.getCreateName());
                cfBankAndCashMapper.insert(cfBankAndCash);
                bankAndCashStateService.updateState(cfBankAndCash.getBankAndCashId(), BankAndCashEnum.BANK_AND_CASH_STATUS_SIX.getCode());
            });
        } else {
            return 0;
        }
        return 1;
    }

    private void prepareDate(ExcelContext excelContext) {
        List<SCompanyRes> list = RpcUtil.getObjNoException(privilegeUserServer.getList(""));
        excelContext.setSCompanyRes(list);
        List<InvoiceTitleVO> invoiceTitleVos = RpcUtil.getObjNoException(baseInfoRemoteServer.getinvoiceTitle());
        excelContext.setInvoiceTitleVos(invoiceTitleVos);
    }

    private List<CfBankAndCash> clearNoAvail(List<CfBankAndCash> cfBankAndCashes) {
        return cfBankAndCashes.stream().filter(t -> Objects.nonNull(t.getJobType())).collect(Collectors.toList());
    }

    /**
     * 校验
     * 性能问题，后续统一考虑
     *
     * @param cfBankAndCashes
     * @return
     */
    private boolean verify(List<CfBankAndCash> cfBankAndCashes, ExcelContext excelContext) {
        Set<String> stringSet = new HashSet<>();
        Map<String, String> codeNameMap = pageInfoUtil.getDictsCodeName(PageInfoUtil.PAYMENT_MODE);
        List<CfBankAndCashOutput> outputs = cfBankAndCashes.stream().map(cfBankAndCash -> {
            CfBankAndCashOutput cfBankAndCashOutput = new CfBankAndCashOutput();
            BeanUtils.copyProperties(cfBankAndCash, cfBankAndCashOutput);
            if (!BankAndCashEnum.JOB_TYPE_MCN.getCode().equals(cfBankAndCash.getJobType())) {
                cfBankAndCashOutput.setError("仅支持业务类型【MCN】数据导入。此功能不能用于大货采购业务Excel导入");
            } else if (!verifyRecordSeqNo(cfBankAndCash.getRecordSeqNo(), stringSet)) {
                cfBankAndCashOutput.setError("【流水号】重复");
            } else if (Objects.isNull(cfBankAndCash) || cfBankAndCash.getAmount().compareTo(BigDecimal.ZERO) <= 0
                    || cfBankAndCash.getAmount().scale() > 2) {
                cfBankAndCashOutput.setError("【金额】不合法或者金额要保留小数点后两位");
            } else if (Objects.isNull(cfBankAndCash.getArapDate())) {
                cfBankAndCashOutput.setError("【收付日期】需要是时间格式");
            } else if (StringUtils.isEmpty(cfBankAndCash.getArapType()) || (
                    !"1".equals(cfBankAndCash.getArapType()))) {
                cfBankAndCashOutput.setError("【收付类型】必填");
            } else if (StringUtils.isEmpty(cfBankAndCash.getRecordSeqNo())) {
                cfBankAndCashOutput.setError("【流水号】必填");
            } else if (StringUtils.isEmpty(cfBankAndCash.getRecordType()) ||
                    !PageInfoUtil.isInteger(cfBankAndCash.getRecordType())) {
                cfBankAndCashOutput.setError("【记录类型】必填");
            } else if (StringUtils.isEmpty(cfBankAndCash.getPaymentBranch())) {
                cfBankAndCashOutput.setError("【交易公司名称】必填");
            } else if (StringUtils.isNotEmpty(cfBankAndCash.getPayCompany())) {
                List<SCompanyRes> list = excelContext.getSCompanyRes();
                if (CollectionUtils.isEmpty(list)) {
                    log.error("privilegeUserServer error");
                    cfBankAndCashOutput.setError("权限系统" + FinanceBizState.SYSTEM_BUSY.getMessage());
                } else {
                    if (!list.stream().filter(t -> t.getCompanyName().equals(cfBankAndCash.getPayCompany())).findFirst().isPresent()) {
                        cfBankAndCashOutput.setError("【出入账公司名称】填写错误");
                    }
                }
            } else if (StringUtils.isEmpty(cfBankAndCash.getPayCompany())) {
                cfBankAndCashOutput.setError("【出入账公司名称】必填");
            }
            //未知原因太多else if就不执行了，
            if (StringUtils.isNotEmpty(cfBankAndCash.getPaymentBranch())) {
                List<InvoiceTitleVO> invoiceTitleVos = excelContext.getInvoiceTitleVos();
                if (CollectionUtils.isEmpty(invoiceTitleVos)) {
                    log.error("BaseInfoRemoteServer error");
                    cfBankAndCashOutput.setError("baseinfo" + FinanceBizState.SYSTEM_BUSY.getMessage());
                } else {
                    //根据迭代版本V1.4.0需求：1.去掉校验交易公司名称的校验逻辑 2.结算主体保存逻辑：如果交易方公司名字能匹配到开票抬头则结算主体等于开票抬头对应客户的名字；如果匹配不上，则默认等于交易方公司名字。
                    Optional<InvoiceTitleVO> invoiceTitleVO = invoiceTitleVos.stream().filter(t -> t.getInvoiceTitle().equals(cfBankAndCash.getPaymentBranch())).findFirst();
                    cfBankAndCash.setBalance(invoiceTitleVO.isPresent() ? invoiceTitleVO.get().getCustomerNameC() : cfBankAndCash.getPaymentBranch());
                    /*if (!invoiceTitleVos.stream().filter(t -> t.getInvoiceTitle().equals(cfBankAndCash.getPaymentBranch())).findFirst().isPresent()) {
                        cfBankAndCashOutput.setError("【交易公司名称】找不到对应公司名称，请检查");
                    }*/
                }
            }

            if (StringUtils.isEmpty(cfBankAndCash.getPayBank())) {
                cfBankAndCashOutput.setError("【出入账银行】必填");
            }
            if (StringUtils.isEmpty(cfBankAndCash.getPayBankNo())) {
                cfBankAndCashOutput.setError("【出入账银行账号】必填");
            }
            cfBankAndCashOutput.setRecordType(codeNameMap.get(cfBankAndCashOutput.getRecordType()));
            return cfBankAndCashOutput;
        }).filter(t -> StringUtils.isNotEmpty(t.getError())).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(outputs)) {
            errorExcelPut(outputs);
            return false;
        }
        errorExcelRemove(pageInfoUtil.getUser().getUserId());
        return true;
    }

    private boolean verifyRecordSeqNo(String recordSeqNo, Set<String> stringSet) {
        boolean result = stringSet.add(recordSeqNo);
        if (result) {
            //这里就有点恶心人了。没时间优化，工期太紧了
            CfBankAndCash cfBankAndCash = cfBankAndCashMapper.selectOne(Wrappers.lambdaQuery(CfBankAndCash.class).eq(CfBankAndCash::getRecordSeqNo, recordSeqNo));
            result = Objects.isNull(cfBankAndCash);
        }
        return result;
    }

    private List<CfBankAndCash> bankAndCashImportToCfBankAndCash(List<CfBankAndCashImport> cfBankAndCashImportList) {
        Map<String, String> nameCodeMap = pageInfoUtil.getDictsPayChannel();
        return PageInfoUtil.copyPropertiesList(cfBankAndCashImportList, CfBankAndCash.class, (cfBankAndCash -> {
            cfBankAndCash.setBrandId(mcnConfiguration.getBrandIdMcn());
            //cfBankAndCash.setBalance(cfBankAndCash.getPaymentBranch());
            cfBankAndCash.setBalanceName(cfBankAndCash.getPaymentBranch());
            cfBankAndCash.setBalanceBalance(cfBankAndCash.getAmount());
            cfBankAndCash.setRecordType(nameCodeMap.get(cfBankAndCash.getRecordType()));
        }));
    }
}
