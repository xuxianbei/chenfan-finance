package com.chenfan.finance.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.config.BillNoConstantClassField;
import com.chenfan.finance.dao.*;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.*;
import com.chenfan.finance.model.dto.*;
import com.chenfan.finance.model.vo.*;
import com.chenfan.finance.server.BaseInfoRemoteServer;
import com.chenfan.finance.server.BaseRemoteServer;
import com.chenfan.finance.service.AdvancePayService;
import com.chenfan.finance.service.ApprovalFlowService;
import com.chenfan.finance.service.CfBankAndCashService;
import com.chenfan.finance.service.CfClearHeaderService;
import com.chenfan.finance.service.common.*;
import com.chenfan.finance.service.common.state.BankAndCashStateService;
import com.chenfan.finance.service.common.state.InvoiceStateService;
import com.chenfan.finance.service.common.state.TaxInvoiceStateService;
import com.chenfan.finance.utils.AuthorizationUtil;
import com.chenfan.finance.utils.BeanUtilCopy;
import com.chenfan.finance.utils.FileUtil;
import com.chenfan.finance.utils.OperateUtil;
import com.chenfan.finance.utils.RpcUtil;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author liran
 */
@Slf4j
@Service
public class CfBankAndCashServiceImpl implements CfBankAndCashService {

    @Autowired
    private CfBankAndCashMapper cfBankAndCashMapper;

    @Autowired
    private PageInfoUtil pageInfoUtil;

    @Resource
    private BaseRemoteServer baseRemoteService;
    @Autowired
    private CfChargeMapper cfChargeMapper;
    @Autowired
    private CfClearHeaderService cfClearHeaderService;
    @Autowired
    private AdvancepayApplicationMapper advancepayApplicationMapper;
    @Autowired
    private CfBankAndCashService cfBankAndCashService;
    @Autowired
    private CfClearHeaderMapper cfClearHeaderMapper;
    @Resource
    private CfClearDetailMapper cfClearDetailMapper;

    @Autowired
    private CfInvoiceSettlementMapper invoiceSettlementMapper;

    @Autowired
    private BaseInfoRemoteServer baseInfoRemoteServer;

    @Autowired
    private CombinationService combinationService;

