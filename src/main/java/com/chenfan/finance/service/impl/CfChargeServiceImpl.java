package com.chenfan.finance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.common.exception.BusinessException;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.exception.FinanceException;
import com.chenfan.finance.config.BillNoConstantClassField;
import com.chenfan.finance.dao.*;
import com.chenfan.finance.enums.ChargeCheckStatusEnum;
import com.chenfan.finance.enums.ChargeEnum;
import com.chenfan.finance.enums.HttpStateEnum;
import com.chenfan.finance.enums.NumberEnum;
import com.chenfan.finance.model.*;
import com.chenfan.finance.model.dto.BaseGetBrandInfoList;
import com.chenfan.finance.model.dto.CfChargeListQuery;
import com.chenfan.finance.model.dto.CfChargeSaveQuery;
import com.chenfan.finance.model.vo.CfChargeDetailVO;
import com.chenfan.finance.model.vo.CfChargeListVO;
import com.chenfan.finance.model.vo.CfChargeSkuListVO;

import com.chenfan.finance.server.BaseInfoRemoteServer;
import com.chenfan.finance.server.BaseRemoteServer;
import com.chenfan.finance.server.PrivilegeUserServer;
import com.chenfan.finance.server.VendorCenterServer;
import com.chenfan.finance.server.remote.model.BaseDicts;
import com.chenfan.finance.server.remote.request.BrandFeignRequest;
import com.chenfan.finance.server.remote.vo.BrandFeignVO;
import com.chenfan.finance.service.CfChargeService;
import com.chenfan.finance.service.RuleBillingHeaderService;
import com.chenfan.finance.utils.AuthorizationUtil;
import com.chenfan.finance.utils.BeanUtilCopy;
import com.chenfan.finance.utils.FileUtil;
import com.chenfan.finance.utils.OperateUtil;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.chenfan.vendor.response.VendorResModel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author liran
 */
@Slf4j
@Service
public class CfChargeServiceImpl implements CfChargeService {
    @Autowired
    private CfChargeMapper cfChargeMapper;
    @Autowired
    private CfChargeHistoryMapper cfChargeHistoryMapper;
    @Autowired
    private VendorCenterServer vendorCenterServer;
    @Autowired
    private BaseRemoteServer baseRemoteServer;
    @Autowired
    private CfInvoiceHeaderMapper invoiceHeaderMapper;
    @Autowired
    private CfWdtRdRecordHeaderMapper cfWdtRdRecordHeaderMapper;
    @Autowired
    private CfWdtRdRecordDetailMapper cfWdtRdRecordDetailMapper;
    @Autowired
    private CfPoDetailMapper cfPoDetailMapper;
    @Autowired
    private CfInvoiceDetailMapper invoiceDetailMapper;
    @Autowired
    private BaseInfoRemoteServer baseInfoRemoteServer;

    @Autowired
    private PageInfoUtil pageInfoUtil;
    @Resource
    private AuthorizationUtil authorizationUtil;
    @Resource
    private RuleBillingHeaderService ruleBillingHeaderService;

    @Override
    public PageInfo<CfChargeListVO> queryChargeList(CfChargeListQuery query) {

        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        query.setTimeEnd(OperateUtil.getEndTime(query.getTimeEnd()));
        List<CfCharge> list = cfChargeMapper.queryChargeList(query);
        PageInfo<CfCharge> pageDb = new PageInfo<>(list);
        PageInfo<CfChargeListVO> pageInfo = new PageInfo<>(null);
        BeanUtilCopy.copyProperties(pageDb, pageInfo);
        List<String> vendors = list.stream().map(CfCharge::getBalance).collect(Collectors.toList());
        Map<String, String> vendorList = getVendorList(vendors);
        Map<String, String> dicts1 = getDicts("charge_type", list);
        Map<String, String> dicts2 = getDicts("Charge_Source_Type", list);


        List<CfChargeListVO> vos = BeanUtilCopy.copyListProperties(list, CfChargeListVO::new, (s, t) -> {
            t.setChargeSourceType(Integer.valueOf(s.getChargeSource()));
            t.setBalance(vendorList.get(s.getBalance()));
            t.setBalanceCode(s.getBalance());
            //AR=收；AP=付
            t.setBrandName(setBrandName(s.getBrandId()));
            t.setChargeType(dicts1.get(s.getChargeType()));
            t.setChargeSource(dicts2.get(s.getChargeSource() + ""));
            if (s.getAmountPp() == null) {
                s.setAmountPp(BigDecimal.ZERO);
            }
            if (s.getActualAmount() == null) {
                s.setActualAmount(BigDecimal.ZERO);
            }
            t.setBalanceAmount(s.getAmountPp().subtract(s.getActualAmount()));
            String ar = ChargeEnum.ARAP_TYPE_AR.getCode();
            if (ar.equalsIgnoreCase(s.getArapType())) {
                t.setArAmount(s.getAmountPp());
                t.setApAmount(BigDecimal.ZERO);
            } else {
                t.setApAmount(s.getAmountPp());
                t.setArAmount(BigDecimal.ZERO);
            }
        });
        pageInfo.setList(vos);
        return pageInfo;
    }

