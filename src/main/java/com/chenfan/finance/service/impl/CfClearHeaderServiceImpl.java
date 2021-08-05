package com.chenfan.finance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.common.exception.BusinessException;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.commons.exception.FinanceTipException;
import com.chenfan.finance.config.BillNoConstantClassField;
import com.chenfan.finance.model.dto.*;
import com.chenfan.finance.service.ApprovalFlowService;
import com.chenfan.finance.service.common.ChargeCommonService;
import com.chenfan.finance.service.common.DelegateClearService;
import com.chenfan.finance.dao.*;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.model.*;
import com.chenfan.finance.model.vo.*;
import com.chenfan.finance.server.BaseInfoRemoteServer;
import com.chenfan.finance.server.BaseRemoteServer;
import com.chenfan.finance.server.PrivilegeUserServer;
import com.chenfan.finance.server.VendorCenterServer;
import com.chenfan.finance.server.remote.request.BrandFeignRequest;
import com.chenfan.finance.server.remote.vo.BrandFeignVO;
import com.chenfan.finance.service.CfClearHeaderService;
import com.chenfan.finance.service.common.state.ClearStateService;
import com.chenfan.finance.utils.OperateUtil;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.chenfan.finance.utils.RpcUtil;
import com.chenfan.privilege.request.SUserVOReq;
import com.chenfan.privilege.response.SUserVORes;
import com.chenfan.vendor.response.VendorResModel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 核销服务
 *
 * @Author:lywang
 * @Date:2020/8/22
 */
@Slf4j
@Service
public class CfClearHeaderServiceImpl implements CfClearHeaderService {

    @Autowired
    private PageInfoUtil pageInfoUtil;

    @Resource
    private BaseRemoteServer baseRemoteService;
    @Autowired
    private PrivilegeUserServer privilegeUserServer;
    @Resource
    private CfClearHeaderMapper clearHeaderMapper;
    @Resource
    private CfClearDetailMapper clearDetailMapper;
    @Resource
    private CfBankAndCashMapper cfBankAndCashMapper;

    @Resource
    private CfClearBankDetailMapper clearBankDetailMapper;
    @Resource
    private CfChargeMapper cfChargeMapper;

    @Autowired
    private VendorCenterServer vendorCenterServer;

    @Resource
    private CfInvoiceHeaderMapper cfInvoiceHeaderMapper;
    @Autowired
    private BaseInfoRemoteServer baseInfoRemoteServer;

    @Autowired
    private ChargeCommonService chargeCommonService;

    @Autowired
    private DelegateClearService delegateClearService;

