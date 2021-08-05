package com.chenfan.finance.service.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.common.vo.Response;
import com.chenfan.finance.config.BillNoConstantClassField;
import com.chenfan.finance.dao.CfInvoiceDetailMapper;
import com.chenfan.finance.dao.CfInvoiceHeaderMapper;
import com.chenfan.finance.dao.CfMultyImageMapper;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.exception.FinanceBizState;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.*;
import com.chenfan.finance.model.dto.*;
import com.chenfan.finance.model.vo.*;
import com.chenfan.finance.server.BaseInfoRemoteServer;
import com.chenfan.finance.server.McnRemoteServer;
import com.chenfan.finance.server.PrivilegeUserServer;
import com.chenfan.finance.server.dto.AccountInfoQueryDTO;
import com.chenfan.finance.server.dto.CompanyNameReq;
import com.chenfan.finance.server.remote.model.SCompanyBank;
import com.chenfan.finance.server.remote.model.ThirdPayAccount;
import com.chenfan.finance.server.remote.request.AccountPlatformReq;
import com.chenfan.finance.server.remote.vo.ExcutionSettleInfoVO;
import com.chenfan.finance.server.remote.vo.FinanceAccountInfoVO;
import com.chenfan.finance.server.remote.vo.InvoiceAccountInfoVO;
import com.chenfan.finance.service.common.state.InvoiceStateService;
import com.chenfan.finance.service.impl.CfClearHeaderServiceImpl;
import com.chenfan.finance.utils.OperateUtil;
import com.chenfan.finance.utils.RpcUtil;
import com.chenfan.finance.utils.pageinfo.MultyImageVo;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.chenfan.finance.utils.pageinfo.TimeThreadSafeUtils;
import com.chenfan.finance.utils.pageinfo.model.CreateVo;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 账单
 *
 * @author: xuxianbei
 * Date: 2021/3/1
 * Time: 15:18
 * Version:V1.0
 */
@Slf4j
@Service
public class InvoiceCommonService {

    @Autowired
    private McnConfiguration mcnConfiguration;

    @Autowired
    private ChargeCommonService chargeCommonService;

    @Autowired
    private InvoiceStateService invoiceStateService;

    @Autowired
    private McnRemoteServer mcnRemoteServer;

    @Autowired
    private PageInfoUtil pageInfoUtil;

    @Resource
    private CfInvoiceHeaderMapper cfInvoiceHeaderMapper;

    @Resource
    private CfMultyImageMapper cfMultyImageMapper;

    @Resource
    private CfInvoiceDetailMapper cfInvoiceDetailMapper;

    @Autowired
    private CfClearHeaderServiceImpl cfClearHeaderService;

    @Autowired
    private PrivilegeUserServer privilegeUserServer;

    @Autowired
    private BaseInfoRemoteServer baseInfoRemoteServer;


    public CfInvoiceCommonPreVo preAddView(Long chargeId) {
        CfChargeCommon cfChargeCommon = chargeCommonService.selectOne(chargeId);
        if (Objects.isNull(cfChargeCommon)) {
            throw FinanceBizState.DATE_ERROR;
        }
        CfInvoiceCommonPreVo cfInvoiceCommonPreVo = cfChargeCommonToInvoiceCommonVo(cfChargeCommon);

        return cfInvoiceCommonPreVo;
    }

    private void vertifyInvoiceCommon(CfInvoiceCommonPreVo cfInvoiceCommonPreVo) {
        Assert.isTrue(StringUtils.isNotEmpty(cfInvoiceCommonPreVo.getBalance()), ModuleBizState.DATE_ERROR.message());
        Assert.isTrue(ChargeEnum.vertify(cfInvoiceCommonPreVo.getInvoiceType()), ModuleBizState.DATE_ERROR.message());
        Assert.isTrue(StringUtils.isNotEmpty(cfInvoiceCommonPreVo.getInvoiceTitle()), ModuleBizState.DATE_ERROR.message());
        Assert.isTrue(StringUtils.isNotEmpty(cfInvoiceCommonPreVo.getInvoiceTitleName()), ModuleBizState.DATE_ERROR.message());
        Assert.isTrue(StringUtils.isNotEmpty(cfInvoiceCommonPreVo.getFinanceEntity()), ModuleBizState.DATE_ERROR.message());
        Assert.isTrue(StringUtils.isNotEmpty(cfInvoiceCommonPreVo.getBank()), ModuleBizState.DATE_ERROR.message());
        Assert.isTrue(StringUtils.isNotEmpty(cfInvoiceCommonPreVo.getBankAccounts()), ModuleBizState.DATE_ERROR.message());
    }