    @Override
    public Map<String, String> getDicts(String businessType, Collection<?> objects) {
        Map<String, String> dictMap = new HashMap<>(4);
        if (objects.size() < 1) {
            return dictMap;
        }
        try {
            Response<List<BaseDicts>> dictList = baseInfoRemoteServer.getDictList(businessType);
            dictMap = dictList.getObj().stream().collect(Collectors.toMap(BaseDicts::getDictsCode, BaseDicts::getDictsNameC));
        } catch (Exception e) {
            log.error("feign 获取字典错误", e);
        }
        return dictMap;
    }

    public String setBrandName(Long brandId) {
        // 供应商和品牌查询
        try {
            Response<BaseGetBrandInfoList> brandInfo = baseInfoRemoteServer.getBrandInfo(brandId.intValue());
            if (brandInfo == null || brandInfo.getCode() != HttpStatus.OK.value() || brandInfo.getObj() == null) {
                return null;
            }
            return brandInfo.getObj().getBrandName();
        } catch (Exception e) {
            log.error("feign 查询供应商和品牌信息错误", e);
        }
        return null;
    }

    @Override
    public Map<String, String> setBrandNames(List<Integer> brandIds) {
        // 供应商和品牌查询

        BrandFeignRequest brandFeignRequest = new BrandFeignRequest();
        brandFeignRequest.setBrandIdList(brandIds);
        Response<List<BrandFeignVO>> response = baseInfoRemoteServer.getBrandByBrandIdList(brandFeignRequest);
        if (HttpStateEnum.OK.getCode() != response.getCode()) {
            log.error("调用baseinfo服务查询品牌信息报错,参数{}", JSONObject.toJSONString(brandFeignRequest));
            throw new BusinessException(response.getCode(), response.getMessage());
        }

        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(response.getObj())) {
            Map<String, String> map = new HashMap<>(response.getObj().size());
            for (BrandFeignVO brandFeignVO : response.getObj()) {
                map.put(brandFeignVO.getBrandId().toString(), brandFeignVO.getBrandName());
            }
            return map;
        }