    @Autowired
    private ApprovalFlowService approvalFlowService;
    @Autowired
    private AdvancepayApplicationMapper advancepayApplicationMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CfClearAddAndUpdateDto cfClearAddAndUpdateDto, UserVO user) {

        //校验费用数据
        vertifyData(cfClearAddAndUpdateDto);

        //1.创建核销单
        CfClearHeader cfClearHeader = createCfClearHeader(cfClearAddAndUpdateDto, user);
        Assert.isTrue(clearHeaderMapper.insert(cfClearHeader) == 1, "数据库异常");
        //2.费用分条计算
        invoiceDetailsCalc(cfClearAddAndUpdateDto, cfClearHeader);
        clearHeaderMapper.updateById(cfClearHeader);
        return cfClearHeader.getClearNo();
    }

    private CfClearHeader createCfClearHeader(CfClearAddAndUpdateDto cfClearAddAndUpdateDto, UserVO user) {
        CfClearHeader cfClearHeader = new CfClearHeader();
        BeanUtils.copyProperties(cfClearAddAndUpdateDto, cfClearHeader);
        cfClearHeader.setCreateBy(user.getUserId());
        cfClearHeader.setCreateDate(LocalDateTime.now());
        cfClearHeader.setClearStatus(ClearHeaderEnum.BEFORE_CLEAR.getCode());
        //新增单号生成规则:CL+200818+0001
        String res = pageInfoUtil.generateBusinessNum(BillNoConstantClassField.CLEAR);
        cfClearHeader.setClearNo(res);
        cfClearHeader.setCurrencyCode("RMB");
        cfClearHeader.setExchangeRate(BigDecimal.ONE);
        cfClearHeader.setClearDate(LocalDateTime.now());
        cfClearHeader.setClearBy(user.getUserId());
        cfClearHeader.setCompanyId(user.getCompanyId());
        cfClearHeader.setCreateName(user.getRealName());
        cfClearHeader.setUpdateBy(user.getUserId());
        cfClearHeader.setUpdateName(user.getRealName());
        cfClearHeader.setUpdateDate(LocalDateTime.now());
        cfClearHeader.setClearMethod("");
        cfClearHeader.setBalance("");
        cfClearHeader.setBank("");
        cfClearHeader.setBankNo("");
        cfClearHeader.setBankAmount(BigDecimal.ZERO);
        cfClearHeader.setCheckNo("");
        cfClearHeader.setClearDate(null);
        cfClearHeader.setClearDebit(BigDecimal.ZERO);
        cfClearHeader.setClearCredit(BigDecimal.ZERO);
        cfClearHeader.setClearType("");
        cfClearHeader.setClearBalance(BigDecimal.ZERO);
        cfClearHeader.setNowClearDebit(BigDecimal.ZERO);
        cfClearHeader.setNowClearCredit(BigDecimal.ZERO);
        cfClearHeader.setNowClearType("");
        cfClearHeader.setNowClearBalance(BigDecimal.ZERO);
        cfClearHeader.setLastBalanceCredit(BigDecimal.ZERO);
        cfClearHeader.setLastBalanceDebit(BigDecimal.ZERO);
        cfClearHeader.setLastBalanceType("");
        cfClearHeader.setLastBalanceBalance(BigDecimal.ZERO);
        cfClearHeader.setNowBalanceDebit(BigDecimal.ZERO);
        cfClearHeader.setNowBalanceCredit(BigDecimal.ZERO);
        cfClearHeader.setNowBalanceType("");
        cfClearHeader.setNowBalanceBalance(BigDecimal.ZERO);
        cfClearHeader.setInvoiceNo("");
        return cfClearHeader;
    }

    private void vertifyData(CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
        if (CollectionUtils.isEmpty(cfClearAddAndUpdateDto.getChargeIds())) {
            return;
        }
        /**
         * 1.不属于同一张账单的费用不允许生成核销单
         * 2.校验费用核销状态，
         * 3.费用是被核销完了
         * 4.账单必须是已审核的。（费用中的账单号被反写了，说明该账单号一定是已审核的）
         */
        List<CfCharge> cfCharges = cfChargeMapper.selectList(Wrappers.lambdaQuery(CfCharge.class)
                .in(CfCharge::getChargeId, cfClearAddAndUpdateDto.getChargeIds()));
        if (cfCharges.stream().map(CfCharge::getBrandId).distinct().count() > 1 ||
                cfCharges.stream().map(CfCharge::getBrandId).distinct().count() > 1) {
            throw new FinanceTipException("请选择同一品牌，同一结算主体");
        }
        CfInvoiceHeader cfInvoiceHeader = null;
        for (CfCharge cfCharge : cfCharges) {
            if (!cfCharge.getCheckStatus().equals(ChargeCheckStatusEnum.SH.getCode())) {
                throw new FinanceTipException("数据异常：费用状态不是已审核");
            }
            if (Objects.nonNull(cfCharge.getActualAmount()) && cfCharge.getAmountPp().compareTo(cfCharge.getActualAmount()) <= 0) {
                throw new FinanceTipException("数据异常：费用已经被核销完了");
            }
            if (Objects.isNull(cfInvoiceHeader)) {
                cfInvoiceHeader = cfInvoiceHeaderMapper.selectOne(Wrappers.lambdaQuery(CfInvoiceHeader.class).eq(CfInvoiceHeader::getInvoiceNo, cfCharge.getInvoiceNo()));
            }
            if (StringUtils.isBlank(cfCharge.getInvoiceNo())) {
                throw new FinanceTipException("数据异常：该费用:%s的账单没有审核或者为空", cfCharge.getChargeCode());
            }
        }

    }

    /**
     * 处理核销明细
     *
     * @param cfClearAddAndUpdateDto
     * @param cfClearHeader
     */
    private void invoiceDetailsCalc(CfClearAddAndUpdateDto cfClearAddAndUpdateDto, CfClearHeader cfClearHeader) {
        /**
         * 判断该费用总和类型
         * 处理实收付单号
         */
        if (CollectionUtils.isEmpty(cfClearAddAndUpdateDto.getChargeIds())) {
            return;
        }
        LambdaQueryWrapper<CfCharge> lambdaQueryWrapper = Wrappers.lambdaQuery(CfCharge.class);
        lambdaQueryWrapper.in(CfCharge::getChargeId, cfClearAddAndUpdateDto.getChargeIds());
        List<CfCharge> cfCharges = cfChargeMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(cfCharges)) {
            return;
        }

        //反写核销主体
        cfClearHeader.setBalance(cfCharges.get(0).getBalance());

        Optional<BigDecimal> debitSumOpt =
                cfCharges.stream().filter(t -> t.getArapType().equals(ChargeEnum.ARAP_TYPE_AR.getCode())).map(
                        cfCharge -> cfCharge.getAmountPp().subtract(
                                Objects.isNull(cfCharge.getActualAmount()) ? BigDecimal.ZERO : cfCharge.getActualAmount()))
                        .reduce(BigDecimal::add);
        Optional<BigDecimal> creditSumOpt =
                cfCharges.stream().filter(t -> t.getArapType().equals(ChargeEnum.ARAP_TYPE_AP.getCode())).map(cfCharge -> cfCharge.getAmountPp().subtract(
                        Objects.isNull(cfCharge.getActualAmount()) ? BigDecimal.ZERO : cfCharge.getActualAmount())).reduce(BigDecimal::add);
        BigDecimal debitSum = debitSumOpt.orElse(BigDecimal.ZERO);
        BigDecimal creditSum = creditSumOpt.orElse(BigDecimal.ZERO);
        creditSum = creditSum.multiply(BigDecimal.valueOf(-1));
        //3.费用总计存主表
        BigDecimal sum = debitSum.add(creditSum);
        ClearHeaderSaveDbContext clearHeaderSaveDbContext = createClearHeaderSaveDbContext(cfCharges);
        clearHeaderSaveDbContext.setCfCharges(cfCharges);
        //校验 实收付款单是 收入，支出;
        List<CfBankAndCash> cfBankAndCashs = vertifyBankCash(cfClearAddAndUpdateDto, sum, clearHeaderSaveDbContext, cfClearHeader);
        clearHeaderSaveDbContext.setLastInvoiceSum(sum);
        clearHeaderSaveDbContext.setLastInvoiceDebit(debitSum);
        clearHeaderSaveDbContext.setLastInvoiceCredit(creditSum);
        clearHeaderSaveDbContext.setCfBankAndCashs(cfBankAndCashs);
        //上一次实收付合计
        BigDecimal preCash = cfBankAndCashs.stream().map(
                cfBankAndCash -> Objects.isNull(cfBankAndCash.getBalanceBalance()) ? BigDecimal.ZERO : cfBankAndCash.getBalanceBalance())
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        //上一次费用已核销费用总计
        BigDecimal actualAmoutSum = cfCharges.stream().map(
                cfCharge -> Objects.isNull(cfCharge.getActualAmount()) ? BigDecimal.ZERO : cfCharge.getActualAmount())
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        clearHeaderSaveDbContext.setLastActualAmoutSum(actualAmoutSum);
        clearHeaderSaveDbContext.setLastBankAndCashSum(preCash);
        clearHeaderSaveDbContext.setCfCharges(cfCharges);
        clearHeaderSaveDbContext.setCfClearHeader(cfClearHeader);

        CfInvoiceHeader cfInvoiceHeader = cfInvoiceHeaderMapper.selectOne(Wrappers.lambdaQuery(CfInvoiceHeader.class)
                .eq(CfInvoiceHeader::getInvoiceNo, cfCharges.get(0).getInvoiceNo()));
        clearHeaderSaveDbContext.setCfInvoiceHeader(cfInvoiceHeader);

        //费用处理
        createCfClearDetails(clearHeaderSaveDbContext);

        //费用总计处理
        totalInvoiceCharge(clearHeaderSaveDbContext);

        //实收付 反写逻辑，明细填充
        if (!CollectionUtils.isEmpty(clearHeaderSaveDbContext.getCfBankAndCashs())) {
            chargeAndBankAndCashFill(clearHeaderSaveDbContext, sum);
        }

        doSave(clearHeaderSaveDbContext);

    }

    private void totalInvoiceCharge(ClearHeaderSaveDbContext clearHeaderSaveDbContext) {
        //核销总计
        CfClearHeader cfClearHeader = clearHeaderSaveDbContext.getCfClearHeader();
        cfClearHeader.setClearDebit(clearHeaderSaveDbContext.getInvoiceDebit());
        cfClearHeader.setClearCredit(clearHeaderSaveDbContext.getInvoiceCredit().abs());
        cfClearHeader.setClearBalance(clearHeaderSaveDbContext.getInvoiceSum().abs());
        cfClearHeader.setClearType(clearHeaderSaveDbContext.getInvoiceDebit()
                .compareTo(clearHeaderSaveDbContext.getInvoiceCredit()) >= 0 ?
                ChargeEnum.ARAP_TYPE_AR.getCode() : ChargeEnum.ARAP_TYPE_AP.getCode());

        cfClearHeader.setLastBalanceCredit(clearHeaderSaveDbContext.getLastInvoiceCredit().abs());
        cfClearHeader.setLastBalanceDebit(clearHeaderSaveDbContext.getLastInvoiceDebit().abs());
        cfClearHeader.setLastBalanceBalance(clearHeaderSaveDbContext.getLastInvoiceSum().abs());
        cfClearHeader.setLastBalanceType(clearHeaderSaveDbContext.getLastInvoiceSum().compareTo(BigDecimal.ZERO) >= 0 ?
                ChargeEnum.ARAP_TYPE_AR.getCode() : ChargeEnum.ARAP_TYPE_AP.getCode());
    }

    private ClearHeaderSaveDbContext createClearHeaderSaveDbContext(List<CfCharge> cfCharges) {
        BigDecimal debitSum =
                cfCharges.stream().filter(t -> t.getArapType().equals(ChargeEnum.ARAP_TYPE_AR.getCode()))
                        .map(CfCharge::getAmountPp).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal creditSum =
                cfCharges.stream().filter(t -> t.getArapType().equals(ChargeEnum.ARAP_TYPE_AP.getCode()))
                        .map(CfCharge::getAmountPp).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return new ClearHeaderSaveDbContext(debitSum.subtract(creditSum), debitSum, creditSum);
    }

    private void createCfClearDetails(ClearHeaderSaveDbContext clearHeaderSaveDbContext) {
        List<CfCharge> cfCharges = clearHeaderSaveDbContext.getCfCharges();
        List<CfClearDetail> cfClearDetails = cfCharges.stream().map(cfCharge ->
                createCfClearDetail(clearHeaderSaveDbContext.getCfClearHeader(), cfCharge)
        ).collect(Collectors.toList());
        clearHeaderSaveDbContext.setCfClearDetails(cfClearDetails);
    }

    private void doSave(ClearHeaderSaveDbContext clearHeaderSaveDbContext) {
        //性能略差，在体量上来之后  不过不用考虑，因为不可能选数十万记录的
        clearHeaderSaveDbContext.getCfCharges().forEach(cfCharge -> cfChargeMapper.updateById(cfCharge));
        cfInvoiceHeaderMapper.updateById(clearHeaderSaveDbContext.getCfInvoiceHeader());
        clearHeaderSaveDbContext.getCfBankAndCashs().forEach(cfBankAndCash -> cfBankAndCashMapper.updateById(cfBankAndCash));
        if (clearHeaderSaveDbContext.getCfClearHeader().getClearId() != null) {
            clearHeaderMapper.updateById(clearHeaderSaveDbContext.getCfClearHeader());
        } else {
            clearHeaderMapper.insert(clearHeaderSaveDbContext.getCfClearHeader());
        }
        clearHeaderSaveDbContext.getCfClearDetails().forEach(cfClearDetail -> {
            if (Objects.isNull(cfClearDetail.getClearDetailId())) {
                clearDetailMapper.insert(cfClearDetail);
            } else {
                clearDetailMapper.updateById(cfClearDetail);
            }
        });

    }

    private void chargeAndBankAndCashFill(ClearHeaderSaveDbContext clearHeaderSaveDbContext, BigDecimal sum) {
        /**
         * 1.实收付金额和费用总和相等；
         * 2.核销金额大于实收付金额;
         * 3.核销金额小于实收付金额；
         */
        List<CfBankAndCash> cfBankAndCash = clearHeaderSaveDbContext.getCfBankAndCashs();
        //实收付
        Optional<BigDecimal> sumBalance = cfBankAndCash.stream().map(CfBankAndCash::getBalanceBalance).reduce(BigDecimal::add);
        clearHeaderSaveDbContext.setBankAndCashSum(sumBalance.get());

        //创建多个核销
        createMultiClear(clearHeaderSaveDbContext);

        if (sum.abs().compareTo(sumBalance.get()) == 0) {
            bankAndCashEqualsCharge(clearHeaderSaveDbContext);
        } else if (sum.abs().compareTo(sumBalance.get()) > 0) {
            bankAndCashLessThanCharge(clearHeaderSaveDbContext);
        } else if (sum.abs().compareTo(sumBalance.get()) < 0) {
            bankAndCashGreaterThanCharge(clearHeaderSaveDbContext);
        }
        //核销总计
        finishClearHeader(clearHeaderSaveDbContext);
    }


    private void bankAndCashGreaterThanCharge(ClearHeaderSaveDbContext clearHeaderSaveDbContext) {
        List<CfBankAndCash> cfBankAndCashs = clearHeaderSaveDbContext.getCfBankAndCashs();
        CfClearHeader cfClearHeader = clearHeaderSaveDbContext.getCfClearHeader();

        updateCfClearDetails(clearHeaderSaveDbContext.getCfClearDetails(), clearHeaderSaveDbContext, cfBankAndCashs, cfClearHeader);

        List<CfBankAndCash> cfBankAndCashsNew = cfBankAndCashs.stream().sorted(Comparator.comparing(CfBankAndCash::getBalanceBalance)).collect(Collectors.toList());
        BigDecimal invoiceSum = clearHeaderSaveDbContext.getLastInvoiceSum().abs();

        for (CfBankAndCash cfBankAndCash : cfBankAndCashsNew) {
            if (cfBankAndCash.getBalanceBalance().compareTo(invoiceSum) <= 0) {
                invoiceSum = invoiceSum.subtract(cfBankAndCash.getBalanceBalance());
                cfBankAndCash.setBankAndCashStatus(BankAndCashStatusEnum.BANK_AND_CASH_STATUS_4.getCode());
                cfBankAndCash.setBalanceBalance(BigDecimal.ZERO);
                cfBankAndCash.setClearNo(StringUtils.isBlank(cfBankAndCash.getClearNo()) ?
                        cfClearHeader.getClearNo() : cfBankAndCash.getClearNo() + "," + cfClearHeader.getClearNo());
            } else {
                if (invoiceSum.compareTo(BigDecimal.ZERO) > 0) {
                    if(CFRequestHolder.getBigDecimalThreadLocal().compareTo(BigDecimal.ONE)==0){
                        cfBankAndCash.setBankAndCashStatus(BankAndCashStatusEnum.BANK_AND_CASH_STATUS_4.getCode());
                        cfBankAndCash.setBalanceBalance(BigDecimal.ZERO);
                        cfBankAndCash.setClearNo(StringUtils.isBlank(cfBankAndCash.getClearNo()) ?
                                cfClearHeader.getClearNo() : cfBankAndCash.getClearNo() + "," + cfClearHeader.getClearNo());
                    }else {
                        cfBankAndCash.setBankAndCashStatus(BankAndCashStatusEnum.BANK_AND_CASH_STATUS_3.getCode());
                        cfBankAndCash.setBalanceBalance(cfBankAndCash.getBalanceBalance().subtract(invoiceSum));
                        cfBankAndCash.setClearNo(StringUtils.isBlank(cfBankAndCash.getClearNo()) ?
                                cfClearHeader.getClearNo() : cfBankAndCash.getClearNo() + "," + cfClearHeader.getClearNo());
                        break;
                    }
                }
            }
        }

        //反写账单
        fillInvoiceHeader(clearHeaderSaveDbContext, InvoiceHeaderEnum.CLEAR_STATUS_ALL);
    }

    private void bankAndCashLessThanCharge(ClearHeaderSaveDbContext clearHeaderSaveDbContext) {
        List<CfBankAndCash> cfBankAndCashs = clearHeaderSaveDbContext.getCfBankAndCashs();
        CfClearHeader cfClearHeader = clearHeaderSaveDbContext.getCfClearHeader();

        List<CfClearDetail> needs, others;
        if (clearHeaderSaveDbContext.getChargeEnum() == ChargeEnum.ARAP_TYPE_AR) {
            needs = clearHeaderSaveDbContext.getDebits();
            others = clearHeaderSaveDbContext.getCredits();
        } else if (clearHeaderSaveDbContext.getChargeEnum() == ChargeEnum.ARAP_TYPE_AP) {
            needs = clearHeaderSaveDbContext.getCredits();
            others = clearHeaderSaveDbContext.getDebits();
        } else {
            throw new FinanceTipException("数据异常: 未知arap_type");
        }

        needs = updateClearDetailsNo(clearHeaderSaveDbContext, cfBankAndCashs, cfClearHeader, needs);
        updateCfClearDetails(others, clearHeaderSaveDbContext, cfBankAndCashs, cfClearHeader);
        //多次费用核销
        multiCfClearDetails(clearHeaderSaveDbContext, needs, others);
        //实收付单为已核销
        bankAndCashToAllVerifacation(cfBankAndCashs, cfClearHeader);

        //反写账单
        fillInvoiceHeader(clearHeaderSaveDbContext, InvoiceHeaderEnum.CLEAR_STATUS_PAR);
    }

    private void createMultiClear(ClearHeaderSaveDbContext clearHeaderSaveDbContext) {
        Map<String, List<CfClearDetail>> map = clearHeaderSaveDbContext.getCfClearDetails().stream().collect(Collectors.groupingBy(CfClearDetail::getArapType));
        if(CollectionUtils.isEmpty(map.get(ChargeEnum.ARAP_TYPE_AP.getCode()))){
            map.put(ChargeEnum.ARAP_TYPE_AP.getCode(), new ArrayList<>());
        }
        if (CollectionUtils.isEmpty(map.get(ChargeEnum.ARAP_TYPE_AR.getCode()))) {
            map.put(ChargeEnum.ARAP_TYPE_AR.getCode(), new ArrayList<>());
        }
        List<CfClearDetail> cfClearDebits = map.get(ChargeEnum.ARAP_TYPE_AR.getCode());
        List<CfClearDetail> cfClearCredits = map.get(ChargeEnum.ARAP_TYPE_AP.getCode());

        clearHeaderSaveDbContext.setDebits(cfClearDebits);
        clearHeaderSaveDbContext.setCredits(cfClearCredits);
    }

    private List<CfClearDetail> updateClearDetailsNo(ClearHeaderSaveDbContext clearHeaderSaveDbContext, List<CfBankAndCash> cfBankAndCashs, CfClearHeader cfClearHeader, List<CfClearDetail> cfClearDetails) {
        return cfClearDetails.stream().map(cfClearDetail -> {
            CfCharge cfCharge = indexOfCfCharges(clearHeaderSaveDbContext, cfClearDetail);
            updateCfClearDetail(cfClearHeader, cfClearDetail, cfBankAndCashs.get(0).getBalanceName(), cfCharge, false);
            return cfClearDetail;
        }).sorted(Comparator.comparing(CfClearDetail::getClearedAmount))
                .collect(Collectors.toList());
    }

    private void fillInvoiceHeader(ClearHeaderSaveDbContext clearHeaderSaveDbContext, InvoiceHeaderEnum invoiceHeaderEnum) {
        clearHeaderSaveDbContext.getCfInvoiceHeader().setClearStatus(invoiceHeaderEnum.getCode());
        BigDecimal amout = clearHeaderSaveDbContext.getBankAndCashSum().min(clearHeaderSaveDbContext.getLastInvoiceSum().abs());
        clearHeaderSaveDbContext.getCfInvoiceHeader().setClearAmount(
                Objects.isNull(clearHeaderSaveDbContext.getCfInvoiceHeader().getClearAmount()) ?
                        amout : clearHeaderSaveDbContext.getCfInvoiceHeader().getClearAmount().add(amout));
    }


    private void multiCfClearDetails(ClearHeaderSaveDbContext clearHeaderSaveDbContext,
                                     List<CfClearDetail> cfClearDetails, List<CfClearDetail> others) {
        BigDecimal balance = clearHeaderSaveDbContext.getBankAndCashSum();
        List<CfClearDetail> needcfClearDetails = new ArrayList<>();
        BigDecimal other =
                others.stream().map(CfClearDetail::getActualClearAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        balance = balance.add(other);
        needcfClearDetails.addAll(others);
        clearHeaderSaveDbContext.setCfClearDetails(needcfClearDetails);
        CfClearDetail cfClearDetailFirst = cfClearDetails.get(0);
        needcfClearDetails.add(cfClearDetailFirst);
        if (cfClearDetailFirst.getActualClearAmount().compareTo(balance) >= 0) {
            CfCharge cfChargeFirst = indexOfCfCharges(clearHeaderSaveDbContext, cfClearDetailFirst);
            chargeAndClearDetailMultiVerification(cfChargeFirst, needcfClearDetails, balance, cfClearDetailFirst, clearHeaderSaveDbContext);
            return;
        }

        int i;
        //100 100  150
        for (i = 0; i < cfClearDetails.size(); i++) {
            CfClearDetail cfClearDetail = cfClearDetails.get(i);
            CfCharge cfCharge = indexOfCfCharges(clearHeaderSaveDbContext, cfClearDetail);
            //余额
            BigDecimal amout = cfCharge.getAmountPp().subtract(cfCharge.getActualAmount());
            if (amout.compareTo(balance) <= 0) {
                cfCharge.setActualAmount(
                        Objects.isNull(cfCharge.getActualAmount()) ? BigDecimal.ZERO : cfCharge.getActualAmount().add(amout));
                cfClearDetail.setActualClearAmount(amout);
                needcfClearDetails.add(cfClearDetail);
            } else {
                //费用多次核销处理
                chargeAndClearDetailMultiVerification(cfCharge, needcfClearDetails, balance, cfClearDetail, clearHeaderSaveDbContext);
                break;
            }
            balance = balance.subtract(cfClearDetail.getActualClearAmount());
        }
        i++;
        for (; i < cfClearDetails.size(); i++) {
            CfClearDetail cfClearDetail = cfClearDetails.get(i);
            cfClearDetail.setClearedAmount(BigDecimal.ZERO);
            cfClearDetail.setActualClearAmount(BigDecimal.ZERO);
            CfCharge cfCharge = indexOfCfCharges(clearHeaderSaveDbContext, cfClearDetail);
            cfClearDetail.setNowBalance(cfCharge.getAmountPp());
            needcfClearDetails.add(cfClearDetail);
        }
    }

    private CfCharge indexOfCfCharges(ClearHeaderSaveDbContext clearHeaderSaveDbContext, CfClearDetail cfClearDetail) {
        Optional<CfCharge> optionalCfCharge =
                clearHeaderSaveDbContext.getCfCharges().stream().filter(t -> t.getChargeId().equals(cfClearDetail.getChargeId())).findFirst();
        if (!optionalCfCharge.isPresent()) {
            throw new FinanceTipException("数据异常:  费用ChargeId：" + cfClearDetail.getChargeId());
        }
        return optionalCfCharge.get();
    }

    private void chargeAndClearDetailMultiVerification(CfCharge cfCharge, List<CfClearDetail> needcfClearDetails,
                                                       BigDecimal balance, CfClearDetail cfClearDetail,
                                                       ClearHeaderSaveDbContext clearHeaderSaveDbContext) {
        //这里有两种情况，一种这是第一次。第二种这是第二次同一笔费用没有被核销掉
        if (Objects.isNull(cfCharge.getActualAmount()) || (cfCharge.getActualAmount().compareTo(BigDecimal.ZERO) == 0)) {
            cfCharge.setActualAmount(balance);
            cfClearDetail.setClearedAmount(balance);
            cfClearDetail.setNowBalance(cfCharge.getAmountPp().subtract(cfCharge.getActualAmount()));
            cfClearDetail.setActualClearAmount(balance);
            needcfClearDetails.add(cfClearDetail);
        } else {
            cfClearDetail.setLastBalance(cfCharge.getAmountPp().subtract(cfCharge.getActualAmount()));
            cfCharge.setActualAmount(cfCharge.getActualAmount().add(balance));
            cfClearDetail.setClearedAmount(cfCharge.getActualAmount());
            cfClearDetail.setNowBalance(cfCharge.getAmountPp().subtract(cfCharge.getActualAmount()));
            cfClearDetail.setActualClearAmount(balance);
            needcfClearDetails.add(cfClearDetail);
        }

    }

    private void bankAndCashToAllVerifacation(List<CfBankAndCash> cfBankAndCashs, CfClearHeader cfClearHeader) {
        cfBankAndCashs.stream().forEach(cfBankAndCash -> {
            cfBankAndCash.setBankAndCashStatus(BankAndCashStatusEnum.BANK_AND_CASH_STATUS_4.getCode());
            cfBankAndCash.setBalanceBalance(BigDecimal.ZERO);
            cfBankAndCash.setClearNo(StringUtils.isBlank(cfBankAndCash.getClearNo()) ?
                    cfClearHeader.getClearNo() : cfBankAndCash.getClearNo() + "," + cfClearHeader.getClearNo());
        });
    }

    private void updateCfClearDetail(CfClearHeader cfClearHeader, CfClearDetail cfClearDetail, String balanceName,
                                     CfCharge cfCharge, Boolean isDefault) {
        cfClearDetail.setBalanceName(balanceName);
        cfCharge.setClearNo(StringUtils.isBlank(cfCharge.getClearNo()) ? cfClearHeader.getClearNo() :
                cfCharge.getClearNo() + "," + cfClearHeader.getClearNo());
        cfCharge.setActualHistoryDate(StringUtils.isBlank(cfCharge.getActualHistoryDate()) ?
                cfCharge.getActualDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
                cfCharge.getActualHistoryDate() + "," +
                        cfCharge.getActualDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (isDefault) {
            cfClearDetail.setActualClearAmount(cfCharge.getAmountPp().subtract(cfCharge.getActualAmount()));
            cfCharge.setActualAmount(cfCharge.getAmountPp().subtract(cfCharge.getActualAmount()));
        }
    }

    private CfClearDetail createCfClearDetail(CfClearHeader cfClearHeader,
                                              CfCharge cfCharge) {
        CfClearDetail cfClearDetail = new CfClearDetail();
        cfClearDetail.setLastBalance(cfCharge.getAmountPp());
        cfClearDetail.setNowBalance(BigDecimal.ZERO);
        cfClearDetail.setClearDebit(BigDecimal.ZERO);
        cfClearDetail.setClearCredit(BigDecimal.ZERO);
        cfClearDetail.setActualClearAmount(BigDecimal.ZERO);
        if (Objects.nonNull(cfCharge.getActualAmount()) && cfCharge.getActualAmount().compareTo(BigDecimal.ZERO) > 0) {
            cfClearDetail.setLastBalance(cfCharge.getAmountPp().subtract(cfCharge.getActualAmount()));
        }
        cfClearDetail.setClearedAmount(cfCharge.getAmountPp());
        cfCharge.setActualDate(LocalDateTime.now());
        cfClearDetail.setClearId(cfClearHeader.getClearId());
        cfClearDetail.setInvoiceNo(cfCharge.getInvoiceNo());
        cfClearDetail.setInvoiceTitle(cfCharge.getInvoiceTitle());
        cfClearDetail.setBalance(cfCharge.getBalance());
        cfClearDetail.setInvoiceTitleName(cfCharge.getInvoiceTitleName());
        cfClearDetail.setChargeType(cfCharge.getChargeType());
        cfClearDetail.setSourceCurrencyCode(cfCharge.getCurrencyCode());
        cfClearDetail.setSourceExchangeRate(cfCharge.getExchangeRate());
        cfClearDetail.setChargeId(cfCharge.getChargeId());
        cfClearDetail.setArapType(cfCharge.getArapType());
        if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(cfCharge.getArapType())) {
            cfClearDetail.setClearDebit(cfCharge.getAmountPp());
        }
        if (ChargeEnum.ARAP_TYPE_AP.getCode().equals(cfCharge.getArapType())) {
            cfClearDetail.setClearCredit(cfCharge.getAmountPp());
        }
        cfClearDetail.setChargeSourceCode(cfCharge.getChargeSourceCode());
        return cfClearDetail;
    }

    /**
     * 实收付和费用相等
     *
     * @param clearHeaderSaveDbContext
     */
    private void bankAndCashEqualsCharge(ClearHeaderSaveDbContext clearHeaderSaveDbContext) {
        List<CfBankAndCash> cfBankAndCashs = clearHeaderSaveDbContext.getCfBankAndCashs();
        CfClearHeader cfClearHeader = clearHeaderSaveDbContext.getCfClearHeader();
        updateCfClearDetails(clearHeaderSaveDbContext.getCfClearDetails(), clearHeaderSaveDbContext, cfBankAndCashs, cfClearHeader);

        //实收付单为已核销
        bankAndCashToAllVerifacation(cfBankAndCashs, cfClearHeader);

        //反写账单
        fillInvoiceHeader(clearHeaderSaveDbContext, InvoiceHeaderEnum.CLEAR_STATUS_ALL);
    }

    private void updateCfClearDetails(List<CfClearDetail> cfClearDetails, ClearHeaderSaveDbContext clearHeaderSaveDbContext,
                                      List<CfBankAndCash> cfBankAndCashs, CfClearHeader cfClearHeader) {
        cfClearDetails.stream().forEach(cfClearDetail -> {
            CfCharge cfCharge = indexOfCfCharges(clearHeaderSaveDbContext, cfClearDetail);
            updateCfClearDetail(cfClearHeader, cfClearDetail, cfBankAndCashs.get(0).getBalanceName(), cfCharge, true);
        });
    }

    private void finishClearHeader(ClearHeaderSaveDbContext clearHeaderSaveDbContext) {
        CfClearHeader cfClearHeader = clearHeaderSaveDbContext.getCfClearHeader();
        BigDecimal nowCreditBalance =
                clearHeaderSaveDbContext.getCredits().stream().map(CfClearDetail::getActualClearAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal nowDebitBalance =
                clearHeaderSaveDbContext.getDebits().stream().map(CfClearDetail::getActualClearAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        BigDecimal nowBalanceCreditBalance =
                clearHeaderSaveDbContext.getCredits().stream().map(CfClearDetail::getNowBalance).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal nowBalanceDebitBalance =
                clearHeaderSaveDbContext.getDebits().stream().map(CfClearDetail::getNowBalance).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        //如果是0 上面已经校验过了 这里只会出现大于0 或 小于0
        cfClearHeader.setNowClearDebit(nowDebitBalance);
        cfClearHeader.setNowClearCredit(nowCreditBalance);
        cfClearHeader.setNowClearBalance(nowDebitBalance.subtract(nowCreditBalance).abs());
        cfClearHeader.setNowBalanceDebit(nowBalanceDebitBalance);
        cfClearHeader.setNowBalanceCredit(nowBalanceCreditBalance);
        cfClearHeader.setNowBalanceBalance(nowBalanceDebitBalance.subtract(nowBalanceCreditBalance).abs());
        if (clearHeaderSaveDbContext.getLastInvoiceSum().compareTo(BigDecimal.ZERO) > 0) {
            cfClearHeader.setClearType(ChargeEnum.ARAP_TYPE_AR.getCode());
            cfClearHeader.setNowClearType(ChargeEnum.ARAP_TYPE_AR.getCode());
            cfClearHeader.setNowBalanceType(ChargeEnum.ARAP_TYPE_AR.getCode());
        } else {
            cfClearHeader.setClearType(ChargeEnum.ARAP_TYPE_AP.getCode());
            cfClearHeader.setNowClearType(ChargeEnum.ARAP_TYPE_AP.getCode());
            cfClearHeader.setNowBalanceType(ChargeEnum.ARAP_TYPE_AP.getCode());
        }
        cfClearHeader.setClearStatus(ClearHeaderEnum.AFTER_CLEAR.getCode());
        cfClearHeader.setClearDate(LocalDateTime.now());
        CfBankAndCash cfBankAndCash =
                clearHeaderSaveDbContext.getCfBankAndCashs().get(0);
        cfClearHeader.setBalance(cfBankAndCash.getBalance());
        cfClearHeader.setBank(cfBankAndCash.getBank());
        cfClearHeader.setBankNo(cfBankAndCash.getBankNo());
        cfClearHeader.setClearMethod(cfBankAndCash.getRecordType());
        cfClearHeader.setInvoiceNo(clearHeaderSaveDbContext.getCfCharges().get(0).getInvoiceNo());
    }

    private List<CfBankAndCash> vertifyBankCash(CfClearAddAndUpdateDto cfClearAddAndUpdateDto, BigDecimal sum,
                                                ClearHeaderSaveDbContext clearHeaderSaveDbContext, CfClearHeader cfClearHeader) {
        /**
         * 1.待核销金额大于0，那么选择待实支付单的收付类型必须是实收或者预收反之亦然；
         * 2.未核销的金额为0，则该实付核销单不能被选择
         * 3.实收付单的状态：必须为 2=已入账（待核销） 3=部分核销
         * 4.如果是已入账：那么未核销金额等于总金额
         *
         */
        List<AdvancepayApplication> advancepayApplications = advancepayApplicationMapper.selectList(Wrappers.<AdvancepayApplication>lambdaQuery().eq(AdvancepayApplication::getInvoiceNo, CFRequestHolder.getStringThreadLocal()));
        Boolean isCheck =CollectionUtils.isEmpty(advancepayApplications);
        List<CfBankAndCash> cfBankAndCashs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cfClearAddAndUpdateDto.getRecordSeqNos())) {
            cfBankAndCashs = cfBankAndCashMapper.selectList(Wrappers.lambdaQuery(CfBankAndCash.class)
                    .in(CfBankAndCash::getRecordSeqNo, cfClearAddAndUpdateDto.getRecordSeqNos())
                     .orderByAsc(CfBankAndCash::getBankAndCashId))
                    ;
            if (!CollectionUtils.isEmpty(cfBankAndCashs)) {
                cfBankAndCashs.stream().forEach(cfBankAndCash -> {
                        if (sum.compareTo(BigDecimal.ZERO) > 0) {
                            if (isCheck&&(!(Objects.equals(cfBankAndCash.getArapType(), BankAndCashBusinessEnum.ARAP_TYPE_DEBIT.getCode()) ||
                                    Objects.equals(cfBankAndCash.getArapType(), BankAndCashBusinessEnum.ARAP_TYPE_DEBIT_PRE.getCode())))) {
                                throw new FinanceTipException("数据异常：实收付款单的收付类型应该为%s 或 %s", BankAndCashBusinessEnum.ARAP_TYPE_DEBIT.getMsg(),
                                        BankAndCashBusinessEnum.ARAP_TYPE_DEBIT_PRE.getMsg());
                            }
                        } else {
                            if (sum.compareTo(BigDecimal.ZERO) == 0) {
                                throw new FinanceTipException("数据异常");
                            }
                            if (isCheck&&(!(Objects.equals(cfBankAndCash.getArapType(), BankAndCashBusinessEnum.ARAP_TYPE_CREDIT.getCode()) ||
                                    Objects.equals(cfBankAndCash.getArapType(), BankAndCashBusinessEnum.ARAP_TYPE_CREDIT_PRE.getCode())))) {
                                throw new FinanceTipException("数据异常：实收付款单的收付类型应该为%s 或 %s", BankAndCashBusinessEnum.ARAP_TYPE_CREDIT.getMsg(),
                                        BankAndCashBusinessEnum.ARAP_TYPE_CREDIT_PRE.getMsg());
                            }
                        }
                        clearHeaderSaveDbContext.setChargeEnum(ChargeEnum.getByBankAndCashBusinessCode(cfBankAndCash.getArapType()));
                    if (!Objects.equals(cfBankAndCash.getBankAndCashStatus(), BankAndCashStatusEnum.BANK_AND_CASH_STATUS_2.getCode())
                            && !Objects.equals(cfBankAndCash.getBankAndCashStatus(), BankAndCashStatusEnum.BANK_AND_CASH_STATUS_3.getCode())) {
                        throw new FinanceTipException("数据异常: 实收付款单%s的状态：%s", cfBankAndCash.getRecordSeqNo(),
                                BankAndCashStatusEnum.getStatusMsg(cfBankAndCash.getBankAndCashStatus()));
                    }
                    if (Objects.equals(cfBankAndCash.getBankAndCashStatus(), BankAndCashStatusEnum.BANK_AND_CASH_STATUS_2.getCode())) {
                        Assert.isTrue(cfBankAndCash.getBalanceBalance().equals(cfBankAndCash.getAmount()), "数据异常: 实收付款单未核销金额不等于总金额");
                    }
                    if (cfBankAndCash.getBalanceBalance().equals(BigDecimal.ZERO)) {
                        throw new FinanceTipException("数据异常：该实收付单的未核销金额为0");
                    }
                    CfCharge cfCharge =
                            clearHeaderSaveDbContext.getCfCharges().get(0);
                    if (!(cfBankAndCash.getBalance().equals(cfCharge.getBalance()) && cfBankAndCash.getBrandId().equals(cfCharge.getBrandId().intValue()))) {
                        throw new FinanceTipException("数据异常：核销单必须和费用一样的结算主体和品牌id");
                    }
                });
            }
        }
        return cfBankAndCashs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String update(CfClearAddAndUpdateDto cfClearAddAndUpdateDto, UserVO user) {
        CfClearHeader cfClearHeader = clearHeaderMapper.selectOne(Wrappers.lambdaQuery(CfClearHeader.class)
                .eq(CfClearHeader::getClearNo, cfClearAddAndUpdateDto.getClearNo()));
        if (!cfClearHeader.getClearStatus().equals(ClearHeaderEnum.BEFORE_CLEAR.getCode())) {
            throw new FinanceTipException("数据异常: 所选核销单必须是未核销状态");
        }
        cfClearHeader.setUpdateBy(user.getUserId());
        cfClearHeader.setUpdateName(user.getRealName());
        deleteClearDetail(cfClearHeader, cfClearAddAndUpdateDto);
        //校验费用数据
        vertifyData(cfClearAddAndUpdateDto);
        invoiceDetailsCalc(cfClearAddAndUpdateDto, cfClearHeader);
        clearHeaderMapper.updateById(cfClearHeader);
        return cfClearHeader.getClearNo();
    }

    private void deleteClearDetail(CfClearHeader cfClearHeader, CfClearAddAndUpdateDto cfClearAddAndUpdateDto) {
        if (!CollectionUtils.isEmpty(cfClearAddAndUpdateDto.getChargeIds())) {
            clearDetailMapper.delete(Wrappers.lambdaQuery(CfClearDetail.class)
                    .eq(CfClearDetail::getClearId, cfClearHeader.getClearId()));
        }
    }


    @Override
    public PageInfo<ClearHeaderVo> customList(CfClearListDto cfClearListDto) {
        LambdaQueryWrapper<CfClearHeader> queryWrapper = Wrappers.lambdaQuery(CfClearHeader.class);
        createQuery(cfClearListDto, queryWrapper);

        List<SUserVORes> sUserVoResList;
        Response<List<SUserVORes>> serverUserInfos = privilegeUserServer.listUsers(SUserVOReq.builder().build());
        if (Objects.nonNull(serverUserInfos) && serverUserInfos.getCode() == ResponseCode.SUCCESS.getCode()) {
            sUserVoResList = serverUserInfos.getObj();
        } else {
            throw new FinanceTipException("权限用户系统繁忙，稍后重试");
        }
        List<VendorResModel> vendorResModels = getBalances();
        //查询品牌信息
        List<BrandDetailResModelRpc> brands = getBrandInfoList();
        PageHelper.startPage(cfClearListDto.getPageNum(), cfClearListDto.getPageSize());
        List<CfClearHeader> cfClearHeaders = clearHeaderMapper.selectList(queryWrapper);

//        List<CfChargeCommon> chargeCommons = chargeCommonService.selectListByClearNos(cfClearHeaders.stream().map(CfClearHeader::getClearNo).collect(Collectors.toList()));
        List<McnChargeCommonVO> chargeCommons = chargeCommonService.selectListByClearids(cfClearHeaders.stream().map(CfClearHeader::getClearId).collect(Collectors.toList()));
        Map<Long, String> clearNoBalanceMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(chargeCommons)) {
            clearNoBalanceMap.putAll(chargeCommons.stream().collect(Collectors.toMap(McnChargeCommonVO::getClearId, McnChargeCommonVO::getBalance, (a, b) -> b)));
        }
        //v4.9.0
        //补充实收付信息
        List<String> recordSeqNos =
                cfClearHeaders.stream().filter(s -> org.apache.commons.lang3.StringUtils.isNotBlank(s.getRecordSeqNo())).map(CfClearHeader::getRecordSeqNo).collect(Collectors.toList());
        Map<String, CfBankAndCash> recordSeqNosMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(recordSeqNos)){
            List<CfBankAndCash> cfBankAndCashList= cfBankAndCashMapper.selectList(Wrappers.lambdaQuery(CfBankAndCash.class).in(CfBankAndCash::getRecordSeqNo,recordSeqNos));
            if (!CollectionUtils.isEmpty(cfBankAndCashList)){
                recordSeqNosMap.putAll(cfBankAndCashList.stream().collect(Collectors.toMap(CfBankAndCash::getRecordSeqNo, t -> t, (a, b) -> b)));
            }
        }
        List<ClearHeaderVo> clearHeaderVos = cfClearHeaders.stream().map(cfClearHeader -> {
            ClearHeaderVo clearHeaderVo = new ClearHeaderVo();
            BeanUtils.copyProperties(cfClearHeader, clearHeaderVo);
            Optional<SUserVORes> optionalsUserVoRes =
                    sUserVoResList.stream().filter(t -> t.getUserId().equals(cfClearHeader.getClearBy())).findFirst();
            if (optionalsUserVoRes.isPresent()) {
                SUserVORes sUserVoRes = optionalsUserVoRes.get();
                clearHeaderVo.setClearName(sUserVoRes.getUsername());
            }
            Optional<BrandDetailResModelRpc> optional = brands.stream().filter(t -> t.getBrandId().equals(cfClearHeader.getBrandId().intValue())).findFirst();
            if (optional.isPresent()) {
                clearHeaderVo.setBrandName(optional.get().getBrandName());
            }
            Optional<VendorResModel> optionalVendorResModel =
                    vendorResModels.stream().filter(t -> t.getVendorCode().equals(clearHeaderVo.getBalance())).findFirst();
            if (optionalVendorResModel.isPresent()) {
                clearHeaderVo.setBalanceName(optionalVendorResModel.get().getVenAbbName());
            }
            if (ClearHeaderEnum.JOB_TYPE_MCN.getCode().equals(cfClearHeader.getJobType())) {
                String balance = clearNoBalanceMap.get(cfClearHeader.getClearId());
                clearHeaderVo.setBalanceName(balance);
                clearHeaderVo.setBalance(balance);
            }
            //v4.9.0
            //补充实收付信息
            if (net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils.isNoneBlank(cfClearHeader.getRecordSeqNo()) && recordSeqNosMap.containsKey(cfClearHeader.getRecordSeqNo())
                    && (ClearHeaderEnum.CLEAR_STATUS_THREE.getCode().equals(cfClearHeader.getClearStatus()) || ClearHeaderEnum.AFTER_CLEAR.getCode().equals(cfClearHeader.getClearStatus()))) {
                CfBankAndCash cfBankAndCash = recordSeqNosMap.get(cfClearHeader.getRecordSeqNo());
                clearHeaderVo.setPaymentBranch(cfBankAndCash.getPaymentBranch());
                clearHeaderVo.setPayCompany(cfBankAndCash.getPayCompany());
                clearHeaderVo.setAmount(cfBankAndCash.getAmount());
            }
            return clearHeaderVo;
        }).collect(Collectors.toList());
        return PageInfoUtil.toPageInfo(cfClearHeaders, clearHeaderVos);
    }

    private List<BrandDetailResModelRpc> getBrandInfoList() {
        BrandFeignRequest brandFeignRequest = new BrandFeignRequest();
        Response<List<BrandFeignVO>> response = baseInfoRemoteServer.getBrandByBrandIdList(brandFeignRequest);
        if (HttpStateEnum.OK.getCode() != response.getCode()) {
            log.error("调用baseinfo服务查询品牌信息报错,参数{}", JSONObject.toJSONString(brandFeignRequest));
            throw new BusinessException(response.getCode(), response.getMessage());
        }

        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(response.getObj())) {
            List<BrandDetailResModelRpc> brandDetailResModelRpcList = new ArrayList<>(response.getObj().size());
            for (BrandFeignVO brandFeignVO : response.getObj()) {
                BrandDetailResModelRpc brandDetailResModelRpc = new BrandDetailResModelRpc();
                brandDetailResModelRpc.setBrandId(brandFeignVO.getBrandId());
                brandDetailResModelRpc.setBrandCode(brandFeignVO.getBrandCode());
                brandDetailResModelRpc.setBrandName(brandFeignVO.getBrandName());
                brandDetailResModelRpcList.add(brandDetailResModelRpc);
            }
            return brandDetailResModelRpcList;
        }
        return new ArrayList<>();
    }

    private void createQuery(CfClearListDto cfClearListDto, LambdaQueryWrapper<CfClearHeader> queryWrapper) {
        if (Strings.isNotBlank(cfClearListDto.getClearNo())) {
            queryWrapper.like(CfClearHeader::getClearNo, cfClearListDto.getClearNo());
        }
        if (Strings.isNotBlank(cfClearListDto.getClearType())) {
            queryWrapper.eq(CfClearHeader::getClearType, cfClearListDto.getClearType());
        }
        if (Strings.isNotBlank(cfClearListDto.getClearMethod())) {
            queryWrapper.eq(CfClearHeader::getClearMethod, cfClearListDto.getClearMethod());
        }
        if (Strings.isNotBlank(cfClearListDto.getBalance())) {
            queryWrapper.eq(CfClearHeader::getBalance, cfClearListDto.getBalance());
        }
        if (Objects.nonNull(cfClearListDto.getBrandId())) {
            queryWrapper.eq(CfClearHeader::getBrandId, cfClearListDto.getBrandId());
        }
        if (Objects.nonNull(cfClearListDto.getClearStatus())) {
            queryWrapper.eq(CfClearHeader::getClearStatus, cfClearListDto.getClearStatus());
        }
        if (Objects.nonNull(cfClearListDto.getClearBy())) {
            queryWrapper.eq(CfClearHeader::getClearBy, cfClearListDto.getClearBy());
        }
        if (Objects.nonNull(cfClearListDto.getStartTime()) && Objects.nonNull(cfClearListDto.getEndTime())) {
            queryWrapper.between(CfClearHeader::getCreateDate, cfClearListDto.getStartTime(), cfClearListDto.getEndTime());
        }
        //过滤公司权限
        if (Objects.nonNull(cfClearListDto.getCompanyIds())) {
            queryWrapper.in(CfClearHeader::getCompanyId, cfClearListDto.getCompanyIds());
        }
        //过滤用户权限
        if (Objects.nonNull(cfClearListDto.getUserIds())) {
            queryWrapper.in(CfClearHeader::getCreateBy, cfClearListDto.getUserIds());
        }
        //过滤品牌
        if (Objects.nonNull(cfClearListDto.getBrandIds())) {
            queryWrapper.in(CfClearHeader::getBrandId, cfClearListDto.getBrandIds());
        }
        queryWrapper.ne(CfClearHeader::getClearStatus, ClearHeaderEnum.IS_DELETE.getCode());
        queryWrapper.eq(Objects.nonNull(cfClearListDto.getJobType()), CfClearHeader::getJobType, cfClearListDto.getJobType());

        queryWrapper.orderByDesc(CfClearHeader::getCreateDate);
    }

    @Override
    public ClearHeaderDetailVo detail(String clearNo) {
        CfClearHeader cfClearHeader = clearHeaderMapper.selectOne(
                Wrappers.lambdaQuery(CfClearHeader.class).eq(CfClearHeader::getClearNo, clearNo));
        Long clearId = cfClearHeader.getClearId();
        ClearHeaderDetailVo clearHeaderDetailVo = new ClearHeaderDetailVo();
        BeanUtils.copyProperties(cfClearHeader, clearHeaderDetailVo.getClearHeaderDetailBase());
        Response<BaseGetBrandInfoList> response = baseInfoRemoteServer.getBrandInfo(cfClearHeader.getBrandId().intValue());
        BaseGetBrandInfoList baseGetBrandInfoList = RpcUtil.getObjNoException(response);
        clearHeaderDetailVo.getClearHeaderDetailBase().setBrandName(
                Objects.nonNull(baseGetBrandInfoList) ? baseGetBrandInfoList.getBrandName() : "");
        clearHeaderDetailVo.getClearHeaderDetailBase().setBalanceCode(cfClearHeader.getBalance());

        delegateClearService.clearHeaderDetailBase(clearHeaderDetailVo, cfClearHeader);

        Response<SUserVORes> serverUserInfo = privilegeUserServer.getUserInfo(SUserVOReq.builder().userId(cfClearHeader.getCreateBy()).build());
        SUserVORes userVoRes = RpcUtil.getObjNoException(serverUserInfo);
        if (Objects.nonNull(userVoRes)) {
            clearHeaderDetailVo.getClearHeaderDetailBase().setClearName(userVoRes.getUsername());
        }

        List<CfBankAndCash> cfBankAndCashes =
                cfBankAndCashMapper.selectList(Wrappers.lambdaQuery(CfBankAndCash.class).like(CfBankAndCash::getClearNo, clearNo));
        List<CfClearBankDetail> cfClearBankDetails =
                clearBankDetailMapper.selectList(Wrappers.lambdaQuery(CfClearBankDetail.class).eq(CfClearBankDetail::getClearId, cfClearHeader.getClearId()));
        Map<Long, CfClearBankDetail> bankAndCashIdMap = cfClearBankDetails.stream().collect(Collectors.toMap(CfClearBankDetail::getBankAndCashId, Function.identity()));
        List<ClearHeaderDetailBankAndCashVo> clearHeaderDetailBankAndCashVos = cfBankAndCashes.stream().map(cfBankAndCash -> {
            ClearHeaderDetailBankAndCashVo clearHeaderDetailBankAndCashVo = new ClearHeaderDetailBankAndCashVo();
            BeanUtils.copyProperties(cfBankAndCash, clearHeaderDetailBankAndCashVo);
            CfClearBankDetail cfClearBankDetail = bankAndCashIdMap.get(cfBankAndCash.getBankAndCashId());
            if (Objects.nonNull(cfClearBankDetail)) {
                clearHeaderDetailBankAndCashVo.setLastBalance(cfClearBankDetail.getLastBalance());
            }
            //MCN 付  则取本身金额
            if (ChargeCommonEnum.CHARGE_SOURCE_TYPE_MCN.getCode().equals(cfClearHeader.getJobType())&&"AP".equals(cfClearHeader.getClearType())){
                clearHeaderDetailBankAndCashVo.setLastBalance(clearHeaderDetailBankAndCashVo.getAmount());
            }
            return clearHeaderDetailBankAndCashVo;
        }).collect(Collectors.toList());
        clearHeaderDetailVo.setClearHeaderDetailBankAndCashVos(clearHeaderDetailBankAndCashVos);

        List<ClearHeaderDetailInvoiceDetailVo> clearHeaderDetailInvoiceDetailVos = delegateClearService.detailInvoiceDetailVos(clearHeaderDetailVo, cfClearHeader);
        clearHeaderDetailVo.setDetailInvoiceDetailVos(clearHeaderDetailInvoiceDetailVos);
        //日志
        clearHeaderDetailVo.setCfBsOperationLogList(OperateUtil.selectOperationLogsByBs(OperationBsTypeEnum.OPERATION_BS_MCN_Clear, clearNo, clearId));
        BeanUtils.copyProperties(cfClearHeader, clearHeaderDetailVo.getClearHeaderDetailTotalVo());
        delegateClearService.clearHeaderDetailTotalVo(clearHeaderDetailVo, cfClearHeader);
        pageInfoUtil.fillProcessFlowDesc(cfClearHeader.getClearId(), ClearStateService.APPROVAL_PROCESS_ID, clearHeaderDetailVo);
        return clearHeaderDetailVo;
    }

    private List<VendorResModel> getBalances() {
        Response<List<VendorResModel>> vendorByCodeRes = vendorCenterServer.getAllVendorList(new HashMap<>(16));
        List<VendorResModel> vendorResModels = vendorByCodeRes.getObj();
        if (CollectionUtils.isEmpty(vendorResModels)) {
            throw new FinanceTipException("vendorCenterServer服务繁忙，稍后重试");
        }
        return vendorResModels;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer delete(List<String> clearNos) {
        List<CfClearHeader> cfClearHeaders =
                clearHeaderMapper.selectList(
                        Wrappers.lambdaQuery(CfClearHeader.class).in(CfClearHeader::getClearNo, clearNos));
        cfClearHeaders.stream().forEach(cfClearHeader -> {
            if (!cfClearHeader.getClearStatus().equals(ClearHeaderEnum.BEFORE_CLEAR.getCode())) {
                throw new FinanceTipException("数据异常：该状态不是未核销");
            }
            cfClearHeader.setClearStatus(ClearHeaderEnum.IS_DELETE.getCode());
            clearHeaderMapper.updateById(cfClearHeader);
        });
        return 1;
    }

    @Override
    public List<ClearUserVo> clearUsers() {
        List<CfClearHeader> cfClearHeaders =
                clearHeaderMapper.selectList(Wrappers.lambdaQuery(CfClearHeader.class).groupBy(CfClearHeader::getCreateBy));
        List<ClearUserVo> clearUserVos = cfClearHeaders.stream().map(cfClearHeader -> {
            ClearUserVo clearUserVo = new ClearUserVo();
            clearUserVo.setClearBy(cfClearHeader.getCreateBy());
            clearUserVo.setClearName(cfClearHeader.getCreateName());
            return clearUserVo;
        }).collect(Collectors.toList());
        return clearUserVos;
    }

    @Override
    public void export(CfClearListDto cfClearListDto, UserVO userVO, HttpServletResponse response) {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalCallback(CfClearHeader cfClearHeader, ApprovalFlowDTO approvalFlowDTO, Boolean status) {
        cfClearHeader = clearHeaderMapper.selectById(cfClearHeader.getClearId());
        approvalFlowDTO.setSrcCode(cfClearHeader.getClearNo());
        approvalFlowDTO.setProcessId(ApprovalEnum.CFCLEAR_APPROVAL.getProcessId());
        if (Objects.nonNull(status)) {
            //审批通过（流程结束）或审批被拒绝发给创建人
            approvalFlowService.sendNotify(approvalFlowDTO, cfClearHeader.getClearId(),
                    cfClearHeader.getClearNo()
                    , ApprovalEnum.CFCLEAR_APPROVAL, status,
                    cfClearHeader.getCreateBy(),
                    cfClearHeader.getCreateName());
        } else {
            for (int i = 0; i < approvalFlowDTO.getTargetUserId().size(); i++) {
                approvalFlowService.sendNotify(approvalFlowDTO, cfClearHeader.getClearId(),
                        cfClearHeader.getClearNo()
                        , ApprovalEnum.CFCLEAR_APPROVAL, status,
                        Long.parseLong(approvalFlowDTO.getTargetUserId().get(i)),
                        approvalFlowDTO.getTargetUserName().get(i));
            }
        }
    }


    /**
     * 根据账单、开票流水号获取核销数据
     *
     * @return
     */
    public CfClearHeader getCfClearHeaderByInvoiceNo(String invoiceNo) {
        return clearHeaderMapper.selectOne(Wrappers.lambdaQuery(CfClearHeader.class).ne(CfClearHeader::getClearStatus, 0)
                .eq(CfClearHeader::getInvoiceNo, invoiceNo));
    }
}