    private CfInvoiceCommonPreVo cfChargeCommonToInvoiceCommonVo(CfChargeCommon cfChargeCommon) {
        CfInvoiceCommonPreVo cfInvoiceCommonPreVo = new CfInvoiceCommonPreVo();
        BeanUtils.copyProperties(cfChargeCommon, cfInvoiceCommonPreVo, "remark", "invoiceDate");
        cfInvoiceCommonPreVo.setJobType(String.valueOf(InvoiceHeaderEnum.JOB_TYPE_THREE.getCode()));
        cfInvoiceCommonPreVo.setInvoicelCredit(cfChargeCommon.getAmountPp());
        cfInvoiceCommonPreVo.setInvoiceType(cfChargeCommon.getArapType());
        CfChargeCommonVo cfChargeCommonVo = new CfChargeCommonVo();
        BeanUtils.copyProperties(cfChargeCommon, cfChargeCommonVo);
        cfInvoiceCommonPreVo.getChargeCommonVos().add(cfChargeCommonVo);
        Response<FinanceAccountInfoVO> response = mcnRemoteServer.getFinanceAccount(cfChargeCommon.getChargeType(), cfChargeCommon.getChargeSourceCode(),
                cfChargeCommon.getChargeSourceDetail());
        FinanceAccountInfoVO financeAccountInfoVO = RpcUtil.getObjException(response, ModuleBizState.DATE_ERROR.message());
        cfInvoiceCommonPreVo.setBank(financeAccountInfoVO.getAccountBank());
        cfInvoiceCommonPreVo.setBankAccounts(financeAccountInfoVO.getAccountNumber());
        vertifyInvoiceCommon(cfInvoiceCommonPreVo);
        return cfInvoiceCommonPreVo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer add(InvoiceCommonAddDto invoiceCommonAddDto) {
        /**
         * 支出生成账单：一个费用只能生成一个非作废账单且只能选择一个费用且类型必须是支出；红人采购费--增加校验图片
         *   账单反写费用，
         *   费用种类为红人采购费，需要增加一个上传双盖合同字段
         */
        CfChargeCommon cfChargeCommon = chargeCommonService.selectOne(invoiceCommonAddDto.getChargeId());
        Assert.isTrue(ChargeEnum.ARAP_TYPE_AP.getCode().equals(cfChargeCommon.getArapType()) &&
                        StringUtils.isBlank(cfChargeCommon.getInvoiceNo()) && StringUtils.isBlank(cfChargeCommon.getTaxInvoiceNo())
                        && ChargeCommonEnum.CHECK_STSTUS_ZERO.getCode().intValue() != cfChargeCommon.getCheckStatus()
                , ModuleBizState.DATE_ERROR.message());
        vertify(invoiceCommonAddDto.getContractUrls(), cfChargeCommon.getChargeType());
        //新增账单
        CfInvoiceHeader cfInvoiceHeader = createInvoiceHeader(invoiceCommonAddDto, cfChargeCommon);
        ExcutionSettleInfoVO excutionSettleInfoVO = getExcutionSettleInfoVOForCharge(cfChargeCommon.getChargeType(), cfChargeCommon.getChargeSourceCode());
        cfInvoiceHeader.setSettleTemplate(excutionSettleInfoVO.getSettleTemplate());
        cfInvoiceHeaderMapper.insert(cfInvoiceHeader);
        //更新合同
        updateContactUrls(invoiceCommonAddDto.getContractUrls(), cfInvoiceHeader);

        CfInvoiceDetail cfInvoiceDetail = createInvoiceDetail(cfChargeCommon, cfInvoiceHeader);
        cfInvoiceDetailMapper.insert(cfInvoiceDetail);
        //反写费用的账单
        chargeCommonService.updateByInvoice(cfChargeCommon.getChargeId(), cfInvoiceHeader.getInvoiceNo(), cfInvoiceHeader.getCreateDate());
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_INVOICE, OperationTypeEnum.OPERATION_CREATE,cfInvoiceHeader.getInvoiceNo(),cfInvoiceHeader.getInvoiceId());

        return 1;
    }

    /**
     * 用种类为红人采购费，需要增加一个上传双盖合同字段
     *
     * @param contractUrl
     * @param chargeType
     */
    private void vertify(List<MultyImageVo> contractUrl, Integer chargeType) {
        if (ChargeCommonEnum.CHARGE_TYPE_FOUR.getCode().equals(chargeType)) {
            Assert.isTrue(CollectionUtils.isNotEmpty(contractUrl), ModuleBizState.DATE_ERROR.message());
        }
    }

    private CfInvoiceDetail createInvoiceDetail(CfChargeCommon cfChargeCommon, CfInvoiceHeader cfInvoiceHeader) {
        CfInvoiceDetail cfInvoiceDetail = new CfInvoiceDetail();
        BeanUtils.copyProperties(cfChargeCommon, cfInvoiceDetail);
        cfInvoiceDetail.setInvoiceId(cfInvoiceHeader.getInvoiceId());
        cfInvoiceDetail.setInvoiceQty(1);
        cfInvoiceDetail.setInvoiceCredit(cfChargeCommon.getAmountPp());
        cfInvoiceDetail.setInvoiceDebit(BigDecimal.ZERO);
        cfInvoiceDetail.setChargeType(String.valueOf(cfChargeCommon.getChargeType()));
        cfInvoiceDetail.setProductCode("");
        return cfInvoiceDetail;
    }

    private CfInvoiceHeader createInvoiceHeader(InvoiceCommonAddDto invoiceCommonAddDto, CfChargeCommon cfChargeCommon) {
        CfInvoiceHeader cfInvoiceHeader = new CfInvoiceHeader();
        CfInvoiceCommonPreVo cfInvoiceCommonPreVo = cfChargeCommonToInvoiceCommonVo(cfChargeCommon);
        BeanUtils.copyProperties(cfInvoiceCommonPreVo, cfInvoiceHeader);
        BeanUtils.copyProperties(invoiceCommonAddDto, cfInvoiceHeader);
        cfInvoiceHeader.setInvoiceNo(pageInfoUtil.generateBusinessNum(BillNoConstantClassField.INVOICE));
        cfInvoiceHeader.setInvoiceStatus(InvoiceHeaderEnum.INVOICE_STATUS_ONE.getCode());
        cfInvoiceHeader.setInvoicelDebit(BigDecimal.ZERO);
        cfInvoiceHeader.setClearAmount(BigDecimal.ZERO);
        cfInvoiceHeader.setBrandId(Long.valueOf(mcnConfiguration.getBrandIdMcn()));
        pageInfoUtil.baseInfoFillOld(cfInvoiceHeader);
        return cfInvoiceHeader;
    }

