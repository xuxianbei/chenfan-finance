package com.chenfan.finance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.common.config.Constant;
import com.chenfan.common.exception.BusinessException;
import com.chenfan.common.exception.SystemState;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.commons.exception.FinanceTipException;
import com.chenfan.finance.config.BillNoConstantClassField;
import com.chenfan.finance.constant.CfFinanceConstant;
import com.chenfan.finance.dao.*;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.model.*;
import com.chenfan.finance.model.bo.CfInvoiceHeaderAddBO;
import com.chenfan.finance.model.bo.CfInvoiceHeaderBO;
import com.chenfan.finance.model.bo.InvoiceSwitchBanBO;
import com.chenfan.finance.model.dto.*;
import com.chenfan.finance.model.vo.*;
import com.chenfan.finance.mq.PurBillVouchProduceService;

import com.chenfan.finance.server.BaseInfoRemoteServer;
import com.chenfan.finance.server.BaseRemoteServer;
import com.chenfan.finance.server.VendorCenterServer;
import com.chenfan.finance.server.dto.BaseVendorSettlemet;
import com.chenfan.finance.server.remote.model.InventoryCategoryNew;
import com.chenfan.finance.server.remote.model.InventoryGetInfoModel;
import com.chenfan.finance.server.remote.request.BrandFeignRequest;
import com.chenfan.finance.server.remote.vo.BrandFeignVO;
import com.chenfan.finance.service.*;
import com.chenfan.finance.utils.BeanUtilCopy;
import com.chenfan.finance.utils.TwitterIdentifier;
import com.chenfan.finance.utils.FileUtil;
import com.chenfan.finance.utils.OperateUtil;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.chenfan.vendor.response.VendorResModel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * @author xiongbin
 * @date 2020-08-20
 */
@Slf4j
@Service
public class CfInvoiceHeaderServiceImpl implements CfInvoiceHeaderService {
    @Resource
    private RuleBillingHeaderService ruleBillingHeaderService;
    @Resource
    private CfClearDetailMapper clearDetailMapper;

    @Autowired
    private CfInvoiceHeaderMapper invoiceHeaderMapper;
    @Autowired
    private CfInvoiceDetailMapper invoiceDetailMapper;
    @Autowired
    private CfChargeMapper chargeMapper;
    @Autowired
    private CfChargeInMapper chargeInMapper;
    @Autowired
    private CfInvoiceSettlementService cfInvoiceSettlementService;
    @Autowired
    private VendorCenterServer vendorCenterServer;
    @Autowired
    private BaseRemoteServer baseRemoteServer;
    @Autowired
    private CfChargeService cfChargeService;
    @Resource
    private CfClearHeaderMapper cfClearHeaderMapper;
    @Autowired
    private CfChargeMapper cfChargeMapper;
    @Resource
    private CfRdRecordMapper cfRdRecordMapper;
    @Resource
    private CfRdRecordDetailMapper cfRdRecordDetailMapper;
    @Resource
    private CfPoDetailMapper cfPoDetailMapper;
    @Resource
    private CfPoHeaderMapper cfPoHeaderMapper;

    @Resource
    private PurBillVouchProduceService purBillVouchProduceService;
    @Autowired
    private PurchaseInvoiceMapper purchaseInvoiceMapper;
    @Autowired
    private PurchaseInvoiceDetailMapper purchaseInvoiceDetailMapper;
    @Autowired
    private CfInvoiceSettlementMapper invoiceSettlementMapper;
    @Autowired
    private CfBankAndCashService actualPaymentService;
    @Autowired
    private CfInvoiceHeaderService invoiceHeaderService;
    @Autowired
    private BaseInfoRemoteServer baseInfoRemoteServer;
    @Autowired
    private AdvancepayApplicationMapper advancepayApplicationMapper;
    @Resource
    private CfInvoiceSettlementMapper cfInvoiceSettlementMapper;
    @Autowired
    private CfChargeSplitMapper chargeSplitMapper;
    @Autowired
    private CfChargeInSplitMapper chargeInSplitMapper;
    @Autowired
    private PageInfoUtil pageInfoUtil;
    @Autowired
    private ApprovalFlowService approvalFlowService;
    @Autowired
    private CfQcRecordAsnDetailMapper cfQcRecordAsnDetailMapper;