        return null;
    }

    @Override
    public Map<String, String> getVendorList(List<String> vendors) {
        Map<String, String> stringMap = new HashMap<>(4);
        try {
            if (vendors.size() < 1) {
                return stringMap;
            }
            Response<List<VendorResModel>> allVendorListRes = vendorCenterServer.getVendorNamesByCodes(vendors);
            List<VendorResModel> allVendorList = allVendorListRes.getObj();
            stringMap = allVendorList.stream().collect(Collectors.toMap(VendorResModel::getVendorCode, VendorResModel::getVenAbbName));
        } catch (Exception e) {
            log.error("feign无法查询供应商{}", e.getMessage());
        }
        return stringMap;
    }

    @Override
    public Map<String, String> getVendorFullNameList(String... vendorsString) {
        List<String> vendors = Arrays.asList(vendorsString);
        Map<String, String> stringMap = new HashMap<>(4);
        try {
            if (vendors.size() < 1) {
                return stringMap;
            }
            Response<List<VendorResModel>> allVendorListRes = vendorCenterServer.getVendorNamesByCodes(vendors);
            List<VendorResModel> allVendorList = allVendorListRes.getObj();
            stringMap = allVendorList.stream().collect(Collectors.toMap(VendorResModel::getVendorCode, VendorResModel::getVendorName));
        } catch (Exception e) {
            log.error("feign无法查询供应商{}", e.getMessage());
        }
        return stringMap;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public long saveCharge(CfChargeSaveQuery saveQuery, UserVO userVO) {
        CfCharge save = new CfCharge();
        BeanUtilCopy.copyProperties(saveQuery, save);
        OperateUtil.onSave(save, userVO);
        String res = pageInfoUtil.generateBusinessNum(BillNoConstantClassField.CRG);
        save.setChargeCode(res);
        if (save.getChargeUnit() == null) {
            save.setChargeUnit("件");
        }
        save.setCheckStatus(1);
        cfChargeMapper.insert(save);
        // 生成质检和延期扣款
        // createDelayAndCheck(save);
        return save.getChargeId();
    }


    private CfPoDetail getPoDetailVo(Long wdtRdRecordId, String sku) {
        List<CfWdtRdRecordDetail> details = cfWdtRdRecordDetailMapper.selectList(Wrappers.<CfWdtRdRecordDetail>lambdaQuery().
                eq(CfWdtRdRecordDetail::getWdtRdRecordId, wdtRdRecordId)
                .eq(CfWdtRdRecordDetail::getIsDelete, 0)
                .eq(CfWdtRdRecordDetail::getInventoryCode, sku)
        );
        Assert.isTrue(!CollectionUtils.isEmpty(details), "根据旺店通单号未找到对应sku入库记录");
        CfWdtRdRecordDetail cfWdtRdRecordDetail = details.get(0);
        Long poDetailId = cfWdtRdRecordDetail.getPoDetailId();
        CfPoDetail cfPoDetail = cfPoDetailMapper.selectById(poDetailId);
        Assert.notNull(cfPoDetail, "手动创建延期扣款/质检费用，根据旺店通入库单单号和sku未匹配到对应的采购单信息");
        return cfPoDetail;
    }

    private CfWdtRdRecordHeader getChargeInBySourceCode(String chargeSourceCode) {
        List<CfWdtRdRecordHeader> wdtHeaders = cfWdtRdRecordHeaderMapper.selectList(Wrappers.<CfWdtRdRecordHeader>lambdaQuery().eq(CfWdtRdRecordHeader::getWdtRdRecordCode, chargeSourceCode).
                eq(CfWdtRdRecordHeader::getIsDelete, 0));
        Assert.isTrue(!CollectionUtils.isEmpty(wdtHeaders), "根据旺店通单号未找到对应的旺店通入库单");
        return wdtHeaders.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCharge(CfChargeSaveQuery saveQuery, UserVO userVO) {
        CfCharge save = new CfCharge();
        BeanUtilCopy.copyProperties(saveQuery, save);
        OperateUtil.onUpdate(save, userVO);
        CfCharge cfCharge = cfChargeMapper.selectByChargeId(save.getChargeId());
        Assert.notNull(cfCharge, "费用记录不存在");
        save.setChargeEdited(2);
        cfChargeMapper.updateById(save);
        // 放入历史表
        CfChargeHistory his = new CfChargeHistory();
        BeanUtilCopy.copyProperties(cfCharge, his);
        cfChargeHistoryMapper.insert(his);
    }

    @Override
    public CfChargeDetailVO detailCharge(Long chargeId) {
        CfCharge cfCharge = cfChargeMapper.selectByChargeId(chargeId);
        CfChargeDetailVO vo = new CfChargeDetailVO();
        BeanUtilCopy.copyProperties(cfCharge, vo);
        setBrandAndVendor(vo);
        return vo;
    }

    @Override
    public void updateChargeCheckStatus(Long chargeId, int status, UserVO userVO) {
        CfCharge cfCharge = cfChargeMapper.selectByChargeId(chargeId);
        Assert.notNull(cfCharge, "费用记录不存在");
        Assert.notNull(cfCharge.getCheckStatus(), "费用状态不存在");
        checkStatus(cfCharge.getCheckStatus(), status);
        CfCharge update = new CfCharge();
        update.setCheckStatus(status);
        update.setChargeId(chargeId);
        OperateUtil.onUpdate(update, userVO);
        // 审核状态修改审核日期
        if (status == ChargeCheckStatusEnum.SH.getCode()) {
            update.setArapCheckDate(LocalDateTime.now());
        }
        cfChargeMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChargeListCheckStatus(Set<Long> chargeIds, int status, UserVO userVO) {
        for (Long chargeId : chargeIds) {
            try {
                updateChargeCheckStatus(chargeId, status, userVO);
            } catch (IllegalArgumentException e) {
                CfCharge cfCharge = cfChargeMapper.selectByChargeId(chargeId);
                throw new IllegalArgumentException(cfCharge == null ? e.getMessage() : "单号：" + cfCharge.getChargeCode() + e.getMessage());
            }
        }
    }

    /**
     * @param checkStatus 原来状态
     * @param status      要修改的状态
     */
    public void checkStatus(Integer checkStatus, int status) {
        ChargeCheckStatusEnum in = ChargeCheckStatusEnum.getMsgByCode(checkStatus);
        ChargeCheckStatusEnum up = ChargeCheckStatusEnum.getMsgByCode(status);
        //1草稿、2已提交、3已审核、4已驳回、5已作废、0已删除
        Assert.notNull(in, "当前费用状态不存在");
        Assert.notNull(up, "要修改的状态不存在");
        Assert.isTrue(in != ChargeCheckStatusEnum.SC, "费用记录已删除");
        Assert.isTrue(in != up, "当前状态已是 " + in.getMsg());

        // 1草稿  -》   NOT  3已审核、4已驳回
        if (in == ChargeCheckStatusEnum.CG) {
            doCheckStatus(in, up, ChargeCheckStatusEnum.SH, ChargeCheckStatusEnum.BH);
        }
        // 2已提交  NOT  1草稿  5已作废、0已删除
        if (in == ChargeCheckStatusEnum.TJ) {
            doCheckStatus(in, up, ChargeCheckStatusEnum.CG, ChargeCheckStatusEnum.ZF, ChargeCheckStatusEnum.SC);
        }
        // 3已审核  NOT 1草稿、2已提交、4已驳回、5已作废、0已删除
        if (in == ChargeCheckStatusEnum.SH) {
            doCheckStatus(in, up, ChargeCheckStatusEnum.CG, ChargeCheckStatusEnum.TJ, ChargeCheckStatusEnum.BH, ChargeCheckStatusEnum.ZF, ChargeCheckStatusEnum.SC);
        }
        // 5已作废  NOT 1草稿、2已提交、3已审核、4已驳回
        if (in == ChargeCheckStatusEnum.ZF) {
            doCheckStatus(in, up, ChargeCheckStatusEnum.CG, ChargeCheckStatusEnum.TJ, ChargeCheckStatusEnum.SH, ChargeCheckStatusEnum.BH);
        }

    }

    private void doCheckStatus(ChargeCheckStatusEnum in, ChargeCheckStatusEnum update, ChargeCheckStatusEnum... not) {
        String ms = " 状态不能变更为 ";
        for (ChargeCheckStatusEnum chargeCheckStatusEnum : not) {
            Assert.isTrue(update != chargeCheckStatusEnum, in.getMsg() + ms + chargeCheckStatusEnum.getMsg());
        }
    }

    private void setBrandAndVendor(CfChargeDetailVO vo) {
        // 供应商和品牌查询
        try {
            Response<VendorResModel> vendorByCode = vendorCenterServer.getVendorByCode(null, vo.getBalance());
            vo.setBalanceName(vendorByCode.getObj().getVenAbbName());
            vo.setCvenBank(vendorByCode.getObj().getCvenBank());
            vo.setCvenAccount(vendorByCode.getObj().getCvenAccount());
            vo.setBrandName(setBrandName(vo.getBrandId()));
        } catch (Exception e) {
            log.error("feign 查询供应商和品牌信息错误 : {}", e.getMessage());
        }
    }

    @Override
    public int delRelevance(String invoiceNo, List<Long> inputcharges) throws FinanceException {
        List<CfCharge> all = cfChargeMapper.selectList(Wrappers.lambdaQuery(CfCharge.class)
                .eq(CfCharge::getInvoiceNo, invoiceNo));
        List<CfCharge> rels = all.stream().filter(a -> inputcharges.stream().anyMatch(b -> b.equals(a.getChargeId()))).collect(Collectors.toList());
        if (rels.size() < 1) {
            return 1;
        }
        List<Long> chargeIds = rels.stream().map(CfCharge::getChargeId).collect(Collectors.toList());
        int i = cfChargeMapper.delRelevance(invoiceNo, chargeIds);
        List<CfInvoiceHeader> cfInvoiceHeaders = invoiceHeaderMapper.selectList(Wrappers.lambdaQuery(CfInvoiceHeader.class)
                .eq(CfInvoiceHeader::getInvoiceNo, invoiceNo));
        CfInvoiceHeader cfInvoiceHeader = cfInvoiceHeaders.get(0);
        invoiceDetailMapper.delete(Wrappers.lambdaQuery(CfInvoiceDetail.class)
                .eq(CfInvoiceDetail::getInvoiceId, cfInvoiceHeader.getInvoiceId())
                .in(CfInvoiceDetail::getChargeId, chargeIds));
        List<CfCharge> charges = cfChargeMapper.selectList(Wrappers.lambdaQuery(CfCharge.class)
                .eq(CfCharge::getInvoiceNo, invoiceNo));
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
        return i;
    }


    private List<CfChargeSkuListVO> getMatchByGroupid(List<CfCharge> chargesAll, String chargeIds) {
        Set<String> chargeIdsNew = new HashSet<>();
        getIds(chargeIds, chargeIdsNew);
        if (chargeIdsNew.size() < 1) {
            return new LinkedList<>();
        }
        List<Long> longs = transferIdsToLong(chargeIdsNew);
        List<CfCharge> match = chargesAll.stream().filter(a -> longs.stream().anyMatch(id -> id.equals(a.getChargeId()))).collect(Collectors.toList());
        return BeanUtilCopy.copyListProperties(match, CfChargeSkuListVO::new);
    }


    private List<Integer> transferIdsToInt(Set<String> brandIdsSet) {
        List<Integer> ids = new ArrayList<>(brandIdsSet.size());
        for (String s : brandIdsSet) {
            ids.add(Integer.parseInt(s));
        }
        return ids;
    }

    public static List<Long> transferIdsToLong(Set<String> brandIdsSet) {
        List<Long> ids = new ArrayList<>(brandIdsSet.size());
        for (String s : brandIdsSet) {
            ids.add(Long.parseLong(s));
        }
        return ids;
    }

    public static void getIds(String sss, Set<String> set) {
        if (StringUtils.isNotBlank(sss)) {
            String[] split = sss.split(",");
            for (String s : split) {
                set.add(s);
            }
        }
    }

    @Override
    public PageInfo<CfChargeListVO> chargeListGroupByChargeSource(CfChargeListQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        query.setTimeEnd(OperateUtil.getEndTime(query.getTimeEnd()));
        List<CfChargeListVO> list = cfChargeMapper.chargeListChargeSourceGroup(query);
        PageInfo<CfChargeListVO> pageInfo = new PageInfo<>(list);
        Set<String> vendorsSet = new HashSet<>();
        Set<String> brandsSet = new HashSet<>();
        Set<String> chargeIds = new HashSet<>();
        for (CfChargeListVO cfChargeListVO : list) {
            getIds(cfChargeListVO.getBrands(), brandsSet);
            getIds(cfChargeListVO.getBalances(), vendorsSet);
            getIds(cfChargeListVO.getChargeIds(), chargeIds);
        }
        Map<String, String> vendorList = getVendorList(new ArrayList<>(vendorsSet));
        Map<String, String> brandIdList = setBrandNames(transferIdsToInt(brandsSet));
        List<Long> longs = transferIdsToLong(chargeIds);
        List<CfCharge> chargesAll;
        if (longs.size() < 1) {
            pageInfo.setList(list);
            return pageInfo;
        }
        chargesAll = cfChargeMapper.selectBatchIds(longs);
        Map<String, String> dicts1 = getDicts("charge_type", list);
        Map<String, String> dicts2 = getDicts("Charge_Source_Type", list);

        //新逻辑: 根据同一spu、同一销售类型、同一费用来源单号、同一费用种类、同一费用来源获取sku维度的数据
        for (CfChargeListVO cfCharge : list) {
            List<CfChargeSkuListVO> chargeSkuList = getMatchByGroupid(chargesAll, cfCharge.getChargeIds());

            for (CfChargeSkuListVO skuListVO : chargeSkuList) {
                skuListVO.setBalance(vendorList.get(skuListVO.getBalance()));
                skuListVO.setBalanceCode(skuListVO.getBalance());
                skuListVO.setBrandName(brandIdList.get(skuListVO.getBrandId().toString()));
                skuListVO.setChargeType(dicts1.get(skuListVO.getChargeType()));
                skuListVO.setChargeSource(dicts2.get(skuListVO.getChargeSource() + ""));
                if (skuListVO.getAmountPp() == null) {
                    skuListVO.setAmountPp(BigDecimal.ZERO);
                }
                if (skuListVO.getActualAmount() == null) {
                    skuListVO.setActualAmount(BigDecimal.ZERO);
                }
                skuListVO.setBalanceAmount(skuListVO.getAmountPp().subtract(skuListVO.getActualAmount()));
                String ar = ChargeEnum.ARAP_TYPE_AR.getCode();
                if (ar.equalsIgnoreCase(skuListVO.getArapType())) {
                    skuListVO.setArAmount(skuListVO.getAmountPp());
                    skuListVO.setApAmount(BigDecimal.ZERO);
                } else {
                    skuListVO.setApAmount(skuListVO.getAmountPp());
                    skuListVO.setArAmount(BigDecimal.ZERO);
                }
            }

            cfCharge.setChargeQty(chargeSkuList.stream().map(CfChargeSkuListVO::getChargeQty).reduce(0, Integer::sum));
            cfCharge.setAmountPp(chargeSkuList.stream().map(CfChargeSkuListVO::getAmountPp).reduce(BigDecimal.ZERO, BigDecimal::add));
            BigDecimal qty = new BigDecimal(cfCharge.getChargeQty());
            cfCharge.setPricePp(cfCharge.getAmountPp().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : cfCharge.getAmountPp().divide(qty, 4, BigDecimal.ROUND_HALF_UP));
            cfCharge.setBalance(vendorList.get(cfCharge.getBalance()));
            cfCharge.setBalanceCode(cfCharge.getBalance());
            //AR=收；AP=付
            cfCharge.setBrandName(brandIdList.get(cfCharge.getBrandId().toString()));
            cfCharge.setChargeType(dicts1.get(cfCharge.getChargeType()));
            cfCharge.setChargeSource(dicts2.get(cfCharge.getChargeSource() + ""));
            if (cfCharge.getAmountPp() == null) {
                cfCharge.setAmountPp(BigDecimal.ZERO);
            }
            if (cfCharge.getActualAmount() == null) {
                cfCharge.setActualAmount(BigDecimal.ZERO);
            }
            cfCharge.setBalanceAmount(cfCharge.getAmountPp().subtract(cfCharge.getActualAmount()));
            String ar = ChargeEnum.ARAP_TYPE_AR.getCode();
            if (ar.equalsIgnoreCase(cfCharge.getArapType())) {
                cfCharge.setArAmount(cfCharge.getAmountPp());
                cfCharge.setApAmount(BigDecimal.ZERO);
            } else {
                cfCharge.setApAmount(cfCharge.getAmountPp());
                cfCharge.setArAmount(BigDecimal.ZERO);
            }
            cfCharge.setChargeCode("-");
            cfCharge.setChargeSkuListVO(chargeSkuList);
        }
        pageInfo.setList(list);
        return pageInfo;
    }



    @Override
    public Object verifyChargeList(CfChargeListQuery query, HttpServletResponse response) throws Exception {

        query.setTimeEnd(OperateUtil.getEndTime(query.getTimeEnd()));
        Map<String, String> dicts1 = getDicts("charge_type", Collections.singletonList("1"));
        query.setHk(getTargeHk(dicts1, "货品采购"));
        query.setDelay(getTargeHk(dicts1, "延期"));
        query.setRed(getTargeHk(dicts1, "退次"));
        query.setQc(getTargeHk(dicts1,"质检"));
        List<Long> longs = cfChargeMapper.selectRdRecodeDetailIdsByCharge(query);
        if(CollectionUtils.isEmpty(longs)){
            longs.add(-1L);
        }
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        if (query.getExport() != null && query.getExport()) {
            PageHelper.startPage(query.getPageNum(), Integer.MAX_VALUE);
        }
        query.setRdRecordDetailIdList(longs);
        List<CfChargeListVO> list = cfChargeMapper.verifyChargeListPre(query);

        Set<String> vendorsSet = new HashSet<>();
        Set<String> brandsSet = new HashSet<>();
        for (CfChargeListVO cfChargeListVO : list) {
            getIds(cfChargeListVO.getBrands(), brandsSet);
            getIds(cfChargeListVO.getBalances(), vendorsSet);
        }
        Map<String, String> vendorList = getVendorList(new ArrayList<>(vendorsSet));
        Map<String, String> brandIdList = setBrandNames(transferIdsToInt(brandsSet));

        mergePostponeDeltyDetail(list);
        for (CfChargeListVO d : list) {
            Set<String> chargeIdAll = new HashSet<>();
            if (StringUtils.isNotBlank(d.getSalesTypeAll())) {
                d.setSalesTypeAll(setSalstype(d.getSalesTypeAll()));
            }
            if (d.getSettlementAmount().compareTo(BigDecimal.ZERO) >= 0) {
                d.setArapType("应付");
            } else {
                d.setArapType("应收");
            }
            String[] brandsList = d.getBrands().split(",");
            String[] bals = d.getBalances().split(",");
            d.setBrandName(getBrandListName(brandIdList, brandsList));
            d.setBalance(getBrandListName(vendorList, bals));
            getIds(d.getChargeIds(), chargeIdAll);
            getIds(d.getChargeIdsDelay(), chargeIdAll);
            getIds(d.getChargeIdsQc(),chargeIdAll);
            d.setChargeIdAll(chargeIdAll);
            d.setPricePp(d.getPricePp());
            d.setSettlementAmountActual(Objects.isNull(d.getSettlementAmountActual())?BigDecimal.ZERO:d.getSettlementAmountActual().setScale(2,BigDecimal.ROUND_HALF_UP));
            d.setSettlementAmount(Objects.isNull(d.getSettlementAmount())?BigDecimal.ZERO:d.getSettlementAmount().setScale(2,BigDecimal.ROUND_HALF_UP));
            d.setPostponeDeductionsTotal(Objects.isNull(d.getPostponeDeductionsTotal())?BigDecimal.ZERO:d.getPostponeDeductionsTotal().setScale(2,BigDecimal.ROUND_HALF_UP));
            if(Objects.nonNull(d.getIsOffset())&&d.getIsOffset()==1){
                d.setActualQty(1);
                d.setSettlementAmount(d.getPricePp().multiply(BigDecimal.ONE).setScale(2,BigDecimal.ROUND_HALF_UP));
                d.setSettlementAmountActual(d.getPricePp().multiply(BigDecimal.ONE).setScale(2,BigDecimal.ROUND_HALF_UP));
            }
        }
        PageInfo<CfChargeListVO> pageInfo = new PageInfo<>(list);
        getAllThisPage(pageInfo);
        if (query.getExport() != null && query.getExport()) {
            String name = "费用对账列表";
            FileUtil.exportExcelV2(list, name,"导出人：" + authorizationUtil.getUserName(), name, CfChargeListVO.class, name + ".xlsx", response);
            return null;
        }
        return pageInfo;
    }

    private void getAllThisPage(PageInfo<CfChargeListVO> pageInfo) {
        List<CfChargeListVO> list = pageInfo.getList();
        if (list.size() < 1) {
            return;
        }
        BigDecimal settlementAmount = BigDecimal.ZERO;
        BigDecimal settlementAmountActul = BigDecimal.ZERO;
        BigDecimal postponeDeductionsTotal = BigDecimal.ZERO;
        BigDecimal advancepayAmount = BigDecimal.ZERO;
        BigDecimal advancepayAmountActual = BigDecimal.ZERO;
        BigDecimal qa = BigDecimal.ZERO;
        int arrQty = 0;
        int rejQty = 0;
        int actQty = 0;
        int defQty = 0;
        int asQty=0;
        CfChargeListVO all = new CfChargeListVO();
        for (CfChargeListVO d : list) {
            if (d.getArrivalQty() != null) {
                arrQty = arrQty + d.getArrivalQty();
            }
            if (d.getRejectionQty() != null) {
                rejQty = rejQty + d.getRejectionQty();
            }
            if (d.getActualQty() != null) {
                actQty = actQty + d.getActualQty();
            }
            if (d.getDefectiveRejectionQty() != null) {
                defQty = defQty + d.getDefectiveRejectionQty();
            }
            if(d.getSettlementQty() !=null ){
                asQty=asQty+d.getSettlementQty();
            }
            if (d.getSettlementAmount() != null) {
                settlementAmount = settlementAmount.add(d.getSettlementAmount());
            }
            if (d.getSettlementAmountActual() != null) {
                settlementAmountActul = settlementAmountActul.add(d.getSettlementAmountActual());
            }
            if (d.getPostponeDeductionsTotal() != null) {
                postponeDeductionsTotal = postponeDeductionsTotal.add(d.getPostponeDeductionsTotal());
            }
            if (d.getAdvancepayAmountActual() != null) {
                advancepayAmountActual = advancepayAmountActual.add(d.getAdvancepayAmountActual());
            }
            if (d.getAdvancepayAmount() != null) {
                advancepayAmount = advancepayAmount.add(d.getAdvancepayAmount());
            }
            if (d.getQaDeductions() != null) {
                qa = qa.add(d.getQaDeductions());
            }

        }
        all.setArrivalQty(arrQty);
        all.setRejectionQty(rejQty);
        all.setDefectiveRejectionQty(defQty);
        all.setActualQty(actQty);
        all.setSettlementQty(asQty);
        all.setSettlementAmount(settlementAmount);
        all.setSettlementAmountActual(settlementAmountActul);
        all.setPostponeDeductionsTotal(postponeDeductionsTotal);
        all.setAdvancepayAmount(advancepayAmount);
        all.setAdvancepayAmountActual(advancepayAmountActual);
        all.setQaDeductions(qa);
        all.setBalance("合计");
        list.add(all);
    }

    @Override
    public void updateCheckStatus(Set<Long> chargeIds, Integer status, UserVO userVO) {
        status = ChargeCheckStatusEnum.SH.getCode();
        List<CfCharge> charges = cfChargeMapper.selectBatchIds(new ArrayList<>(chargeIds));
        for (CfCharge charge : charges) {
            Assert.isTrue(StringUtils.isBlank(charge.getInvoiceNo()), charge.getChargeCode() + " 费用" + charge.getInvoiceNo() + "账单引用了");
            CfCharge update = new CfCharge();
            update.setCheckStatus(status);
            update.setChargeId(charge.getChargeId());
            OperateUtil.onUpdate(update, userVO);
            // 审核状态修改审核日期
            if (status == ChargeCheckStatusEnum.SH.getCode()) {
                update.setArapCheckDate(LocalDateTime.now());
            }
            cfChargeMapper.updateById(update);
        }
    }

    private Integer getTargeHk(Map<String, String> chargeType, String... s) {
        AtomicReference<String> code = new AtomicReference<>();
        chargeType.forEach((k, v) -> {
            for (String s1 : s) {
                if (v.contains(s1)) {
                    code.set(k);
                }
            }
        });
        return Integer.parseInt(code.get());
    }

    private String getBrandListName(Map<String, String> map, String[] brandsList) {
        StringBuilder bd = new StringBuilder("");
        for (String s : brandsList) {
            bd.append(map.get(s)).append(",");
        }
        return bd.substring(0, bd.length() - 1);
    }

    private String setSalstype(String salesTypeAll) {
        String[] collect = salesTypeAll.split(",");
        StringBuilder bd = new StringBuilder("");
        if (collect.length < 1) {
            bd.append("类型不存在").append(",");
        }
        for (String integer : collect) {
            String ty = "1";
            if (ty.equals(integer)) {
                bd.append("直播款").append(",");
            } else {
                bd.append("订单款").append(",");
            }
        }
        return bd.substring(0, bd.length() - 1);
    }

    private Set<String> getAllRuleBillDetails(Integer goodsRange) {
        String ruleType = "1";
        List<RuleBillingDetail> ruleBillingDetailList = ruleBillingHeaderService.queryRuleBilling(ruleType);
        return ruleBillingDetailList.stream().filter((a) -> ruleType.equals(a.getRuleType()) && goodsRange.equals(a.getGoodsRange()))
                .map(RuleBillingDetail::getRuleLevelCondition).collect(Collectors.toSet());
    }

    /**
     * 处理延期
     * @param chargeInList
     */
    private void mergePostponeDeltyDetail(List<CfChargeListVO> chargeInList){
        Set<String> allRuleBillDetails = getAllRuleBillDetails(1);
        for (CfChargeListVO c:chargeInList) {
        //HashMap<String, Integer> objectObjectHashMap = new HashMap<>();
            Integer postponeDetail35=0;
            Integer postponeDetail67=0;
            Integer postponeDetail79=0;
            if(org.apache.commons.lang.StringUtils.isNotEmpty(c.getPostponeDetail())){
                String postponeDetail = c.getPostponeDetail();
                String[] split = postponeDetail.split(",");
                for (String s : split) {
                    HashMap<String,Integer> object = JSONObject.parseObject(s, HashMap.class);
                    if (object == null) {
                        continue;
                    }
                    for(Map.Entry<String, Integer> entry: object.entrySet()){
                        String key = entry.getKey();
                        Integer value = entry.getValue();
                       /* if(objectObjectHashMap.containsKey(key)){
                            objectObjectHashMap.put(key,objectObjectHashMap.get(key)+value);
                        }else {
                            objectObjectHashMap.put(key,value);
                        }*/
                        switch (key){
                            case "3-5":
                                postponeDetail35=postponeDetail35+value;
                                break;
                            case "6-7":
                                postponeDetail67=postponeDetail67+value;
                                break;
                            case "7-9999":
                                postponeDetail79=postponeDetail79+value;
                                break;
                            default:
                                log.info("延期计费方案错误,不允许配置以为的数据：{}",key);
                                break;
                        }

                    }


                   /* String[] keys = s.split("-");
                    if(keys!=null&&keys.length>1){

                    }*/
                }
            }
            c.setPostponeDetail35(String.valueOf(postponeDetail35));
            c.setPostponeDetail67(String.valueOf(postponeDetail67));
            c.setPostponeDetail79(String.valueOf(postponeDetail79));
           // c.setPostponeDetailArray(objectObjectHashMap)
        }
    }
    @Deprecated
    private void mergePostponeDetail(List<CfChargeListVO> chargeInList) {
        // {"1-3": 29},{"8-": 50},{"8-": 50}
        Set<String> keys = new HashSet<>();
        for (CfChargeListVO chargeInVO : chargeInList) {
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
        if (keys.size() > 0) {
            keys.addAll(getAllRuleBillDetails(1));
        }
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
        for (CfChargeListVO chargeInVO : chargeInList) {
            String postponeDetail = chargeInVO.getPostponeDetail();
            String[] substring = postponeDetail.split(",");
            Map<String, Integer> objectToArray = new LinkedHashMap<>();
            List<Map<String, Integer>> sortArray = new LinkedList<>();
            for (String key : keysSorted) {
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
                jsonObject.put(keyAlias, objectToArray.get(key));
                sortArray.add(jsonObject);
            }
            chargeInVO.setPostponeDetail(JSONObject.toJSONString(sortArray));
            chargeInVO.setPostponeDetailArray(sortArray);
            if (sortArray.size() >= 3) {
                sortArray.get(0).forEach((k, v) -> {
                    chargeInVO.setPostponeDetail35(String.valueOf(v));
                });
                sortArray.get(1).forEach((k, v) -> {
                    chargeInVO.setPostponeDetail67(String.valueOf(v));
                });
                sortArray.get(2).forEach((k, v) -> {
                    chargeInVO.setPostponeDetail79(String.valueOf(v));
                });
            }

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


}