    @Autowired
    private ApprovalFlowService approvalFlowService;
    @Autowired
    private AdvancePayService advancePayService;
    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer create(CfBankAndCash acVo, UserVO userVO) {
        //新增单号生成规则?
        String res = pageInfoUtil.generateBusinessNum(BillNoConstantClassField.ACTUALPAYMENT);
        acVo.setRecordSeqNo(res);
        //新建的时候默认未核算金额为总金额，因为还没有核算过
        acVo.setBalanceBalance(acVo.getAmount());
        acVo.setRecordUser(userVO.getRealName());
        acVo.setCompanyId(userVO.getCompanyId());
        acVo.setCreateBy(userVO.getUserId());
        acVo.setCreateName(userVO.getRealName());
        //创建时间和修改时间都使用当前时间来控制
        acVo.setCreateDate(new Date());
        acVo.setUpdateBy(userVO.getUserId());
        acVo.setUpdateName(userVO.getRealName());
        acVo.setUpdateDate(new Date());
        acVo.setCompanyId(userVO.getCompanyId());
        Integer result = cfBankAndCashMapper.create(acVo);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(CfBankAndCash acVo, UserVO userVO) {
        CfBankAndCashInfoVo info = cfBankAndCashMapper.info(acVo.getBankAndCashId());
        /**
         * 目前只有草稿状态的单据可以支持编辑（新建的）
         */
        if (info.getBankAndCashStatus() != ActualPaymentStateEnum.DRAFT.getState()) {
            throw new RuntimeException("只有草稿状态的单据支持编辑");
        }
        acVo.setUpdateBy(userVO.getUserId());
        acVo.setUpdateName(userVO.getRealName());
        return cfBankAndCashMapper.updateByPrimaryKey(acVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(CfBankAndCashShowRequestVo acVo) {
        return cfBankAndCashMapper.delete(acVo);
    }

    @Override
    public List<CfBankAndCashListShowVo> getList(CfBankAndCashShowRequestVo acVo) {
        return cfBankAndCashMapper.getList(acVo);
    }

    @Override
    public CfBankAndCashInfoVo info(Long bankAndCashId) {
        //根据详情中的brandId，远程获取brandName，一起返回
        CfBankAndCashInfoVo info = cfBankAndCashMapper.info(bankAndCashId);
        if (null != info) {
            //获取brandName
            Integer brandId = info.getBrandId();
            Response<BaseGetBrandInfoList> brandInfo = baseInfoRemoteServer.getBrandInfo(brandId);
            if (null != brandInfo && null != brandInfo.getObj() && !StringUtils.isEmpty(brandInfo.getObj().getBrandName())) {
                info.setBrandName(brandInfo.getObj().getBrandName());
            }
            //核销号有可能是多个，则为字符串拼接"a,b,c",拆分成集合
            if (!StringUtils.isEmpty(info.getClearNo())) {
                String[] str = info.getClearNo().split(",");
                List<String> list = Arrays.asList(str);
                info.setClearNos(list);
            }
            info.setCfBsOperationLogList(OperateUtil.selectOperationLogsByBs(OperationBsTypeEnum.OPERATION_BS_MCN_CASH,info.getRecordSeqNo(),bankAndCashId));
            pageInfoUtil.fillProcessFlowDesc(info.getBankAndCashId(), BankAndCashStateService.APPROVAL_PROCESS_ID, info);
        }
        return info;

    }

    @Override
    public void export(CfBankAndCashShowRequestVo apsVo, UserVO userVO, HttpServletResponse response) {
        List<CfBankAndCashListExportVo> list = cfBankAndCashMapper.export(apsVo);
        try {
            FileUtil.exportExcelV2(
                    list,
                    "财务-实收付款单",
                    "导出人：" + userVO.getRealName(),
                    "财务-实收付款单",
                    CfBankAndCashListExportVo.class,
                    "财务-实收付款单.xlsx",
                    response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CfBankAndCashInfoVo getInfoByCode(String recordSeqNo) {
        //根据详情中的brandId，远程获取brandName，一起返回
        CfBankAndCashInfoVo infoByCode = cfBankAndCashMapper.getInfoByCode(recordSeqNo);
        if (null != infoByCode) {
            Integer brandId = infoByCode.getBrandId();
            Response<BaseGetBrandInfoList> brandInfo = baseInfoRemoteServer.getBrandInfo(brandId);
            if (null != brandInfo && null != brandInfo.getObj().getBrandName()) {
                infoByCode.setBrandName(brandInfo.getObj().getBrandName());
            }
        }
        return infoByCode;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer review(CfBankAndCash cfBankAndCash, UserVO user) {
        String resultMsg = "";
        Integer state = 0;
        /**
         * 当前传入收付款单id和期待转变的状态值
         * 单据状态变化动作
         * 默认创建出来的单子状态为【1 草稿】，点击入账确认按钮变为【2 已入账（未核销）】,
         * 【3 部分核销】,【4 已核销】 由核销那边动作触发生成
         * 只有草稿状态的单据可以作废【5 作废】,目前不可以再次变成开立状态
         * 删除则有批量删除接口
         */
        //转为已入账（未核销）
        CfBankAndCashInfoVo info = cfBankAndCashMapper.info(cfBankAndCash.getBankAndCashId());
        switch (ActualPaymentStateEnum.getEnumByState(cfBankAndCash.getBankAndCashStatus())) {
            //入账确认按钮-转为已入账
            case NOTSTARTED:
                if (ActualPaymentStateEnum.DRAFT.getState() != info.getBankAndCashStatus()) {
                    resultMsg = "此单据状态不符合入账条件";
                    break;
                }
                //此时设置入账确认人和入账时间
                cfBankAndCash.setAccountInUser(user.getRealName());
                cfBankAndCash.setAccountInDate(new Date());
                cfBankAndCashMapper.updateByPrimaryKey(cfBankAndCash);
                //修改状态之后将state返回
                CfBankAndCashInfoVo lastInfo = cfBankAndCashMapper.info(cfBankAndCash.getBankAndCashId());
                state = lastInfo.getBankAndCashStatus();
                resultMsg = "入账成功";
                break;
            case CANCELLED:
                if (ActualPaymentStateEnum.DRAFT.getState() != info.getBankAndCashStatus()) {
                    resultMsg = "只有草稿状态下的单据可以作废";
                    break;
                }
                cfBankAndCashMapper.updateByPrimaryKey(cfBankAndCash);
                //修改状态之后将state返回
                CfBankAndCashInfoVo lastInfo1 = cfBankAndCashMapper.info(cfBankAndCash.getBankAndCashId());
                state = lastInfo1.getBankAndCashStatus();
                resultMsg = "作废成功";
                break;
            default:
                break;
        }
        return state;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createBankCashAndClear(CfBankAndCashInvoiceExtend apVo, UserVO userVO) {
        apVo.setAmount(apVo.getAmount().add(apVo.getAdvancePayMoney()));
        Assert.notNull(apVo.getInvoiceNo(), "参数账单号不可为空");
        String advancePayNos = apVo.getAdvancePayNos();
        log.info("创建实收付的预付款单号：{}", advancePayNos);
        String[] advancePayNoArray = null;
        if (StringUtils.isNotBlank(advancePayNos)) {
            advancePayNoArray = advancePayNos.split(",");
        }

        // 所有的 未核销完毕的实收付 (2=已入账（待核销） 3=部分核销)
        List<AdvancepayApplication> applyPays = new ArrayList<>();
        List<CfBankAndCash> cashes = new ArrayList<>();
        if (advancePayNoArray != null && advancePayNoArray.length > 0) {
            applyPays = advancepayApplicationMapper.selectList(Wrappers.<AdvancepayApplication>lambdaQuery().
                    in(AdvancepayApplication::getAdvancePayCode, advancePayNoArray)
                    .orderByAsc(AdvancepayApplication::getAdvancePayId)
            );
            List<CfBankAndCash> cfBankAndCashes = cfBankAndCashMapper.selectList(Wrappers.<CfBankAndCash>lambdaQuery().
                    in(CfBankAndCash::getSourceCode, advancePayNoArray));
            Map<String, String> collect = cfBankAndCashes.stream().collect(Collectors.toMap(CfBankAndCash::getSourceCode, CfBankAndCash::getSourceCode));
            applyPays.forEach(x->{
                if(!collect.containsKey(x.getAdvancePayCode())){
                    advancePayService.createBankAndCash(BeanUtilCopy.copyPropertiesIgnoreType(x,AdvancePayVo.class), authorizationUtil.getUser());
                }
            });
            cashes = cfBankAndCashMapper.selectList(Wrappers.<CfBankAndCash>lambdaQuery().
                    in(CfBankAndCash::getSourceCode, advancePayNoArray)
                    .in(CfBankAndCash::getBankAndCashStatus, 2, 3)
            );
        }

        // 根据账单找到所有可以核销的费用
        List<CfCharge> allCharge = getAllCharge(apVo.getInvoiceNo());
        List<Long> chargeIds = allCharge.stream().map(CfCharge::getChargeId).distinct().collect(Collectors.toList());
        log.info("账单中的费用ids {}", chargeIds);
        // 获取总金额是否创建 实收付
        BigDecimal sum = cashes.stream().map(CfBankAndCash::getBalanceBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        String clearNo;
        BigDecimal bigDecimalThreadLocal = CFRequestHolder.getBigDecimalThreadLocal();
        log.info(" 未核销完毕的实收付：{},预付款金额：{}，账单金额：{}",sum,apVo.getAdvancePayMoney(),apVo.getAmount());
        if (cashes.size() > 0 && sum.compareTo(apVo.getAmount()) >= 0&&BigDecimal.ONE.compareTo(bigDecimalThreadLocal)!=0) {
            log.info("已存在的实收付 可以进行核销，不用新创建，创建核销单");
            clearNo = createClearAndClear(allCharge, cashes, chargeIds, userVO);
        } else {
            log.info("要创建实收付单据");
            // 创建实收付单据
            BigDecimal insertCashMoney = apVo.getAmount().subtract(sum);
            if (insertCashMoney.compareTo(BigDecimal.ZERO) < 0) {
                insertCashMoney = insertCashMoney.multiply(new BigDecimal("-1"));
            }
            // 创建
            createBankAndCash(applyPays, userVO, insertCashMoney, cashes, apVo);
            // 优先核销 部分核销的实收付，然后核销新创建的
            clearNo = createClearAndClear(allCharge, cashes, chargeIds, userVO);
        }
        // 保存核销到结算单中
        saveClearNoToSettlement(clearNo, apVo.getInvoiceSettlementId());
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer createAndClear(CreateAndClearDto createAndClearDto) {
        return combinationService.createAndClear(createAndClearDto);
    }

    @Override
    public PageInfo<BankAndCashVo> list(BankAndCashDto bankAndCashDto) {
        LambdaQueryWrapper<CfBankAndCash> lambdaQueryWrapper = Wrappers.lambdaQuery(CfBankAndCash.class)
                .like(StringUtils.isNotBlank(bankAndCashDto.getRecordSeqNo()), CfBankAndCash::getRecordSeqNo, bankAndCashDto.getRecordSeqNo())
                .eq(StringUtils.isNotBlank(bankAndCashDto.getBalance()), CfBankAndCash::getBalance, bankAndCashDto.getBalance())
                .eq(StringUtils.isNotBlank(bankAndCashDto.getPaymentBranch()), CfBankAndCash::getPaymentBranch, bankAndCashDto.getPaymentBranch())
                .in(CollectionUtils.isNotEmpty(bankAndCashDto.getBankAndCashStatus()), CfBankAndCash::getBankAndCashStatus, bankAndCashDto.getBankAndCashStatus())
                .in(CollectionUtils.isNotEmpty(bankAndCashDto.getBankAndCashIds()), CfBankAndCash::getBankAndCashId, bankAndCashDto.getBankAndCashIds())
                .between(Objects.nonNull(bankAndCashDto.getBeginArapDate()) && Objects.nonNull(bankAndCashDto.getEndArapDate()),
                        CfBankAndCash::getArapDate, bankAndCashDto.getBeginArapDate(), bankAndCashDto.getEndArapDate())
                .eq(Objects.nonNull(bankAndCashDto.getJobType()), CfBankAndCash::getJobType, bankAndCashDto.getJobType())
                .ne(CfBankAndCash::getBankAndCashStatus, BankAndCashEnum.BANK_AND_CASH_STATUS_ZERO.getCode())
                .in(CollectionUtils.isNotEmpty(bankAndCashDto.getRecordTypes()),CfBankAndCash::getRecordType,bankAndCashDto.getRecordTypes())
                .orderByDesc(CfBankAndCash::getCreateDate);
        PageInfoUtil.startPage(bankAndCashDto);
        //过滤公司权限
        if (Objects.nonNull(bankAndCashDto.getCompanyIds())) {
            lambdaQueryWrapper.in(CfBankAndCash::getCompanyId, bankAndCashDto.getCompanyIds());
        }
        //过滤用户权限
        if (Objects.nonNull(bankAndCashDto.getUserIds())) {
            lambdaQueryWrapper.in(CfBankAndCash::getCreateBy, bankAndCashDto.getUserIds());
        }
        //过滤品牌权限
        if (Objects.nonNull(bankAndCashDto.getBrandIds())) {
            lambdaQueryWrapper.in(CfBankAndCash::getBrandId, bankAndCashDto.getBrandIds());
        }

        List<CfBankAndCash> cfBankAndCashes = cfBankAndCashMapper.selectList(lambdaQueryWrapper);
        Map<String, String> clearNoCreateNameMap = new HashMap<>();
        List<String> clearNos = cfBankAndCashes.stream().map(CfBankAndCash::getClearNo).filter(t -> StringUtils.isNotBlank(t)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(clearNos)) {
            List<List<String>> clearNoss = clearNos.stream().map(t -> Arrays.asList(t.split(","))).collect(Collectors.toList());
            List<String> clearNoList = PageInfoUtil.sonToSum(clearNoss);
            List<CfClearHeader> cfClearHeaders =
                    cfClearHeaderMapper.selectList(Wrappers.lambdaQuery(CfClearHeader.class).in(CfClearHeader::getClearNo, clearNoList));
            clearNoCreateNameMap.putAll(cfClearHeaders.stream().collect(Collectors.toMap(CfClearHeader::getClearNo, CfClearHeader::getCreateName)));
        }

        List<BankAndCashVo> bankAndCashVos = cfBankAndCashes.stream().map(cfBankAndCash -> {
            BankAndCashVo bankAndCashVo = new BankAndCashVo();
            PageInfoUtil.copyProperties(cfBankAndCash, bankAndCashVo);
            bankAndCashVo.setClearAmout(bankAndCashVo.getAmount().subtract(bankAndCashVo.getBalanceBalance()));
            String clearName = clearNoCreateNameMap.get(cfBankAndCash.getClearNo());
            bankAndCashVo.setClearName(clearName);
            pageInfoUtil.fillProcessFlowDesc(bankAndCashVo.getBankAndCashId(), BankAndCashStateService.APPROVAL_PROCESS_ID, bankAndCashVo);
            return bankAndCashVo;
        }).collect(Collectors.toList());

        return PageInfoUtil.toPageInfo(cfBankAndCashes, bankAndCashVos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalCallback(CfBankAndCash cfBankAndCash, ApprovalFlowDTO approvalFlowDTO, Boolean status) {
        cfBankAndCash = cfBankAndCashMapper.selectById(cfBankAndCash.getBankAndCashId());
        approvalFlowDTO.setSrcCode(cfBankAndCash.getRecordSeqNo());
        approvalFlowDTO.setProcessId(ApprovalEnum.BANK_AND_CASH_APPROVAL.getProcessId());
        if (Objects.nonNull(status)) {
            //审批通过（流程结束）或审批被拒绝发给创建人
            approvalFlowService.sendNotify(approvalFlowDTO, Long.valueOf(cfBankAndCash.getBankAndCashId()),
                    cfBankAndCash.getRecordSeqNo()
                    , ApprovalEnum.BANK_AND_CASH_APPROVAL, status,
                    cfBankAndCash.getCreateBy(),
                    cfBankAndCash.getCreateName());
        } else {
            for (int i = 0; i < approvalFlowDTO.getTargetUserId().size(); i++) {
                approvalFlowService.sendNotify(approvalFlowDTO, Long.valueOf(cfBankAndCash.getBankAndCashId()),
                        cfBankAndCash.getRecordSeqNo()
                        , ApprovalEnum.BANK_AND_CASH_APPROVAL, status,
                        Long.parseLong(approvalFlowDTO.getTargetUserId().get(i)),
                        approvalFlowDTO.getTargetUserName().get(i));
            }
        }
    }


    private void saveClearNoToSettlement(String clearNo, Long invoiceSettlementId) {
        List<CfClearHeader> cfCharges = cfClearHeaderMapper.selectList(Wrappers.lambdaQuery(CfClearHeader.class)
                .in(CfClearHeader::getClearNo, clearNo));
        LocalDateTime createDate = cfCharges.get(0).getCreateDate();
        CfInvoiceSettlement settlement = new CfInvoiceSettlement();
        settlement.setInvoiceSettlementId(invoiceSettlementId);
        settlement.setClearDate(createDate);
        settlement.setClearNo(clearNo);
        invoiceSettlementMapper.updateById(settlement);

    }

    private void createBankAndCash(List<AdvancepayApplication> applyPays, UserVO userVo, BigDecimal insertCashMoney, List<CfBankAndCash> cashes, CfBankAndCashInvoiceExtend apVo) {
        for (AdvancepayApplication applyPay : applyPays) {
            boolean b = cashes.stream().anyMatch((a) -> a.getSourceCode().equals(applyPay.getAdvancePayCode()));
            if (b) {
                log.info("当前的预付款 已经有实收付款了");
                continue;
            }
            CfBankAndCash cash = new CfBankAndCash();
            cash.setRecordType(apVo.getRecordType());
            cash.setArapDate(new Date());
            cash.setBrandId(apVo.getBrandId());
            cash.setBankAndCashStatus(2);
            cash.setBalance(apVo.getBalance());
            cash.setBalanceName(apVo.getBalanceName());
            cash.setRecordUser(apVo.getRecordUser());
            cash.setArapType(apVo.getArapType());
            BigDecimal cur = applyPay.getMoney();
            // 要创建的实付款《= 当前预付款的
            if (insertCashMoney.compareTo(cur) <= 0) {
                cash.setAmount(insertCashMoney);
                break;
            } else {
                // 要创建的实付款=要创建的实付款-当前预付款的金额
                insertCashMoney = insertCashMoney.subtract(cur);
                cash.setAmount(cur);
            }

            cash.setBank(apVo.getBank());
            cash.setBankNo(apVo.getBankNo());
            cash.setCollectionUnit(apVo.getCollectionUnit());
            cash.setSourceCode(applyPay.getAdvancePayCode());
            cfBankAndCashService.create(cash, userVo);
            //入账确认
            CfBankAndCash up = new CfBankAndCash();
            up.setBankAndCashId(cash.getBankAndCashId());
            up.setBankAndCashStatus(ActualPaymentStateEnum.NOTSTARTED.getState());
            review(up, userVo);
            CfBankAndCash cashCreated = cfBankAndCashMapper.selectById(cash.getBankAndCashId());
            cashes.add(cashCreated);
        }
        // 可能有剩余的
        if (insertCashMoney.compareTo(BigDecimal.ZERO) > 0) {
            log.warn("创建了所有的实收付，大于预付款单中金额总和，继续创建一个实收付单据");
            CfBankAndCash cash = new CfBankAndCash();
            cash.setRecordType(apVo.getRecordType());
            cash.setArapDate(new Date());
            cash.setBrandId(apVo.getBrandId());
            cash.setBankAndCashStatus(2);
            cash.setBalance(apVo.getBalance());
            cash.setBalanceName(apVo.getBalanceName());
            cash.setRecordUser(apVo.getRecordUser());
            cash.setAmount(insertCashMoney);
            cash.setArapType(apVo.getArapType());
            cash.setBank(apVo.getBank());
            cash.setBankNo(apVo.getBankNo());
            cash.setCollectionUnit(apVo.getCollectionUnit());
            cfBankAndCashService.create(cash, userVo);
            //入账确认
            CfBankAndCash up = new CfBankAndCash();
            up.setBankAndCashId(cash.getBankAndCashId());
            up.setBankAndCashStatus(ActualPaymentStateEnum.NOTSTARTED.getState());
            review(up, userVo);
            CfBankAndCash cashCreated = cfBankAndCashMapper.selectById(cash.getBankAndCashId());
            cashes.add(cashCreated);
        }

    }


    private String createClearAndClear(List<CfCharge> allCharge, List<CfBankAndCash> cashes, List<Long> collect, UserVO userVO) {
        List<String> seqs = cashes.stream().map(CfBankAndCash::getRecordSeqNo).distinct().collect(Collectors.toList());
        CfClearAddAndUpdateDto createClear = new CfClearAddAndUpdateDto();
        CfCharge cfCharge = allCharge.get(0);
        createClear.setBalance(cfCharge.getBalance());
        createClear.setBrandId(cfCharge.getBrandId());
        createClear.setClearBy(userVO.getUserId());
        createClear.setClearDate(LocalDateTime.now());
        createClear.setFiUser(userVO.getRealName());
        createClear.setBalance(cfCharge.getBalance());
        createClear.setChargeIds(collect);
        createClear.setRecordSeqNos(seqs);
        return cfClearHeaderService.create(createClear, userVO);
    }

    private List<CfCharge> getAllCharge(String no) {
        // 校验费用
        CfChargeListQuery query = new CfChargeListQuery();
        query.setInvoiceNo(no);
        query.setInvoiceNeed(true);
        List<CfCharge> list = cfChargeMapper.queryChargeList(query);
        Assert.isTrue(list.size() > 0, "账单中没有可以进行核销的费用了");
        return list;
    }

    /**
     * 获取实收付
     * 状态为：2=已入账（待核销） 3=部分核销
     *
     * @param recordSeqNos
     * @return
     */
    public List<CfBankAndCash> selectListActive(List<String> recordSeqNos) {
        List<CfBankAndCash> cfBankAndCashes =
                cfBankAndCashMapper.selectList(Wrappers.lambdaQuery(CfBankAndCash.class)
                        .in(CfBankAndCash::getRecordSeqNo, recordSeqNos)
                        .in(CfBankAndCash::getBankAndCashStatus, BankAndCashStatusEnum.BANK_AND_CASH_STATUS_2.getCode(),
                                BankAndCashStatusEnum.BANK_AND_CASH_STATUS_3.getCode()));
        return cfBankAndCashes;
    }


    @Override
    public int getCount(CfBankAndCashShowRequestVo acVo) {
        return cfBankAndCashMapper.count(acVo);
    }

    @Override
    public boolean saveBatch(Collection<CfBankAndCash> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<CfBankAndCash> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<CfBankAndCash> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(CfBankAndCash entity) {
        return false;
    }

    @Override
    public CfBankAndCash getOne(Wrapper<CfBankAndCash> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<CfBankAndCash> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<CfBankAndCash> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<CfBankAndCash> getBaseMapper() {
        return null;
    }
}