    /**
     * 账单保存
     *
     * @param userVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<Object> save(CfInvoiceHeaderAddBO invoiceHeaderAddBO, UserVO userVO) {
        CfInvoiceHeader invoiceHeader = invoiceHeaderAddBO.getInvoiceHeader();
        invoiceHeader.setInvoiceNo(pageInfoUtil.generateBusinessNum(BillNoConstantClassField.INVOICE));
        OperateUtil.onSave(invoiceHeader, userVO);
        invoiceHeaderMapper.insert(invoiceHeader);
        List<CfInvoiceDetail> invoiceDetailList = invoiceHeaderAddBO.getInvoiceDetailList();
        for (CfInvoiceDetail invoiceDetail : invoiceDetailList) {
            invoiceDetail.setInvoiceId(invoiceHeader.getInvoiceId());
            invoiceDetailMapper.insert(invoiceDetail);
        }
        List<Long> collect = invoiceDetailList.stream().map(CfInvoiceDetail::getChargeId).collect(Collectors.toList());
        chargeMapper.updateInvoiceInfo(invoiceHeader, collect);
        updatePrePayByInvoiceNo(invoiceHeader, true,false);
        return new Response<>(ResponseCode.SUCCESS, invoiceHeader.getInvoiceId());
    }

    /**
     * // 创建 预付款 要被账单引用
     * // create 创建/删除，解除账单下关联的预付款
     *
     * @param invoiceHeader
     */
    private void updatePrePayByInvoiceNo(CfInvoiceHeader invoiceHeader, boolean create,boolean isAli) {
        String invoiceNo = invoiceHeader.getInvoiceNo();
        List<CfCharge> allChargeSourceCode = getAllChargeSourceCode(invoiceNo);
        List<Long> details = allChargeSourceCode.stream().filter((a) -> a.getChargeSourceDetailId() != null&&Objects.equals(a.getChargeType(),CfFinanceConstant.CHARGE_SOURCE_FOR_PO )).map(CfCharge::getChargeSourceDetailId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(details)){
            return;
        }
        //Assert.isTrue(details.size() > 0, "入库明细数据不存在");
        List<CfRdRecordDetail> allRdRecordDetail = getAllRdRecordDetailByDetailsId(details);
        Set<Long> poIds = allRdRecordDetail.stream().map(CfRdRecordDetail::getPoId).filter(Objects::nonNull).collect(Collectors.toSet());

        if (create) {
            // 校验预付款
            checkAdPay(poIds);
            List<AdvancepayApplication> applicationList = getAdvancepayListByPoIds(new ArrayList<>(poIds), AdvencePayEnum.PAID.getCode());
            for (AdvancepayApplication s : applicationList) {
                AdvancepayApplication up = new AdvancepayApplication();
                up.setAdvancePayId(s.getAdvancePayId());
                up.setInvoiceNo(invoiceNo);
                advancepayApplicationMapper.updateById(up);
            }
            BigDecimal adv = applicationList.stream().map(AdvancepayApplication::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
            CfInvoiceHeader invoup = new CfInvoiceHeader();
            invoup.setInvoiceId(invoiceHeader.getInvoiceId());
            invoup.setAdvancePayMoney(adv);
            if(isAli){
                if(CollectionUtils.isNotEmpty(applicationList)){
                    AdvancepayApplication advancepayApplication = applicationList.stream().findFirst().get();
                    invoup.setBank(StringUtils.isBlank(advancepayApplication.getBank())?"支付宝":advancepayApplication.getBank());
                    invoup.setBankAccounts(StringUtils.isBlank(advancepayApplication.getBankAccount())?"0123456789":advancepayApplication.getBankAccount());
                    invoup.setInvoiceTitle(StringUtils.isBlank(advancepayApplication.getAccName())?"支付宝":advancepayApplication.getAccName());
                    invoup.setInvoiceTitleName(StringUtils.isBlank(advancepayApplication.getReceiptDepartment())?"支付宝":advancepayApplication.getReceiptDepartment());
                }else {
                    Optional<BaseVendorSettlemet> first = vendorCenterServer.getVendorSettlementInfo(null, invoiceHeader.getBalance()).getObj().stream().findFirst();
                    if(first.isPresent()){
                        BaseVendorSettlemet baseVendorSettlemet = first.get();
                        invoup.setBank(StringUtils.isBlank(baseVendorSettlemet.getCvenBank())?"支付宝":baseVendorSettlemet.getCvenBank());
                        invoup.setBankAccounts(StringUtils.isBlank(baseVendorSettlemet.getCvenBank())?"0123456789":baseVendorSettlemet.getCvenAccount());
                        invoup.setInvoiceTitle(StringUtils.isBlank(baseVendorSettlemet.getAccname())?"支付宝":baseVendorSettlemet.getAccname());
                        invoup.setInvoiceTitleName(StringUtils.isBlank(baseVendorSettlemet.getVendorLetterhead())?"支付宝":baseVendorSettlemet.getVendorLetterhead());
                    }else {
                        invoup.setBank("支付宝");
                        invoup.setBankAccounts("0123456789");
                        invoup.setInvoiceTitle("支付宝");
                        invoup.setInvoiceTitleName("支付宝");
                    }
                }
                invoiceHeader.setBank(invoup.getBank());
                invoiceHeader.setBankAccounts(invoup.getBankAccounts());
                invoiceHeader.setInvoiceTitle(invoup.getInvoiceTitle());
                invoiceHeader.setInvoiceTitleName(invoup.getInvoiceTitleName());
            }
            invoiceHeaderMapper.updateById(invoup);
            return;
        }
        List<AdvancepayApplication> applicationList = getAdvancepayListByPoIds(new ArrayList<>(poIds), invoiceNo);
        List<Integer> adIds = applicationList.stream().map(AdvancepayApplication::getAdvancePayId).collect(Collectors.toList());
        if (adIds.size() < 1) {
            log.info("{} 账单下没有预付款" + invoiceNo);
            return;
        }

        advancepayApplicationMapper.update(
                null,
                Wrappers.<AdvancepayApplication>lambdaUpdate()
                        .set(AdvancepayApplication::getInvoiceNo, null)
                        .in(AdvancepayApplication::getAdvancePayId, adIds)
        );
    }

    private void checkAdPay(Set<Long> poIds) {
        List<AdvancepayApplication> checks = getAdvancepayListByPoIds(new ArrayList<>(poIds),   AdvencePayEnum.NOT_CONFIRM.getCode(),
                AdvencePayEnum.CONFIRM.getCode(),
                AdvencePayEnum.AUDIT.getCode(),
                AdvencePayEnum.SUBMIT.getCode(),
                AdvencePayEnum.FINANCE_AUDIT.getCode(),
                AdvencePayEnum.COMPLETE.getCode(),
                AdvencePayEnum.REJECT.getCode());
        if (CollectionUtils.isNotEmpty(checks)) {
            StringBuilder bd = new StringBuilder("存在未打款的预付款申请单：");
            for (AdvancepayApplication check : checks) {
                bd.append(check.getAdvancePayCode()).append(",");
            }
            throw new FinanceTipException(bd.substring(0, bd.length() - 1));
        }
    }

    @Override
    public Response<CfInvoiceHeaderBO> goAdd(List<Long> chargeIds, String no) {
        if (CollectionUtils.isEmpty(chargeIds)) {
            return new Response<>(Constant.ERROR_PARAM_CODE, "参数异常，费用id不能为空");
        }
        List<CfChargeVO> list = chargeMapper.queryChargeListByChargeIds(chargeIds, no);
        if (CollectionUtils.isEmpty(list)) {
            return new Response<>(Constant.ERROR_PARAM_CODE, "未查询到可生成账单的费用数据");
        }
        //检查是否是统一采购类型的费用（成衣 辅料）
        List<Long> chargeIdsOfRd = list.parallelStream().filter(x -> Objects.equals(x.getChargeType(), "1") || Objects.equals(x.getChargeType(), "7")).map(CfChargeVO::getChargeId).collect(Collectors.toList());
        List<Integer> integers = chargeMapper.checkRdTypeByChargeId(chargeIdsOfRd);
        if(integers!=null&&integers.size()>1){
            return new Response<>(Constant.ERROR_PARAM_CODE, "成衣和辅料不能生成同一笔账单");
        }

        List<CfCharge> charges = getAllChargeByChargeIds(chargeIds);
       /* List<CfCharge> collect = charges.parallelStream().filter(x -> Objects.equals(x.getChargeType(), "1") || Objects.equals(x.getChargeType(), "7")).collect(Collectors.toList());
        if(collect!=null&&collect.stream().filter(x->!Objects.equals(x.getTaxRate(),collect.get(0).getTaxRate())).findFirst()!=null&&){
            return new Response<>(Constant.ERROR_PARAM_CODE, "同一笔账单只能支持同一种采购税率的");
        }*/

        CfInvoiceHeaderBO invoiceHeaderBO = new CfInvoiceHeaderBO();
        CfInvoiceHeader invoiceHeader = new CfInvoiceHeader();
        invoiceHeader.setInvoiceStatus(1);
        //获取费用列表的结算主体和来源类型
        List<String> vendors = list.stream().map(CfCharge::getBalance).collect(Collectors.toList());
        Map<String, String> vendorList = cfChargeService.getVendorList(vendors);
        Map<String, String> chargetyps = cfChargeService.getDicts("charge_type", list);
        Map<String, String> sourceTypes = cfChargeService.getDicts("Charge_Source_Type", list);

        for (CfChargeVO cfChargeVO : list) {
            Set<String> chargeIdsNew = new HashSet<>();
            CfChargeServiceImpl.getIds(cfChargeVO.getChargeIds(), chargeIdsNew);
            List<Long> longs = CfChargeServiceImpl.transferIdsToLong(chargeIdsNew);
            List<CfCharge> match = charges.stream().filter(a -> longs.stream().anyMatch(id -> id.equals(a.getChargeId()))).collect(Collectors.toList());

            List<CfChargeSkuVO> cfChargeSkuVoS = BeanUtilCopy.copyListProperties(match, CfChargeSkuVO::new);

            for (CfChargeSkuVO cfChargeSkuVO : cfChargeSkuVoS) {
                cfChargeSkuVO.setBalanceName(vendorList.get(cfChargeSkuVO.getBalance()));
                cfChargeSkuVO.setChargeSourceName(sourceTypes.get(cfChargeSkuVO.getChargeSource() + ""));
                cfChargeSkuVO.setChargeType(chargetyps.get(cfChargeSkuVO.getChargeType()));
            }
            cfChargeVO.setChargeQty(cfChargeSkuVoS.stream().map(CfChargeSkuVO::getChargeQty).reduce(0, Integer::sum));
            cfChargeVO.setAmountPp(cfChargeSkuVoS.stream().map(CfChargeSkuVO::getAmountPp).reduce(BigDecimal.ZERO, BigDecimal::add));
            BigDecimal qty = new BigDecimal(cfChargeVO.getChargeQty());
            cfChargeVO.setPricePp(cfChargeVO.getAmountPp());
            cfChargeVO.setBalanceName(vendorList.get(cfChargeVO.getBalance()));
            cfChargeVO.setChargeSourceName(sourceTypes.get(cfChargeVO.getChargeSource() + ""));
            cfChargeVO.setChargeType(chargetyps.get(cfChargeVO.getChargeType()));
            cfChargeVO.setCfChargeSkuVOList(cfChargeSkuVoS);
        }

        invoiceHeaderBO.setChargeList(list);
        invoiceHeaderBO.setChargeListSize(list.size());

        //收入总金额
        BigDecimal totalAr = list.stream().filter(a -> ChargeEnum.ARAP_TYPE_AR.getCode().equals(a.getArapType())).map(CfCharge::getAmountPp).reduce(BigDecimal.ZERO, BigDecimal::add);
        //支出总金额
        BigDecimal totalAp = list.stream().filter(a -> ChargeEnum.ARAP_TYPE_AP.getCode().equals(a.getArapType())).map(CfCharge::getAmountPp).reduce(BigDecimal.ZERO, BigDecimal::add);
        CfChargeVO charge = list.get(0);
        invoiceHeader.setInvoicelDebit(totalAr);
        invoiceHeader.setInvoicelCredit(totalAp);
        invoiceHeader.setJobType(charge.getChargeSource());
        if (totalAr.compareTo(totalAp) < 0) {
            invoiceHeader.setInvoiceType(ChargeEnum.ARAP_TYPE_AP.getCode());
        } else {
            invoiceHeader.setInvoiceType(ChargeEnum.ARAP_TYPE_AR.getCode());
        }
        invoiceHeader.setBrandId(charge.getBrandId());
        invoiceHeader.setBalance(charge.getBalance());
        invoiceHeaderBO.setInvoiceHeader(invoiceHeader);
        List<ChargeInVO> chargeInList = chargeInMapper.queryChargeInByChargeIds(chargeIds);
        // 合并延期明细
        mergePostponeDetail(chargeInList);
        invoiceHeaderBO.setChargeInList(chargeInList);
        return new Response<>(ResponseCode.SUCCESS, invoiceHeaderBO);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<Object> addInvoices(InvoiceHeaderAddDTO invoiceHeaderAddDTO,  UserVO user) {
        List<Long> chargeIds = invoiceHeaderAddDTO.getChargeIds();
        //第一步验证当前数据是否可以生成费用
        if (CollectionUtils.isEmpty(chargeIds)) {
            return new Response<>(Constant.ERROR_PARAM_CODE, "参数异常，费用id不能为空");
        }
        if(Objects.isNull(invoiceHeaderAddDTO.getDateStart())||Objects.isNull(invoiceHeaderAddDTO.getDateEnd())){
            return new Response<>(Constant.ERROR_PARAM_CODE, "参数异常，结算起止时间不能为空");
        }
        List<CfChargeVO> list = chargeMapper.queryChargeListByChargeIds(chargeIds, null);
        if (CollectionUtils.isEmpty(list)) {
            return new Response<>(Constant.ERROR_PARAM_CODE, "当前费用不存在，或者已被其他账单引用");
        }
        List<CfCharge> chargesOfOld = getAllChargeByChargeIds(chargeIds);
        //TODO 检查当前数据
        Map<String, List<CfCharge>> chargeTypeMap = chargesOfOld.stream().collect(Collectors.groupingBy(CfCharge::getChargeType));
        //蓝字入库（绑定的费用）
        List<CfCharge> rdChargeList = chargeTypeMap.get(CfFinanceConstant.CHARGE_SOURCE_FOR_PO)==null?new ArrayList<>():chargeTypeMap.get(CfFinanceConstant.CHARGE_SOURCE_FOR_PO);
        //延期(不允许单独生成账单)
        List<CfCharge> dyChargeList = chargeTypeMap.get(String.valueOf(NumberEnum.EIGHT.getCode()))==null?new ArrayList<>():chargeTypeMap.get(String.valueOf(NumberEnum.EIGHT.getCode()));
        for (CfCharge cfCharge: dyChargeList) {
            Optional<CfCharge> first = rdChargeList.stream().filter(x -> Objects.equals(cfCharge.getChargeSourceDetailId(), x.getChargeSourceDetailId())).findFirst();
            Assert.isTrue(first.isPresent(),StringUtils.format("费用：%s 延期扣费单据不允许单独生成账单",cfCharge.getChargeCode()));
        }
        //质检（不允许单独生成账单）
        List<CfCharge> qaChargeList = chargeTypeMap.get(String.valueOf(NumberEnum.NINE.getCode()))==null?new ArrayList<>():chargeTypeMap.get(String.valueOf(NumberEnum.NINE.getCode()));
        for (CfCharge cfCharge: qaChargeList) {
            CfQcRecordAsnDetail cfQcRecordAsnDetail = cfQcRecordAsnDetailMapper.selectList(Wrappers.<CfQcRecordAsnDetail>lambdaQuery().eq(CfQcRecordAsnDetail::getQcChargingId, cfCharge.getChargeSourceDetailId())).get(NumberEnum.ZERO.getCode());
            Optional<CfCharge> first = rdChargeList.stream().filter(x -> Objects.equals(cfQcRecordAsnDetail.getRdRecordDetailId(), x.getChargeSourceDetailId())).findFirst();
            Assert.isTrue(first.isPresent(),StringUtils.format("费用：%s 质检扣费单据不允许单独生成账单",cfCharge.getChargeCode()));
        }

        for (CfCharge cfCharge: rdChargeList) {
            Long chargeSourceDetailId = cfCharge.getChargeSourceDetailId();
            List<CfCharge> deOfChargeList = cfChargeMapper.selectList(Wrappers.<CfCharge>lambdaQuery().eq(CfCharge::getChargeSourceDetailId, chargeSourceDetailId).eq(CfCharge::getChargeType, String.valueOf(NumberEnum.EIGHT.getCode())));
            if(CollectionUtils.isNotEmpty(deOfChargeList)){
                chargesOfOld.addAll(deOfChargeList);
            }
            Set<Long> qcIdCollect = cfQcRecordAsnDetailMapper.selectList(Wrappers.<CfQcRecordAsnDetail>lambdaQuery().eq(CfQcRecordAsnDetail::getRdRecordDetailId, chargeSourceDetailId)).stream().map(x -> x.getQcChargingId()).collect(Collectors.toSet());
            List<CfCharge> qcOfChargeList = cfChargeMapper.selectList(Wrappers.<CfCharge>lambdaQuery().in(CfCharge::getChargeSourceDetailId, qcIdCollect).eq(CfCharge::getChargeType, String.valueOf(NumberEnum.NINE.getCode())));
            if(CollectionUtils.isNotEmpty(qcOfChargeList)){
                chargesOfOld.addAll(qaChargeList);
            }
        }
        List<CfCharge> uniqueCharges = chargesOfOld.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(CfCharge::getChargeId))), ArrayList::new)
        );


        //检查是否已经生成费用，或者已经被删除
        List<CfCharge> unPassCheck = uniqueCharges.stream().filter(x->!Objects.isNull(x.getInvoiceNo())||Objects.equals(x.getCheckStatus(),NumberEnum.ZERO.getCode())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(unPassCheck)){
            return new Response<>(Constant.ERROR_PARAM_CODE, StringUtils.format("费用：%s 已经生成费用或者已被删除",unPassCheck.stream().map(x->x.getChargeCode())));
        }
        //检查是否一致（同一）
        Integer salesType = uniqueCharges.get(NumberEnum.ZERO.getCode()).getSalesType();
        Optional<CfCharge> checkSalesType = uniqueCharges.stream().filter(x -> !Objects.equals(x.getSalesType(), salesType)).findFirst();
        if(checkSalesType.isPresent()){
            return new Response<>(Constant.ERROR_PARAM_CODE, "非同一销售类型不能生成同一笔账单");
        }
        //检查采购税率是否一致
        Optional<CfCharge> checkTaxRateP = uniqueCharges.parallelStream().filter(x -> !Objects.equals(x.getTaxRate(), uniqueCharges.get(0).getTaxRate())).findFirst();
        if(checkTaxRateP.isPresent()){
            return new Response<>(Constant.ERROR_PARAM_CODE, "非统一采购税率不能生成同一笔账单");
        }
        CfChargeVO charge = list.get(0);
        //检查是否是统一采购类型的费用（成衣 辅料）
        List<Integer> rdRecordTypeS = chargeMapper.checkRdTypeByChargeId(chargeIds);
        if(rdRecordTypeS==null||rdRecordTypeS.size()>1){
            return new Response<>(Constant.ERROR_PARAM_CODE, "非同一采购类型（成衣、辅料）不能生成同一笔账单");
        }
        Optional<CfChargeVO> brandCheck = list.parallelStream().filter(x -> !Objects.equals(x.getBrandId(), charge.getBrandId())).findFirst();
        if(brandCheck.isPresent()){
            return new Response<>(Constant.ERROR_PARAM_CODE, "非同一品牌费用不能生成同一笔账单");
        }
        Optional<CfChargeVO> balanceCheck = list.parallelStream().filter(x -> !Objects.equals(x.getBalance(), charge.getBalance())).findFirst();
        if(balanceCheck.isPresent()){
            return new Response<>(Constant.ERROR_PARAM_CODE, "非同一结算主体的费用不能生成同一笔账单");
        }
        List<Long> chargeIdsOfChargeInIsNotHaving = cfChargeMapper.checkChargeAndChargeInIsHaving(chargeIds);
        if(CollectionUtils.isNotEmpty(chargeIdsOfChargeInIsNotHaving)){
            log.error("费用未生成对应的cf_charge_in",chargeIdsOfChargeInIsNotHaving);
            return Response.error(Constant.ERROR_BUSINESS_CODE,"当前选中的费用，部分数据异常，请联系开发检查！！！");
        }
        /**
         * 处理账单主数据
         */
        //收入总金额
        BigDecimal totalAr = uniqueCharges.stream().filter(a -> ChargeEnum.ARAP_TYPE_AR.getCode().equals(a.getArapType())).map(CfCharge::getAmountPp).reduce(BigDecimal.ZERO, BigDecimal::add);
        //支出总金额
        BigDecimal totalAp = uniqueCharges.stream().filter(a -> ChargeEnum.ARAP_TYPE_AP.getCode().equals(a.getArapType())).map(CfCharge::getAmountPp).reduce(BigDecimal.ZERO, BigDecimal::add);
        //检查ar和ap 是否一致
        String invoiceType=null;
        List<ChargeInVO> chargeInVOS = chargeInMapper.checkQtyByChargeIds(chargeIds);
        if (totalAr.compareTo(totalAp) < 0) {
            invoiceType=ChargeEnum.ARAP_TYPE_AP.getCode();
            List<ChargeInVO> collect = chargeInVOS.stream().filter(x -> x.getSettlementQty() < 0).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(collect)){
                return  Response.error(Constant.ERROR_BUSINESS_CODE,"当前所选费用不符合红蓝字结算规则（蓝字中存在红字），请筛选费用后重新生成");
            }
        } else {
            invoiceType=ChargeEnum.ARAP_TYPE_AR.getCode();
            List<ChargeInVO> collect = chargeInVOS.stream().filter(x -> x.getSettlementQty() > 0).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(collect)){
                return  Response.error(Constant.ERROR_BUSINESS_CODE,"当前所选费用不符合红蓝字结算规则（红字中存在蓝字），请筛选费用后重新生成");
            }
        }
        CfInvoiceHeader cfInvoiceHeader = new CfInvoiceHeader();
        BeanUtils.copyProperties(invoiceHeaderAddDTO,cfInvoiceHeader);
        Map<String, String> stringStringMap = cfChargeService.getVendorList(Arrays.asList(charge.getBalance()));
        cfInvoiceHeader.setInvoiceNo(pageInfoUtil.generateBusinessNum(BillNoConstantClassField.INVOICE));
        OperateUtil.onSave(cfInvoiceHeader, user);
        cfInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        cfInvoiceHeader.setInvoiceStatus(1);
        cfInvoiceHeader.setInvoicelDebit(totalAr);
        cfInvoiceHeader.setInvoicelCredit(totalAp);
        cfInvoiceHeader.setJobType(charge.getChargeSource());
        cfInvoiceHeader.setInvoiceType(invoiceType);
        cfInvoiceHeader.setBrandId(charge.getBrandId());
        cfInvoiceHeader.setBalance(charge.getBalance());
        cfInvoiceHeader.setBalanceName(stringStringMap.get(charge.getBalance()));
        cfInvoiceHeader.setMaterialType(rdRecordTypeS.get(NumberEnum.ZERO.getCode()));
        cfInvoiceHeader.setSalesType(salesType);
        Response<VendorResModel> vendorByCode = vendorCenterServer.getVendorByCode(null, charge.getBalance());
        VendorResModel obj = vendorByCode.getObj();
        Boolean isAil=false;
        if(Objects.equals(obj.getVentype(),"阿里巴巴")){
            isAil=true;
        }else {
            Optional<BaseVendorSettlemet> first = vendorCenterServer.getVendorSettlementInfo(null, charge.getBalance()).getObj().stream().findFirst();
            if(first.isPresent()){
                BaseVendorSettlemet baseVendorSettlemet = first.get();
                if(StringUtils.isBlank(baseVendorSettlemet.getCvenBank())||StringUtils.isBlank(baseVendorSettlemet.getCvenBank())
                        ||StringUtils.isBlank(baseVendorSettlemet.getAccname())||StringUtils.isBlank(baseVendorSettlemet.getVendorLetterhead())){
                    return new Response<>(Constant.ERROR_PARAM_CODE, "当前供应商的财务结算信息不完善，请在供应商管理中完善信息后再提交结算");
                }
                cfInvoiceHeader.setBank(baseVendorSettlemet.getCvenBank());
                cfInvoiceHeader.setBankAccounts(baseVendorSettlemet.getCvenAccount());
                cfInvoiceHeader.setInvoiceTitle(baseVendorSettlemet.getAccname());
                cfInvoiceHeader.setInvoiceTitleName(baseVendorSettlemet.getVendorLetterhead());
            }else {
                return new Response<>(Constant.ERROR_PARAM_CODE, "当前供应商的财务结算信息不完善，请在供应商管理中完善信息后再提交结算");
            }
        }
        /**
         * 处理账单详情数据
         */

        List<CfInvoiceDetail> cfInvoiceDetailListOfAll=new ArrayList<>();
        for (CfChargeVO cfChargeVO : list) {
            Set<String> chargeIdsNew = new HashSet<>();
            CfChargeServiceImpl.getIds(cfChargeVO.getChargeIds(), chargeIdsNew);
            List<Long> longs = CfChargeServiceImpl.transferIdsToLong(chargeIdsNew);
            List<CfCharge> match = uniqueCharges.stream().filter(a -> longs.stream().anyMatch(id -> id.equals(a.getChargeId()))).collect(Collectors.toList());
            for (CfCharge cf : match) {
                CfInvoiceDetail cfInvoiceDetail = new CfInvoiceDetail();
                BeanUtilCopy.copyProperties(cf, cfInvoiceDetail);
                cfInvoiceDetail.setInvoiceQty(cf.getChargeQty());
                if(cf.getArapType().equals(ChargeEnum.ARAP_TYPE_AR.getCode())){
                    cfInvoiceDetail.setInvoiceDebit(cf.getAmountPp());
                    cfInvoiceDetail.setInvoiceCredit(BigDecimal.ZERO);
                }else {
                    cfInvoiceDetail.setInvoiceCredit(cf.getAmountPp());
                    cfInvoiceDetail.setInvoiceDebit(BigDecimal.ZERO);

                }
                cfInvoiceDetailListOfAll.add(cfInvoiceDetail);
            }
        }
        invoiceHeaderMapper.insert(cfInvoiceHeader);
        for (CfInvoiceDetail invoiceDetail : cfInvoiceDetailListOfAll) {
            invoiceDetail.setInvoiceId(cfInvoiceHeader.getInvoiceId());
            invoiceDetailMapper.insert(invoiceDetail);
        }
        List<Long> collect = uniqueCharges.stream().map(CfCharge::getChargeId).collect(Collectors.toList());
        chargeMapper.updateInvoiceInfo(cfInvoiceHeader, collect);
        updatePrePayByInvoiceNo(cfInvoiceHeader, true,isAil);
        chargeMapper.updateInvoiceInfo(cfInvoiceHeader, collect);
        return new Response<>(ResponseCode.SUCCESS, cfInvoiceHeader.getInvoiceId());

    }


    private Set<String> getAllRuleBillDetails() {
        String ruleType = "1";
        List<RuleBillingDetail> ruleBillingDetailList = ruleBillingHeaderService.queryRuleBilling(ruleType);
        Set<String> collect = ruleBillingDetailList.stream().filter((a) -> ruleType.equals(a.getRuleType()) && Objects.equals(0,a.getGoodsRange()))
                .map(RuleBillingDetail::getRuleLevelCondition).collect(Collectors.toSet());
        if(CollectionUtils.isEmpty(collect)){
           collect=ruleBillingDetailList.stream().filter((a) -> ruleType.equals(a.getRuleType()) &&  Objects.equals(1,a.getGoodsRange()))
                    .map(RuleBillingDetail::getRuleLevelCondition).collect(Collectors.toSet());

        }
        return collect;
    }


    /**
     * 账单列表
     *
     * @param dto
     * @return
     */
    @Override
    public PageInfo<CfInvoiceHeaderListVO> invoiceHeaderList(CfInvoiceHeaderListDTO dto) {

        //提交注释
        if (dto.getInvoiceNo() != null) {
            dto.setInvoiceNo(dto.getInvoiceNo().trim());
        }
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        PageInfo<CfInvoiceHeaderListVO> pageInfo = new PageInfo<>(invoiceHeaderMapper.invoiceHeaderList(dto));
        List<CfInvoiceHeaderListVO> list = pageInfo.getList();
        List<String> vendors = list.stream().map(CfInvoiceHeaderListVO::getBalance).collect(Collectors.toList());
        Map<String, String> stringMap = cfChargeService.getVendorList(vendors);
        Map<String, String> dicts2 = cfChargeService.getDicts("Charge_Source_Type", list);
        for (int i = 0; i < list.size(); i++) {
            CfInvoiceHeaderListVO listVO = list.get(i);
            //补充账单对应入库信息（入库通知单类型，销售类型，发票号）
            if (stringMap.containsKey(listVO.getBalance())) {
                listVO.setBalance(stringMap.get(listVO.getBalance()));
            }
            if (dicts2.containsKey(listVO.getJobType())) {
                listVO.setJobType(dicts2.get(listVO.getJobType()));
            }
            //获取结算信息
            LambdaQueryWrapper<CfInvoiceSettlement> lambdaQueryWrapper = Wrappers.lambdaQuery(CfInvoiceSettlement.class);
            lambdaQueryWrapper.eq(CfInvoiceSettlement::getInvoiceId, listVO.getInvoiceId()).notIn(CfInvoiceSettlement::getInvoiceSettlementStatus, 0, 8);
            List<CfInvoiceSettlement> selectList = invoiceSettlementMapper.selectList(lambdaQueryWrapper);
            if (selectList.size() > 0) {
                StringBuilder sd = new StringBuilder();
                StringBuilder settlementStatusSb = new StringBuilder();
                selectList.forEach(a -> {
                    sd.append(a.getInvoiceSettlementNo()).append(",");
                    settlementStatusSb.append(a.getInvoiceSettlementNo()).append(":").append(SettlementStatusEnum.getMsgByCode(a.getInvoiceSettlementStatus()).getMsg()).append("\n");
                });
                listVO.setSettlementNos(sd.substring(0, sd.length() - 1));
                listVO.setSettlementStatus(settlementStatusSb.toString());
                BigDecimal settlementAmount = selectList.stream().map(CfInvoiceSettlement::getInvoiceSettlementMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
                listVO.setAdvancePayMoney(listVO.getAdvancePayMoney() == null ? BigDecimal.ZERO : listVO.getAdvancePayMoney());
                //帐单收付类型(应收应付类型(AR=收debit, AP=付credit))
                if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(listVO.getInvoiceType())) {
                    listVO.setBalanceOfStatement(listVO.getInvoicelDebit().subtract(listVO.getInvoicelCredit()).subtract(BigDecimal.ZERO.subtract(settlementAmount)).add(listVO.getAdvancePayMoney()));
                    listVO.setSettlementAmount(BigDecimal.ZERO.subtract(settlementAmount));
                } else if (ChargeEnum.ARAP_TYPE_AP.getCode().equals(listVO.getInvoiceType())) {
                    listVO.setBalanceOfStatement(listVO.getInvoicelCredit().subtract(listVO.getInvoicelDebit()).subtract(settlementAmount).subtract(listVO.getAdvancePayMoney()));
                    listVO.setSettlementAmount(settlementAmount);
                }
                //结算单已经核销的状态下，获取结算单的金额作为已核销金额
                List<CfInvoiceSettlement> clearedList = selectList.stream().filter(a ->
                        StringUtil.isNotEmpty(a.getClearNo())).collect(Collectors.toList());
                BigDecimal totalCleared = BigDecimal.ZERO;
                if(CollectionUtils.isNotEmpty(clearedList)){
                    totalCleared = totalCleared.add(clearedList.stream().map(CfInvoiceSettlement::getInvoiceSettlementMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
                }
                if(Objects.equals(listVO.getInvoiceType(),ChargeEnum.ARAP_TYPE_AP.getCode())){
                    if(listVO.getInvoicelTotal().compareTo(BigDecimal.ZERO)>=0){
                        if(totalCleared.compareTo(BigDecimal.ZERO) == 1){
                            //已核销
                            listVO.setClearAmount(totalCleared);
                            listVO.setOverInvoicel(listVO.getInvoicelTotal().subtract(totalCleared));
                        }else if(totalCleared.compareTo(BigDecimal.ZERO) == -1){
                            //已核销
                            listVO.setClearAmount(BigDecimal.ZERO.subtract(totalCleared));
                            listVO.setOverInvoicel(listVO.getInvoicelTotal().subtract(totalCleared));
                        }else{
                            //未核销
                            listVO.setOverInvoicel(listVO.getInvoicelTotal());
                            listVO.setClearAmount(BigDecimal.ZERO);
                        }
                    }else {
                        if(totalCleared.compareTo(BigDecimal.ZERO) == 1){
                            //已核销
                            listVO.setClearAmount(BigDecimal.ZERO.subtract(totalCleared));
                            listVO.setOverInvoicel(listVO.getInvoicelTotal().subtract(totalCleared));
                        }else if(totalCleared.compareTo(BigDecimal.ZERO) == -1){
                            //已核销
                            listVO.setClearAmount(totalCleared);
                            listVO.setOverInvoicel(listVO.getInvoicelTotal().subtract(totalCleared));
                        }else{
                            //未核销
                            listVO.setOverInvoicel(listVO.getInvoicelTotal());
                            listVO.setClearAmount(BigDecimal.ZERO);
                        }
                    }
                }else {
                    if(totalCleared.compareTo(BigDecimal.ZERO) == 1){
                        //已核销
                        listVO.setClearAmount(totalCleared);
                        listVO.setOverInvoicel(listVO.getInvoicelTotal().subtract(totalCleared));
                    }else if(totalCleared.compareTo(BigDecimal.ZERO) == -1){
                        //已核销
                        listVO.setClearAmount(BigDecimal.ZERO.subtract(totalCleared));
                        listVO.setOverInvoicel(listVO.getInvoicelTotal().add(totalCleared));
                    }else{
                        //未核销
                        listVO.setOverInvoicel(listVO.getInvoicelTotal());
                        listVO.setClearAmount(BigDecimal.ZERO);
                    }
                }


            } else {
                BigDecimal settlementAmount = BigDecimal.ZERO;
                listVO.setSettlementAmount(settlementAmount);
                //帐单收付类型(应收应付类型(AR=收debit, AP=付credit))
                if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(listVO.getInvoiceType())) {
                    listVO.setBalanceOfStatement(listVO.getInvoicelDebit().subtract(listVO.getInvoicelCredit()).add(listVO.getAdvancePayMoney() == null ? BigDecimal.ZERO : listVO.getAdvancePayMoney()));
                } else {
                    listVO.setBalanceOfStatement(listVO.getInvoicelCredit().subtract(listVO.getInvoicelDebit()).subtract(listVO.getAdvancePayMoney() == null ? BigDecimal.ZERO : listVO.getAdvancePayMoney()));
                }
                //待结算状态下，已核销金额是0，核销余额应该是总账单金额
                listVO.setOverInvoicel(listVO.getInvoicelTotal());
            }
            OperateUtil.onConvertedDecimal(listVO,"invoicelTotal","settlementAmount","overInvoicel","balanceOfStatement");
            OperateUtil.onConvertedDecimalBySuper(listVO,"clearAmount");
        }
        return pageInfo;
    }

    @Override
    public PageInfo<CfInvoiceHeaderListVO> invoiceHeaderListOfAssociated(CfInvoiceHeaderListOfAssociatedDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        PageInfo<CfInvoiceHeaderListVO> pageInfo = new PageInfo<>(invoiceHeaderMapper.invoiceHeaderListOfAssociated(dto));
        List<CfInvoiceHeaderListVO> list = pageInfo.getList();
        List<String> vendors = list.stream().map(CfInvoiceHeaderListVO::getBalance).collect(Collectors.toList());
        Map<String, String> stringMap = cfChargeService.getVendorList(vendors);
        Map<String, String> dicts2 = cfChargeService.getDicts("Charge_Source_Type", list);
        HashMap<Long, String> brandNameById = this.getBrandNameById();
        for (int i = 0; i < list.size(); i++) {
            CfInvoiceHeaderListVO listVO = list.get(i);
            if(brandNameById.containsKey(listVO.getBrandId())){
                listVO.setBrandName(brandNameById.get(listVO.getBrandId()));
            }
            //补充账单对应入库信息（入库通知单类型，销售类型，发票号）
            if (stringMap.containsKey(listVO.getBalance())) {
                listVO.setBalance(stringMap.get(listVO.getBalance()));
            }
            if (dicts2.containsKey(listVO.getJobType())) {
                listVO.setJobType(dicts2.get(listVO.getJobType()));
            }
            //获取结算信息
            LambdaQueryWrapper<CfInvoiceSettlement> lambdaQueryWrapper = Wrappers.lambdaQuery(CfInvoiceSettlement.class);
            lambdaQueryWrapper.eq(CfInvoiceSettlement::getInvoiceId, listVO.getInvoiceId()).notIn(CfInvoiceSettlement::getInvoiceSettlementStatus, 0, 8);
            List<CfInvoiceSettlement> selectList = invoiceSettlementMapper.selectList(lambdaQueryWrapper);
            if (selectList.size() > 0) {
                StringBuilder sd = new StringBuilder();
                StringBuilder settlementStatusSb = new StringBuilder();
                selectList.forEach(a -> {
                    sd.append(a.getInvoiceSettlementNo()).append(",");
                    settlementStatusSb.append(a.getInvoiceSettlementNo()).append(":").append(SettlementStatusEnum.getMsgByCode(a.getInvoiceSettlementStatus()).getMsg()).append("\n");
                });
                listVO.setSettlementNos(sd.substring(0, sd.length() - 1));
                listVO.setSettlementStatus(settlementStatusSb.toString());
                BigDecimal settlementAmount = selectList.stream().map(CfInvoiceSettlement::getInvoiceSettlementMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
                listVO.setAdvancePayMoney(listVO.getAdvancePayMoney() == null ? BigDecimal.ZERO : listVO.getAdvancePayMoney());
                //帐单收付类型(应收应付类型(AR=收debit, AP=付credit))
                if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(listVO.getInvoiceType())) {
                    listVO.setBalanceOfStatement(listVO.getInvoicelDebit().subtract(listVO.getInvoicelCredit()).subtract(BigDecimal.ZERO.subtract(settlementAmount)).add(listVO.getAdvancePayMoney()));
                    listVO.setSettlementAmount(BigDecimal.ZERO.subtract(settlementAmount));
                } else if (ChargeEnum.ARAP_TYPE_AP.getCode().equals(listVO.getInvoiceType())) {
                    listVO.setBalanceOfStatement(listVO.getInvoicelCredit().subtract(listVO.getInvoicelDebit()).subtract(settlementAmount).subtract(listVO.getAdvancePayMoney()));
                    listVO.setSettlementAmount(settlementAmount);
                }
                //结算单已经核销的状态下，获取结算单的金额作为已核销金额
                List<CfInvoiceSettlement> clearedList = selectList.stream().filter(a ->
                        StringUtil.isNotEmpty(a.getClearNo())).collect(Collectors.toList());
                BigDecimal totalCleared = BigDecimal.ZERO;
                if(CollectionUtils.isNotEmpty(clearedList)){
                    totalCleared = totalCleared.add(clearedList.stream().map(CfInvoiceSettlement::getInvoiceSettlementMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
                }
                if(totalCleared.compareTo(BigDecimal.ZERO) == 1){
                    //已核销
                    listVO.setClearAmount(totalCleared);
                    listVO.setOverInvoicel(listVO.getInvoicelTotal().subtract(totalCleared));
                }else if(totalCleared.compareTo(BigDecimal.ZERO) == -1){
                    //已核销
                    listVO.setClearAmount(BigDecimal.ZERO.subtract(totalCleared));
                    listVO.setOverInvoicel(listVO.getInvoicelTotal().add(totalCleared));
                }else{
                    //未核销
                    listVO.setOverInvoicel(listVO.getInvoicelTotal());
                    listVO.setClearAmount(BigDecimal.ZERO);
                }
            } else {
                BigDecimal settlementAmount = BigDecimal.ZERO;
                listVO.setSettlementAmount(settlementAmount);
                //帐单收付类型(应收应付类型(AR=收debit, AP=付credit))
                if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(listVO.getInvoiceType())) {
                    listVO.setBalanceOfStatement(listVO.getInvoicelDebit().subtract(listVO.getInvoicelCredit()).add(listVO.getAdvancePayMoney() == null ? BigDecimal.ZERO : listVO.getAdvancePayMoney()));
                } else {
                    listVO.setBalanceOfStatement(listVO.getInvoicelCredit().subtract(listVO.getInvoicelDebit()).subtract(listVO.getAdvancePayMoney() == null ? BigDecimal.ZERO : listVO.getAdvancePayMoney()));
                }
                //待结算状态下，已核销金额是0，核销余额应该是总账单金额
                listVO.setOverInvoicel(listVO.getInvoicelTotal());
            }
            OperateUtil.onConvertedDecimal(listVO,"invoicelTotal","settlementAmount","overInvoicel","balanceOfStatement");
            OperateUtil.onConvertedDecimalBySuper(listVO,"clearAmount");
        }
        return pageInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void auditInvoices(List<InvoiceSwitchBanBO> modes, UserVO userVO) {
        for (InvoiceSwitchBanBO model : modes) {
            auditInvoice(model, userVO);
        }
    }

    /**
     * 账单详情
     *
     * @param invoiceId
     * @return
     */
    @Override
    public CfInvoiceHeaderDetailVO detail(Long invoiceId,Boolean isMvc) {
        CfInvoiceHeaderDetailVO detailVO = new CfInvoiceHeaderDetailVO();
        List<Long> longList = new ArrayList<>();
        CfInvoiceHeader cfInvoiceHeader = invoiceHeaderMapper.selectById(invoiceId);
        BeanUtils.copyProperties(cfInvoiceHeader, detailVO);
        if(isMvc){
            List<CfInvoiceHeaderListVO> cfInvoiceHeaderListVOS = invoiceHeaderMapper.selectRedListByAssociatedNo(cfInvoiceHeader.getInvoiceNo());
            if(CollectionUtils.isNotEmpty(cfInvoiceHeaderListVOS)){
                this.toTurn(cfInvoiceHeaderListVOS);
                detailVO.setAssociatedInvoiceList(cfInvoiceHeaderListVOS);
            }
        }
        Response<BaseGetBrandInfoList> brandInfo = baseInfoRemoteServer.getBrandInfo(cfInvoiceHeader.getBrandId().intValue());
        if (null != brandInfo && null != brandInfo.getObj()) {
            detailVO.setBrandName(brandInfo.getObj().getBrandName());
        }
        Response<VendorResModel> vendorByCode = vendorCenterServer.getVendorByCode(null, detailVO.getBalance());
        detailVO.setVendorName(vendorByCode.getObj().getVendorName());
        detailVO.setVendorAbbName(vendorByCode.getObj().getVenAbbName());
        detailVO.setVendorId(vendorByCode.getObj().getVendorId());
        List<ChargeInvoiceDetailVO> list = chargeMapper.queryChargeListByInvoiceId(invoiceId);
        //获取费用列表的结算主体和来源类型
        List<String> vendors = list.stream().map(ChargeInvoiceDetailVO::getBalance).collect(Collectors.toList());
        Map<String, String> vendorList = cfChargeService.getVendorList(vendors);
        Map<String, String> chargetypes = cfChargeService.getDicts("charge_type", list);
        Map<String, String> sourcetypes = cfChargeService.getDicts("Charge_Source_Type", list);
        List<CfCharge> allCharge = getAllChargeSourceCode(cfInvoiceHeader.getInvoiceNo());
        allCharge = allCharge.stream().filter(a -> a.getProductCode() != null && a.getChargeType() != null && a.getChargeSourceCode() != null).collect(Collectors.toList());
        for (ChargeInvoiceDetailVO ch : list) {
            String productCode = ch.getProductCode();
            String chargeType = ch.getChargeType();
            String chargeSourceCode = ch.getChargeSourceCode();
            //获取同一spu、同一来源单号、同一费用种类的sku
            List<ChargeInvoiceSkuDetailVO> invoiceSkuDetailVoS = matchAdpChargeSkuListByInvoiceId(productCode, chargeType, chargeSourceCode, allCharge);
            if (CollectionUtils.isNotEmpty(invoiceSkuDetailVoS)) {
                for (ChargeInvoiceSkuDetailVO detailSku : invoiceSkuDetailVoS) {
                    detailSku.setBalanceName(vendorList.get(detailSku.getBalance()));
                    detailSku.setChargeSourceName(sourcetypes.get(detailSku.getChargeSource() + ""));
                    detailSku.setChargeType(chargetypes.get(detailSku.getChargeType()));
                    longList.add(detailSku.getChargeId());
                }
                ch.setChargeQty(invoiceSkuDetailVoS.stream().map(ChargeInvoiceSkuDetailVO::getChargeQty).reduce(0, Integer::sum));
                ch.setAmountPp(invoiceSkuDetailVoS.stream().map(ChargeInvoiceSkuDetailVO::getAmountPp).reduce(BigDecimal.ZERO, BigDecimal::add));
                BigDecimal qty = new BigDecimal(ch.getChargeQty());
                ch.setPricePp(ch.getAmountPp().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                        ch.getAmountPp().divide(qty, 4, BigDecimal.ROUND_HALF_UP));
                ch.setBalanceName(vendorList.get(ch.getBalance()));
                ch.setChargeSourceName(sourcetypes.get(ch.getChargeSource() + ""));
                ch.setChargeType(chargetypes.get(ch.getChargeType()));
                ch.setChargeInvoiceDetailVOList(invoiceSkuDetailVoS);
            }
        }

        detailVO.setChargeList(list);
        detailVO.setChargeListSize(list.size());

        if (longList.size() > 0) {
            List<ChargeInVO> chargeInList = chargeInMapper.selectChargeInDetail(invoiceId);
            // 合并延期明细
            mergePostponeDetail(chargeInList);
            // 获取预付款
            getPrePay(chargeInList, cfInvoiceHeader);
            //设置合计
            setTotal(cfInvoiceHeader, chargeInList);
            for (ChargeInVO c: chargeInList) {
                Integer actualQty = Objects.isNull(c.getActualQty())?0: c.getActualQty();
                Integer defectiveRejectionQty=Objects.isNull(c.getDefectiveRejectionQty()) ?0:c.getDefectiveRejectionQty();
                c.setSettlementAllQty( actualQty-defectiveRejectionQty);
            }
            detailVO.setChargeInList(chargeInList);

            //获取销售类型
            List<Integer> salesTypeList = chargeInList.stream().filter(chargeInVO -> chargeInVO.getSalesType() != null).map(ChargeInVO::getSalesType).distinct().collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(salesTypeList)) {
                detailVO.setSalesType(salesTypeList.get(0));
            } else {
                detailVO.setSalesType(2);
            }
            List<CfClearHeader> cfClearHeaders =
                    cfClearHeaderMapper.selectList(Wrappers.lambdaQuery(CfClearHeader.class)
                            .eq(CfClearHeader::getInvoiceNo, cfInvoiceHeader.getInvoiceNo()));
            detailVO.setCfInvoiceHeaderClearVos(
                    cfClearHeaders.stream().map(cfClearHeader -> {
                        CfInvoiceHeaderClearVo cfInvoiceHeaderClearVo = new CfInvoiceHeaderClearVo();
                        BeanUtils.copyProperties(cfClearHeader, cfInvoiceHeaderClearVo);
                        return cfInvoiceHeaderClearVo;
                    }).collect(Collectors.toList())
            );
        }
        //获取结算信息
        List<CfInvoiceSettlement> selectList = getAllSettlement(invoiceId);
        List<CfInvoiceSettlementVo> settlementList = BeanUtilCopy.copyListProperties(selectList, CfInvoiceSettlementVo::new);
        if (settlementList.size() > 0) {
            BigDecimal settlementAmount = settlementList.stream().filter(x -> x.getInvoiceSettlementStatus() != 8).map(CfInvoiceSettlementVo::getInvoiceSettlementMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
            settlementAmount=BigDecimal.ZERO.compareTo(settlementAmount)>-1? settlementAmount.multiply(new BigDecimal(-1)):settlementAmount;
            detailVO.setSettlementAmount(settlementAmount);
            //帐单收付类型(应收应付类型(AR=收debit, AP=付credit))
            if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(detailVO.getInvoiceType())) {
                detailVO.setBalanceOfStatement(detailVO.getInvoicelDebit().subtract(detailVO.getInvoicelCredit()).subtract(settlementAmount));
            } else {
                detailVO.setBalanceOfStatement(detailVO.getInvoicelCredit().subtract(detailVO.getInvoicelDebit()).subtract(settlementAmount));
            }
        } else {
            BigDecimal settlementAmount = BigDecimal.ZERO;
            detailVO.setSettlementAmount(settlementAmount);
            //帐单收付类型(应收应付类型(AR=收debit, AP=付credit))
            if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(detailVO.getInvoiceType())) {
                detailVO.setBalanceOfStatement(detailVO.getInvoicelDebit().subtract(detailVO.getInvoicelCredit()));
            } else {
                detailVO.setBalanceOfStatement(detailVO.getInvoicelCredit().subtract(detailVO.getInvoicelDebit()));
            }
        }
        BigDecimal adv = BigDecimal.ZERO;
        if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(detailVO.getInvoiceType())) {
            adv = detailVO.getBalanceOfStatement().add(CfInvoiceSettlementServiceImpl.getHeji(detailVO.getChargeInList()).getAdvancepayAmountActual());
        }else {
            adv = detailVO.getBalanceOfStatement().subtract(CfInvoiceSettlementServiceImpl.getHeji(detailVO.getChargeInList()).getAdvancepayAmountActual());
        }
        detailVO.setBalanceOfStatement(adv);
        settlementList.forEach(cfInvoiceSettlementVo -> {
            cfInvoiceSettlementVo.setJobType(cfInvoiceHeader.getJobType());
            cfInvoiceSettlementVo.setInvoiceType(cfInvoiceHeader.getInvoiceType());
            cfInvoiceSettlementVo.setBalanceName(cfInvoiceHeader.getBalanceName());
            NumberFormat percent = NumberFormat.getPercentInstance();
            percent.setMaximumFractionDigits(2);
            cfInvoiceSettlementVo.setSettlementRate(percent.format(cfInvoiceSettlementVo.getInvoiceSettlementRate().doubleValue()));
            if (StringUtils.isNotBlank(cfInvoiceSettlementVo.getClearNo())) {
                cfInvoiceSettlementVo.setClearMoney(cfInvoiceSettlementVo.getInvoiceSettlementMoney());
            }
        });
        detailVO.setSettlementList(settlementList);
        detailVO.setSettlementListSize(settlementList.size());
        if(detailVO.getAdjustQcMoney()==null){
            ChargeInVO chargeInVO=detailVO.getChargeInList().parallelStream().filter(x->Objects.equals(x.getProductCode(),"合计")).findFirst().get();
            detailVO.setAdjustQcMoney(chargeInVO.getQaDeductions());
        }

        return detailVO;
    }

    private List<ChargeInvoiceSkuDetailVO> matchAdpChargeSkuListByInvoiceId(String productCode, String chargeType, String chargeSourceCode, List<CfCharge> allCharge) {
        List<CfCharge> collect = allCharge.stream().filter(a -> a.getProductCode().equals(productCode) && a.getChargeType().equals(chargeType) && a.getChargeSourceCode().equals(chargeSourceCode)).collect(Collectors.toList());
        return BeanUtilCopy.copyListProperties(collect, ChargeInvoiceSkuDetailVO::new);
    }

    private void getPrePay(List<ChargeInVO> chargeInList, CfInvoiceHeader invoiceHeader) {
        // 第一步 获取所有费用里面的来源单号
        List<CfCharge> allChargeSourceCode = getAllChargeSourceCode(invoiceHeader.getInvoiceNo());
        List<Long> details = allChargeSourceCode.stream().filter((a) -> a.getChargeSourceDetailId() != null&&Objects.equals(a.getChargeType(),CfFinanceConstant.CHARGE_SOURCE_FOR_PO )).map(CfCharge::getChargeSourceDetailId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(details)){
            return;
        }
        // 第二步 获取入库单 +入库单明细表
        List<CfRdRecordDetail> allRdRecordDetail = getAllRdRecordDetailByDetailsId(details);
        log.info("入库单明细：{}", JSON.toJSONString(allRdRecordDetail));
        List<RdRecordDetailPrePay> pays = getPoids(allRdRecordDetail);
        HashMap<Integer,String> advancepayMap=new HashMap();
        for (ChargeInVO s : chargeInList) {
            List<RdRecordDetailPrePay> onePay = pays.stream().filter((p) -> p.getProductCode().equals(s.getProductCode())
                    && p.getCostsPrice().compareTo(s.getCostsPrice()) == 0
                    && p.getTaxRate().compareTo(s.getTaxRate()) == 0).collect(Collectors.toList());
            if (onePay.size() < 1 || onePay.get(0).getPoIds().size() < 1) {
                continue;
            }
            // 查询审核状态为已打款的预付款申请单集合
            List<AdvancepayApplication> applicationList = getAdvancepayListByPoIds(onePay.get(0).getPoIds(), invoiceHeader.getInvoiceNo());
            if(CollectionUtils.isEmpty(applicationList)){
                continue;
            }
            applicationList = applicationList.stream().filter(x -> !advancepayMap.containsKey(x.getAdvancePayId())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(applicationList)){
                continue;
            }
            advancepayMap.putAll( applicationList.stream().collect(Collectors.toMap(AdvancepayApplication::getAdvancePayId,AdvancepayApplication::getAdvancePayCode)));
            Set<Long> collect = applicationList.stream().map(AdvancepayApplication::getPoId).filter(Objects::nonNull).collect(Collectors.toSet());
            // 预付款实付金额（预付款申请单 已打款金额和）
            BigDecimal advancepayAmountActual = applicationList.stream().map(AdvancepayApplication::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
            s.setAdvancepayAmountActual(advancepayAmountActual);
            // 合同金额
            List<CfPoHeader> allPo = getAllPo(new ArrayList<>(collect));
            // 预付款预付金额(采购单里未结算 尾款+定金)
            BigDecimal advancePayAmount = BigDecimal.ZERO;
            for (CfPoHeader a : allPo) {
                if (a.getBargain() == null) {
                    a.setBargain(BigDecimal.ZERO);
                }
                if (a.getRetainage() == null) {
                    a.setRetainage(BigDecimal.ZERO);
                }
                advancePayAmount = advancePayAmount.add(a.getBargain().add(a.getRetainage()));
            }
            s.setAdvancepayAmount(advancePayAmount);
        }
    }

    /**
     * // 查询审核状态为已打款的预付款申请单集合
     *
     * @param poIds
     * @param invoiceNo
     * @return
     */
    private List<AdvancepayApplication> getAdvancepayListByPoIds(List<Long> poIds, String invoiceNo) {
        if (poIds.size() < 1) {
            return new LinkedList<>();
        }
        return advancepayApplicationMapper.selectList(Wrappers.lambdaQuery(AdvancepayApplication.class)
                .in(AdvancepayApplication::getPoId, poIds)
                .eq(AdvancepayApplication::getState, CfFinanceConstant.ADVANCEPAY_STATE_FOR_PAID)
                .eq(AdvancepayApplication::getInvoiceNo, invoiceNo)
                .eq(AdvancepayApplication::getIsDelete, 0));
    }

    private List<AdvancepayApplication> getAdvancepayListByPoIds(List<Long> poIds, Integer... state) {
        if (poIds.size() < 1) {
            return new LinkedList<>();
        }
        return advancepayApplicationMapper.selectList(Wrappers.lambdaQuery(AdvancepayApplication.class)
                .in(AdvancepayApplication::getPoId, poIds)
                .in(AdvancepayApplication::getState, state)
                .isNull(AdvancepayApplication::getInvoiceNo)
                .eq(AdvancepayApplication::getIsDelete, 0)
                .orderByAsc(AdvancepayApplication::getAdvancePayId));
    }


    private static List<RdRecordDetailPrePay> getPoids(List<CfRdRecordDetail> allRdRecordDetail) {
        List<RdRecordDetailPrePay> pays = new ArrayList<>();
        Map<String, List<CfRdRecordDetail>> collect3 = allRdRecordDetail.stream().collect(Collectors.groupingBy(item -> item.getProductCode() + "_,_" + item.getTaxUnitPrice() + "_,_" + item.getTaxRate()));
        collect3.forEach((k,v)->{
            RdRecordDetailPrePay pay = new RdRecordDetailPrePay();
            String[] split = k.split("_,_");
            pay.setProductCode(split[0]);
            pay.setCostsPrice(new BigDecimal(split[1]));
            pay.setTaxRate(new BigDecimal(split[2]));
            pay.setDetails(v);
            pay.setPoIds(new ArrayList<>(v.stream().filter(x->x.getPoId()!=null).map(CfRdRecordDetail::getPoId).collect(Collectors.toSet())));
            pays.add(pay);
        });
 /*       Map<String, List<CfRdRecordDetail>> collect = allRdRecordDetail.stream().collect(Collectors.groupingBy(CfRdRecordDetail::getProductCode));
        collect.forEach((k, v) -> {
            RdRecordDetailPrePay pay = new RdRecordDetailPrePay();
            pay.setProductCode(k);
            pays.add(pay);
            Map<BigDecimal, List<CfRdRecordDetail>> collect1 = v.stream().collect(Collectors.groupingBy(CfRdRecordDetail::getTaxUnitPrice));
            collect1.forEach((k1, v1) -> {
                pay.setCostsPrice(k1);
                Map<BigDecimal, List<CfRdRecordDetail>> collect2 = v1.stream().collect(Collectors.groupingBy(CfRdRecordDetail::getTaxRate));
                collect2.forEach((k2, v2) -> {
                    pay.setTaxRate(k2);
                    pay.setDetails(v2);
                    Set<Long> pos = v2.stream().filter((a) -> a.getPoId() != null).map(CfRdRecordDetail::getPoId).collect(Collectors.toSet());
                    pay.setPoIds(new ArrayList<>(pos));
                });
            });
        });*/
        return pays;
    }




    private void mergePostponeDetail(List<ChargeInVO> chargeInList) {
        // {"1-3": 29},{"8-": 50},{"8-": 50}
        Set<String> keys = new HashSet<>();

        for (ChargeInVO chargeInVO : chargeInList) {
            if (chargeInVO.getPostponeDetail() == null) {
                chargeInVO.setPostponeDetail("");
            }
            String postponeDetail = chargeInVO.getPostponeDetail();
            String[] substring = postponeDetail.split(",");
            for (String s : substring) {
                JSONObject object = JSONObject.parseObject(s, JSONObject.class);
                if (object == null) {
                    continue;
                }
                Set<String> strings = object.keySet();
                keys.addAll(strings);
            }
        }
        keys.addAll(getAllRuleBillDetails());
        // 排序
        List<Integer> sortList = new ArrayList<>(5);
        Map<Integer, String> mapsort = new HashMap<>(8);
        Set<String> keysSorted = new LinkedHashSet<>();
        for (String key : keys) {
            String[] split = key.split("-");
            String s = split[0];
            int i = Integer.parseInt(s);
            sortList.add(i);
            mapsort.put(i, key);
        }
        Collections.sort(sortList);
        for (Integer integer : sortList) {
            String s = mapsort.get(integer);
            keysSorted.add(s);
        }
        // 字符串 数字汇总
        log.info("延期key-value:{}",keysSorted);
        for (ChargeInVO chargeInVO : chargeInList) {
            String postponeDetail = chargeInVO.getPostponeDetail();
            String[] substring = postponeDetail.split(",");
            Map<String, Integer> objectToArray = new LinkedHashMap<>();
            List<Map<String, Integer>> sortArray = new LinkedList<>();
            for (String key : keysSorted) {
                log.info("延期key{}",key);
                for (String s : substring) {
                    JSONObject object = JSONObject.parseObject(s, JSONObject.class);
                    if (object == null) {
                        object = new JSONObject();
                    }
                    if (!objectToArray.containsKey(key)) {
                        objectToArray.put(key, 0);
                    }
                    if (object.containsKey(key)) {
                        int sum = Integer.parseInt(objectToArray.get(key).toString());
                        int num = Integer.parseInt(object.get(key).toString());
                        objectToArray.put(key, sum + num);
                    }
                }
                Map<String, Integer> jsonObject = new HashMap<>(4);
                String keyAlias = getKeyAlias(key);
                log.info("延期key-s:{}",keyAlias);
                log.info("延期key-so:{}",jsonObject);
                jsonObject.put(keyAlias, objectToArray.get(key));
                sortArray.add(jsonObject);
            }
            chargeInVO.setPostponeDetail(JSONObject.toJSONString(sortArray));
            chargeInVO.setPostponeDetailArray(sortArray);
        }
    }

    private String getKeyAlias(String key) {
        String keyAlias = "延期";
        if (StringUtils.isNotBlank(key)) {
            String[] split = key.split("-");
            if (split.length > 0) {
                if (split.length == 1 || StringUtils.isBlank(split[1])) {
                    keyAlias = "延期" + split[0] + "天以上";
                } else {
                    keyAlias = "延期" + key + "天";
                }
            }
        }
        return keyAlias;
    }


    /**
     * 账单编辑
     *
     * @param invoiceHeaderAddBO
     * @param userVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<Object> update(CfInvoiceHeaderAddBO invoiceHeaderAddBO, UserVO userVO) {
        CfInvoiceHeader invoiceHeader = invoiceHeaderAddBO.getInvoiceHeader();
        invoiceHeader.setUpdateBy(userVO.getUserId());
        invoiceHeader.setUpdateName(userVO.getRealName());
        invoiceHeader.setUpdateDate(LocalDateTime.now());
        invoiceHeader.setAssociatedInvoiceNo(null);
        invoiceHeader.setAssociatedInvoiceSettlementNo(null);
        invoiceHeaderMapper.updateById(invoiceHeader);
        CfInvoiceHeader header = invoiceHeaderMapper.selectById(invoiceHeader.getInvoiceId());
        List<CfInvoiceDetail> invoiceDetailList = invoiceHeaderAddBO.getChargeList();
        Assert.isTrue(invoiceDetailList != null && invoiceDetailList.size() > 0, "账单对应的费用至少保留一笔");
        List<CfInvoiceDetail> details = invoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfInvoiceDetail.class)
                .eq(CfInvoiceDetail::getInvoiceId, invoiceHeader.getInvoiceId()));

        List<CfInvoiceDetail> inserts = invoiceDetailList.stream().filter(a -> details.stream().noneMatch(b -> b.getChargeId().equals(a.getChargeId()))).collect(Collectors.toList());
        for (CfInvoiceDetail invoiceDetail : inserts) {
            invoiceDetail.setInvoiceId(invoiceHeader.getInvoiceId());
            invoiceDetailMapper.insert(invoiceDetail);
        }

        List<Long> insetList = inserts.stream().map(CfInvoiceDetail::getChargeId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(insetList)) {
            chargeMapper.updateInvoiceInfo(invoiceHeader, insetList);
        }
        List<CfCharge> charges = chargeMapper.selectList(Wrappers.lambdaQuery(CfCharge.class)
                .eq(CfCharge::getInvoiceNo, header.getInvoiceNo()));

        BigDecimal credit = BigDecimal.ZERO;
        BigDecimal debit = BigDecimal.ZERO;
        for (CfCharge t : charges) {
            if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(t.getArapType())) {
                debit = debit.add(t.getAmountPp());
            } else {
                credit = credit.add(t.getAmountPp());
            }
        }
        header.setInvoicelCredit(credit);
        header.setInvoicelDebit(debit);
        invoiceHeaderMapper.updateById(header);
        return new Response<>(ResponseCode.SUCCESS, invoiceHeader.getInvoiceId());
    }

    /**
     * 填写发票信息
     *
     * @param settlement
     * @param userVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<Object> updateInvoiceInfo(CfInvoiceSettlement settlement, UserVO userVO) {
        Assert.notNull(settlement.getInvoiceSettlementId(), "核算单id不可为空");
        CfInvoiceSettlement exist = invoiceSettlementMapper.selectById(settlement.getInvoiceSettlementId());
        Assert.isTrue(StringUtils.isBlank(exist.getClearNo()), "费用已被核销");
        CfInvoiceSettlement update = new CfInvoiceSettlement();
        update.setInvoiceSettlementId(settlement.getInvoiceSettlementId());
        update.setCustomerInvoiceNo(settlement.getCustomerInvoiceNo());
        update.setCustomerInvoiceDate(settlement.getCustomerInvoiceDate());
        update.setRemark(settlement.getRemark());
        invoiceSettlementMapper.updateById(update);

        // 一键核销
        createBankAndCash(exist.getInvoiceId(), exist.getInvoiceSettlementId(), userVO);
        return new Response<>(ResponseCode.SUCCESS);
    }

    @Override
    public Response<Object> editInvoiceInfo(CfInvoiceSettlement settlement, UserVO userVO) {
        Assert.notNull(settlement.getInvoiceSettlementId(), "核算单id不可为空");
        CfInvoiceSettlement exist = invoiceSettlementMapper.selectById(settlement.getInvoiceSettlementId());
        Assert.isTrue(StringUtils.isNotBlank(exist.getClearNo()), "当前费用尚未核销，暂不支持修改发票");
        CfInvoiceSettlement update = new CfInvoiceSettlement();
        update.setInvoiceSettlementId(settlement.getInvoiceSettlementId());
        update.setCustomerInvoiceNo(settlement.getCustomerInvoiceNo());
        update.setCustomerInvoiceDate(settlement.getCustomerInvoiceDate());
        update.setRemark(settlement.getRemark());
        invoiceSettlementMapper.updateById(update);
        return new Response<>(ResponseCode.SUCCESS);
    }

    private BaseGetBrandInfoList getBrandInfo(Long brandId) {
        BrandFeignRequest brandFeignRequest = new BrandFeignRequest();
        List<Integer> brandIdList = new ArrayList<>();
        brandIdList.add(brandId.intValue());
        brandFeignRequest.setBrandIdList(brandIdList);
        Response<List<BrandFeignVO>> response = baseInfoRemoteServer.getBrandByBrandIdList(brandFeignRequest);
        if (HttpStateEnum.OK.getCode() != response.getCode()) {
            log.error("调用baseinfo服务查询品牌信息报错,参数{}", JSONObject.toJSONString(brandFeignRequest));
            throw new BusinessException(response.getCode(), response.getMessage());
        }

        BaseGetBrandInfoList brandInfo = new BaseGetBrandInfoList();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(response.getObj())) {
            BrandFeignVO brandFeignVO = response.getObj().get(0);
            brandInfo.setBrandId(brandFeignVO.getBrandId());
            brandInfo.setBrandCode(brandFeignVO.getBrandCode());
            brandInfo.setBrandName(brandFeignVO.getBrandName());
            brandInfo.setBrandType(brandFeignVO.getBrandType());
        }
        return brandInfo;
    }

    /**
     * 发票校验
     *
     * @param invoiceId
     * @param userVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invoiceVerify(Long invoiceId, Long settlementId, UserVO userVO) {
        CfInvoiceHeader invoiceHeader = invoiceHeaderMapper.selectById(invoiceId);
        if (invoiceHeader == null) {
            throw new BusinessException(500, "参数错误");
        }
        //查询品牌信息
        BaseGetBrandInfoList brandInfo = getBrandInfo(invoiceHeader.getBrandId());


        // 根据结算找被核销的费用编号
        CfInvoiceSettlement settlement = invoiceSettlementMapper.selectById(settlementId);
        Assert.isTrue(StringUtils.isNotBlank(settlement.getCustomerInvoiceNo()), "一键核销前需要填写发票号");
        // 第一步 获取所有费用里面的来源单号
        List<CfCharge> allChargeSourceCode = getAllChargeSourceCode(invoiceHeader.getInvoiceNo());
        List<String> sourceCodes = allChargeSourceCode.stream().filter((a) -> a.getChargeSourceCode() != null).map(CfCharge::getChargeSourceCode).collect(Collectors.toList());

        // 第二步 获取入库单 +入库单明细表
        List<CfRdRecord> allRdRecord = getAllRdRecord(sourceCodes);
        // 根据费用里面入库单明细id 查询子表入库明细
        Set<Long> collectCharge = allChargeSourceCode.stream().filter((a) -> a.getChargeSourceDetailId() != null&&!Objects.equals(String.valueOf(NumberEnum.NINE.getCode()),a.getChargeType())).map(CfCharge::getChargeSourceDetailId).collect(Collectors.toSet());
        List<CfRdRecordDetail> allRdRecordDetail = getAllRdRecordDetail(collectCharge);

        Set<Long> collectDetails = allRdRecordDetail.stream().filter((a) -> a.getRdRecordDetailId() != null).map(CfRdRecordDetail::getRdRecordDetailId).collect(Collectors.toSet());
        Assert.isTrue(collectDetails.size() == collectCharge.size(), "费用入库明细数据 和 入库单明细数据有偏差");

        // 获取结算单打印明细中每个spu 的数量
        CfInvoiceHeaderDetailVO print = cfInvoiceSettlementService.print(settlementId);
        List<ChargeInVO> settlementSpuNums = getNewChargeInvoOfRemoveVirtual(print.getChargeInList());

        List<PriceDetailInfo> priceDetailInfos = new ArrayList<>();
        for (ChargeInVO s : settlementSpuNums) {
            //List<PriceDetailInfo> detailInfos = allocationRdRecordDetial(s.getProductCode(), s.getCostsPrice(), s.getTaxRate(), s.getSettlementQty(), allRdRecordDetail, allRdRecord);
            List<PriceDetailInfo> detailInfos =assignTheBillingQuantityOfEachBill(allChargeSourceCode,s.getProductCode(),s.getCostsPrice(),s.getTaxRate(),s.getSettlementQty(),allRdRecordDetail,allRdRecord);
            if(CollectionUtils.isNotEmpty(detailInfos)){
                priceDetailInfos.addAll(detailInfos);
            }
        }
        // 1、推送条件：
        //  企业店铺，推送到对应的U8账套中去；
        // 非企业店铺，用户端无发票填写，无内容可推送；
        String f = "2";
        if (f.equals(brandInfo.getBrandType())) {
            log.info("非企业店铺不推送U8，brandId:{} ", invoiceHeader.getBrandId());
            return;
        }
        //将发票主表和详情推送U8，调用推送的service
        insertData(invoiceHeader, userVO, priceDetailInfos, brandInfo, settlement);
    }


    private void insertData(CfInvoiceHeader invoiceHeader, UserVO userVO, List<PriceDetailInfo> priceDetailInfos, BaseGetBrandInfoList brandInfo, CfInvoiceSettlement settlement) {

        Integer live = 1;
        if (priceDetailInfos.size() < 1) {
            log.info("没有入库信息，不用推送U8");
            return;
        }
        PurchaseInvoice purchaseInvoice = new PurchaseInvoice();
        //根据账单no查询关联费用，因为已经做了限制，不允许订单款和直播款一起生成，所以获取费用的销售类型即可
        Integer saleType = cfChargeMapper.getSaleTypeWithInvoiceNo(invoiceHeader.getInvoiceNo());

        log.info("saleType:{}",saleType);
        if(null == saleType){
            purchaseInvoice.setInventoryType(2);
        }else{
            purchaseInvoice.setInventoryType(saleType);
        }
        purchaseInvoice.setVendorId(priceDetailInfos.get(0).getVendorId());
        purchaseInvoice.setVendorCode(invoiceHeader.getBalance());

        String res = pageInfoUtil.generateBusinessNum(BillNoConstantClassField.SETTLE);
        purchaseInvoice.setPurchaseInvoiceCode(res);
        purchaseInvoice.setAccountBillId(invoiceHeader.getInvoiceId().intValue());
        purchaseInvoice.setAccountBillCode(settlement.getInvoiceSettlementNo());
        String one = "1";
        String invoiceType = "02";
        BigDecimal taxRateOfInvoiceNo = cfChargeMapper.selectTaxRateOfInvoiceNo(invoiceHeader.getInvoiceNo());

        if(BigDecimal.ZERO.compareTo(taxRateOfInvoiceNo)!=0){
            invoiceType = "01";
        }

        purchaseInvoice.setCustomerInvoiceNo(settlement.getCustomerInvoiceNo());
        purchaseInvoice.setRemark(settlement.getRemark());
        purchaseInvoice.setInvoiceType(invoiceType);
        purchaseInvoice.setCreateName(userVO.getRealName());
        purchaseInvoice.setCreateBy(userVO.getUserId());
        Date date = new Date();
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        Date parse = null;
        try {
            parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        purchaseInvoice.setCreateDate(parse);
        purchaseInvoice.setUpdateName(userVO.getRealName());
        purchaseInvoice.setUpdateBy(userVO.getUserId());
        purchaseInvoice.setUpdateDate(parse);
        purchaseInvoice.setIsDelete(false);

        List<PurchaseInvoiceDetail> invoicesList = new ArrayList<>();
        List<InventoryCategoryNew> inventoryCategoryNewList = baseRemoteServer.getInventoryCategoryNewList(null).getObj();
        Set<String> collect = priceDetailInfos.stream().filter(x->Objects.nonNull(x.getProductCode())).map(x -> x.getProductCode()).collect(Collectors.toSet());
        HashMap<String,InventoryCategoryNew> inventoryCategoryNewHashMap=new HashMap<>();
        for (String productCode:collect) {
            InventoryGetInfoModel obj = baseRemoteServer.getInfo(productCode, null).getObj();
            if(obj!=null&&CollectionUtils.isNotEmpty(inventoryCategoryNewList)){
                Optional<InventoryCategoryNew> first = inventoryCategoryNewList.stream().filter(x -> Objects.equals(x.getId(), obj.getClassifyId())).findFirst();
                if(first.isPresent()){
                    inventoryCategoryNewHashMap.put(productCode,first.get());
                }
            }
        }
        for (PriceDetailInfo vo : priceDetailInfos) {
            PurchaseInvoiceDetail invoicesEntity = new PurchaseInvoiceDetail();
            invoicesEntity.setInventoryId(vo.getInventoryId());
            invoicesEntity.setInventoryCode(vo.getInventoryCode());

            invoicesEntity.setQuantity(new BigDecimal(vo.getQuantity()));
            if (invoicesEntity.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            invoicesEntity.setTaxUnitPrice(vo.getTaxPrice());
            invoicesEntity.setRecordCode(vo.getRecordCode());
            invoicesEntity.setTaxRate(vo.getTaxRate());
            invoicesEntity.setUnitPrice(
                    vo.getTaxPrice()
                            .divide(
                                    (vo.getTaxRate().divide(new BigDecimal(100)).add(new BigDecimal(1))),
                                    4,
                                    BigDecimal.ROUND_HALF_UP));
            invoicesEntity.setUpdateBy(userVO.getUserId());
            invoicesEntity.setUpdateDate(parse);
            invoicesEntity.setIsDelete(false);
            invoicesEntity.setRdRecordDetailId(vo.getRdId());
            invoicesEntity.setRdId(vo.getRdId());
            invoicesEntity.setPoId(vo.getPoId());
            invoicesList.add(invoicesEntity);
            if(StringUtils.isNotBlank(vo.getProductCode())&&inventoryCategoryNewHashMap.containsKey(vo.getProductCode())){

                invoicesEntity.setInventoryCategory(inventoryCategoryNewHashMap.get(vo.getProductCode()).getCode());
            }
        }
        addMain(invoiceHeader,purchaseInvoice, invoicesList);

        purchaseInvoice.setBrandId(invoiceHeader.getBrandId());
        // 结算筛选0蓝字 1红字
        String accountScreen = ChargeEnum.ARAP_TYPE_AR.getCode().equals(invoiceHeader.getInvoiceType()) ? "1" : "0";
        purchaseInvoice.setAccountScreen(accountScreen);
        purBillVouchProduceService.syncSendWdtRecordToMq(purchaseInvoice, invoicesList, settlement.getInvoiceSettlementNo());
    }

    private String getRemark(CfInvoiceSettlement settlement) {
        StringBuilder remark = new StringBuilder();
        if (StringUtils.isNotBlank(settlement.getCustomerInvoiceNo())) {
            remark.append(settlement.getCustomerInvoiceNo()).append(" ");
        }
        if (settlement.getCustomerInvoiceDate() != null) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String format = df.format(settlement.getCustomerInvoiceDate());
            remark.append(format).append(" ");
        }
        if (StringUtils.isNotBlank(settlement.getRemark())) {
            remark.append(settlement.getRemark());
        }
        return remark.toString();
    }

    public void addMain(CfInvoiceHeader invoiceHeader,PurchaseInvoice purchaseInvoice, List<PurchaseInvoiceDetail> purchaseInvoicesList) {

        purchaseInvoiceMapper.insertAuto(purchaseInvoice);
        for (PurchaseInvoiceDetail purchaseInvoices : purchaseInvoicesList) {
            purchaseInvoices.setPurchaseInvoiceId(purchaseInvoice.getPurchaseInvoiceId());
            purchaseInvoiceDetailMapper.insertAuto(purchaseInvoices);
        }
    }

    static String intMax = Integer.MAX_VALUE + "";

    public static int getId(Long input) {
        String id = input + "";
        String idString = id.substring(0,1)+org.apache.commons.lang3.StringUtils.substring(id + "", id.length() - (id.length() - intMax.length()));
        return Integer.parseInt(idString);
    }


    /**
     * 登记人
     *
     * @return
     */
    @Override
    public Response<Object> createNameList() {
        return new Response(ResponseCode.SUCCESS, invoiceHeaderMapper.createNameList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditInvoice(InvoiceSwitchBanBO model, UserVO userVO) {
        CfInvoiceHeader invoiceHeader = invoiceHeaderMapper.selectById(model.getInvoiceId());
        if((model.getInvoiceStatus()==InvoiceStatusEnum.ZF.getCode()||model.getInvoiceStatus()==InvoiceStatusEnum.SC.getCode())&&StringUtils.isNotBlank(invoiceHeader.getAssociatedInvoiceNo())){
            throw new BusinessException(SystemState.BUSINESS_ERROR.code(),"当前结算单已被抵充到蓝字："+invoiceHeader.getAssociatedInvoiceNo()+"中，无法作废");
        }
        Assert.notNull(invoiceHeader, "账单信息不存在");
        Assert.notNull(invoiceHeader.getInvoiceStatus(), "账单状态不存在");
        Assert.notNull(model.getInvoiceStatus(), "要修改的账单状态不存在");

        checkStatus(invoiceHeader.getInvoiceStatus(), model.getInvoiceStatus());
        CfInvoiceHeader update = new CfInvoiceHeader();
        update.setInvoiceStatus(model.getInvoiceStatus());
        update.setInvoiceId(invoiceHeader.getInvoiceId());
        OperateUtil.onUpdate(update, userVO);
        if (InvoiceStatusEnum.TJCW.getCode() == model.getInvoiceStatus()) {
            update.setReviewer(userVO.getRealName());
            update.setReviewerDate(LocalDateTime.now());
        }
        invoiceHeaderMapper.updateById(update);
        // 变更状态后，如果是提交动作，回写到费用，如果是变成草稿状态，修改回写到费用的数据
        updateCharge(model.getInvoiceId());
        if((model.getInvoiceStatus()==InvoiceStatusEnum.ZF.getCode()||model.getInvoiceStatus()==InvoiceStatusEnum.SC.getCode())){
            Assert.isNull(invoiceHeader.getAssociatedInvoiceNo(),"当前红字账单已关联蓝字账单："+invoiceHeader.getAssociatedInvoiceNo()+" 不允许作废");
            invoiceHeaderMapper.unAssociated(invoiceHeader.getInvoiceNo(),null);
        }
    }

    private void updateCharge(Long invoiceId) {
        CfInvoiceHeader inv = invoiceHeaderMapper.selectById(invoiceId);
        List<Long> collect = invoiceDetailMapper.selectChargeIdsByInvoiceId(invoiceId);
        if (inv.getInvoiceStatus() == InvoiceStatusEnum.SC.getCode()) {
            //归还采购单下的预付款
            updatePrePayByInvoiceNo(inv, false,false);
            // 删除手动调整过的费用
            List<CfCharge> existOffsetFee = getExistOffsetFee(inv);
            // 删除原来调整的费用
            delExistFee(existOffsetFee, inv);
            inv.setInvoiceNo(null);
            inv.setInvoiceTitle(null);
            inv.setInvoiceTitleName(null);
            inv.setInvoiceDate(null);
            if (CollectionUtils.isNotEmpty(collect)) {
                chargeMapper.updateInvoiceInfo(inv, collect);
            }
        }
    }

    private void setTotal(CfInvoiceHeader cfInvoiceHeader, List<ChargeInVO> chargeInList) {
        for (ChargeInVO  c: chargeInList) {
            if(Objects.nonNull(c.getTaxRate())&&BigDecimal.ZERO.compareTo(c.getTaxRate())!=0){
                BigDecimal taxRate= c.getTaxRate().divide(new BigDecimal(100),4, BigDecimal.ROUND_HALF_UP);
                c.setTaxMoney(c.getSettlementAmount().divide(BigDecimal.ONE.add(taxRate),8, BigDecimal.ROUND_HALF_UP).multiply(taxRate).setScale(2,BigDecimal.ROUND_HALF_UP));
                c.setUnitAmount(c.getSettlementAmount().subtract(c.getTaxMoney()));
            }else {
                c.setTaxMoney(BigDecimal.ZERO);
                c.setUnitAmount(c.getSettlementAmount());
            }
            BigDecimal otherMoney = cfInvoiceSettlementService.getTotalSumOtherMoney(c, BigDecimal.ONE);
            c.setActualAmount(c.getSettlementAmount().subtract(otherMoney));
        }
        Integer arrivalQty = chargeInList.stream().map(ChargeInVO::getArrivalQty).reduce(Integer::sum).orElse(0);
        Integer rejectionQty = chargeInList.stream().map(ChargeInVO::getRejectionQty).reduce(Integer::sum).orElse(0);
        Integer actualQty = chargeInList.stream().map(ChargeInVO::getActualQty).reduce(Integer::sum).orElse(0);
        Integer defectiveRejectionQty = chargeInList.stream().map(ChargeInVO::getDefectiveRejectionQty).reduce(Integer::sum).orElse(0);
        Integer settlementQty = chargeInList.stream().map(ChargeInVO::getSettlementQty).filter(Objects::nonNull).reduce(Integer::sum).orElse(0);
        BigDecimal settlementAmount = chargeInList.stream().map(ChargeInVO::getSettlementAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal actualAmount = chargeInList.stream().map(ChargeInVO::getActualAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal postponeDeductionsTotal = chargeInList.stream().map(ChargeInVO::getPostponeDeductionsTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal getAdvancepayAmount = chargeInList.stream().map(ChargeInVO::getAdvancepayAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal getAdvancepayAmountActual = chargeInList.stream().map(ChargeInVO::getAdvancepayAmountActual).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal qaDeductions=chargeInList.stream().map(ChargeInVO::getQaDeductions).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal redDeductions=chargeInList.stream().map(ChargeInVO::getRedDeductions).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal taxMoney=chargeInList.stream().map(ChargeInVO::getTaxMoney).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal unitMoney=chargeInList.stream().map(ChargeInVO::getUnitAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
        ChargeInVO oldChargeIn = new ChargeInVO();
        oldChargeIn.setProductCode("原金额");
        oldChargeIn.setArrivalQty(arrivalQty);
        oldChargeIn.setRejectionQty(rejectionQty);
        oldChargeIn.setActualQty(actualQty);
        oldChargeIn.setDefectiveRejectionQty(defectiveRejectionQty);
        oldChargeIn.setSettlementQty(settlementQty);
        oldChargeIn.setSettlementAmount(settlementAmount);
        oldChargeIn.setActualAmount(actualAmount);
        oldChargeIn.setPostponeDeductionsTotal(postponeDeductionsTotal);
        oldChargeIn.setAdvancepayAmount(getAdvancepayAmount);
        oldChargeIn.setAdvancepayAmountActual(getAdvancepayAmountActual);
        oldChargeIn.setQaDeductions(qaDeductions);
        oldChargeIn.setTaxMoney(taxMoney);
        oldChargeIn.setUnitAmount(unitMoney);
        chargeInList.add(oldChargeIn);

        ChargeInVO totalChargeIn = new ChargeInVO();
        totalChargeIn.setProductCode("合计");
        totalChargeIn.setArrivalQty(arrivalQty);
        totalChargeIn.setRejectionQty(rejectionQty);
        totalChargeIn.setActualQty(actualQty);
        totalChargeIn.setDefectiveRejectionQty(defectiveRejectionQty);
        totalChargeIn.setSettlementQty(settlementQty);
        totalChargeIn.setSettlementAmount(settlementAmount);
        totalChargeIn.setPostponeDeductionsTotal(cfInvoiceHeader.getAdjustDelayMoney() == null ? postponeDeductionsTotal : cfInvoiceHeader.getAdjustDelayMoney());
        totalChargeIn.setActualAmount(cfInvoiceHeader.getAdjustRealMoney() != null ? cfInvoiceHeader.getAdjustRealMoney() : actualAmount);
        //     * 延期扣款总金额 postponeDeductionsTotal
        //     质检扣款 qaDeductions
        //  红字扣款  redDeductions
        //  税差  taxDiff
        //    其他扣费  othersDeductions
        totalChargeIn.setQaDeductions(cfInvoiceHeader.getAdjustQcMoney()!=null?cfInvoiceHeader.getAdjustQcMoney(): qaDeductions);
        totalChargeIn.setRedDeductions(cfInvoiceHeader.getAdjustRedMoney()!=null? cfInvoiceHeader.getAdjustRedMoney() :redDeductions);
        totalChargeIn.setTaxDiff(cfInvoiceHeader.getAdjustTaxMoney());
        totalChargeIn.setOthersDeductions(cfInvoiceHeader.getAdjustOtherMoney());
        totalChargeIn.setAdvancepayAmount(getAdvancepayAmount);
        totalChargeIn.setAdvancepayAmountActual(getAdvancepayAmountActual);
        totalChargeIn.setTaxMoney(taxMoney);
        totalChargeIn.setUnitAmount(unitMoney);
        // 设置默认值
        for (ChargeInVO chargeInVO : chargeInList) {
            setDefaultMoney(chargeInVO);
            if(Objects.nonNull(chargeInVO.getUnitPrice())&&Objects.nonNull(chargeInVO.getActualQty())){
                chargeInVO.setUnitAmount(chargeInVO.getUnitPrice().multiply(new BigDecimal(chargeInVO.getActualQty())));
            }
        }
        setDefaultMoney(totalChargeIn);
        BigDecimal otherMoney = cfInvoiceSettlementService.getTotalSumOtherMoney(totalChargeIn, BigDecimal.ONE);
        totalChargeIn.setActualAmount(totalChargeIn.getSettlementAmount().subtract(otherMoney));
        ChargeInVO adjustChargeIn = new ChargeInVO();
        adjustChargeIn.setProductCode("调整金额");
        adjustChargeIn.setPostponeDeductionsTotal(totalChargeIn.getPostponeDeductionsTotal().subtract(oldChargeIn.getPostponeDeductionsTotal()));
        adjustChargeIn.setQaDeductions(totalChargeIn.getQaDeductions().subtract(oldChargeIn.getQaDeductions()));
        adjustChargeIn.setRedDeductions(totalChargeIn.getRedDeductions().subtract(oldChargeIn.getRedDeductions()));
        adjustChargeIn.setTaxDiff(totalChargeIn.getTaxDiff().subtract(oldChargeIn.getTaxDiff()));
        adjustChargeIn.setOthersDeductions(totalChargeIn.getOthersDeductions().subtract(oldChargeIn.getOthersDeductions()));
        adjustChargeIn.setActualAmount(oldChargeIn.getActualAmount().subtract(totalChargeIn.getActualAmount()));
        adjustChargeIn.setUnitAmount(BigDecimal.ZERO);
        adjustChargeIn.setTaxMoney(BigDecimal.ZERO);
        chargeInList.add(adjustChargeIn);
        chargeInList.add(totalChargeIn);
    }

    private void setDefaultMoney(ChargeInVO d) {
        //延期扣款金额/元	质检扣费/元	红字扣减/元	税差/元	其它扣费/元 本期账单金额/元
        //     * 延期扣款总金额 postponeDeductionsTotal
        //     质检扣款 qaDeductions
        //  红字扣款  redDeductions
        //  税差  taxDiff
        //    其他扣费  othersDeductions
        if (d.getPostponeDeductionsTotal() == null) {
            d.setPostponeDeductionsTotal(BigDecimal.ZERO);
        }
        if (d.getQaDeductions() == null) {
            d.setQaDeductions(BigDecimal.ZERO);
        }
        if (d.getRedDeductions() == null) {
            d.setRedDeductions(BigDecimal.ZERO);
        }
        if (d.getTaxDiff() == null) {
            d.setTaxDiff(BigDecimal.ZERO);
        }
        if (d.getOthersDeductions() == null) {
            d.setOthersDeductions(BigDecimal.ZERO);
        }
        if (d.getActualAmount() == null) {
            d.setActualAmount(BigDecimal.ZERO);
        }

    }


    /**
     * 一键核销
     *
     * @param invoiceId    invoiceId
     * @param settlementId
     * @param userVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<Object> createBankAndCash(Long invoiceId, Long settlementId, UserVO userVO) {
        CfInvoiceHeaderDetailVO detail = detail(invoiceId,false);
        Assert.isTrue(detail.getClearStatus() != 2, "账单已被全部核销");
        CFRequestHolder.setStringThreadLocal(detail.getInvoiceNo());
        // 单号+money
        StringBuilder sb = new StringBuilder();
        BigDecimal advancePayMoney = BigDecimal.ZERO;

        CfInvoiceHeaderDetailAndBankVO vo = new CfInvoiceHeaderDetailAndBankVO();
        BeanUtils.copyProperties(detail, vo);

        //第一次结算 预付款做核销
        boolean first = cfInvoiceSettlementService.checkFirstSettlement(invoiceId, settlementId);
        if (first) {
            advancePayMoney = getPrePayMoneyByInvoiceNo(detail.getInvoiceNo(), sb);
        }
        vo.setAdvancePayNos(sb.toString());
        // 获取结算金额
        if(ChargeEnum.ARAP_TYPE_AR.getCode().equals(detail.getInvoiceType())){
            vo.setPoMoney(getSettlementMoney(settlementId));
            vo.setAdvancePayMoney(advancePayMoney);
            vo.setNeedPayMoney(vo.getPoMoney());
        }else {
            vo.setPoMoney(advancePayMoney.add(getSettlementMoney(settlementId)));
            vo.setAdvancePayMoney(advancePayMoney);
            vo.setNeedPayMoney(vo.getPoMoney().subtract(vo.getAdvancePayMoney()));
        }


        // 核销
        clearInvoice(vo, userVO, settlementId);

        //推送U8
        invoiceHeaderService.invoiceVerify(invoiceId, settlementId, userVO);

        //推送U8修改账单状态
        cfInvoiceSettlementService.updateInvoiceAndSettlementStatus(settlementId, invoiceId, userVO);
        return new Response<>(ResponseCode.SUCCESS);
    }

    @Override
    public BigDecimal getPrePayMoneyByInvoiceNo(String invoiceNo, StringBuilder sb) {
        List<CfCharge> allChargeSourceCode = getAllChargeSourceCode(invoiceNo);
        List<Long> details = allChargeSourceCode.stream().filter((a) -> a.getChargeSourceDetailId() != null&&Objects.equals(a.getChargeType(),CfFinanceConstant.CHARGE_SOURCE_FOR_PO )).map(CfCharge::getChargeSourceDetailId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(details)){
            return BigDecimal.ZERO;
        }
        //Assert.isTrue(details.size() > 0, "入库明细数据不存在");
        List<CfRdRecordDetail> allRdRecordDetail = getAllRdRecordDetailByDetailsId(details);
        List<Long> poIds = allRdRecordDetail.stream().map(CfRdRecordDetail::getPoId).filter(Objects::nonNull).collect(Collectors.toList());
        if (poIds.size() > 0) {
            // 查询审核状态为已打款的预付款申请单集合
            List<AdvancepayApplication> applicationList = getAdvancepayListByPoIds(poIds, invoiceNo);
            for (AdvancepayApplication s : applicationList) {
                sb.append(s.getAdvancePayCode()).append(",");
            }
            // 预付款实付金额（预付款申请单 已打款金额和）
            return applicationList.stream().map(AdvancepayApplication::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public List<CfPoDetail> getAllPoDetails(String invoiceNo) {
        List<CfCharge> allChargeSourceCode = getAllChargeSourceCode(invoiceNo);
        List<Long> details = allChargeSourceCode.stream().filter((a) -> a.getChargeSourceDetailId() != null).map(CfCharge::getChargeSourceDetailId).collect(Collectors.toList());
        Assert.isTrue(details.size() > 0, "入库明细数据不存在");
        List<CfRdRecordDetail> allRdRecordDetail = getAllRdRecordDetailByDetailsId(details);
        List<Long> ids = allRdRecordDetail.stream().map(CfRdRecordDetail::getPoDetailId).filter(Objects::nonNull).collect(Collectors.toList());
        List<CfPoDetail> allPoDetails = getAllPoDetails(ids);
        return allPoDetails;
    }

    private void clearInvoice(CfInvoiceHeaderDetailAndBankVO vo, UserVO userVO, Long settlementId) {
        CfBankAndCashInvoiceExtend apVo = new CfBankAndCashInvoiceExtend();
        BeanUtils.copyProperties(vo, apVo);
        apVo.setInvoiceSettlementId(settlementId);
        apVo.setAmount(vo.getNeedPayMoney());
        apVo.setRecordType("1");
        apVo.setArapDate(new Date());
        apVo.setBrandId(vo.getBrandId().intValue());
        apVo.setBalance(vo.getBalance());
        Response<VendorResModel> vendorByCode = vendorCenterServer.getVendorByCode(null, vo.getBalance());
        log.info(JSONObject.toJSONString(vendorByCode));
        Response<VendorResModel> info = vendorCenterServer.getInfo(vendorByCode.getObj().getVendorId());
        log.info(JSONObject.toJSONString(info));
        Assert.isTrue(StringUtils.isNotBlank(info.getObj().getVendorLetterhead()), "供应商收款单位不能为空");
        apVo.setCollectionUnit(info.getObj().getVendorLetterhead());
        apVo.setBank(vo.getBank());
        apVo.setBankNo(vo.getBankAccounts());
        apVo.setRecordUser(userVO.getRealName());
        if (apVo.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            apVo.setArapType(BankAndCashBusinessEnum.ARAP_TYPE_CREDIT.getCode());
        } else {
            apVo.setArapType(BankAndCashBusinessEnum.ARAP_TYPE_DEBIT.getCode());
        }
        log.info("userVo");
        log.info(JSONObject.toJSONString(apVo));
        actualPaymentService.createBankCashAndClear(apVo, userVO);
    }

    @Override
    public boolean settlementCheck(Long invoiceId, BigDecimal amount) {
        CfInvoiceHeader cfInvoiceHeader = invoiceHeaderMapper.selectById(invoiceId);
        LambdaQueryWrapper<CfInvoiceSettlement> lambdaQueryWrapper = Wrappers.lambdaQuery(CfInvoiceSettlement.class);
        lambdaQueryWrapper.eq(CfInvoiceSettlement::getInvoiceId, invoiceId).notIn(CfInvoiceSettlement::getInvoiceSettlementStatus, 0, 8);
        List<CfInvoiceSettlement> selectList = invoiceSettlementMapper.selectList(lambdaQueryWrapper);
        boolean result = true;
        if (null != selectList && selectList.size() > 0) {
            BigDecimal settlementAmount = selectList.stream().filter(x -> x.getClearNo() != null).map(CfInvoiceSettlement::getInvoiceSettlementMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
            //帐单收付类型(应收应付类型(AR=收debit, AP=付credit))
            if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(cfInvoiceHeader.getInvoiceType())) {
                result = amount.compareTo(cfInvoiceHeader.getInvoicelDebit().subtract(settlementAmount)) < 0;
            } else if (ChargeEnum.ARAP_TYPE_AP.getCode().equals(cfInvoiceHeader.getInvoiceType())) {
                result = amount.compareTo(cfInvoiceHeader.getInvoicelCredit().subtract(settlementAmount)) < 0;
            }
        }
        return result;
    }

    @Override
    public void updateInvoiceStatus(Long invoiceId, UserVO userVO,CfInvoiceSettlement cfInvoiceSettlement) {
        List<CfInvoiceSettlement> selectList = getAllAvailableSettlement(invoiceId);
        //作废该笔账单下最后一张结算单时把账单状态修改为待结算
        CfInvoiceHeader update = new CfInvoiceHeader();
        update.setInvoiceId(invoiceId);
        BigDecimal allRate = selectList.stream().map(CfInvoiceSettlement::getInvoiceSettlementRate).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (allRate.compareTo(BigDecimal.ZERO) == 0) {
            update.setInvoiceStatus(InvoiceStatusEnum.DJS.getCode());
        } else if (allRate.compareTo(BigDecimal.ONE) < 0) {
            update.setInvoiceStatus(InvoiceStatusEnum.BJS.getCode());
        } else if (allRate.compareTo(BigDecimal.ONE) == 0) {
            update.setInvoiceStatus(InvoiceStatusEnum.QJS.getCode());
            BigDecimal reduce = selectList.stream().filter(a -> a.getInvoiceSettlementStatus().equals(SettlementStatusEnum.YFK.getCode())).map(CfInvoiceSettlement::getInvoiceSettlementRate).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (reduce.compareTo(BigDecimal.ONE) == 0) {
                update.setInvoiceStatus(InvoiceStatusEnum.QBFK.getCode());
            }
        }
        if(Objects.nonNull(cfInvoiceSettlement)){
            update.setBank(cfInvoiceSettlement.getBank());
            update.setBankAccounts(cfInvoiceSettlement.getBankAccounts());
            update.setInvoiceTitle(cfInvoiceSettlement.getAccname());
            update.setInvoiceTitleName(cfInvoiceSettlement.getVendorLetterHead());
        }
        OperateUtil.onUpdate(update, userVO);
        invoiceHeaderMapper.updateById(update);
    }

    private List<CfInvoiceSettlement> getAllAvailableSettlement(Long invoiceId) {
        LambdaQueryWrapper<CfInvoiceSettlement> lambdaQueryWrapper = Wrappers.lambdaQuery(CfInvoiceSettlement.class);
        lambdaQueryWrapper.eq(CfInvoiceSettlement::getInvoiceId, invoiceId).notIn(CfInvoiceSettlement::getInvoiceSettlementStatus,
                SettlementStatusEnum.SC.getCode(), SettlementStatusEnum.ZF.getCode());
        return invoiceSettlementMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 调整延期扣款金额
     * 手动调整 延期扣款、质检扣款、红字扣减、税差、其他费用功能
     *
     * @param cfInvoiceUpdateMoneyDto
     * @param userVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public  Response<Object> updateInvoiceDelayMoney(CfInvoiceUpdateMoneyDto cfInvoiceUpdateMoneyDto, UserVO userVO) {
        //清楚历史的记录
        invoiceHeaderMapper.unAssociated(cfInvoiceUpdateMoneyDto.getInvoiceNo(),null);
        if(CollectionUtils.isNotEmpty(cfInvoiceUpdateMoneyDto.getInvoiceIdsOfAssociated())){
            List<Long> invoiceIdsOfAssociated = new ArrayList<>(cfInvoiceUpdateMoneyDto.getInvoiceIdsOfAssociated());
            List<CfInvoiceHeader> cfInvoiceHeaders = invoiceHeaderMapper.selectList(Wrappers.<CfInvoiceHeader>lambdaQuery().in(CfInvoiceHeader::getInvoiceId, cfInvoiceUpdateMoneyDto.getInvoiceIdsOfAssociated()).isNull(CfInvoiceHeader::getAssociatedInvoiceNo));
            Iterator<Long> iterator = invoiceIdsOfAssociated.iterator();
            while (iterator.hasNext()) {
                Long invoiceId = iterator.next();
                //当前存在此账单未被引用
                if(cfInvoiceHeaders.stream().filter(x->Objects.equals(x.getInvoiceId(),invoiceId)).findFirst().isPresent()){
                    iterator.remove();
                }
            }
            if(CollectionUtils.isNotEmpty(invoiceIdsOfAssociated)){
                List<String> usedNos = invoiceHeaderMapper.selectList(Wrappers.<CfInvoiceHeader>lambdaQuery().in(CfInvoiceHeader::getInvoiceId, invoiceIdsOfAssociated)).stream().map(CfInvoiceHeader::getInvoiceNo).collect(Collectors.toList());
                throw new BusinessException(SystemState.BUSINESS_ERROR.code(),"当前引用的红字账单:"+usedNos+"不存在，或者已被其他账单引用");
            }
            if(CollectionUtils.isNotEmpty(cfInvoiceHeaders)){
                BigDecimal redRealMoney = this.getRedRealMoney(BeanUtilCopy.copyPropertiesIgnoreType(cfInvoiceUpdateMoneyDto, CalculateRedRealMoneyDto.class)).getObj();
                if(Objects.isNull(cfInvoiceUpdateMoneyDto.getAdjustRedMoney())||redRealMoney.compareTo(cfInvoiceUpdateMoneyDto.getAdjustRedMoney())!=0){
                    throw new BusinessException(SystemState.BUSINESS_ERROR.code(),"抵扣金额："+redRealMoney+"与页面调整金额:"+cfInvoiceUpdateMoneyDto.getAdjustRedMoney()+"不符，请联系开发查看！！！");
                }
                if(cfInvoiceHeaders.stream().filter(x->StringUtils.isNotBlank(x.getAssociatedInvoiceNo())&&!Objects.equals(x.getAssociatedInvoiceNo(),cfInvoiceUpdateMoneyDto.getInvoiceNo())).findFirst().isPresent()){
                    throw new BusinessException(SystemState.BUSINESS_ERROR.code(),"当前引用的红字账单已经被其他蓝字账单抵扣");
                }
                CfInvoiceHeader header = new CfInvoiceHeader();
                header.setAssociatedInvoiceNo(cfInvoiceUpdateMoneyDto.getInvoiceNo());
                invoiceHeaderMapper.update(header,Wrappers.<CfInvoiceHeader>lambdaQuery().in(CfInvoiceHeader::getInvoiceId,cfInvoiceHeaders.stream().map(x->x.getInvoiceId()).collect(Collectors.toList())));
            }
        }
        CfInvoiceHeader cfInvoiceHeader = BeanUtilCopy.copyPropertiesIgnoreType(cfInvoiceUpdateMoneyDto, CfInvoiceHeader.class);
        // 修改账单 金额
        invoiceHeaderMapper.updateById(cfInvoiceHeader);
        List<CfCharge> existOffsetFee = getExistOffsetFee(cfInvoiceHeader);
        Integer salesType = cfChargeMapper.selectList(Wrappers.lambdaQuery(CfCharge.class)
                .eq(CfCharge::getIsOffset, 0)
                .eq(CfCharge::getInvoiceNo, cfInvoiceHeader.getInvoiceNo())).get(0).getSalesType();
        // 删除原来调整的费用
        delExistFee(existOffsetFee, cfInvoiceHeader);
        // 调用账单详情 获取调整的金额
        CfInvoiceHeaderDetailVO detail = detail(cfInvoiceHeader.getInvoiceId(),false);
        Map<String, String> chargeType = cfChargeService.getDicts("charge_type", detail.getChargeList());

        log.info("数据字典  {}", JSONObject.toJSONString(chargeType));
        ChargeInVO tz = getTz(detail.getChargeInList());
        List<CfCharge> charges = new ArrayList<>(5);

        if (cfInvoiceHeader.getAdjustDelayMoney() != null) {
            String feeTye = getFeeType(chargeType, "延期");
            Assert.notNull(feeTye, "延期费用 数据字典未配置");
            CfCharge delayFee = createCharge(salesType,detail, feeTye, tz.getPostponeDeductionsTotal(), userVO, chargeType, existOffsetFee);
            charges.add(delayFee);
        }
        if (cfInvoiceHeader.getAdjustQcMoney() != null) {
            String feeTye = getFeeType(chargeType, "质检");
            Assert.notNull(feeTye, "质检扣款费 数据字典未配置");
            CfCharge delayFee = createCharge(salesType,detail, feeTye, tz.getQaDeductions(), userVO, chargeType, existOffsetFee);
            charges.add(delayFee);
        }

        if (cfInvoiceHeader.getAdjustOtherMoney() != null) {
            String feeTye = getFeeType(chargeType, "其它");
            Assert.notNull(feeTye, "其他扣款 数据字典未配置");
            CfCharge delayFee = createCharge(salesType,detail, feeTye, tz.getOthersDeductions(), userVO, chargeType, existOffsetFee);
            charges.add(delayFee);
        }
        if (cfInvoiceHeader.getAdjustRedMoney() != null) {
            String feeTye = getFeeType(chargeType, "退次", "红字");
            Assert.notNull(feeTye, "退次/红字货款费 数据字典未配置");
            CfCharge delayFee = createCharge(salesType,detail, feeTye, tz.getRedDeductions(), userVO, chargeType, existOffsetFee);
            charges.add(delayFee);
        }

        if (cfInvoiceHeader.getAdjustTaxMoney() != null) {
            String feeTye = getFeeType(chargeType, "税差");
            Assert.notNull(feeTye, "税差扣款费 数据字典未配置");
            CfCharge delayFee = createCharge(salesType,detail, feeTye, tz.getTaxDiff(), userVO, chargeType, existOffsetFee);
            charges.add(delayFee);
        }
        // 把费用添加到账单中
        saveChargeToInvoice(cfInvoiceHeader, charges);

        // 如果账单状态是业务待审,修改账单状态为待结算，如果账单状态时草稿，不修改账单状态
        CfInvoiceHeader invoiceHeader = invoiceHeaderMapper.selectById(cfInvoiceHeader.getInvoiceId());
        if(!invoiceHeader.getInvoiceStatus().equals(InvoiceStatusEnum.CG.getCode())){
            InvoiceSwitchBanBO model = new InvoiceSwitchBanBO();
            model.setInvoiceId(cfInvoiceHeader.getInvoiceId());
            model.setInvoiceStatus(InvoiceStatusEnum.TJCW.getCode());
            auditInvoice(model, userVO);
        }

        // 重新计算
        fixAdjustInvoice(cfInvoiceHeader.getInvoiceNo());
        return new Response<>(ResponseCode.SUCCESS);
    }


    private String getFeeType(Map<String, String> chargeType, String... s) {
        AtomicReference<String> code = new AtomicReference<>();
        chargeType.forEach((k, v) -> {
            for (String s1 : s) {
                if (v.contains(s1)) {
                    code.set(k);
                }
            }
        });
        return code.get();
    }

    private void delExistFee(List<CfCharge> existOffsetFee, CfInvoiceHeader inv) {
        List<CfInvoiceSettlement> allSettlement = getAllSettlement(inv.getInvoiceId());
        for (CfInvoiceSettlement settlement : allSettlement) {
            Assert.isTrue(StringUtils.isBlank(settlement.getCustomerInvoiceNo()), "无法删除调整费用，此账单已经推送u8");
        }
        for (CfCharge cfCharge : existOffsetFee) {
            cfChargeMapper.deleteById(cfCharge.getChargeId());
        }
    }
    @Override
    public List<CfInvoiceSettlement> getAllSettlement(Long invoiceId) {
        LambdaQueryWrapper<CfInvoiceSettlement> lambdaQueryWrapper = Wrappers.lambdaQuery(CfInvoiceSettlement.class);
        lambdaQueryWrapper.eq(CfInvoiceSettlement::getInvoiceId, invoiceId).notIn(CfInvoiceSettlement::getInvoiceSettlementStatus, 0);
        lambdaQueryWrapper.orderByDesc(CfInvoiceSettlement::getInvoiceSettlementId);
        return cfInvoiceSettlementMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 回调待办/通知消息
     *
     * @param cfInvoiceHeader
     * @param approvalFlowDTO
     * @param status
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalCallback(CfInvoiceHeader cfInvoiceHeader, ApprovalFlowDTO approvalFlowDTO, Boolean status) {
        cfInvoiceHeader = invoiceHeaderMapper.selectById(cfInvoiceHeader.getInvoiceId());
        approvalFlowDTO.setSrcCode(cfInvoiceHeader.getInvoiceNo());
        approvalFlowDTO.setProcessId(ApprovalEnum.INVOICE_APPROVAL.getProcessId());
        if (Objects.nonNull(status)) {
            //审批通过（流程结束）或审批被拒绝发给创建人
            approvalFlowService.sendNotify(approvalFlowDTO, Long.valueOf(cfInvoiceHeader.getInvoiceId()),
                                           cfInvoiceHeader.getInvoiceNo()
                    , ApprovalEnum.INVOICE_APPROVAL, status,
                                           cfInvoiceHeader.getCreateBy(),
                                           cfInvoiceHeader.getCreateName());
        } else {
            for (int i = 0; i < approvalFlowDTO.getTargetUserId().size(); i++) {
                approvalFlowService.sendNotify(approvalFlowDTO, Long.valueOf(cfInvoiceHeader.getInvoiceId()),
                                               cfInvoiceHeader.getInvoiceNo()
                        , ApprovalEnum.INVOICE_APPROVAL, status,
                                               Long.parseLong(approvalFlowDTO.getTargetUserId().get(i)),
                                               approvalFlowDTO.getTargetUserName().get(i));
            }
        }
    }

    @Override
    public Response<BigDecimal> getRedRealMoney(CalculateRedRealMoneyDto calculateRedRealMoneyDto) {
        if(CollectionUtils.isEmpty(calculateRedRealMoneyDto.getInvoiceIdsOfAssociated())){
            calculateRedRealMoneyDto.setInvoiceIdsOfAssociated(Arrays.asList(-1L));
        }
        CfInvoiceHeader cfInvoiceHeader = invoiceHeaderMapper.selectById(calculateRedRealMoneyDto.getInvoiceId());
        //历史已经关联的红字结算单
        //List<CfInvoiceHeader> cfInvoiceHeaders = invoiceHeaderMapper.selectList(Wrappers.<CfInvoiceHeader>lambdaQuery().eq(CfInvoiceHeader::getAssociatedInvoiceNo, cfInvoiceHeader.getInvoiceNo()));
        //准备关联的红字结算单
        List<CfInvoiceHeader> cfInvoiceHeadersOfNew = invoiceHeaderMapper.selectBatchIds(calculateRedRealMoneyDto.getInvoiceIdsOfAssociated());

        Optional<CfInvoiceHeader> isUsed = cfInvoiceHeadersOfNew.stream().filter(x -> Objects.nonNull(x.getAssociatedInvoiceNo()) && !Objects.equals(x.getAssociatedInvoiceNo(), cfInvoiceHeader.getInvoiceNo())).findFirst();
        Assert.isTrue(!isUsed.isPresent(),"当前抵扣的红字账单已被其他蓝字账单抵扣");
        //判断当前是否存在部分数据是蓝字账单
        Optional<CfInvoiceHeader> first = cfInvoiceHeadersOfNew.stream().filter(x -> Objects.equals(x.getInvoiceType(), ChargeEnum.ARAP_TYPE_AP.getCode())).findFirst();
        Assert.isTrue(!first.isPresent(),"当前所选的红字抵充账单中含有蓝字账单");
        //判断当前蓝字总金额-红字总金额
        CfInvoiceHeaderDetailVO detail = detail(calculateRedRealMoneyDto.getInvoiceId(), false);
        List<ChargeInVO> chargeInList = detail.getChargeInList();
        BigDecimal sumMoney =BigDecimal.ZERO;
        for (ChargeInVO chargeInVO: chargeInList) {
            if(chargeInVO.getProductCode().contains("原金额")){
                     sumMoney=chargeInVO.getActualAmount();
            }
        }

        cfInvoiceHeadersOfNew=cfInvoiceHeadersOfNew.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(CfInvoiceHeader :: getInvoiceId))), ArrayList::new));
        BigDecimal sumRedMoney=BigDecimal.ZERO;
        for (CfInvoiceHeader ch:cfInvoiceHeadersOfNew) {
            //计算的金额是负数
            sumRedMoney=sumRedMoney.add(ch.getInvoicelCredit().subtract(ch.getInvoicelDebit()));
        }
        Assert.isTrue(BigDecimal.ZERO.compareTo(sumMoney.add(sumRedMoney))<=0,"抵充的红字金额:"+BigDecimal.ZERO.subtract(sumRedMoney)+"大于账单金额:"+sumMoney);
        return Response.success(BigDecimal.ZERO.subtract(sumRedMoney));

    }

    private void getThisTypeFeeAndDel(String feeTye, List<CfCharge> existOffsetFee) {
        List<CfCharge> collect = existOffsetFee.stream().filter((a) -> a.getChargeType().equals(feeTye)).collect(Collectors.toList());
        for (CfCharge cfCharge : collect) {
            cfChargeMapper.deleteById(cfCharge.getChargeId());
        }
    }

    private List<CfCharge> getExistOffsetFee(CfInvoiceHeader inv) {
        // 找到之前生成过费用的调整费用
        return cfChargeMapper.selectList(Wrappers.lambdaQuery(CfCharge.class)
                .eq(CfCharge::getIsOffset, 1)
                .eq(CfCharge::getInvoiceNo, inv.getInvoiceNo()));
    }

    private void saveChargeToInvoice(CfInvoiceHeader inv, List<CfCharge> charges) {

        charges = charges.stream().filter(Objects::nonNull).collect(Collectors.toList());

        CfInvoiceHeader db = invoiceHeaderMapper.selectById(inv.getInvoiceId());
        List<CfInvoiceDetail> invoiceDetailList = new ArrayList<>(charges.size());

        for (CfCharge charge : charges) {
            CfInvoiceDetail t = new CfInvoiceDetail();
            BeanUtilCopy.copyProperties(charge, t);
            t.setInvoiceQty(charge.getChargeQty());
            if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(charge.getArapType())) {
                t.setInvoiceDebit(charge.getAmountPp());
                t.setInvoiceCredit(BigDecimal.ZERO);
            } else {
                t.setInvoiceCredit(charge.getAmountPp());
                t.setInvoiceDebit(BigDecimal.ZERO);
            }
            invoiceDetailList.add(t);
        }

        // 保存账单明细数据
        for (CfInvoiceDetail invoiceDetail : invoiceDetailList) {
            invoiceDetail.setInvoiceId(inv.getInvoiceId());
            invoiceDetailMapper.insert(invoiceDetail);
        }

        List<Long> collect1 = invoiceDetailList.stream().map(CfInvoiceDetail::getChargeId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect1)) {
            chargeMapper.updateInvoiceInfo(db, collect1);
        }
    }

    private ChargeInVO getTz(List<ChargeInVO> chargeInList) {
        String t = "调整金额";
        return getChargeInByProduce(chargeInList, t);
    }

    public ChargeInVO getChargeInByProduce(List<ChargeInVO> chargeInList, String produce) {
        for (ChargeInVO chargeInVO : chargeInList) {
            if (produce.equals(chargeInVO.getProductCode())) {
                return chargeInVO;
            }
        }
        return null;
    }

    private CfCharge createCharge(Integer salesType,CfInvoiceHeaderDetailVO detail, String type, BigDecimal adjustDelayMoney, UserVO userVO, Map<String, String> stringMap, List<CfCharge> existFee) {
        if (adjustDelayMoney.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        CfCharge saveQuery = new CfCharge();
        String res = pageInfoUtil.generateBusinessNum(BillNoConstantClassField.CRG);
        saveQuery.setChargeCode(res);
        saveQuery.setChargeUnit("件");
        saveQuery.setChargeType(type);
        saveQuery.setArapType(adjustDelayMoney.compareTo(BigDecimal.ZERO) >= 0 ? ChargeEnum.ARAP_TYPE_AR.getCode() : ChargeEnum.ARAP_TYPE_AP.getCode());
        saveQuery.setBrandId(detail.getBrandId());
        saveQuery.setBalance(detail.getBalance());
        saveQuery.setChargeQty(1);
        if (adjustDelayMoney.compareTo(BigDecimal.ZERO) < 0) {
            adjustDelayMoney = adjustDelayMoney.multiply(new BigDecimal("-1"));
        }
        saveQuery.setPricePp(adjustDelayMoney);
        saveQuery.setAmountPp(adjustDelayMoney);
        saveQuery.setRemark(stringMap.get(type) + " 冲抵费用");
        List<ChargeInvoiceDetailVO> chargeList = detail.getChargeList();
        ChargeInvoiceDetailVO chargeInvoiceDetailVO = chargeList.get(0);
        saveQuery.setChargeMonthBelongTo(chargeInvoiceDetailVO.getChargeMonthBelongTo());
        saveQuery.setChargeSource(CfFinanceConstant.CHARGE_SOURCE_FOR_PO);
        saveQuery.setProductCode(chargeInvoiceDetailVO.getProductCode());

        Map<String, List<CfCharge>> complexMap = existFee.stream().collect(Collectors.groupingBy(CfCharge::getArapType));
        List<CfCharge> charges = complexMap.get(ChargeEnum.ARAP_TYPE_AR.getCode().equals(saveQuery.getArapType()) ? ChargeEnum.ARAP_TYPE_AP.getCode() : ChargeEnum.ARAP_TYPE_AR.getCode());
        if (CollectionUtils.isEmpty(charges)) {
            saveQuery.setChargeSourceCode(chargeInvoiceDetailVO.getProductCode());
        } else {
            saveQuery.setChargeSourceCode(charges.get(0).getChargeSourceCode());
        }

        saveQuery.setSalesType(salesType);
        saveQuery.setTaxRate(BigDecimal.ZERO);
        saveQuery.setIsOffset(1);
        saveQuery.setCheckStatus(3);
        saveQuery.setInvoiceNo(detail.getInvoiceNo());
        saveQuery.setInvoiceTitle(detail.getInvoiceTitle());
        saveQuery.setInvoiceTitleName(detail.getInvoiceTitleName());
        saveQuery.setInvoiceDate(detail.getInvoiceDate());
        OperateUtil.onSave(saveQuery, userVO);
        chargeMapper.insert(saveQuery);
        return saveQuery;
    }


    @Override
    public Response<Object> invoiceExport(CfInvoiceHeaderListDTO dto, UserVO userVO, HttpServletResponse response) {
        //导出不需要分页,根据条件查询，无条件则全量
        List<CfInvoiceHeaderExportVO> exportVoS = new ArrayList<>(16);
        List<CfInvoiceHeaderListVO> list = invoiceHeaderMapper.invoiceHeaderList(dto);
        List<String> vendors = list.stream().map(CfInvoiceHeaderListVO::getBalance).collect(Collectors.toList());
        Map<String, String> stringMap = cfChargeService.getVendorList(vendors);
        Map<String, String> dicts2 = cfChargeService.getDicts("Charge_Source_Type", list);
        for (int i = 0; i < list.size(); i++) {
            CfInvoiceHeaderExportVO exportVO = new CfInvoiceHeaderExportVO();
            CfInvoiceHeaderListVO listVO = list.get(i);
            exportVO.setInvoiceStatus(listVO.getInvoiceStatus());
            exportVO.setInvoiceNo(listVO.getInvoiceNo());
            exportVO.setInvoiceType(listVO.getInvoiceType());
            exportVO.setCreateBy(listVO.getCreateName());
            exportVO.setSalesType(listVO.getSalesType());
            LocalDateTime createDate = listVO.getCreateDate();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateString = null;
            try {
                dateString = df.format(createDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            exportVO.setCreateDate(dateString);

            LocalDateTime dateStart = listVO.getDateStart();
            try {
                if (null != dateStart) {
                    dateString = df.format(dateStart);
                    exportVO.setDateStart(dateString);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            LocalDateTime dateEnd = listVO.getDateEnd();
            try {
                if (null != dateEnd) {
                    dateString = df.format(dateEnd);
                    exportVO.setDateEnd(dateString);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            exportVO.setRemark(listVO.getRemark());
            exportVO.setInvoiceAmount(listVO.getInvoicelTotal() == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : listVO.getInvoicelTotal().setScale(2, RoundingMode.HALF_UP));

            exportVO.setClearedAmount(listVO.getClearAmount() == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : listVO.getClearAmount().setScale(2, RoundingMode.HALF_UP));
            exportVO.setClearedStatus(listVO.getClearStatus());
            exportVO.setSettleLeftAmount(exportVO.getInvoiceAmount() == null ? BigDecimal.ZERO : exportVO.getInvoiceAmount().subtract(
                    exportVO.getSettleAmount() == null ? BigDecimal.ZERO : exportVO.getSettleAmount()
            ).setScale(2, RoundingMode.HALF_UP));
            exportVO.setClearedLeftAmount(exportVO.getInvoiceAmount() == null ? BigDecimal.ZERO : exportVO.getInvoiceAmount().subtract(
                    exportVO.getClearedAmount() == null ? BigDecimal.ZERO : exportVO.getClearedAmount()
            ).setScale(2, RoundingMode.HALF_UP));
            if (stringMap.containsKey(listVO.getBalance())) {
                listVO.setBalance(stringMap.get(listVO.getBalance()));
                exportVO.setBalance(listVO.getBalance());
            }
            if (dicts2.containsKey(listVO.getJobType())) {
                listVO.setJobType(dicts2.get(listVO.getJobType()));
                exportVO.setJobType(listVO.getJobType());
            }
            //获取结算信息
            LambdaQueryWrapper<CfInvoiceSettlement> lambdaQueryWrapper = Wrappers.lambdaQuery(CfInvoiceSettlement.class);
            lambdaQueryWrapper.eq(CfInvoiceSettlement::getInvoiceId, listVO.getInvoiceId()).notIn(CfInvoiceSettlement::getInvoiceSettlementStatus, 0, 8);
            List<CfInvoiceSettlement> selectList = invoiceSettlementMapper.selectList(lambdaQueryWrapper);
            if (selectList.size() > 0) {
                BigDecimal settlementAmount = selectList.stream().map(CfInvoiceSettlement::getInvoiceSettlementMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
                listVO.setSettlementAmount(settlementAmount);
                exportVO.setSettleAmount(listVO.getSettlementAmount() == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : listVO.getSettlementAmount().setScale(2, RoundingMode.HALF_UP));
                listVO.setAdvancePayMoney(listVO.getAdvancePayMoney() == null ? BigDecimal.ZERO : listVO.getAdvancePayMoney());
                exportVO.setAdvancePayAmount(listVO.getAdvancePayMoney() == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : listVO.getAdvancePayMoney().setScale(2, RoundingMode.HALF_UP));
                //帐单收付类型(应收应付类型(AR=收debit, AP=付credit))
                if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(listVO.getInvoiceType())) {
                    listVO.setBalanceOfStatement(listVO.getInvoicelDebit().subtract(listVO.getInvoicelCredit()).add(settlementAmount).subtract(listVO.getAdvancePayMoney()));
                    exportVO.setSettleLeftAmount(listVO.getBalanceOfStatement() == null ? BigDecimal.ZERO : listVO.getBalanceOfStatement().setScale(2, RoundingMode.HALF_UP));
                } else if (ChargeEnum.ARAP_TYPE_AP.getCode().equals(listVO.getInvoiceType())) {
                    listVO.setBalanceOfStatement(listVO.getInvoicelCredit().subtract(listVO.getInvoicelDebit()).subtract(settlementAmount).subtract(listVO.getAdvancePayMoney()));
                    exportVO.setSettleLeftAmount(listVO.getBalanceOfStatement() == null ? BigDecimal.ZERO : listVO.getBalanceOfStatement().setScale(2, RoundingMode.HALF_UP));
                }
            } else {
                BigDecimal settlementAmount = BigDecimal.ZERO;
                listVO.setSettlementAmount(settlementAmount);
                exportVO.setSettleAmount(listVO.getSettlementAmount() == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : listVO.getSettlementAmount().setScale(2, RoundingMode.HALF_UP));
                exportVO.setAdvancePayAmount(listVO.getAdvancePayMoney() == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : listVO.getAdvancePayMoney().setScale(2, RoundingMode.HALF_UP));
                //帐单收付类型(应收应付类型(AR=收debit, AP=付credit))
                if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(listVO.getInvoiceType())) {
                    listVO.setBalanceOfStatement(listVO.getInvoicelDebit().subtract(listVO.getInvoicelCredit()));
                    exportVO.setSettleLeftAmount(listVO.getBalanceOfStatement() == null ? BigDecimal.ZERO : listVO.getBalanceOfStatement().setScale(2, RoundingMode.HALF_UP));
                } else {
                    listVO.setBalanceOfStatement(listVO.getInvoicelCredit().subtract(listVO.getInvoicelDebit()));
                    exportVO.setSettleLeftAmount(listVO.getBalanceOfStatement() == null ? BigDecimal.ZERO : listVO.getBalanceOfStatement().setScale(2, RoundingMode.HALF_UP));
                }
            }
            exportVoS.add(exportVO);
        }

        //导出
        try {
            FileUtil.exportExcelV2(
                    exportVoS,
                    "财务-账单列表",
                    "导出人：" + userVO.getRealName(),
                    "财务-账单列表",
                    CfInvoiceHeaderExportVO.class,
                    "财务-账单列表.xlsx",
                    response);
            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new Response<>(ResponseCode.FAIL);
        }
    }

    private BigDecimal getSettlementMoney(Long settlementId) {
        CfInvoiceSettlement settlement = invoiceSettlementMapper.selectById(settlementId);
        Assert.notNull(settlement, "未匹配到对应的结算单");
        Assert.isTrue(StringUtils.isBlank(settlement.getClearNo()), "结算单已经被核销");
        return settlement.getInvoiceSettlementMoney();
    }


    /**
     * @param checkStatus 原来状态
     * @param status      要修改的状态
     */
    public void checkStatus(Integer checkStatus, int status) {
        InvoiceStatusEnum in = InvoiceStatusEnum.getMsgByCode(checkStatus);
        InvoiceStatusEnum up = InvoiceStatusEnum.getMsgByCode(status);
        //1草稿、2已提交、3已审核、4已驳回、5已作废、0已删除
        Assert.notNull(in, "当前账单状态不存在");
        Assert.notNull(up, "要修改的状态不存在");
        Assert.isTrue(in != InvoiceStatusEnum.SC, "账单记录已删除");
        Assert.isTrue(in != up, "当前状态已是 " + in.getMsg());

        if (in == InvoiceStatusEnum.CG) {
            doCheckStatus(in, up, nextStatus(InvoiceStatusEnum.YWDS, InvoiceStatusEnum.CG, InvoiceStatusEnum.ZF));
        } else if (in == InvoiceStatusEnum.YWDS) {
            doCheckStatus(in, up, nextStatus(InvoiceStatusEnum.TJCW, InvoiceStatusEnum.CG, InvoiceStatusEnum.ZF));
        } else if (in == InvoiceStatusEnum.TJCW) {
            doCheckStatus(in, up, nextStatus(InvoiceStatusEnum.YWDS, InvoiceStatusEnum.DJS, InvoiceStatusEnum.ZF));
        } else if (in == InvoiceStatusEnum.DJS) {
            doCheckStatus(in, up, nextStatus(InvoiceStatusEnum.ZF, InvoiceStatusEnum.SC));
        } else if (in == InvoiceStatusEnum.ZF) {
            doCheckStatus(in, up, nextStatus(InvoiceStatusEnum.SC));
        } else {
            doCheckStatus(in, up, InvoiceStatusEnum.values());
        }
    }

    private InvoiceStatusEnum[] nextStatus(InvoiceStatusEnum... next) {
        InvoiceStatusEnum[] values = InvoiceStatusEnum.values();
        List<InvoiceStatusEnum> notIn = new ArrayList<>();
        for (InvoiceStatusEnum value : values) {
            boolean exist = false;
            for (InvoiceStatusEnum n : next) {
                if (value == n) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                notIn.add(value);
            }
        }
        return notIn.toArray(new InvoiceStatusEnum[0]);
    }

    private void doCheckStatus(InvoiceStatusEnum in, InvoiceStatusEnum update, InvoiceStatusEnum... not) {
        String ms = " 状态不能变更为 ";
        for (InvoiceStatusEnum chargeCheckStatusEnum : not) {
            Assert.isTrue(update != chargeCheckStatusEnum, in.getMsg() + ms + chargeCheckStatusEnum.getMsg());
        }
    }


    private List<CfCharge> getAllChargeSourceCode(String invoiceNo) {
        return cfChargeMapper.selectList(Wrappers.lambdaQuery(CfCharge.class)
                .in(CfCharge::getInvoiceNo, invoiceNo)
                .notIn(CfCharge::getCheckStatus, 0, 5));
    }

    private List<CfRdRecord> getAllRdRecord(List<String> codes) {
        return cfRdRecordMapper.selectList(Wrappers.lambdaQuery(CfRdRecord.class)
                .in(CfRdRecord::getRdRecordCode, codes)
                .eq(CfRdRecord::getCreateChargeFlag, 1));
    }

    private List<CfRdRecordDetail> getAllRdRecordDetail(List<Long> ids) {
        return cfRdRecordDetailMapper.selectList(Wrappers.lambdaQuery(CfRdRecordDetail.class)
                .in(CfRdRecordDetail::getRdRecordId, ids)
                .eq(CfRdRecordDetail::getCreateChargeFlag, 1));
    }


    private List<CfRdRecordDetail> getAllRdRecordDetail(Set<Long> ids) {
        return cfRdRecordDetailMapper.selectList(Wrappers.lambdaQuery(CfRdRecordDetail.class)
                .in(CfRdRecordDetail::getRdRecordDetailId, ids)
                .eq(CfRdRecordDetail::getCreateChargeFlag, 1));
    }

    private List<CfRdRecordDetail> getAllRdRecordDetailByDetailsId(List<Long> ids) {
        return cfRdRecordDetailMapper.selectList(Wrappers.lambdaQuery(CfRdRecordDetail.class)
                .in(CfRdRecordDetail::getRdRecordDetailId, ids)
                .eq(CfRdRecordDetail::getCreateChargeFlag, 1));
    }

    private List<CfPoHeader> getAllPo(List<Long> ids) {
        if (ids.size() < 1) {
            return new LinkedList<>();
        }
        return cfPoHeaderMapper.selectList(Wrappers.lambdaQuery(CfPoHeader.class)
                .in(CfPoHeader::getPoId, ids)
                .eq(CfPoHeader::getIsDelete, 0));
    }

    private List<CfPoDetail> getAllPoDetails(List<Long> ids) {
        if (ids.size() < 1) {
            return new LinkedList<>();
        }
        return cfPoDetailMapper.selectList(Wrappers.lambdaQuery(CfPoDetail.class)
                .in(CfPoDetail::getPoDetailId, ids)
                .eq(CfPoDetail::getIsDelete, 0));
    }

    /**
     * 移除掉合计等虚拟数据
     * @param chargeInListInput
     * @return
     */
    private List<ChargeInVO> getNewChargeInvoOfRemoveVirtual(List<ChargeInVO> chargeInListInput) {
        List<ChargeInVO> charges = new ArrayList<>();
        for (ChargeInVO no : chargeInListInput) {
            if ("原金额".equals(no.getProductCode()) || "调整金额".equals(no.getProductCode()) || "合计".equals(no.getProductCode())) {
                continue;
            }
            charges.add(no);
        }
        return charges;
    }

    /**
     * 根据总结算数量计算当前入库单该结算的数量
     * @param  allCharge 当前结算单关联账单的所有的费用
     * @param productCode spu
     * @param costsPrice 单价
     * @param taxRate 汇率
     * @param settlementQty 结算数量
     * @param allRdRecordDetail 账单下所有的入库单详情
     * @param allRdRecord 账单下所有的入库单
     * @return
     */
    private List<PriceDetailInfo> assignTheBillingQuantityOfEachBill(List<CfCharge> allCharge,String productCode, BigDecimal costsPrice, BigDecimal taxRate, Integer settlementQty, List<CfRdRecordDetail> allRdRecordDetail, List<CfRdRecord> allRdRecord){
        Integer settlementQtyOld=settlementQty;

        List<PriceDetailInfo> priceDetailInfos = new ArrayList<>();
        //获取当前sku的所有关联的入库单 退次和入库
        List<CfRdRecordDetail> matchRds = allRdRecordDetail.stream().filter((a) -> a.getProductCode().equals(productCode)
                && (a.getTaxUnitPrice().setScale(2,BigDecimal.ROUND_HALF_UP).compareTo(costsPrice) == 0||a.getTaxUnitPrice().compareTo(costsPrice) == 0)
                && a.getTaxRate().compareTo(taxRate) == 0).collect(Collectors.toList());
        List<CfQcRecordAsnDetail> cfQcRecordAsnDetails = cfQcRecordAsnDetailMapper.selectList(Wrappers.<CfQcRecordAsnDetail>lambdaQuery().in(CfQcRecordAsnDetail::getRdRecordDetailId, matchRds.stream().map(x -> x.getRdRecordDetailId()).collect(Collectors.toSet())));
        if (settlementQty == 0) {
            // 只有一个，推送0
            if (matchRds.size() == 1 && matchRds.get(0).getQuantity() == 1) {
                return new LinkedList<>();
            }
        }
        if (settlementQty < 0) {
            LinkedList<CfRdRecordDetail> newList = new LinkedList<>();
            for (CfRdRecordDetail d : matchRds) {
                if (d.getRdRecordDetailId() == 0) {
                    continue;
                }
                Optional<CfRdRecord> first = allRdRecord.stream().filter((a) -> a.getRdRecordId().equals(d.getRdRecordId())).findFirst();
                Assert.isTrue(first.isPresent(), "入库单明细 没匹配到，detail id :" + d.getRdRecordDetailId());
                CfRdRecord rdRecord = first.get();
                d.setRdRecord(rdRecord);
                if (rdRecord.getBredVouch() == 1) {
                        //采购拍第一
                        newList.addFirst(d);
                } else {
                        //退次按顺序
                        newList.add(d);
                }
            }
            // 开始分配数量
            for (CfRdRecordDetail d : newList) {

                if (d.getQuantity() <= d.getPushQuantity()) {
                    // 都推送了
                    continue;
                }
                int available = d.getQuantity() - d.getPushQuantity();
                CfRdRecord rdRecord = d.getRdRecord();
                if (rdRecord.getBredVouch() == -1) {
                    available = available * -1;
                }
                 //-1-3
                if (settlementQty - available < 0) {
                    createPriceDetailInfo(productCode,d, rdRecord, available, priceDetailInfos);
                    settlementQty = settlementQty - available;
                } else {
                    createPriceDetailInfo(productCode,d, rdRecord, settlementQty, priceDetailInfos);
                    settlementQty = 0;
                    break;
                }
            }
            Assert.isTrue(settlementQty == 0, "退spu:" + productCode + "成本价：" + costsPrice + " 税率：" + taxRate + " 入库的数量不足以推送结算数量:"+settlementQtyOld+"，汇总后还剩下" + settlementQty);

        }else {
            //所有质检的费用
            List<CfCharge> cfChargesOfQc = allCharge.parallelStream().filter(x -> x.getChargeType().equals(NumberEnum.NINE.getCode())).collect(Collectors.toList());
            Integer sumNumOfAmount=0;
            HashMap<Long,Integer> cacheMap=new HashMap<>();
            LinkedList<CfRdRecordDetail> newList = new LinkedList<>();
            LinkedList<CfRdRecordDetail> rjList=new LinkedList<>();
            for (CfRdRecordDetail cf:matchRds) {
                if (cf.getRdRecordDetailId() == 0) {
                    continue;
                }
                Optional<CfRdRecord> first = allRdRecord.stream().filter((a) -> a.getRdRecordId().equals(cf.getRdRecordId())).findFirst();
                Assert.isTrue(first.isPresent(), "入库单明细 没匹配到，detail id :" + cf.getRdRecordDetailId());
                CfRdRecord rdRecord = first.get();
                cf.setRdRecord(rdRecord);
                if (rdRecord.getBredVouch() == 1) {
                    //第一步（获取当前入库单关联的质检费用）：获取当前采购的质检
                    Set<Long> qcDetailIds = cfQcRecordAsnDetails.stream().filter(x -> Objects.equals(x.getRdRecordDetailId(), cf.getRdRecordDetailId())).map(x -> x.getQcChargingId()).collect(Collectors.toSet());
                    List<CfCharge> qcChargesOfRd = allCharge.stream().filter(x -> qcDetailIds.contains(x.getChargeSourceDetailId())).collect(Collectors.toList());
                    //第二步(获取当前入库单关联的延期费用)
                    BigDecimal qcAmount=qcChargesOfRd.parallelStream().map(CfCharge::getAmountPp).reduce(BigDecimal.ZERO, BigDecimal::add);
                    List<CfCharge> delayChargesOfRd = allCharge.parallelStream().filter(x -> Objects.equals(x.getChargeSourceDetailId(), cf.getRdRecordDetailId()) && Objects.equals(Integer.valueOf(x.getChargeType()), NumberEnum.EIGHT.getCode())).collect(Collectors.toList());
                    log.debug("结算推送-{}:关联的延期扣款charge：{}",cf.getRdRecordDetailId(),delayChargesOfRd);
                    BigDecimal delayAmount = delayChargesOfRd.parallelStream().map(CfCharge::getAmountPp).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal add = qcAmount.add(delayAmount);
                    log.debug("结算推送-{}：关联的扣费总金额：{}",cf.getRdRecordDetailId(),add);
                    if(BigDecimal.ZERO.compareTo(add)<0){
                        //获取当前入库单最小结算数量
                        Integer pushQuantity = cf.getPushQuantity();
                        if(pushQuantity==null||pushQuantity==0){
                            sumNumOfAmount++;
                            cacheMap.put(cf.getRdRecordDetailId(),1);
                        }
                    }
                    newList.add(cf);
                }else {
                    rjList.add(cf);

                }

            }
            for (CfRdRecordDetail d : rjList) {
                if (d.getQuantity() <= d.getPushQuantity()) {
                    // 都推送了
                    continue;
                }
                CfRdRecord rdRecord = d.getRdRecord();
                int available= (d.getQuantity()- d.getPushQuantity())*-1;
                // 1+1,-3+2
                settlementQty=settlementQty-available;
                createPriceDetailInfo(productCode,d, rdRecord, available, priceDetailInfos);
            }
            //总结算可自由分配的数量
            int sumAvailable =settlementQty-sumNumOfAmount;
            log.debug("结算推送-{},需要结算总数量：{},当前剩余结算的数量：{},剩余可自由分配数量：{}",productCode,settlementQtyOld,settlementQty,sumAvailable);
            log.debug("结算推送-{},有扣款的入库通知单详情：{}",productCode,cacheMap);
            for (CfRdRecordDetail d : newList) {
                if (d.getQuantity() <= d.getPushQuantity()) {
                    // 都推送了
                    continue;
                }
                CfRdRecord rdRecord = d.getRdRecord();
                //当前入库单剩余可结算数量
                int divide=0;
                if(cacheMap.containsKey(d.getRdRecordDetailId())){
                    divide=cacheMap.get(d.getRdRecordDetailId());
                }
                int available = d.getQuantity() - d.getPushQuantity();
                log.debug("结算推送-{}最小结算数量：{}",d.getRdRecordDetailId(),divide);
                available=available-divide;
                //  7-14
                if (sumAvailable - available <= 0) {
                    // 抵消一部分 7+4
                    createPriceDetailInfo(productCode,d, rdRecord, sumAvailable+divide, priceDetailInfos);
                    settlementQty=settlementQty-(sumAvailable+divide);
                    sumAvailable = 0;
                } else {
                    // 一次数据可以全部抵消
                    // 16 -2=14  》》  14 - -2=16 退次的计算在内了
                    sumAvailable = sumAvailable - available;
                    settlementQty=settlementQty-(available+divide);
                    createPriceDetailInfo(productCode,d, rdRecord, available+divide, priceDetailInfos);
                }
            }
            Assert.isTrue(settlementQty == 0, "入SPU:" + productCode + "成本价：" + costsPrice + " 税率：" + taxRate + " 入库的数量不足以推送结算数量:"+settlementQtyOld+"，汇总后还剩下" + settlementQty);

        }
        return priceDetailInfos;
    }




    /**
     * 
     * TODO 处理数据到，同SKU的数据优先抵消带有退款和延期，质检等的 第一步：根据入库的id 优先查询是否存在扣款的
     * @param productCode
     * @param costsPrice
     * @param taxRate
     * @param settlementQty
     * @param allRdRecordDetail
     * @param allRdRecord
     * @return
     */
    private List<PriceDetailInfo> allocationRdRecordDetial(String productCode, BigDecimal costsPrice, BigDecimal taxRate, Integer settlementQty, List<CfRdRecordDetail> allRdRecordDetail, List<CfRdRecord> allRdRecord) {
        boolean isTc = false;
        if (settlementQty < 0) {
            isTc = true;
        }
        List<PriceDetailInfo> priceDetailInfos = new ArrayList<>();
        List<CfRdRecordDetail> matchRds = allRdRecordDetail.stream().filter((a) -> a.getProductCode().equals(productCode)
                && a.getTaxUnitPrice().compareTo(costsPrice) == 0
                && a.getTaxRate().compareTo(taxRate) == 0).collect(Collectors.toList());


        LinkedList<CfRdRecordDetail> newList = new LinkedList<>();

        for (CfRdRecordDetail d : matchRds) {
            if (d.getRdRecordDetailId() == 0) {
                continue;
            }
            Optional<CfRdRecord> first = allRdRecord.stream().filter((a) -> a.getRdRecordId().equals(d.getRdRecordId())).findFirst();
            Assert.isTrue(first.isPresent(), "入库单明细 没匹配到，detail id :" + d.getRdRecordDetailId());
            CfRdRecord rdRecord = first.get();
            d.setRdRecord(rdRecord);
            if (isTc) {//判断是否是全是退次
                if (rdRecord.getBredVouch() == 1) {
                    //采购拍第一
                    newList.addFirst(d);
                } else {
                    //退次按顺序
                    newList.add(d);
                }
            } else {//非退次
                if (rdRecord.getBredVouch() == 1) {
                    newList.add(d);
                } else {
                    //退次的排在第一
                    newList.addFirst(d);
                }
            }
        }
        if (settlementQty == 0) {
            // 只有一个，推送0
            if (matchRds.size() == 1 && matchRds.get(0).getQuantity() == 1) {
                return new LinkedList<>();
            }
        }

        // 开始分配数量
        for (CfRdRecordDetail d : newList) {

            if (d.getQuantity() <= d.getPushQuantity()) {
                // 都推送了
                continue;
            }
            int available = d.getQuantity() - d.getPushQuantity();
            CfRdRecord rdRecord = d.getRdRecord();
            if (rdRecord.getBredVouch() == -1) {
                available = available * -1;
            }

            if (isTc) {
                // -1-3 =-4
                if (settlementQty - available < 0) {
                    createPriceDetailInfo(productCode,d, rdRecord, available, priceDetailInfos);
                    settlementQty = settlementQty - available;
                } else {
                    createPriceDetailInfo(productCode,d, rdRecord, settlementQty, priceDetailInfos);
                    settlementQty = 0;
                    break;
                }
            } else {
                if (settlementQty - available <= 0) {
                    // 抵消一部分
                    createPriceDetailInfo(productCode,d, rdRecord, settlementQty, priceDetailInfos);
                    settlementQty = 0;
                    break;
                } else {
                    // 一次数据可以全部抵消
                    // 16 -2=14  》》  14 - -2=16 退次的计算在内了
                    settlementQty = settlementQty - available;
                    createPriceDetailInfo(productCode,d, rdRecord, available, priceDetailInfos);
                }
            }
        }

        Assert.isTrue(settlementQty == 0, "spu:" + productCode + "成本价：" + costsPrice + " 税率：" + taxRate + " 入库的数量不足以推送，汇总后还剩下" + settlementQty);
        return priceDetailInfos;

    }

    private void createPriceDetailInfo(String productCode,CfRdRecordDetail d, CfRdRecord rdRecord, int pushed, List<PriceDetailInfo> priceDetailInfos) {
        PriceDetailInfo info = new PriceDetailInfo();
        info.setQuantity(pushed);
        info.setTaxRate(d.getTaxRate());
        info.setTaxPrice(d.getTaxUnitPrice());
        info.setUnitPrice(d.getUnitPrice());
        info.setInventoryCode(d.getInventoryCode());
        info.setInventoryId(d.getInventoryId());
        info.setRdId(d.getRdRecordId());
        info.setWdtRecordCode("");
        info.setRecordCode(rdRecord.getRdRecordCode());
        info.setVendorId(rdRecord.getVendorId().intValue());
        info.setPoId(d.getPoId() == null ? rdRecord.getRjRetiredId().intValue() : d.getPoId().intValue());
        info.setProductCode(productCode);
        priceDetailInfos.add(info);
        CfRdRecordDetail update = new CfRdRecordDetail();
        update.setId(d.getId());
        if (pushed < 0) {
            pushed = pushed * -1;
        }
        update.setPushQuantity(pushed + d.getPushQuantity());
        cfRdRecordDetailMapper.updateById(update);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Object> splitInvoice(String invoiceNo) {
        List<CfCharge> chargesOf = chargeMapper.selectList(Wrappers.lambdaQuery(CfCharge.class)
                .eq(CfCharge::getInvoiceNo, invoiceNo));
        Optional<CfCharge> first = chargesOf.stream().filter(x -> !Objects.isNull(x.getParentId())).findFirst();
        Assert.isTrue(!first.isPresent(),"当前账单存在已经拆分过的数据，不支持二次拆分");
      /*  Optional<CfCharge> cfChargeOptional = chargesOf.stream().filter(x -> Objects.nonNull(x.getIsOffset()) && x.getIsOffset() == 1).findFirst();
        Assert.isTrue(!cfChargeOptional.isPresent(),"当前账单已经调整过相关费用，暂不支持拆分尾款");*/
        fixAdjustInvoice(invoiceNo);
        //账单40% 的费用拆分
        List<CfInvoiceHeader> cfInvoiceHeaders = invoiceHeaderMapper.selectList(Wrappers.lambdaQuery(CfInvoiceHeader.class)
                .eq(CfInvoiceHeader::getInvoiceNo, invoiceNo));
        CfInvoiceHeader cfInvoiceHeader = cfInvoiceHeaders.get(0);
        List<CfInvoiceSettlement> allSettlements = getAllSettlementByInvoiceId(cfInvoiceHeader.getInvoiceId());
        Assert.isTrue(allSettlements.size() == 1 || allSettlements.size() == 2, "账单下必须有结算信息");
        CfInvoiceSettlement firstSettlement = allSettlements.get(0);

        CfInvoiceSettlement secondSettlement;
        if (allSettlements.size() == NumberEnum.TWO.getCode()) {
            secondSettlement = allSettlements.get(1);
        } else {
            secondSettlement = getSec(firstSettlement);
        }

        Assert.isTrue(StringUtils.isNotBlank(firstSettlement.getCustomerInvoiceNo()), "第一次结算需要核销");
        Assert.isTrue(StringUtils.isBlank(secondSettlement.getCustomerInvoiceNo()), "第二次结算不能核销过");
        SplitChargeByInvoiceDTO dto = new SplitChargeByInvoiceDTO();
        matchChargeAlreadPushed(secondSettlement, dto);
        // 把第一次结算 设置成100
        firstSettlement.setInvoiceSettlementRate(BigDecimal.ONE);
        invoiceSettlementMapper.updateById(firstSettlement);

        List<CfInvoiceDetail> allInvoiceDetail = getAllInvoiceDetail(cfInvoiceHeader.getInvoiceId());
        List<CfCharge> charges = updateInvoiceDetails(invoiceNo, allInvoiceDetail, cfInvoiceHeader);
        // 修改费用的核销记录
        updateChargeClearMsg(charges);
        //修改核销单中的核销
        {
            List<CfClearHeader> cfClearHeaders = cfClearHeaderMapper.selectList(Wrappers.<CfClearHeader>lambdaQuery().eq(CfClearHeader::getInvoiceNo, invoiceNo));
            log.info("获取核销单进行变更：{}",cfClearHeaders);
            //NO1:删除之前的核销单
            for (CfClearHeader cfch:cfClearHeaders) {
                CfInvoiceHeader newCfHeaders= invoiceHeaderMapper.selectList(Wrappers.lambdaQuery(CfInvoiceHeader.class)
                        .eq(CfInvoiceHeader::getInvoiceNo, invoiceNo)).get(0);
                CfClearHeader cfClearHeader = new CfClearHeader();
                BeanUtilCopy.copyProperties(newCfHeaders, cfch);
                cfClearHeader.setClearId(cfch.getClearId());
                cfClearHeader.setClearCredit(newCfHeaders.getInvoicelCredit());
                cfClearHeader.setClearDebit(newCfHeaders.getInvoicelDebit());

                cfClearHeader.setLastBalanceBalance(BigDecimal.ZERO);
                //cfClearHeader.setLastBalanceType(BigDecimal);
                cfClearHeader.setLastBalanceCredit(BigDecimal.ZERO);
                cfClearHeader.setLastBalanceDebit(BigDecimal.ZERO);

                cfClearHeader.setNowBalanceType(newCfHeaders.getInvoiceType());
                cfClearHeader.setNowBalanceBalance(BigDecimal.ZERO);
                cfClearHeader.setNowBalanceCredit(BigDecimal.ZERO);
                cfClearHeader.setNowBalanceDebit(BigDecimal.ZERO);
                cfClearHeader.setNowClearType(newCfHeaders.getInvoiceType());
                BigDecimal subtract = newCfHeaders.getInvoicelCredit().subtract(newCfHeaders.getInvoicelDebit());
                cfClearHeader.setNowClearBalance(subtract.compareTo(BigDecimal.ZERO)>-1?subtract:subtract.multiply(new BigDecimal(-1)));
                cfClearHeader.setNowClearCredit(newCfHeaders.getInvoicelCredit());
                cfClearHeader.setNowClearDebit(newCfHeaders.getInvoicelDebit());
                cfClearHeaderMapper.updateById(cfClearHeader);
                //NO2:创建新的核销单并核销
                clearDetailMapper.delete(Wrappers.<CfClearDetail>lambdaQuery().eq(CfClearDetail::getClearId,cfch.getClearId()));
                List<CfCharge> cfCharges = getAllChargeSourceCode(invoiceNo);
                List<CfClearDetail> cfClearDetails = cfCharges.stream().map(cfCharge ->createCfClearDetail(cfch.getClearId(), cfCharge)).collect(Collectors.toList());
                cfClearDetails.forEach(cfClearDetail -> {
                    if (Objects.isNull(cfClearDetail.getClearDetailId())) {
                        clearDetailMapper.insert(cfClearDetail);
                    } else {
                        clearDetailMapper.updateById(cfClearDetail);
                    }
                });
            }



        }
        return new Response<>(ResponseCode.SUCCESS, dto);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<Object> balanceRelease(String invoiceNo) {
      /*  //第一步：验证是否支持尾款释放
        List<CfInvoiceHeader> cfInvoiceHeaders = invoiceHeaderMapper.selectList(Wrappers.lambdaQuery(CfInvoiceHeader.class)
                .eq(CfInvoiceHeader::getInvoiceNo, invoiceNo));
        CfInvoiceHeader cfInvoiceHeader = cfInvoiceHeaders.get(0);
        List<CfInvoiceSettlement> allSettlements = getAllSettlementByInvoiceId(cfInvoiceHeader.getInvoiceId());
        {
            Assert.isTrue(allSettlements.size() == 1 || allSettlements.size() == 2, "账单下必须有结算信息");
            //必须要有一个结算，一个未结算
            CfInvoiceSettlement doneSettlement=null;
            CfInvoiceSettlement doingSettlement=null;
            for (CfInvoiceSettlement cfInvoiceSettlement: allSettlements) {
                if(cfInvoiceSettlement.getInvoiceSettlementStatus()==NumberEnum.NINE.getCode()){
                    doneSettlement=cfInvoiceSettlement;
                }else {
                    doingSettlement=cfInvoiceSettlement;
                }
            }
            Assert.isTrue(doneSettlement==null, "账单下没有处于已核销的结算单，无法进行尾款释放");
            Assert.isTrue(!(allSettlements.size()==2&&doingSettlement==null), "账单下的结算单处于已核销比例100%，无法进行尾款释放");
                BigDecimal invoiceSettlementRate = doingSettlement.getInvoiceSettlementRate();
            {
                //作废未结算结算单，修改已经核销结算单比例为100% TODO
                doneSettlement.setInvoiceSettlementRate(BigDecimal.ONE);
                invoiceSettlementMapper.updateById(doneSettlement);
                if(doingSettlement!=null){
                    invoiceSettlementMapper.invalidSettlementById(doingSettlement.getInvoiceSettlementId());
                }

            }
            {
                //拆分费用释放至费用池 TODO
                // 第一步 获取当前账单下所有的费用并备份
                List<CfCharge> allChargeSourceCode = getAllChargeSourceCode(invoiceNo);
                this.backSplit(allChargeSourceCode);
                // 根据费用里面入库单明细id 查询子表入库明细数量是否匹配上
                Set<Long> collectCharge= allChargeSourceCode.stream().filter((a)->(Objects.equals(a.getChargeType(),String.valueOf(NumberEnum.ONE.getCode()))||Objects.equals(a.getChargeType(),String.valueOf(NumberEnum.SEVEN.getCode())))).map(CfCharge::getChargeSourceDetailId).collect(Collectors.toSet());
                List<CfRdRecordDetail> allRdRecordDetail = getAllRdRecordDetail(collectCharge);
                Set<Long> collectDetails = allRdRecordDetail.stream().filter((a) -> a.getRdRecordDetailId() != null).map(CfRdRecordDetail::getRdRecordDetailId).collect(Collectors.toSet());
                Assert.isTrue(collectDetails.size() == collectCharge.size(), "费用入库明细数据 和 入库单明细数据有偏差");
                //根据入库单的总数量减去已推送数量，新生成拆分后的费用

            }
            {
                //修改核销单数据
                List<CfClearHeader> cfClearHeaders = cfClearHeaderMapper.selectList(Wrappers.<CfClearHeader>lambdaQuery().eq(CfClearHeader::getInvoiceNo, invoiceNo));
                //NO1:删除之前的核销单
                for (CfClearHeader cfch:cfClearHeaders) {
                    CfInvoiceHeader newCfHeaders= invoiceHeaderMapper.selectList(Wrappers.lambdaQuery(CfInvoiceHeader.class)
                            .eq(CfInvoiceHeader::getInvoiceNo, invoiceNo)).get(0);
                    CfClearHeader cfClearHeader = new CfClearHeader();
                    cfClearHeader.setClearId(cfch.getClearId());
                    cfClearHeader.setClearCredit(newCfHeaders.getInvoicelCredit());
                    cfClearHeader.setClearDebit(newCfHeaders.getInvoicelDebit());


                    cfClearHeader.setNowBalanceType(newCfHeaders.getInvoiceType());
                    cfClearHeader.setNowBalanceBalance(BigDecimal.ZERO);
                    cfClearHeader.setNowBalanceCredit(newCfHeaders.getInvoicelCredit());
                    cfClearHeader.setNowBalanceDebit(newCfHeaders.getInvoicelDebit());
                    cfClearHeader.setNowClearType(newCfHeaders.getInvoiceType());
                     cfClearHeader.setNowClearBalance(newCfHeaders.getInvoicelCredit().subtract(newCfHeaders.getInvoicelDebit()));
                    cfClearHeader.setNowClearCredit(newCfHeaders.getInvoicelCredit());
                    cfClearHeader.setNowClearDebit(newCfHeaders.getInvoicelDebit());
                    //NO2:创建新的核销单并核销
                    clearDetailMapper.delete(Wrappers.<CfClearDetail>lambdaQuery().eq(CfClearDetail::getClearId,cfch.getClearId()));
                    List<CfCharge> cfCharges = getAllChargeSourceCode(invoiceNo);
                    List<CfClearDetail> cfClearDetails = cfCharges.stream().map(cfCharge ->createCfClearDetail(cfch.getClearId(), cfCharge)).collect(Collectors.toList());
                    cfClearDetails.forEach(cfClearDetail -> {
                        if (Objects.isNull(cfClearDetail.getClearDetailId())) {
                            clearDetailMapper.insert(cfClearDetail);
                        } else {
                            clearDetailMapper.updateById(cfClearDetail);
                        }
                    });
                }

            }

        }*/
        //第二步：
        return null;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<Object> fixAdjustInvoice(String invoiceNo) {
        List<CfInvoiceHeader> cfInvoiceHeaders = invoiceHeaderMapper.selectList(Wrappers.lambdaQuery(CfInvoiceHeader.class)
                .eq(CfInvoiceHeader::getInvoiceNo, invoiceNo));
        CfInvoiceHeader cfInvoiceHeader = cfInvoiceHeaders.get(0);

        List<CfInvoiceDetail> details = invoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfInvoiceDetail.class)
                .eq(CfInvoiceDetail::getInvoiceId, cfInvoiceHeader.getInvoiceId()));

        List<CfCharge> charges = chargeMapper.selectList(Wrappers.lambdaQuery(CfCharge.class)
                .eq(CfCharge::getInvoiceNo, invoiceNo));

        List<Long> chids = charges.stream().map(CfCharge::getChargeId).collect(Collectors.toList());
        invoiceDetailMapper.delete(Wrappers.lambdaQuery(CfInvoiceDetail.class)
                .eq(CfInvoiceDetail::getInvoiceId, cfInvoiceHeader.getInvoiceId())
                .notIn(CfInvoiceDetail::getChargeId, chids));
        List<CfCharge> adjusts = charges.stream().filter(a -> details.stream().noneMatch(b -> b.getChargeId().equals(a.getChargeId()))).collect(Collectors.toList());
        if (adjusts.size() > 0) {
            saveCharge(cfInvoiceHeader, adjusts);
        }

        BigDecimal credit = BigDecimal.ZERO;
        BigDecimal debit = BigDecimal.ZERO;
        for (CfCharge t : charges) {
            if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(t.getArapType())) {

                debit = debit.add(t.getAmountPp());
            } else {
                credit = credit.add(t.getAmountPp());
            }
        }
        cfInvoiceHeader.setInvoicelCredit(credit);
        cfInvoiceHeader.setInvoicelDebit(debit);
        invoiceHeaderMapper.updateById(cfInvoiceHeader);
        return new Response<>(ResponseCode.SUCCESS, adjusts);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void u8fix(String myPurchaseInvoiceCode) {
        List<PurchaseInvoice> purchaseInvoices = purchaseInvoiceMapper.selectList(Wrappers.lambdaQuery(PurchaseInvoice.class).eq(PurchaseInvoice::getPurchaseInvoiceCode, myPurchaseInvoiceCode));
        PurchaseInvoice purchaseInvoice = purchaseInvoices.get(0);
        CfInvoiceHeader invoiceHeader = getInvHeader(purchaseInvoice);
        List<PurchaseInvoiceDetail> invoicesList = purchaseInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(PurchaseInvoiceDetail.class).eq(PurchaseInvoiceDetail::getPurchaseInvoiceId, purchaseInvoice.getPurchaseInvoiceId()));
        List<Long> collect = invoicesList.stream().map(PurchaseInvoiceDetail::getPurchaseInvoiceDetailId).collect(Collectors.toList());

        purchaseInvoiceDetailMapper.update(null,
                Wrappers.<PurchaseInvoiceDetail>lambdaUpdate()
                        .set(PurchaseInvoiceDetail::getPurchaseInvoiceId, purchaseInvoice.getPurchaseInvoiceId())
                        .in(PurchaseInvoiceDetail::getPurchaseInvoiceDetailId, collect));
        purchaseInvoiceMapper.update(
                null,
                Wrappers.<PurchaseInvoice>lambdaUpdate()
                        .set(PurchaseInvoice::getPurchaseInvoiceId, purchaseInvoice.getPurchaseInvoiceId())
                        .eq(PurchaseInvoice::getPurchaseInvoiceCode, myPurchaseInvoiceCode)
        );
        purchaseInvoice.setBrandId(invoiceHeader.getBrandId());
        // 结算筛选0蓝字 1红字
        String accountScreen = ChargeEnum.ARAP_TYPE_AR.getCode().equals(invoiceHeader.getInvoiceType()) ? "1" : "0";
        purchaseInvoice.setAccountScreen(accountScreen);
        for (PurchaseInvoiceDetail purchaseInvoiceDetail : invoicesList) {
            purchaseInvoiceDetail.setPurchaseInvoiceId(purchaseInvoice.getPurchaseInvoiceId());
            Long rdid = purchaseInvoiceDetail.getRdRecordDetailId();
            List<CfRdRecordDetail> rdRecordDetails = cfRdRecordDetailMapper.selectList(Wrappers.lambdaQuery(CfRdRecordDetail.class)
                    .in(CfRdRecordDetail::getRdRecordId, rdid).eq(CfRdRecordDetail::getInventoryCode, purchaseInvoiceDetail.getInventoryCode())
            );
            CfRdRecordDetail d = rdRecordDetails.get(0);
            purchaseInvoiceDetail.setRdId(rdid);
            List<CfRdRecord> rdRecords = cfRdRecordMapper.selectList(Wrappers.lambdaQuery(CfRdRecord.class)
                    .in(CfRdRecord::getRdRecordId, rdid)
            );
            CfRdRecord rdRecord = rdRecords.get(0);
            purchaseInvoiceDetail.setPoId(d.getPoId() == null ? rdRecord.getRjRetiredId().intValue() : d.getPoId().intValue());
        }
        purBillVouchProduceService.syncSendWdtRecordToMq(purchaseInvoice, invoicesList, invoiceHeader.getInvoiceNo());
    }

    private CfInvoiceHeader getInvHeader(PurchaseInvoice purchaseInvoice) {
        String accountBillCode = purchaseInvoice.getAccountBillCode();
        List<CfInvoiceSettlement> selectList = invoiceSettlementMapper.selectList(Wrappers.lambdaQuery(CfInvoiceSettlement.class).
                eq(CfInvoiceSettlement::getInvoiceSettlementNo, accountBillCode));
        if (selectList.size() > 0) {
            CfInvoiceSettlement settlement = selectList.get(0);
            return invoiceHeaderMapper.selectById(settlement.getInvoiceId());
        }
        List<CfInvoiceHeader> cfInvoiceHeaders = invoiceHeaderMapper.selectList(Wrappers.lambdaQuery(CfInvoiceHeader.class)
                .eq(CfInvoiceHeader::getInvoiceNo, accountBillCode));
        return cfInvoiceHeaders.get(0);
    }

    private void saveCharge(CfInvoiceHeader inv, List<CfCharge> charges) {
        List<CfInvoiceDetail> invoiceDetailList = new ArrayList<>(charges.size());

        for (CfCharge charge : charges) {
            CfInvoiceDetail t = new CfInvoiceDetail();
            BeanUtilCopy.copyProperties(charge, t);
            t.setInvoiceQty(charge.getChargeQty());
            if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(charge.getArapType())) {
                t.setInvoiceDebit(charge.getAmountPp());
                t.setInvoiceCredit(BigDecimal.ZERO);
            } else {
                t.setInvoiceCredit(charge.getAmountPp());
                t.setInvoiceDebit(BigDecimal.ZERO);
            }
            invoiceDetailList.add(t);
        }

        // 保存账单明细数据
        for (CfInvoiceDetail invoiceDetail : invoiceDetailList) {
            invoiceDetail.setInvoiceId(inv.getInvoiceId());
            invoiceDetailMapper.insert(invoiceDetail);
        }
        for (CfCharge charge : charges) {
            if (charge.getChargeSourceDetailId() != null) {
                continue;
            }
            CfCharge up = new CfCharge();
            up.setChargeId(charge.getChargeId());
            up.setIsOffset(1);
            chargeMapper.updateById(up);
        }
    }

    private CfInvoiceSettlement getSec(CfInvoiceSettlement firstSettlement) {
        CfInvoiceSettlement insert = new CfInvoiceSettlement();
        BeanUtilCopy.copyProperties(firstSettlement, insert);
        insert.setInvoiceSettlementRate(BigDecimal.ONE.subtract(firstSettlement.getInvoiceSettlementRate()));
        insert.setRemark("");
        insert.setCustomerInvoiceNo("");
        insert.setInvoiceSettlementStatus(SettlementStatusEnum.DKP.getCode());
        insert.setCustomerInvoiceDate(null);
        insert.setInvoiceSettlementId(null);

        Object settlementAmount = cfInvoiceSettlementService.getSettlementAmount(insert);
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(settlementAmount), JSONObject.class);

        BigDecimal jsje = jsonObject.getBigDecimal("jsje");
        BigDecimal zdye = jsonObject.getBigDecimal("zdye");
        if(BigDecimal.ZERO.compareTo(zdye)!=0){
            throw new BusinessException(SystemState.BUSINESS_ERROR.code(),"第二次结算后账单余额不是 0 是：" + zdye+" 请联系管理人员查看！！！");
        }
        insert.setInvoiceSettlementMoney(jsje);
        insert.setInvoiceSettlementNo(firstSettlement.getInvoiceSettlementNo() + "_1");
        insert.setCreateDate(LocalDateTime.now());
        invoiceSettlementMapper.insert(insert);
        return insert;
    }

    private void updateChargeClearMsg(List<CfCharge> charges) {
        for (CfCharge charge : charges) {
            chargeMapper.update(
                    null,
                    Wrappers.<CfCharge>lambdaUpdate()
                            .set(CfCharge::getActualAmount, charge.getAmountPp())
                            .eq(CfCharge::getChargeId, charge.getChargeId())
            );
        }
    }

    private List<CfCharge> updateInvoiceDetails(String invoiceNo, List<CfInvoiceDetail> allInvoiceDetail, CfInvoiceHeader cfInvoiceHeader) {
        List<CfCharge> reduceCharge = getAllChargeSourceCode(invoiceNo);
        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal credit = BigDecimal.ZERO;
        for (CfInvoiceDetail t : allInvoiceDetail) {
            List<CfCharge> mat = reduceCharge.stream().filter(a -> a.getChargeId().equals(t.getChargeId())).collect(Collectors.toList());
            if (mat.size() > 0) {
                CfCharge charge = mat.get(0);
                t.setInvoiceQty(charge.getChargeQty());
                if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(charge.getArapType())) {
                    t.setInvoiceDebit(charge.getAmountPp());
                    t.setInvoiceCredit(BigDecimal.ZERO);
                    debit = debit.add(charge.getAmountPp());
                } else {
                    t.setInvoiceCredit(charge.getAmountPp());
                    t.setInvoiceDebit(BigDecimal.ZERO);
                    credit = credit.add(charge.getAmountPp());
                }
                invoiceDetailMapper.updateById(t);
            } else {
                invoiceDetailMapper.deleteById(t.getInvoiceDetailId());
            }
        }
        // 修改账单状态
        cfInvoiceHeader.setInvoiceStatus(InvoiceStatusEnum.YHX.getCode());
        cfInvoiceHeader.setInvoicelDebit(debit);
        cfInvoiceHeader.setInvoicelCredit(credit);
        cfInvoiceHeader.setClearStatus(NumberEnum.TWO.getCode());
        invoiceHeaderMapper.updateById(cfInvoiceHeader);
        return reduceCharge;
    }

    private void matchChargeAlreadPushed(CfInvoiceSettlement secondSettlement, SplitChargeByInvoiceDTO dto) {
        // 第一步 获取所有费用里面的来源单号
        List<CfCharge> allChargeSourceCode = getAllChargeSourceCode(secondSettlement.getInvoiceNo());
        // 备份 split 费用
        backSplit(allChargeSourceCode);
        // 根据费用里面入库单明细id 查询子表入库明细
        Set<Long> collectCharge = allChargeSourceCode.stream().filter((a) -> a.getChargeSourceDetailId() != null&&!Objects.equals(String.valueOf(NumberEnum.NINE.getCode()),a.getChargeType())).map(CfCharge::getChargeSourceDetailId).collect(Collectors.toSet());
        List<CfRdRecordDetail> allRdRecordDetail = getAllRdRecordDetail(collectCharge);

        Set<Long> collectDetails = allRdRecordDetail.stream().filter((a) -> a.getRdRecordDetailId() != null).map(CfRdRecordDetail::getRdRecordDetailId).collect(Collectors.toSet());
        Assert.isTrue(collectDetails.size() == collectCharge.size(), "费用入库明细数据 和 入库单明细数据有偏差");



        // 获取结算单打印明细中每个spu 的数量
        CfInvoiceHeaderDetailVO print = cfInvoiceSettlementService.print(secondSettlement.getInvoiceSettlementId());
        ChargeInVO heji = CfInvoiceSettlementServiceImpl.getHeji(print.getChargeInList());

        //拆出去的费用 = 第二次结算的费用
        getAllNoPushCharge(allRdRecordDetail, allChargeSourceCode, secondSettlement, dto);
        BigDecimal arReduce = dto.getSplitCharges().stream().filter(cfCharge -> Objects.equals(ChargeEnum.ARAP_TYPE_AR.getCode(), cfCharge.getArapType())).map(CfCharge::getAmountPp).reduce(BigDecimal.ZERO, BigDecimal::subtract);
        BigDecimal apReduce = dto.getSplitCharges().stream().filter(cfCharge -> Objects.equals(ChargeEnum.ARAP_TYPE_AP.getCode(), cfCharge.getArapType())).map(CfCharge::getAmountPp).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal secSplitCharge = arReduce.add(apReduce);
        log.info("dto: {}", dto);

        Assert.isTrue(secSplitCharge.compareTo(heji.getSettlementAmount()) == 0, secSplitCharge + "拆分的费用总和 不等于 第二次结算的费用: " + heji.getSettlementAmount());

        //拆分完成 把账单状态设置成 全部核销，
        invoiceSettlementMapper.invalidSettlementById(secondSettlement.getInvoiceSettlementId());
        dto.setSplitMoney(secSplitCharge);
    }

    private void backSplit(List<CfCharge> allChargeSourceCode) {
        List<CfChargeSplit> cfChargeSplits = BeanUtilCopy.copyListProperties(allChargeSourceCode, CfChargeSplit::new);
        for (CfChargeSplit cfChargeSplit : cfChargeSplits) {
            List<CfChargeIn> chargeIns = getChargeInByChargeId(cfChargeSplit.getChargeId());
            if (CollectionUtils.isNotEmpty(chargeIns)) {
                List<CfChargeInSplit> cfChargeInSplits = BeanUtilCopy.copyListProperties(chargeIns, CfChargeInSplit::new);
                for (CfChargeInSplit cfChargeInSplit : cfChargeInSplits) {
                    chargeInSplitMapper.insert(cfChargeInSplit);
                }
            }
            chargeSplitMapper.insert(cfChargeSplit);
        }

    }

    private void getAllNoPushCharge(List<CfRdRecordDetail> allRdRecordDetail, List<CfCharge> allCharges, CfInvoiceSettlement secondSettlement, SplitChargeByInvoiceDTO dto) {
        List<Long> collect = allCharges.stream().map(CfCharge::getChargeId).collect(Collectors.toList());
        LambdaQueryWrapper<CfChargeIn> lambdaQueryWrapper = Wrappers.lambdaQuery(CfChargeIn.class);
        lambdaQueryWrapper.in(CfChargeIn::getChargeId, collect);
        List<CfChargeIn> cfChargeIns = chargeInMapper.selectList(lambdaQueryWrapper);
        allCharges = allCharges.stream().filter(a -> (cfChargeIns.stream().anyMatch(b -> b.getChargeId().equals(a.getChargeId()))) && a.getIsOffset() == 0 && a.getChargeQty() > 0).collect(Collectors.toList());
        allRdRecordDetail = allRdRecordDetail.stream().filter(a -> a.getQuantity() > 0).collect(Collectors.toList());
        List<CfCharge> all = new ArrayList<>();
        List<CfCharge> partSplitCharges = new ArrayList<>();
        for (CfRdRecordDetail detail : allRdRecordDetail) {
            if (detail.getPushQuantity() < detail.getQuantity()) {
                List<CfCharge> splitCharges = allCharges.stream().filter(a -> detail.getRdRecordDetailId().equals(a.getChargeSourceDetailId())).collect(Collectors.toList());
                doSplitCharge(splitCharges, detail, secondSettlement, all, partSplitCharges);
            }
        }
        dto.setSplitCharges(all);
        dto.setPartSplitCharges(partSplitCharges);
    }

    /**
     * 费用拆分
     * @param needSplitCharges 原始所有费用
     * @param cfRdRecordDetail 入库单明细
     * @param cfInvoiceSettlement 结算单
     * @param splitCharges 直接归还费用池的+ 拆分出来的费用
     * @param partSplitCharges 被拆过的费用即原始费用中被拆分过的费用
     */
    private void doSplitCharge(List<CfCharge> needSplitCharges,CfRdRecordDetail cfRdRecordDetail,CfInvoiceSettlement cfInvoiceSettlement,
                               List<CfCharge> splitCharges, List<CfCharge> partSplitCharges ){
        Assert.isTrue(needSplitCharges.size() == 1, cfInvoiceSettlement.getInvoiceNo() + " : 入库对应的费用明细必须是一个");
        CfCharge parentCharge = needSplitCharges.get(0);//原始费用
        Assert.isTrue(parentCharge.getChargeQty().equals(cfRdRecordDetail.getQuantity()), cfInvoiceSettlement.getInvoiceNo() + " :费用数量和入库明细数量不对应 detail id : " + cfRdRecordDetail.getRdRecordDetailId());
        Integer pushQuantity = cfRdRecordDetail.getPushQuantity();
        if(pushQuantity>=NumberEnum.ONE.getCode()){
            //************************处理费用******************************
            List<CfChargeIn> chargeIns = getChargeInByChargeId(parentCharge.getChargeId());
            Assert.isTrue(chargeIns.size() == 1, "生成的费用记录存在多个，请处理后重试");
            //费用
            CfCharge charge = new CfCharge();
            //创建拆分出来的子费用
            BeanUtilCopy.copyProperties(parentCharge, charge);
            charge.setChargeId(null);
            charge.setChargeCode(charge.getChargeCode() + "_1");
            charge.setParentId(parentCharge.getChargeId());
            charge.setInvoiceNo(null);
            charge.setInvoiceDate(null);
            charge.setInvoiceTitleName(null);
            charge.setInvoiceTitle(null);
            charge.setCustomerInvoiceNo(null);
            charge.setTaxInvoiceDate(null);
            charge.setClearNo(null);
            charge.setActualAmount(BigDecimal.ZERO);
            charge.setActualDate(null);
            charge.setActualHistoryDate(null);
            charge.setChargeQty(parentCharge.getChargeQty() - pushQuantity);
            charge.setAmountPp(parentCharge.getPricePp().multiply(new BigDecimal(charge.getChargeQty())));
            charge.setRemark("拆分费用： 原费用 数量:" + parentCharge.getChargeQty() + " 费用id: " + parentCharge.getChargeId() + " 账单号： " + cfInvoiceSettlement.getInvoiceNo() + (charge.getRemark() == null ? "" : charge.getRemark()));
            chargeMapper.insert(charge);
            //修改原费用的费用
            parentCharge.setChargeQty(pushQuantity);
            parentCharge.setAmountPp(parentCharge.getPricePp().multiply(new BigDecimal(parentCharge.getChargeQty())));
            chargeMapper.updateById(parentCharge);
            //************************处理费用生成记录******************************
            //原始费用的生成记录
            CfChargeIn parentChargeIn = chargeIns.get(0);
            //拆分费用的生成记录
            CfChargeIn splitChargeIn = new CfChargeIn();
            BeanUtilCopy.copyProperties(parentChargeIn, splitChargeIn);
            if(parentChargeIn.getDefectiveRejectionQty()>0){//退次
                //修改原费用生成记录中的数据
                parentChargeIn.setDefectiveRejectionQty(pushQuantity);
                //创建新的费用生成记录的数据
                splitChargeIn.setDefectiveRejectionQty(cfRdRecordDetail.getQuantity()-pushQuantity);
            }else {
                //修改原费用生成记录中的数据
                parentChargeIn.setDefectiveRejectionQty(0);
                parentChargeIn.setArrivalQty(pushQuantity+parentChargeIn.getRejectionQty());
                parentChargeIn.setActualQty(pushQuantity);
                //创建新的费用生成记录的数据
                splitChargeIn.setArrivalQty(cfRdRecordDetail.getQuantity()-pushQuantity);
                splitChargeIn.setRejectionQty(0);
                splitChargeIn.setActualQty(cfRdRecordDetail.getQuantity()-pushQuantity);
                splitChargeIn.setDefectiveRejectionQty(0);
            }
            splitChargeIn.setPostponeDeductionsTotal(BigDecimal.ZERO);
            splitChargeIn.setPostponeDetail(null);
            splitChargeIn.setParentId(parentChargeIn.getParentId());
            splitChargeIn.setChargeId(charge.getChargeId());
            splitChargeIn.setRemark("扣费信息在上一个费用记录中 ： " + parentChargeIn.getChargeInId());
            chargeInMapper.updateById(parentChargeIn);
            chargeInMapper.insert(splitChargeIn);
            splitCharges.add(charge);
            partSplitCharges.add(parentCharge);
        }else {
            chargeMapper.update(
                    null,
                    Wrappers.<CfCharge>lambdaUpdate()
                            .set(CfCharge::getInvoiceNo, null)
                            .set(CfCharge::getInvoiceTitle, null)
                            .set(CfCharge::getInvoiceTitleName, null)
                            .set(CfCharge::getInvoiceDate, null)
                            .set(CfCharge::getActualHistoryDate, null)
                            .set(CfCharge::getRemark, "不拆分，直接归还费用池" + (parentCharge.getRemark() == null ? "" : parentCharge.getRemark()))
                            .set(CfCharge::getClearNo, null)
                            .set(CfCharge::getActualAmount, BigDecimal.ZERO)
                            .eq(CfCharge::getChargeId, parentCharge.getChargeId())
            );
            splitCharges.add(parentCharge);
        }
    }


    private List<CfInvoiceSettlement> getAllSettlementByInvoiceId(Long invoiceId) {
        LambdaQueryWrapper<CfInvoiceSettlement> lambdaQueryWrapper = Wrappers.lambdaQuery(CfInvoiceSettlement.class);
        lambdaQueryWrapper.eq(CfInvoiceSettlement::getInvoiceId, invoiceId).notIn(CfInvoiceSettlement::getInvoiceSettlementStatus, 0, 8);
        lambdaQueryWrapper.orderByAsc(CfInvoiceSettlement::getCreateDate);
        return cfInvoiceSettlementMapper.selectList(lambdaQueryWrapper);
    }

    private List<CfChargeIn> getChargeInByChargeId(Long chargeId) {
        LambdaQueryWrapper<CfChargeIn> lambdaQueryWrapper = Wrappers.lambdaQuery(CfChargeIn.class);
        lambdaQueryWrapper.eq(CfChargeIn::getChargeId, chargeId);
        return chargeInMapper.selectList(lambdaQueryWrapper);
    }


    private List<CfInvoiceDetail> getAllInvoiceDetail(Long invoiceId) {
        LambdaQueryWrapper<CfInvoiceDetail> lambdaQueryWrapper = Wrappers.lambdaQuery(CfInvoiceDetail.class);
        lambdaQueryWrapper.eq(CfInvoiceDetail::getInvoiceId, invoiceId);
        return invoiceDetailMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据费用编号列表获取对应的费用详情
     * @param chargeIds
     * @return
     */
    private List<CfCharge> getAllChargeByChargeIds(List<Long> chargeIds) {
        return cfChargeMapper.selectList(Wrappers.lambdaQuery(CfCharge.class)
                .in(CfCharge::getChargeId, chargeIds)
                .notIn(CfCharge::getCheckStatus, 0, 5));
    }


    /**
     *对账单进行强制驳回，对账单的状态设置为草稿
     * @param invoiceIds
     * @param userVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void forcedToDismissInvoices(List<Long> invoiceIds, UserVO userVO) {
        for (Long invoiceId:invoiceIds) {
            CfInvoiceHeader cfInvoiceHeader = invoiceHeaderMapper.selectById(invoiceId);
            Assert.notNull(cfInvoiceHeader, "账单信息不存在");
            Assert.isTrue(StringUtils.isBlank(cfInvoiceHeader.getAssociatedInvoiceNo()),"当前账单已经被蓝字账单:"+cfInvoiceHeader.getAssociatedInvoiceNo()+"引用，不允许驳回");
            Assert.notNull(cfInvoiceHeader.getInvoiceStatus(), "账单状态不存在");
            List<CfInvoiceSettlement> cfInvoiceSettlements = cfInvoiceSettlementMapper.selectList(Wrappers.<CfInvoiceSettlement>lambdaQuery()
                            .eq(CfInvoiceSettlement::getInvoiceId, invoiceId));
            //检查是否可以进行强制驳回
            {
                doCheckStatusRestrict(cfInvoiceSettlements,
                        InvoiceStatusEnum.getMsgByCode(cfInvoiceHeader.getInvoiceStatus()),InvoiceStatusEnum.CG,
                        InvoiceStatusEnum.SC,
                        InvoiceStatusEnum.CG,
                        InvoiceStatusEnum.ZF,
                        InvoiceStatusEnum.YHX,
                        InvoiceStatusEnum.QBFK);
            }
            //作废当前账单下的结算单
            {
                List<CfInvoiceSettlement> collect = cfInvoiceSettlements.stream().filter(x -> x.getInvoiceSettlementStatus() > 0 && x.getInvoiceSettlementStatus() < 8).collect(Collectors.toList());
                for (CfInvoiceSettlement cfInvoiceSettlement:collect) {
                    CfInvoiceSettlement up = new CfInvoiceSettlement();
                    up.setInvoiceSettlementId(cfInvoiceSettlement.getInvoiceSettlementId());
                    up.setInvoiceSettlementStatus(NumberEnum.EIGHT.getCode());
                    OperateUtil.onUpdate(up, userVO);
                    cfInvoiceSettlementMapper.updateById(up);
                }
            }
            //删除当前账单下的调整费用
            {
                invoiceHeaderMapper.updateAdjustById(cfInvoiceHeader.getInvoiceId());
                cfChargeMapper.deleteOfLogic(cfInvoiceHeader.getInvoiceNo(),NumberEnum.ONE.getCode());
            }
            //解除当前账单下关联的红字账单
            {
                invoiceHeaderMapper.unAssociated(cfInvoiceHeader.getInvoiceNo(),null);
            }
            List<CfCharge> charges = cfChargeMapper.selectList(Wrappers.lambdaQuery(CfCharge.class)
                    .eq(CfCharge::getInvoiceNo, cfInvoiceHeader.getInvoiceNo())
                    .notIn(CfCharge::getCheckStatus,0,5));

            BigDecimal credit = BigDecimal.ZERO;
            BigDecimal debit = BigDecimal.ZERO;
            for (CfCharge t : charges) {
                if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(t.getArapType())) {
                    debit = debit.add(t.getAmountPp());
                } else {
                    credit = credit.add(t.getAmountPp());
                }
            }
            //更改账单的状态为草稿状态
            CfInvoiceHeader updateBean = new CfInvoiceHeader();
            updateBean.setInvoiceId(cfInvoiceHeader.getInvoiceId());
            updateBean.setInvoiceStatus(InvoiceStatusEnum.CG.getCode());
            updateBean.setInvoicelCredit(credit);
            updateBean.setInvoicelDebit(debit);
            OperateUtil.onUpdate(updateBean, userVO);
            invoiceHeaderMapper.updateById(updateBean);
        }
    }



    /**
     * 检验当前结算单的状态是否支持变更为草稿
     * @param in 当前状态
     * @param out 想要变更的状态
     * @param notSupport 不支持变更的状态
     */
    private void doCheckStatusRestrict(List<CfInvoiceSettlement> cfInvoiceSettlements,InvoiceStatusEnum in, InvoiceStatusEnum out, InvoiceStatusEnum... notSupport) {
        String ms = " 状态不支持驳回为 草稿 ！！！ ";
        for(InvoiceStatusEnum chargeCheckStatusEnum :notSupport){
            Assert.isTrue(in!=chargeCheckStatusEnum,in.getMsg() +ms);
        }
        if(in==InvoiceStatusEnum.BJS||in==InvoiceStatusEnum.QJS) {//部分结算
            List<CfInvoiceSettlement> collect = cfInvoiceSettlements.stream().filter(x ->
                    x.getInvoiceSettlementStatus() != 8
                            && x.getInvoiceSettlementStatus() != 5
                            && x.getInvoiceSettlementStatus() != 0
                            && x.getInvoiceSettlementStatus() != 1
            ).collect(Collectors.toList());
            Assert.isTrue(CollectionUtils.isEmpty(collect),"当前账单下的结算单状态不允许进行强制驳回！！！");
        }
    }


    private CfClearDetail createCfClearDetail(Long clearId,
                                              CfCharge cfCharge) {
        CfClearDetail cfClearDetail = new CfClearDetail();
        cfClearDetail.setLastBalance(cfCharge.getAmountPp());
        cfClearDetail.setNowBalance(BigDecimal.ZERO);


        if (Objects.nonNull(cfCharge.getActualAmount()) && cfCharge.getActualAmount().compareTo(BigDecimal.ZERO) > 0) {
            cfClearDetail.setLastBalance(cfCharge.getAmountPp().subtract(cfCharge.getActualAmount()));
        }
        cfClearDetail.setClearedAmount(cfCharge.getAmountPp());
        cfClearDetail.setActualClearAmount(cfCharge.getAmountPp());
        cfCharge.setActualDate(LocalDateTime.now());
        cfClearDetail.setClearId(clearId);
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

    private HashMap<Long,String> getBrandNameById(){
        HashMap<Long,String> objectObjectHashMap = new HashMap<>();
        Response<List<BaseGetBrandInfoListResModel>> branInfoList = baseInfoRemoteServer.getBranInfoList(new BaseGetBrandInfoListReqModel());
        List<BaseGetBrandInfoListResModel> obj = branInfoList.getObj();
        for (BaseGetBrandInfoListResModel baseGetBrandInfoListResModel:obj){
            objectObjectHashMap.put(baseGetBrandInfoListResModel.getBrandId().longValue(),baseGetBrandInfoListResModel.getBrandName());
        }
        return objectObjectHashMap;
    }

    private void toTurn(List<CfInvoiceHeaderListVO> cfInvoiceHeaderListVOS){
        List<String> vendors = cfInvoiceHeaderListVOS.stream().map(CfInvoiceHeaderListVO::getBalance).collect(Collectors.toList());
        Map<String, String> stringMap = cfChargeService.getVendorList(vendors);
        Map<String, String> dicts2 = cfChargeService.getDicts("Charge_Source_Type", cfInvoiceHeaderListVOS);
        HashMap<Long, String> brandNameById = this.getBrandNameById();
        for (int i = 0; i < cfInvoiceHeaderListVOS.size(); i++) {
            CfInvoiceHeaderListVO listVO = cfInvoiceHeaderListVOS.get(i);
            if(brandNameById.containsKey(listVO.getBrandId())){
                listVO.setBrandName(brandNameById.get(listVO.getBrandId()));
            }
            //补充账单对应入库信息（入库通知单类型，销售类型，发票号）
            if (stringMap.containsKey(listVO.getBalance())) {
                listVO.setBalance(stringMap.get(listVO.getBalance()));
            }
            if (dicts2.containsKey(listVO.getJobType())) {
                listVO.setJobType(dicts2.get(listVO.getJobType()));
            }
            //获取结算信息
            LambdaQueryWrapper<CfInvoiceSettlement> lambdaQueryWrapper = Wrappers.lambdaQuery(CfInvoiceSettlement.class);
            lambdaQueryWrapper.eq(CfInvoiceSettlement::getInvoiceId, listVO.getInvoiceId()).notIn(CfInvoiceSettlement::getInvoiceSettlementStatus, 0, 8);
            List<CfInvoiceSettlement> selectList = invoiceSettlementMapper.selectList(lambdaQueryWrapper);
            if (selectList.size() > 0) {
                StringBuilder sd = new StringBuilder();
                StringBuilder settlementStatusSb = new StringBuilder();
                selectList.forEach(a -> {
                    sd.append(a.getInvoiceSettlementNo()).append(",");
                    settlementStatusSb.append(a.getInvoiceSettlementNo()).append(":").append(SettlementStatusEnum.getMsgByCode(a.getInvoiceSettlementStatus()).getMsg()).append("\n");
                });
                listVO.setSettlementNos(sd.substring(0, sd.length() - 1));
                listVO.setSettlementStatus(settlementStatusSb.toString());
                BigDecimal settlementAmount = selectList.stream().map(CfInvoiceSettlement::getInvoiceSettlementMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
                listVO.setAdvancePayMoney(listVO.getAdvancePayMoney() == null ? BigDecimal.ZERO : listVO.getAdvancePayMoney());
                //帐单收付类型(应收应付类型(AR=收debit, AP=付credit))
                if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(listVO.getInvoiceType())) {
                    listVO.setBalanceOfStatement(listVO.getInvoicelDebit().subtract(listVO.getInvoicelCredit()).subtract(BigDecimal.ZERO.subtract(settlementAmount)).subtract(listVO.getAdvancePayMoney()));
                    listVO.setSettlementAmount(BigDecimal.ZERO.subtract(settlementAmount));
                } else if (ChargeEnum.ARAP_TYPE_AP.getCode().equals(listVO.getInvoiceType())) {
                    listVO.setBalanceOfStatement(listVO.getInvoicelCredit().subtract(listVO.getInvoicelDebit()).subtract(settlementAmount).subtract(listVO.getAdvancePayMoney()));
                    listVO.setSettlementAmount(settlementAmount);
                }
                //结算单已经核销的状态下，获取结算单的金额作为已核销金额
                List<CfInvoiceSettlement> clearedList = selectList.stream().filter(a ->
                        StringUtil.isNotEmpty(a.getClearNo())).collect(Collectors.toList());
                BigDecimal totalCleared = BigDecimal.ZERO;
                if(CollectionUtils.isNotEmpty(clearedList)){
                    totalCleared = totalCleared.add(clearedList.stream().map(CfInvoiceSettlement::getInvoiceSettlementMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
                }
                if(totalCleared.compareTo(BigDecimal.ZERO) == 1){
                    //已核销
                    listVO.setClearAmount(totalCleared);
                    listVO.setOverInvoicel(listVO.getInvoicelTotal().subtract(totalCleared));
                }else if(totalCleared.compareTo(BigDecimal.ZERO) == -1){
                    //已核销
                    listVO.setClearAmount(BigDecimal.ZERO.subtract(totalCleared));
                    listVO.setOverInvoicel(listVO.getInvoicelTotal().add(totalCleared));
                }else{
                    //未核销
                    listVO.setOverInvoicel(listVO.getInvoicelTotal());
                    listVO.setClearAmount(BigDecimal.ZERO);
                }
            } else {
                BigDecimal settlementAmount = BigDecimal.ZERO;
                listVO.setSettlementAmount(settlementAmount);
                //帐单收付类型(应收应付类型(AR=收debit, AP=付credit))
                if (ChargeEnum.ARAP_TYPE_AR.getCode().equals(listVO.getInvoiceType())) {
                    listVO.setBalanceOfStatement(listVO.getInvoicelDebit().subtract(listVO.getInvoicelCredit()).subtract(listVO.getAdvancePayMoney() == null ? BigDecimal.ZERO : listVO.getAdvancePayMoney()));
                } else {
                    listVO.setBalanceOfStatement(listVO.getInvoicelCredit().subtract(listVO.getInvoicelDebit()).subtract(listVO.getAdvancePayMoney() == null ? BigDecimal.ZERO : listVO.getAdvancePayMoney()));
                }
                //待结算状态下，已核销金额是0，核销余额应该是总账单金额
                listVO.setOverInvoicel(listVO.getInvoicelTotal());
            }
            OperateUtil.onConvertedDecimal(listVO,"invoicelTotal","settlementAmount","overInvoicel","balanceOfStatement");
            OperateUtil.onConvertedDecimalBySuper(listVO,"clearAmount");
        }
    }
}