    public PageInfo<CfInvoiceCommonVo> list(InvoiceCommonDto invoiceCommonDto) {
        LambdaQueryWrapper<CfInvoiceHeader> queryWrapper = condition(invoiceCommonDto);
        PageInfoUtil.startPage(invoiceCommonDto);
        List<CfInvoiceHeader> cfInvoiceHeaderList = cfInvoiceHeaderMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(cfInvoiceHeaderList)) {
            return PageInfoUtil.emptyPageInfo();
        }
        List<CfInvoiceDetail> cfInvoiceDetails = cfInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfInvoiceDetail.class).in(CfInvoiceDetail::getInvoiceId,
                PageInfoUtil.lambdaToList(cfInvoiceHeaderList, CfInvoiceHeader::getInvoiceId)));
        //账单ID，发票号
        Map<Long, CfInvoiceDetail> invoiceIdNoMap =
                cfInvoiceDetails.stream().collect(Collectors.toMap(CfInvoiceDetail::getInvoiceId, a -> a, (a, b) -> a));

        List<CfInvoiceCommonVo> cfInvoiceCommonVos = cfInvoiceHeaderList.stream().map(cfInvoiceHeader -> {
            CfInvoiceCommonVo cfInvoiceCommonVo = new CfInvoiceCommonVo();
            PageInfoUtil.copyProperties(cfInvoiceHeader, cfInvoiceCommonVo);
            CfInvoiceDetail cfInvoiceDetail = invoiceIdNoMap.get(cfInvoiceCommonVo.getInvoiceId());
            cfInvoiceCommonVo.setClearAmountBalance(cfInvoiceCommonVo.getInvoicelCredit().subtract(cfInvoiceCommonVo.getClearAmount()));
            cfInvoiceCommonVo.setCustomerInvoiceNo(cfInvoiceDetail.getCustomerInvoiceNo());
            cfInvoiceCommonVo.setChargeSourceCode(cfInvoiceDetail.getChargeSourceCode());
            cfInvoiceCommonVo.setChargeType(cfInvoiceDetail.getChargeType());
            return cfInvoiceCommonVo;
        }).collect(Collectors.toList());
        return PageInfoUtil.toPageInfo(cfInvoiceHeaderList, cfInvoiceCommonVos);
    }

    private LambdaQueryWrapper<CfInvoiceHeader> condition(InvoiceCommonDto invoiceCommonDto) {
        LambdaQueryWrapper<CfInvoiceHeader> queryWrapper = Wrappers.lambdaQuery(CfInvoiceHeader.class);
        queryWrapper.like(StringUtils.isNotBlank(invoiceCommonDto.getInvoiceNo()), CfInvoiceHeader::getInvoiceNo, invoiceCommonDto.getInvoiceNo())
                .eq(StringUtils.isNotBlank(invoiceCommonDto.getBalance()), CfInvoiceHeader::getBalance, invoiceCommonDto.getBalance())
                .eq(Objects.nonNull(invoiceCommonDto.getInvoiceStatus()), CfInvoiceHeader::getInvoiceStatus, invoiceCommonDto.getInvoiceStatus())
                .eq(StringUtils.isNotBlank(invoiceCommonDto.getInvoiceType()), CfInvoiceHeader::getInvoiceType, invoiceCommonDto.getInvoiceType())
                .eq(Objects.nonNull(invoiceCommonDto.getJobType()), CfInvoiceHeader::getJobType, invoiceCommonDto.getJobType())
                .eq(Objects.nonNull(invoiceCommonDto.getCreateBy()), CfInvoiceHeader::getCreateBy, invoiceCommonDto.getCreateBy())
                .between(Objects.nonNull(invoiceCommonDto.getBeginDate()) && Objects.nonNull(invoiceCommonDto.getEndDate()),
                        CfInvoiceHeader::getCreateDate, invoiceCommonDto.getBeginDate(), invoiceCommonDto.getEndDate())
                .between(Objects.nonNull(invoiceCommonDto.getBeginArapDate()) && Objects.nonNull(invoiceCommonDto.getEndArapDate()),
                        CfInvoiceHeader::getArapDate, invoiceCommonDto.getBeginArapDate(), invoiceCommonDto.getEndArapDate())
                .in(CollectionUtils.isNotEmpty(invoiceCommonDto.getAccountIds()), CfInvoiceHeader::getAccountId, invoiceCommonDto.getAccountIds())
                .in(CollectionUtils.isNotEmpty(invoiceCommonDto.getFinanceEntitys()), CfInvoiceHeader::getFinanceEntity, invoiceCommonDto.getFinanceEntitys())
                .eq(CfInvoiceHeader::getJobType, InvoiceHeaderEnum.JOB_TYPE_THREE.getCode())
                .orderByDesc(CfInvoiceHeader::getCreateDate);
        return queryWrapper;
    }

    public CfInvoiceHeader selectById(Long invoiceId) {
        return cfInvoiceHeaderMapper.selectById(invoiceId);
    }

    public CfInvoiceCommonDetailVo detail(Long invoiceId) {
        CfInvoiceCommonDetailVo cfInvoiceCommonDetailVo = new CfInvoiceCommonDetailVo();

        CfInvoiceHeader cfInvoiceHeader = cfInvoiceHeaderMapper.selectById(invoiceId);
        if (Objects.isNull(cfInvoiceHeader)) {
            throw FinanceBizState.DATE_ERROR;
        }
        BeanUtils.copyProperties(cfInvoiceHeader, cfInvoiceCommonDetailVo);
        getCfChargeCommonVo(cfInvoiceCommonDetailVo, cfInvoiceHeader.getInvoiceId(), cfInvoiceHeader);

        //获取审批流信息
        pageInfoUtil.fillProcessFlowDesc(invoiceId, InvoiceStateService.APPROVAL_PROCESS_ID, cfInvoiceCommonDetailVo);

        List<CfInvoiceDetail> cfInvoiceDetails1 =
                cfInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfInvoiceDetail.class).eq(CfInvoiceDetail::getInvoiceId, cfInvoiceHeader.getInvoiceId()));
        cfInvoiceCommonDetailVo.setCustomerInvoiceNo(cfInvoiceDetails1.get(0).getCustomerInvoiceNo());
        cfInvoiceCommonDetailVo.setCustomerInvoiceDate(cfInvoiceDetails1.get(0).getCustomerInvoiceDate());
        cfInvoiceCommonDetailVo.setCustomerInvoiceRemark(cfInvoiceDetails1.get(0).getRemark());
        cfInvoiceCommonDetailVo.setPaymentDays(cfInvoiceDetails1.get(0).getPaymentDays());

        CfClearHeader cfClearHeader = cfClearHeaderService.getCfClearHeaderByInvoiceNo(cfInvoiceCommonDetailVo.getInvoiceNo());
        if (Objects.nonNull(cfClearHeader)) {
            CfInvoiceClearDetailVo cfInvoiceClearDetailVo = new CfInvoiceClearDetailVo();
            BeanUtils.copyProperties(cfClearHeader, cfInvoiceClearDetailVo);
            cfInvoiceClearDetailVo.setInvoiceSettlementMoney(cfInvoiceHeader.getInvoicelCredit());
            cfInvoiceCommonDetailVo.setCfInvoiceClearDetailVo(cfInvoiceClearDetailVo);
        }
        List<CfMultyImage> cfMultyImages = pageInfoUtil.multyImages(MultyImageEnum.INVOICE, cfInvoiceHeader.getInvoiceId());
        if (CollectionUtils.isNotEmpty(cfMultyImages)) {
            cfInvoiceCommonDetailVo.setContractUrls(PageInfoUtil.copyPropertiesList(cfMultyImages, MultyImageVo.class, null));
        }
        cfInvoiceCommonDetailVo.setCfBsOperationLogList(OperateUtil.selectOperationLogsByBs(OperationBsTypeEnum.OPERATION_BS_MCN_INVOICE,cfInvoiceHeader.getInvoiceNo(),invoiceId));
        return cfInvoiceCommonDetailVo;
    }

    private CfChargeCommonVo getCfChargeCommonVo(CfInvoiceCommonDetailVo cfInvoiceCommonDetailVo, Long invoiceId, CfInvoiceHeader cfInvoiceHeader) {
        List<CfInvoiceDetail> cfInvoiceDetails = getCfInvoiceDetails(invoiceId);

        cfInvoiceCommonDetailVo.setChargeCommonVos(
                cfInvoiceDetails.stream().map(cfInvoiceDetail -> {
                    CfChargeCommonVo cfChargeCommonVo = new CfChargeCommonVo();
                    BeanUtils.copyProperties(cfInvoiceDetail, cfChargeCommonVo);
                    cfChargeCommonVo.setAmountPp(cfInvoiceDetail.getInvoiceCredit());
                    cfChargeCommonVo.setChargeType(Integer.valueOf(cfInvoiceDetail.getChargeType()));
                    cfChargeCommonVo.setCreateDate(cfInvoiceDetail.getCreateDate());
                    return cfChargeCommonVo;
                }).collect(Collectors.toList()));

        List<CfChargeCommonVo> cfChargeCommonVos = cfInvoiceCommonDetailVo.getChargeCommonVos().stream().
                filter(t -> !InvoiceHeaderEnum.CHARGE_TYPE_SIX.getCode().equals(t.getChargeType())).collect(Collectors.toList());
        //存在且唯一
        Assert.isTrue(cfChargeCommonVos.size() == 1, FinanceBizState.DATE_ERROR.getMessage());
        return cfChargeCommonVos.get(0);
    }


    @Transactional(rollbackFor = Exception.class)
    public Integer update(InvoiceCommonUpdateDto invoiceCommonUpdateDto) {
        //费用种类为红人采购费，需要增加一个上传双盖合同字段
        List<CfInvoiceDetail> cfInvoiceDetails = getCfInvoiceDetails(invoiceCommonUpdateDto.getInvoiceId());
        Assert.isTrue(CollectionUtils.isNotEmpty(cfInvoiceDetails), ModuleBizState.DATE_ERROR.message());
        cfInvoiceDetails.stream().forEach(cfInvoiceDetail ->
                vertify(invoiceCommonUpdateDto.getContractUrls(), Integer.valueOf(cfInvoiceDetail.getChargeType())));
        CfInvoiceHeader cfInvoiceHeader = PageInfoUtil.initEntity(CfInvoiceHeader.class, invoiceCommonUpdateDto);
        //更新合同
        updateContactUrls(invoiceCommonUpdateDto.getContractUrls(), cfInvoiceHeader);
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_INVOICE, OperationTypeEnum.OPERATION_UPDATE,cfInvoiceHeader.getInvoiceNo(),cfInvoiceHeader.getInvoiceId());
        return cfInvoiceHeaderMapper.updateById(cfInvoiceHeader);
    }

    private void updateContactUrls(List<MultyImageVo> contractUrls, CfInvoiceHeader cfInvoiceHeader) {
        if (CollectionUtils.isNotEmpty(contractUrls)) {
            cfMultyImageMapper.delete(Wrappers.lambdaQuery(CfMultyImage.class).eq(CfMultyImage::getBusinessId, cfInvoiceHeader.getInvoiceId())
                    .eq(CfMultyImage::getBusinessType, MultyImageEnum.INVOICE.getCode()));
            contractUrls.forEach(value -> {
                CfMultyImage cfMultyImage = new CfMultyImage();
                cfMultyImage.setBusinessId(cfInvoiceHeader.getInvoiceId());
                cfMultyImage.setBusinessType(MultyImageEnum.INVOICE.getCode());
                cfMultyImage.setFileName(value.getFileName());
                cfMultyImage.setId(value.getId());
                cfMultyImageMapper.insert(cfMultyImage);
            });
        }
    }

    public Integer updateState(InvoiceUpdateStateDto invoiceUpdateStateDto) {
        return invoiceStateService.updateState(invoiceUpdateStateDto.getInvoiceId(), invoiceUpdateStateDto.getInvoiceStatus(), null);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer taxInvoiceFill(TaxInvoiceFillDto taxInvoiceFillDto) {
        CfInvoiceHeader cfInvoiceHeader = cfInvoiceHeaderMapper.selectById(taxInvoiceFillDto.getInvoiceId());

        if (cfInvoiceHeader.getCustomerInvoiceWay().equals(InvoiceHeaderEnum.CUSTOMER_INVOICE_WAY_TAX_INVOICE_LATER.getCode())) {
            Assert.isTrue(cfInvoiceHeader.getInvoiceStatus().equals(InvoiceHeaderEnum.INVOICE_STATUS_NINE.getCode()),
                    ModuleBizState.TAX_INVOICE_LATER_FILL_ERROR.message());
        } else {
            Assert.isTrue(cfInvoiceHeader.getInvoiceStatus().equals(InvoiceHeaderEnum.INVOICE_STATUS_ELEVNE.getCode()) ||
                            cfInvoiceHeader.getInvoiceStatus().equals(InvoiceHeaderEnum.INVOICE_STATUS_TWELVE.getCode()) ||
                            cfInvoiceHeader.getInvoiceStatus().equals(InvoiceHeaderEnum.INVOICE_STATUS_NINE.getCode()),
                    ModuleBizState.TAX_INVOICE_FILL_ERROR.message());
        }

        List<CfInvoiceDetail> cfInvoiceDetails = cfInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfInvoiceDetail.class)
                .eq(CfInvoiceDetail::getInvoiceId, taxInvoiceFillDto.getInvoiceId()));

        //反写发票
        cfInvoiceDetails.forEach(cfInvoiceDetail ->
                chargeCommonService.updateByInvoiceSetInvoiceNo(cfInvoiceDetail.getChargeId(), taxInvoiceFillDto.getCustomerInvoiceNo(),
                        taxInvoiceFillDto.getCustomerInvoiceDate()));

        Optional<CfInvoiceDetail> optional = cfInvoiceDetails.stream()
                .filter(t -> !t.getChargeType().equals(InvoiceHeaderEnum.CHARGE_TYPE_SIX.getCode())).findFirst();
        Assert.isTrue(optional.isPresent(), ModuleBizState.DATE_ERROR.message());
        CfInvoiceDetail cfInvoiceDetail = optional.get();
        CfInvoiceDetail updateCfInvoiceDetail = PageInfoUtil.initEntity(CfInvoiceDetail.class, taxInvoiceFillDto);
        updateCfInvoiceDetail.setInvoiceDetailId(cfInvoiceDetail.getInvoiceDetailId());
        cfInvoiceDetailMapper.updateById(updateCfInvoiceDetail);
        if (cfInvoiceHeader.getInvoiceStatus().equals(InvoiceHeaderEnum.INVOICE_STATUS_ELEVNE.getCode())) {
            invoiceStateService.updateState(taxInvoiceFillDto.getInvoiceId(), InvoiceHeaderEnum.INVOICE_STATUS_TWELVE.getCode());
        }
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_INVOICE, OperationTypeEnum.OPERATION_UPDATE,cfInvoiceHeader.getInvoiceNo(),cfInvoiceHeader.getInvoiceId());
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer updateSettlementUpdate(UpdateSettlementDto updateSettlementDto) {
        CfInvoiceHeader cfInvoiceHeader = cfInvoiceHeaderMapper.selectById(updateSettlementDto.getInvoiceId());
        Assert.isTrue((InvoiceHeaderEnum.INVOICE_STATUS_ONE.getCode().equals(cfInvoiceHeader.getInvoiceStatus())||InvoiceHeaderEnum.INVOICE_STATUS_THIRTEEN.getCode().equals(cfInvoiceHeader.getInvoiceStatus())), ModuleBizState.SETTLE_STATE_ERROR.message());

        CfInvoiceHeader cfInvoiceHeaderUpdate = PageInfoUtil.initEntity(CfInvoiceHeader.class, updateSettlementDto, "createDate");
        //第三方平台生成费用
        createThirdPlatformCharge(updateSettlementDto, cfInvoiceHeader, cfInvoiceHeaderUpdate);


        if (updateSettlementDto.getSettleTemplate().equals(InvoiceHeaderEnum.SETTLE_TEMPLATE_THREE.getCode())) {
            Assert.isTrue(StringUtils.isNotEmpty(updateSettlementDto.getTitle()) &&
                    StringUtils.isNotEmpty(updateSettlementDto.getDescription()) &&
                    Objects.nonNull(updateSettlementDto.getCreateDate()), ModuleBizState.DATE_ERROR.message());
            cfInvoiceHeaderUpdate.setSettleTitle(updateSettlementDto.getTitle());
            cfInvoiceHeaderUpdate.setSettleCreateDate(updateSettlementDto.getCreateDate());
            cfInvoiceHeaderUpdate.setSettleDescription(updateSettlementDto.getDescription());
        }
        return cfInvoiceHeaderMapper.updateById(cfInvoiceHeaderUpdate);
    }

    private void createThirdPlatformCharge(UpdateSettlementDto updateSettlementDto, CfInvoiceHeader cfInvoiceHeader,
                                           CfInvoiceHeader cfInvoiceHeaderUpdate) {
        //如果生成过费用就要删除原来费用
        List<CfInvoiceDetail> cfInvoiceDetails =
                cfInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfInvoiceDetail.class).eq(CfInvoiceDetail::getInvoiceId, cfInvoiceHeader.getInvoiceId())
                        .eq(CfInvoiceDetail::getChargeType, InvoiceHeaderEnum.CHARGE_TYPE_SIX.getCode()));
        if (CollectionUtils.isNotEmpty(cfInvoiceDetails)) {
            cfInvoiceDetails.forEach(cfInvoiceDetail -> {
                        chargeCommonService.invoiceNoPhysicsDelete(cfInvoiceDetail.getChargeId(), cfInvoiceHeader.getInvoiceNo());
                        cfInvoiceDetailMapper.deleteById(cfInvoiceDetail.getInvoiceDetailId());
                        cfInvoiceHeader.setInvoicelCredit(cfInvoiceHeader.getInvoicelCredit().subtract(cfInvoiceDetail.getInvoiceCredit()));
                    }
            );
        }

        if (Objects.nonNull(updateSettlementDto.getAccountType()) && InvoiceHeaderEnum.ACCOUNT_TYPE_THIRD_PARTY_ACCOUNT.getCode().equals(updateSettlementDto.getAccountType())) {
            Assert.isTrue(updateSettlementDto.getAccountId() >= -1 &&
                    Objects.nonNull(updateSettlementDto.getPricePp()) &&
                    updateSettlementDto.getPricePp().compareTo(BigDecimal.ZERO) >= 0, ModuleBizState.DATE_ERROR.message());
            CfChargeCommon cfChargeCommon = new CfChargeCommon();
            cfChargeCommon.setChargeSourceType(ChargeCommonEnum.CHARGE_SOURCE_TYPE_MCN.getCode());
            cfChargeCommon.setChargeType(ChargeCommonEnum.CHARGE_TYPE_SIX.getCode());
            cfChargeCommon.setArapType(ChargeEnum.ARAP_TYPE_AP.getCode());
            cfChargeCommon.setChargeSourceCode(cfInvoiceHeader.getInvoiceNo());
            cfChargeCommon.setChargeSourceDetail(cfInvoiceHeader.getInvoiceNo());
            cfChargeCommon.setPricePp(updateSettlementDto.getPricePp());
            cfChargeCommon.setAmountPp(updateSettlementDto.getPricePp());
            cfChargeCommon.setOverage(updateSettlementDto.getPricePp());
            cfChargeCommon.setBrandId(Long.valueOf(mcnConfiguration.getBrandIdMcn()));
            cfChargeCommon.setBalance(cfInvoiceHeader.getBalance());
            cfChargeCommon.setFinanceEntity(cfInvoiceHeader.getFinanceEntity());
            cfChargeCommon.setInvoiceNo(cfInvoiceHeader.getInvoiceNo());
            cfChargeCommon.setInvoiceTitle(cfInvoiceHeader.getInvoiceTitle());
            cfChargeCommon.setInvoiceTitleName(cfInvoiceHeader.getInvoiceTitleName());
            cfChargeCommon.setInvoiceDate(cfInvoiceHeader.getCreateDate());
            cfChargeCommon.setInvoiceEntranceDate(LocalDateTime.now());
            cfChargeCommon.setSettTemplate(0);
            pageInfoUtil.baseInfoFill(cfChargeCommon);
            chargeCommonService.create(cfChargeCommon);

            //同步创建账单明细
            CfInvoiceDetail cfInvoiceDetail = createInvoiceDetail(cfChargeCommon, cfInvoiceHeader);
            cfInvoiceDetailMapper.insert(cfInvoiceDetail);

            cfInvoiceHeaderUpdate.setInvoicelCredit(cfInvoiceHeader.getInvoicelCredit().add(updateSettlementDto.getPricePp()));
        }
    }

    /**
     * 根据账单号获取费用列表
     *
     * @return
     */
    public List<CfChargeCommon> getChargeCommonByInvoiceNo(String invoiceNo) {
        CfInvoiceHeader cfInvoiceHeader =
                cfInvoiceHeaderMapper.selectOne(Wrappers.lambdaQuery(CfInvoiceHeader.class).eq(CfInvoiceHeader::getInvoiceNo, invoiceNo));
        Assert.isTrue(Objects.nonNull(cfInvoiceHeader), ModuleBizState.DATE_ERROR.message());
        List<CfInvoiceDetail> cfInvoiceDetails =
                cfInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfInvoiceDetail.class).eq(CfInvoiceDetail::getInvoiceId, cfInvoiceHeader.getInvoiceId()));
        return PageInfoUtil.listToCustomList(cfInvoiceDetails, CfChargeCommon.class);
    }

    /**
     * 根据账单号获取账单主表信息
     *
     * @param invoiceNo
     * @return
     */
    public CfInvoiceHeader getCfInvoiceHeader(String invoiceNo) {
        CfInvoiceHeader cfInvoiceHeader =
                cfInvoiceHeaderMapper.selectOne(Wrappers.lambdaQuery(CfInvoiceHeader.class)
                        .eq(CfInvoiceHeader::getInvoiceNo, invoiceNo)
                        .notIn(CfInvoiceHeader::getInvoiceStatus, InvoiceHeaderEnum.INVOICE_STATUS_ZERO.getCode(), InvoiceHeaderEnum.INVOICE_STATUS_SEVEN.getCode())
                );
        return cfInvoiceHeader;
    }

    /**
     * 根据账单号获取账单明细信息
     *
     * @param invoiceId
     * @return
     */
    public List<CfInvoiceDetail> getCfInvoiceDetails(Long invoiceId) {
        List<CfInvoiceDetail> cfInvoiceDetails =
                cfInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfInvoiceDetail.class)
                        .eq(CfInvoiceDetail::getInvoiceId, invoiceId));
        return cfInvoiceDetails;
    }

    public List<CreateVo> createNameList() {
        List<CfInvoiceHeader> list = cfInvoiceHeaderMapper.selectList(Wrappers.lambdaQuery(CfInvoiceHeader.class).select(CfInvoiceHeader::getCreateBy, CfInvoiceHeader::getCreateName)
                .groupBy(CfInvoiceHeader::getCreateName));
        return list.stream().map(cfInvoiceHeader -> {
            CreateVo createVo = new CreateVo();
            BeanUtils.copyProperties(cfInvoiceHeader, createVo);
            return createVo;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer taxInvoiceFillBatch(TaxInvoiceFillDtoList taxInvoiceFillDtos) {
        //这部分性能消耗忽略不计
        List<CfInvoiceHeader> cfInvoiceHeaders = cfInvoiceHeaderMapper.selectBatchIds(
                PageInfoUtil.lambdaToList(taxInvoiceFillDtos.getList(), TaxInvoiceFillDto::getInvoiceId));
        Assert.isTrue(CollectionUtils.isNotEmpty(cfInvoiceHeaders), ModuleBizState.DATE_ERROR.message());
        CfInvoiceHeader match = cfInvoiceHeaders.get(0);
        cfInvoiceHeaders.forEach(cfInvoiceHeader -> {
            Assert.isTrue(cfInvoiceHeader.getInvoiceStatus().equals(InvoiceHeaderEnum.INVOICE_STATUS_ELEVNE.getCode()),
                    ModuleBizState.INVOICE_TAX_INVOICE_ERROR.message());
            Assert.isTrue(InvoiceHeaderEnum.CUSTOMER_INVOICE_WAY_TAX_INVOICE.getCode().equals(cfInvoiceHeader.getCustomerInvoiceWay()),
                    ModuleBizState.DATE_ERROR_BUSINESS.format("开票类型必须有票"));
            Assert.isTrue(cfInvoiceHeader.getBalance().equals(match.getBalance()) &&
                    cfInvoiceHeader.getJobType().equals(match.getJobType()) &&
                    cfInvoiceHeader.getInvoiceType().equals(match.getInvoiceType()), ModuleBizState.INVOICE_BALANCE_ERROR.message());
        });
        taxInvoiceFillDtos.getList().forEach(this::taxInvoiceFill);
        return 1;
    }

    public CfInvoiceCommonDetailSettleVo detailSettle(Long invoiceId) {
        CfInvoiceCommonDetailSettleVo cfInvoiceCommonDetailSettleVo = new CfInvoiceCommonDetailSettleVo();
        CfInvoiceHeader cfInvoiceheader = cfInvoiceHeaderMapper.selectById(invoiceId);
        PageInfoUtil.copyProperties(cfInvoiceheader, cfInvoiceCommonDetailSettleVo);
        //获取结算信息
        ExcutionSettleInfoVO excutionSettleInfoVO = getExcutionSettleInfoVOForCharge(invoiceId);
        cfInvoiceCommonDetailSettleVo.setExcutionSettleInfoVO(excutionSettleInfoVO);
        if (InvoiceHeaderEnum.SETTLE_TEMPLATE_THREE.getCode().equals(cfInvoiceheader.getSettleTemplate())
                && Objects.nonNull(cfInvoiceheader.getSettleCreateDate())) {
            excutionSettleInfoVO.setTitle(cfInvoiceheader.getSettleTitle());
            excutionSettleInfoVO.setDescription(cfInvoiceheader.getSettleDescription());
            excutionSettleInfoVO.setCreateDate(TimeThreadSafeUtils.localDateTimeToDate(cfInvoiceheader.getSettleCreateDate()));
        }
        pageInfoUtil.fillProcessFlowDesc(invoiceId, InvoiceStateService.APPROVAL_PROCESS_ID, cfInvoiceCommonDetailSettleVo);
        return cfInvoiceCommonDetailSettleVo;
    }

    public ExcutionSettleInfoVO getExcutionSettleInfoVO(Long invoiceId) {
        CfInvoiceCommonDetailVo cfInvoiceCommonDetailVo = new CfInvoiceCommonDetailVo();
        CfChargeCommonVo cfChargeCommonVo = getCfChargeCommonVo(cfInvoiceCommonDetailVo, invoiceId, null);
        return getExcutionSettleInfoVO(cfChargeCommonVo.getChargeType(), cfChargeCommonVo.getChargeSourceCode());
    }

    private ExcutionSettleInfoVO getExcutionSettleInfoVO(Integer chargeType, String chargeSourceCode) {
        //获取结算信息
        Response<ExcutionSettleInfoVO> response = mcnRemoteServer.getSettleInfo(chargeType, chargeSourceCode);
        return RpcUtil.getObjNoException(response);
    }

    private ExcutionSettleInfoVO getExcutionSettleInfoVOForCharge(Integer chargeType, String chargeSourceCode) {
        //获取结算信息
        Response<ExcutionSettleInfoVO> response = mcnRemoteServer.getSettleInfoForCharge(chargeType, chargeSourceCode);
        return RpcUtil.getObjNoException(response);
    }

    public ExcutionSettleInfoVO getExcutionSettleInfoVOForCharge(Long invoiceId) {
        CfInvoiceCommonDetailVo cfInvoiceCommonDetailVo = new CfInvoiceCommonDetailVo();
        CfChargeCommonVo cfChargeCommonVo = getCfChargeCommonVo(cfInvoiceCommonDetailVo, invoiceId, null);
        //获取结算信息
        Response<ExcutionSettleInfoVO> response = mcnRemoteServer.getSettleInfoForCharge(cfChargeCommonVo.getChargeType(), cfChargeCommonVo.getChargeSourceCode());
        return RpcUtil.getObjNoException(response);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer updateBatchState(InvoiceCommonUpdateBatchDto invoiceCommonUpdateBatchDto) {
        for (Long invoiceId : invoiceCommonUpdateBatchDto.getInvoiceIds()) {
            invoiceStateService.updateState(invoiceId, invoiceCommonUpdateBatchDto.getInvoiceStatus());
        }
        return 1;
    }

    public void listExport(InvoiceCommonDto invoiceCommonDto, HttpServletResponse response) {
        final String split = "-";
        invoiceCommonDto.setPageNum(1);
        invoiceCommonDto.setPageSize(1000);
        PageInfo<CfInvoiceCommonVo> pageInfo = list(invoiceCommonDto);
        List<CfInvoiceCommonExportVo> result = PageInfoUtil.copyPropertiesList(pageInfo.getList(), CfInvoiceCommonExportVo.class, value -> {
            value.setSettleAccountName(value.getAccountName());
            value.setAccountName(Strings.EMPTY);
        });
        if (CollectionUtils.isNotEmpty(result)) {
            //获取公司信息  结算主体
            Map<String, SCompanyBank> companyBankMap = getCompanyInfo(PageInfoUtil.lambdaToList(result, CfInvoiceCommonExportVo::getFinanceEntity));
            //mcn 获取
            List<AccountInfoQueryDTO> accountInfoQueryList = pageInfo.getList().stream().map(cfInvoiceCommonVo -> {
                AccountInfoQueryDTO accountInfoQueryDTO = new AccountInfoQueryDTO();
                accountInfoQueryDTO.setChargeSourceCode(cfInvoiceCommonVo.getChargeSourceCode());
                accountInfoQueryDTO.setChargeType(Integer.valueOf(cfInvoiceCommonVo.getChargeType()));
                return accountInfoQueryDTO;
            }).collect(Collectors.toList());

            List<InvoiceAccountInfoVO> invoiceAccountInfoVos = RpcUtil.getObjException(mcnRemoteServer.getAccountInfo(accountInfoQueryList), "mcnRemoteServer 异常");
            //来源单号，来源类型
            Map<String, InvoiceAccountInfoVO> stringInvoiceAccountInfoVoMap = invoiceAccountInfoVos.stream().collect(Collectors.toMap(key -> key.getChargeSourceCode() + split + key.getChargeType(), value -> value, (a, b) -> {
                log.error("数据异常 存在相同的 ChargeSourceCode()：getChargeType()", a.getChargeSourceCode() + split + a.getChargeType());
                throw FinanceBizState.DATE_ERROR;
            }));

            //获取平台信息 accountName 既账单结算选择的 打款方式
            Map<String, ThirdPayAccount> platThirdPayMap = getPlatInfo(split, pageInfo);

            result.stream().forEach(cfInvoiceCommonExportVo -> {
                SCompanyBank sCompanyBank = companyBankMap.get(cfInvoiceCommonExportVo.getFinanceEntity());
                if (Objects.nonNull(sCompanyBank)) {
                    cfInvoiceCommonExportVo.setAccountName(sCompanyBank.getCompanyName());
                    cfInvoiceCommonExportVo.setCardNumber(sCompanyBank.getCardNumber());
                    cfInvoiceCommonExportVo.setBankName(sCompanyBank.getBankName());
                }
                InvoiceAccountInfoVO invoiceAccountInfoVO = stringInvoiceAccountInfoVoMap.get(cfInvoiceCommonExportVo.getChargeSourceCode() + split + cfInvoiceCommonExportVo.getChargeType());
                if (Objects.nonNull(invoiceAccountInfoVO)) {
                    BeanUtils.copyProperties(invoiceAccountInfoVO, cfInvoiceCommonExportVo, "accountName", "cardNumber", "bankName");
                    cfInvoiceCommonExportVo.setMcnAccountName(invoiceAccountInfoVO.getAccountName());
                }
                ThirdPayAccount thirdPayAccount = platThirdPayMap.get(cfInvoiceCommonExportVo.getSettleAccountName());
                if (Objects.nonNull(thirdPayAccount)) {
                    cfInvoiceCommonExportVo.setThirdPaymentAccount(thirdPayAccount.getPaymentAccount());
                    cfInvoiceCommonExportVo.setThirdAccountBank(thirdPayAccount.getAccountBank());
                    cfInvoiceCommonExportVo.setThirdAccountNumber(thirdPayAccount.getAccountNumber());
                    cfInvoiceCommonExportVo.setThirdAccountPlatform(thirdPayAccount.getAccountPlatform());
                }
            });
        }
        PageInfoUtil.exportExcel(result, CfInvoiceCommonExportVo.class, "账单", response);
    }

    public Map<String, SCompanyBank> getCompanyInfo(List<String> names) {
        CompanyNameReq companyNameReq = new CompanyNameReq();
        companyNameReq.setNames(names);
        Map<String, SCompanyBank> companyBankMap = RpcUtil.getObjException(privilegeUserServer.selectBankByCompanyName(companyNameReq), "privilegeUserServer 异常");
        return companyBankMap;
    }

    private Map<String, ThirdPayAccount> getPlatInfo(String split, PageInfo<CfInvoiceCommonVo> pageInfo) {
        AccountPlatformReq accountPlatformReq = new AccountPlatformReq();

        List<ThirdPayAccount> thirdPayAccounts = pageInfo.getList().stream().filter(t -> InvoiceHeaderEnum.ACCOUNT_TYPE_FOUR.getCode().equals(t.getAccountType()))
                .map(CfInvoiceCommonVo::getAccountName).distinct()
                .map(value -> {
                    String[] platAccountName = value.split(split);
                    ThirdPayAccount thirdPayAccount = new ThirdPayAccount();
                    thirdPayAccount.setAccountPlatform(platAccountName[0]);
                    thirdPayAccount.setPaymentAccount(platAccountName[1]);
                    return thirdPayAccount;
                }).collect(Collectors.toList());
        accountPlatformReq.setThirdPayAccounts(thirdPayAccounts);

        //调用张杰的函数  selectAccountByName
        List<ThirdPayAccount> thirdPayAccounts1 = RpcUtil.getObjException(baseInfoRemoteServer.selectAccountByName(accountPlatformReq), "baseInfoRemoteServer 异常");


        Map<String, ThirdPayAccount> platThirdPayMap =
                thirdPayAccounts1.stream().collect(Collectors.toMap(key -> key.getAccountPlatform() + split + key.getPaymentAccount(), value -> value));
        return platThirdPayMap;
    }

    public List<CfInvoiceHeader> selectList(LambdaQueryWrapper<CfInvoiceHeader> in) {
        return cfInvoiceHeaderMapper.selectList(in.notIn(CfInvoiceHeader::getInvoiceStatus,
                InvoiceHeaderEnum.INVOICE_STATUS_SEVEN.getCode(), InvoiceHeaderEnum.INVOICE_STATUS_ZERO.getCode()));
    }

    /**
     * 通过财务账单号获取账单ID
     *
     * @param invoiceNo
     * @return
     */
    public Long getInvoiceIdByNo(String invoiceNo) {
        CfInvoiceHeader entity = cfInvoiceHeaderMapper.selectOne(Wrappers.lambdaQuery(CfInvoiceHeader.class)
                .eq(CfInvoiceHeader::getInvoiceNo, invoiceNo));
        return Optional.ofNullable(entity).map(CfInvoiceHeader -> entity.getInvoiceId()).orElse(null);
    }
}
