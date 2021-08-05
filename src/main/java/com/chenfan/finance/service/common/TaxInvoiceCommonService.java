package com.chenfan.finance.service.common;

import com.baidu.unbiz.fluentvalidator.util.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.common.exception.BusinessException;
import com.chenfan.common.exception.SystemState;
import com.chenfan.common.vo.Response;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.config.BillNoConstantClassField;
import com.chenfan.finance.convertor.TaxInvoiceConvertor;
import com.chenfan.finance.dao.CfChargeCommonMapper;
import com.chenfan.finance.dao.CfTaxInvoiceDetailMapper;
import com.chenfan.finance.dao.CfTaxInvoiceHeaderMapper;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.exception.FinanceBizState;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.*;
import com.chenfan.finance.model.dto.*;
import com.chenfan.finance.model.vo.*;
import com.chenfan.finance.server.BaseInfoRemoteServer;
import com.chenfan.finance.server.McnRemoteServer;
import com.chenfan.finance.server.remote.model.BaseCustomerBilling;
import com.chenfan.finance.server.remote.model.IncomeContract;
import com.chenfan.finance.service.ApprovalFlowService;
import com.chenfan.finance.service.common.state.TaxInvoiceStateService;
import com.chenfan.finance.service.impl.CfClearHeaderServiceImpl;
import com.chenfan.finance.utils.AuthorizationUtil;
import com.chenfan.finance.utils.FileUtil;
import com.chenfan.finance.utils.OperateUtil;
import com.chenfan.finance.utils.RpcUtil;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.github.pagehelper.PageInfo;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 发票管理
 *
 * @author: xuxianbei
 * Date: 2021/3/5
 * Time: 14:16
 * Version:V1.0
 */
@Service
public class TaxInvoiceCommonService {

    @Autowired
    private CfClearHeaderServiceImpl cfClearHeaderService;

    @Resource
    private CfTaxInvoiceDetailMapper cfTaxInvoiceDetailMapper;

    @Resource
    private CfTaxInvoiceHeaderMapper cfTaxInvoiceHeaderMapper;

    @Autowired
    private ChargeCommonService chargeCommonService;

    @Autowired
    private TaxInvoiceStateService taxInvoiceStateService;

    @Autowired
    private PageInfoUtil pageInfoUtil;

    @Autowired
    private McnRemoteServer mcnRemoteServer;

    @Autowired
    private BaseInfoRemoteServer baseInfoRemoteServer;

    @Autowired
    private McnConfiguration mcnConfiguration;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ApprovalFlowService approvalFlowService;

    @Resource
    private CfChargeCommonMapper cfChargeCommonMapper;

    private final String INVOICE_DOWNLOAD_PREFIX = "invoice:download:";

    @Transactional(rollbackFor = Exception.class)
    public Long add(TaxInvoiceAddDto taxInvoiceAddDto) {
        verifyInvoice(taxInvoiceAddDto.getTaxInvoiceWay(), taxInvoiceAddDto.getBillingContent(), taxInvoiceAddDto.getTaxInvoiceType());
        List<CfChargeCommon> chargeCommons = chargeCommonService.selectList(taxInvoiceAddDto.getChargeIds());
        if (CollectionUtils.isEmpty(chargeCommons)) {
            throw FinanceBizState.DATE_ERROR;
        }
        //校验费用
        vertifyCharge(chargeCommons);

        CfTaxInvoiceHeader cfTaxInvoiceHeader = PageInfoUtil.initEntity(CfTaxInvoiceHeader.class, taxInvoiceAddDto);
        cfTaxInvoiceHeader.setTaxInvoiceNo(pageInfoUtil.generateBusinessNum(BillNoConstantClassField.TAX_INVOICE));
        pageInfoUtil.baseInfoFillOld(cfTaxInvoiceHeader);

        BigDecimal sum;
        sum = chargeCommons.stream().map(CfChargeCommon::getAmountPp).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        cfTaxInvoiceHeader.setInvoicelDebit(sum);
        cfTaxInvoiceHeader.setBrandId(Long.valueOf(mcnConfiguration.getBrandIdMcn()));
        PageInfoUtil.vertifyBigDecimal(sum);
        cfTaxInvoiceHeaderMapper.insert(cfTaxInvoiceHeader);

        chargeCommons.forEach(cfChargeCommon -> {
            CfTaxInvoiceDetail cfTaxInvoiceDetail = new CfTaxInvoiceDetail();
            BeanUtils.copyProperties(cfChargeCommon, cfTaxInvoiceDetail);
            cfTaxInvoiceDetail.setInvoiceQty(1);
            cfTaxInvoiceDetail.setTaxInvoiceId(cfTaxInvoiceHeader.getTaxInvoiceId());
            cfTaxInvoiceDetail.setInvoiceDebit(cfChargeCommon.getAmountPp());
            PageInfoUtil.vertifyBigDecimal(cfChargeCommon.getAmountPp());
            cfTaxInvoiceDetailMapper.insert(cfTaxInvoiceDetail);
            //反写费用
            chargeCommonService.updateByInvoice(cfChargeCommon.getChargeId(), cfTaxInvoiceHeader.getTaxInvoiceNo(), cfTaxInvoiceHeader.getCreateDate());
        });
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_TAX, OperationTypeEnum.OPERATION_CREATE,cfTaxInvoiceHeader.getTaxInvoiceNo(),cfTaxInvoiceHeader.getTaxInvoiceId());

        return cfTaxInvoiceHeader.getTaxInvoiceId();
    }

    private void verifyInvoice(Integer taxInvoiceWay, String billingContent, String taxInvoiceType) {
        if (!taxInvoiceWay.equals(InvoiceHeaderEnum.CUSTOMER_INVOICE_WAY_TAX_INVOICE_NONE.getCode())) {
            if (StringUtils.isBlank(billingContent) || StringUtils.isBlank(taxInvoiceType)) {
                throw new BusinessException(SystemState.PARAM_ERROR);
            }
        }
    }

    private void vertifyCharge(List<CfChargeCommon> chargeCommons) {
        CfChargeCommon baseJudgeCharge = chargeCommons.get(0);

        chargeCommons.forEach(cfChargeCommon -> {
            Assert.isTrue(ChargeEnum.ARAP_TYPE_AR.getCode().equals(cfChargeCommon.getArapType())
                    && StringUtils.isEmpty(cfChargeCommon.getInvoiceNo()), ModuleBizState.DATE_ERROR.message());
            Assert.isTrue(baseJudgeCharge.getBalance().equals(cfChargeCommon.getBalance())
                    && baseJudgeCharge.getChargeSourceCode().equals(cfChargeCommon.getChargeSourceCode())
                    && baseJudgeCharge.getChargeType().equals(cfChargeCommon.getChargeType()), ModuleBizState.TAX_INVOICE_ADD.message());
        });
    }

    public PageInfo<TaxInvoiceVo> list(TaxInvoiceCommonListDto taxInvoiceCommonListDto) {
        LambdaQueryWrapper<CfTaxInvoiceHeader> lambdaQueryWrapper = condition(taxInvoiceCommonListDto);
        if (Objects.isNull(lambdaQueryWrapper)) {
            return PageInfoUtil.emptyPageInfo();
        }
        PageInfoUtil.startPage(taxInvoiceCommonListDto);
        List<CfTaxInvoiceHeader> invoiceHeaders = cfTaxInvoiceHeaderMapper.selectList(lambdaQueryWrapper);
        PageInfo<TaxInvoiceVo> taxInvoiceVoPageInfo = PageInfoUtil.toPageInfo(invoiceHeaders, TaxInvoiceVo.class);
        if(!CollectionUtils.isEmpty(taxInvoiceVoPageInfo.getList())){
            List<CfTaxInvoiceDetail> cfTaxInvoiceDetails = cfTaxInvoiceDetailMapper.selectList(Wrappers.<CfTaxInvoiceDetail>lambdaQuery().in(CfTaxInvoiceDetail::getTaxInvoiceId, invoiceHeaders.stream().map(x -> x.getTaxInvoiceId()).collect(Collectors.toList())));
            List<CfChargeCommon> cfChargeCommons = cfChargeCommonMapper.selectList(Wrappers.lambdaQuery(CfChargeCommon.class).in(CfChargeCommon::getChargeSourceCode, cfTaxInvoiceDetails.stream().map(x->x.getChargeSourceCode()).collect(Collectors.toSet())));
            Map<Long, List<CfChargeCommon>> mapOfChargeByParentId = cfChargeCommons.stream().collect(Collectors.groupingBy(x -> x.getParentId()));
            Map<Long, List<CfTaxInvoiceDetail>> collect = cfTaxInvoiceDetails.stream().collect(Collectors.groupingBy(CfTaxInvoiceDetail::getTaxInvoiceId));
            for (TaxInvoiceVo taxInvoiceVo:taxInvoiceVoPageInfo.getList()) {
                List<CfTaxInvoiceDetail> cfTaxInvoiceDetails1 = collect.get(taxInvoiceVo.getTaxInvoiceId());
                String contractNo = cfTaxInvoiceDetails1.stream().map(x -> x.getChargeSourceCode()).collect(Collectors.toSet()).stream().collect(Collectors.joining(","));
                String financeEntity = cfTaxInvoiceDetails1.stream().map(x -> x.getFinanceEntity()).collect(Collectors.toSet()).stream().collect(Collectors.joining(","));
                CfChargeCommon cfChargeCommon = cfChargeCommons.stream().filter(x -> Objects.equals(x.getChargeId(), cfTaxInvoiceDetails1.get(0).getChargeId())).findFirst().get();
                BigDecimal contractMoney=null;
                if(cfChargeCommon.getParentId()>0L){
                    contractMoney = mapOfChargeByParentId.get(cfChargeCommon.getParentId()).stream().map(x -> x.getAmountPp()).reduce(BigDecimal.ZERO, BigDecimal::add);
                }else {
                    contractMoney =cfTaxInvoiceDetails1.stream().map(x->x.getInvoiceDebit()).reduce(BigDecimal.ZERO, BigDecimal::add);
                }
              taxInvoiceVo.setContractNo(contractNo);
              taxInvoiceVo.setFinanceEntity(financeEntity);
              taxInvoiceVo.setContractMoney(contractMoney);
            }
        }
        return taxInvoiceVoPageInfo;
    }



    public void export(TaxInvoiceCommonListDto taxInvoiceCommonListDto, HttpServletResponse response) {
        List<TaxInvoiceHeaderExportVO> exportList = cfTaxInvoiceHeaderMapper.exportList(taxInvoiceCommonListDto);
        try {
            FileUtil.exportExcel(exportList, "开票列表", "开票列表", TaxInvoiceHeaderExportVO.class, "开票列表.xls", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private LambdaQueryWrapper<CfTaxInvoiceHeader> condition(TaxInvoiceCommonListDto taxInvoiceCommonListDto) {
        LambdaQueryWrapper<CfTaxInvoiceHeader> lambdaQueryWrapper = Wrappers.lambdaQuery(CfTaxInvoiceHeader.class);
        lambdaQueryWrapper.like(StringUtils.isNotBlank(taxInvoiceCommonListDto.getInvoiceNo()), CfTaxInvoiceHeader::getInvoiceNo, taxInvoiceCommonListDto.getInvoiceNo())
                .like(StringUtils.isNotBlank(taxInvoiceCommonListDto.getInvoiceTitle()), CfTaxInvoiceHeader::getInvoiceTitle, taxInvoiceCommonListDto.getInvoiceTitle())
                .eq(Objects.nonNull(taxInvoiceCommonListDto.getTaxInvoiceStatus()), CfTaxInvoiceHeader::getTaxInvoiceStatus, taxInvoiceCommonListDto.getTaxInvoiceStatus())
                .eq(Objects.nonNull(taxInvoiceCommonListDto.getClearStatus()), CfTaxInvoiceHeader::getClearStatus, taxInvoiceCommonListDto.getClearStatus())
                .eq(StringUtils.isNotBlank(taxInvoiceCommonListDto.getBalance()), CfTaxInvoiceHeader::getBalance, taxInvoiceCommonListDto.getBalance())
                .between(Objects.nonNull(taxInvoiceCommonListDto.getInVoiceBeginDate()) && Objects.nonNull(taxInvoiceCommonListDto.getInvoiceEndDate()),
                        CfTaxInvoiceHeader::getInvoiceDate, taxInvoiceCommonListDto.getInVoiceBeginDate(), taxInvoiceCommonListDto.getInvoiceEndDate())
                .between(Objects.nonNull(taxInvoiceCommonListDto.getCreateBeginDate()) && Objects.nonNull(taxInvoiceCommonListDto.getCreateEndDate()),
                        CfTaxInvoiceHeader::getCreateDate, taxInvoiceCommonListDto.getCreateBeginDate(), taxInvoiceCommonListDto.getCreateEndDate())
                .like(StringUtils.isNotBlank(taxInvoiceCommonListDto.getTaxInvoiceNo()), CfTaxInvoiceHeader::getTaxInvoiceNo, taxInvoiceCommonListDto.getTaxInvoiceNo())
                .orderByDesc(CfTaxInvoiceHeader::getCreateDate);
        if (StringUtils.isNotBlank(taxInvoiceCommonListDto.getChargeSourceCode())) {
            List<CfTaxInvoiceDetail> taxInvoiceDetails = cfTaxInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfTaxInvoiceDetail.class).like(CfTaxInvoiceDetail::getChargeSourceCode, taxInvoiceCommonListDto.getChargeSourceCode()));
            if (!CollectionUtils.isEmpty(taxInvoiceDetails)) {
                List<Long> taxInvoiceIds = PageInfoUtil.lambdaToList(taxInvoiceDetails, CfTaxInvoiceDetail::getTaxInvoiceId);
                lambdaQueryWrapper.in(CfTaxInvoiceHeader::getTaxInvoiceId, taxInvoiceIds);
            } else {
                return null;
            }
        }
        //过滤公司权限
        if (Objects.nonNull(taxInvoiceCommonListDto.getCompanyIds())){
            lambdaQueryWrapper.in(CfTaxInvoiceHeader::getCompanyId,taxInvoiceCommonListDto.getCompanyIds());
        }
        //过滤用户权限
        if (Objects.nonNull(taxInvoiceCommonListDto.getUserIds())){
            lambdaQueryWrapper.in(CfTaxInvoiceHeader::getCreateBy,taxInvoiceCommonListDto.getUserIds());
        }
        //过滤品牌权限
        if (Objects.nonNull(taxInvoiceCommonListDto.getBrandIds())){
            lambdaQueryWrapper.in(CfTaxInvoiceHeader::getBrandId,taxInvoiceCommonListDto.getBrandIds());
        }
        return lambdaQueryWrapper;
    }

    public TaxInvoiceDetailVo detail(Long taxInvoiceId) {
        TaxInvoiceDetailVo taxInvoiceDetailVo = new TaxInvoiceDetailVo();
        CfTaxInvoiceHeader cfTaxInvoiceHeader = cfTaxInvoiceHeaderMapper.selectById(taxInvoiceId);
        if (Objects.isNull(cfTaxInvoiceHeader)) {
            throw FinanceBizState.DATE_ERROR;
        }
        BeanUtils.copyProperties(cfTaxInvoiceHeader, taxInvoiceDetailVo);
        List<CfTaxInvoiceDetail> taxInvoiceDetails =
                cfTaxInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfTaxInvoiceDetail.class).eq(CfTaxInvoiceDetail::getTaxInvoiceId, taxInvoiceId));

        List<CfTaxInvoiceDetailVo> taxInvoiceDetailVos = taxInvoiceDetails.stream().map(cfTaxInvoiceDetail -> {
            CfTaxInvoiceDetailVo cfTaxInvoiceDetailVo = new CfTaxInvoiceDetailVo();
            BeanUtils.copyProperties(cfTaxInvoiceDetail, cfTaxInvoiceDetailVo);
            return cfTaxInvoiceDetailVo;
        }).collect(Collectors.toList());
        taxInvoiceDetailVo.setInvoiceDetailVoList(taxInvoiceDetailVos);

        taxInvoiceDetailVo.setCustomerInvoiceNo(cfTaxInvoiceHeader.getInvoiceNo());
        taxInvoiceDetailVo.setCustomerInvoiceDate(cfTaxInvoiceHeader.getInvoiceDate());
        taxInvoiceDetailVo.setCustomerRemark(cfTaxInvoiceHeader.getInvoiceRemark());
        taxInvoiceDetailVo.setPaymentDays(cfTaxInvoiceHeader.getPaymentDays());

        //核销明细
        CfClearHeader cfClearHeader = cfClearHeaderService.getCfClearHeaderByInvoiceNo(cfTaxInvoiceHeader.getTaxInvoiceNo());
        if (Objects.nonNull(cfClearHeader)) {
            CfInvoiceClearDetailVo cfInvoiceClearDetailVo = new CfInvoiceClearDetailVo();
            BeanUtils.copyProperties(cfClearHeader, cfInvoiceClearDetailVo);
            taxInvoiceDetailVo.setCfInvoiceClearDetailVo(cfInvoiceClearDetailVo);
        }
        //审批流
        pageInfoUtil.fillProcessFlowDesc(taxInvoiceId, TaxInvoiceStateService.APPROVAL_PROCESS_ID, taxInvoiceDetailVo);
        //MCN 如果不是拆费用，就不存在
        CfChargeCommon cfChargeCommon = cfChargeCommonMapper.selectOne(Wrappers.lambdaQuery(CfChargeCommon.class).eq(CfChargeCommon::getChargeId, taxInvoiceDetails.get(0).getChargeId()));
        if(Objects.nonNull(cfChargeCommon)){
            taxInvoiceDetailVo.setSplitType(cfChargeCommon.getSplitType());
            taxInvoiceDetailVo.setSplitVos(chargeCommonService.splitChargeInfo(cfChargeCommon));
        }
        taxInvoiceDetailVo.setCfBsOperationLogList(OperateUtil.selectOperationLogsByBs(OperationBsTypeEnum.OPERATION_BS_MCN_TAX,cfTaxInvoiceHeader.getTaxInvoiceNo(),taxInvoiceId));
        return taxInvoiceDetailVo;
    }

    public Integer update(TaxInvoiceCommonUpdateDto taxInvoiceCommonUpdateDto) {
        verifyInvoice(taxInvoiceCommonUpdateDto.getTaxInvoiceWay(), taxInvoiceCommonUpdateDto.getBillingContent(), taxInvoiceCommonUpdateDto.getTaxInvoiceType());
        CfTaxInvoiceHeader cfTaxInvoiceHeader = cfTaxInvoiceHeaderMapper.selectById(taxInvoiceCommonUpdateDto.getTaxInvoiceId());
        Assert.isTrue(cfTaxInvoiceHeader.getTaxInvoiceStatus().equals(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_ONE.getCode()) ||
                cfTaxInvoiceHeader.getTaxInvoiceStatus().equals(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SEVEN.getCode())
                ||cfTaxInvoiceHeader.getTaxInvoiceStatus().equals(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_THREE.getCode()), ModuleBizState.DATE_ERROR.message());

        CfTaxInvoiceHeader cfTaxInvoiceHeaderUpdate = PageInfoUtil.initEntity(CfTaxInvoiceHeader.class, taxInvoiceCommonUpdateDto);
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_TAX, OperationTypeEnum.OPERATION_UPDATE,cfTaxInvoiceHeader.getTaxInvoiceNo(),cfTaxInvoiceHeader.getTaxInvoiceId());
        return cfTaxInvoiceHeaderMapper.updateById(cfTaxInvoiceHeaderUpdate);
    }

    public Integer updateStatus(TaxInvoiceCommonUpdateStatusDto taxInvoiceCommonUpdateStatusDto) {
        return taxInvoiceStateService.updateState(taxInvoiceCommonUpdateStatusDto.getTaxInvoiceId(), taxInvoiceCommonUpdateStatusDto.getTaxInvoiceStatus());
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer updateInvoice(TaxInvoiceCommonUpdateInvoiceDto taxInvoiceCommonUpdateInvoiceDto) {
        CfTaxInvoiceHeader cfTaxInvoiceHeader = cfTaxInvoiceHeaderMapper.selectById(taxInvoiceCommonUpdateInvoiceDto.getTaxInvoiceId());
        CFRequestHolder.setIsUpdateThreadLocal(StringUtils.isNotBlank(cfTaxInvoiceHeader.getInvoiceNo()));
        Assert.isTrue(Objects.nonNull(cfTaxInvoiceHeader), ModuleBizState.DATE_ERROR.message());
        Assert.isTrue(taxInvoiceStateService.updateState(taxInvoiceCommonUpdateInvoiceDto.getTaxInvoiceId(), TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FIVE.getCode()) == 1,
                ModuleBizState.DATE_ERROR.message());
        CfTaxInvoiceHeader cfTaxInvoiceHeaderUpdate = PageInfoUtil.initEntity(CfTaxInvoiceHeader.class, taxInvoiceCommonUpdateInvoiceDto);
        cfTaxInvoiceHeaderUpdate.setTaxInvoiceStatus(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FIVE.getCode());

        List<CfTaxInvoiceDetail> cfTaxInvoiceDetails =
                cfTaxInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfTaxInvoiceDetail.class).eq(CfTaxInvoiceDetail::getTaxInvoiceId, taxInvoiceCommonUpdateInvoiceDto.getTaxInvoiceId()));
        //反写发票
        cfTaxInvoiceDetails.forEach(cfInvoiceDetail ->
                chargeCommonService.updateByInvoiceSetInvoiceNo(cfInvoiceDetail.getChargeId(), taxInvoiceCommonUpdateInvoiceDto.getInvoiceNo(),
                        taxInvoiceCommonUpdateInvoiceDto.getInvoiceDate()));

        return cfTaxInvoiceHeaderMapper.updateById(cfTaxInvoiceHeaderUpdate);
    }

    public CfTaxInvoiceCommonPreVo preAddView(TaxInvoiceCommonPreAddViewDto taxInvoiceCommonPreAddViewDto) {
        List<CfChargeCommon> cfChargeCommons = chargeCommonService.selectList(taxInvoiceCommonPreAddViewDto.getChargeIds());
        Assert.isTrue(!CollectionUtils.isEmpty(cfChargeCommons) && cfChargeCommons.size() ==
                taxInvoiceCommonPreAddViewDto.getChargeIds().size(), ModuleBizState.DATE_ERROR.message());

        vertifyCharge(cfChargeCommons);
        CfChargeCommon cfChargeCommon = cfChargeCommons.get(0);

        Response<IncomeContract> incomeContractResponse = mcnRemoteServer.getByCode(cfChargeCommon.getChargeSourceCode());
        IncomeContract incomeContract = RpcUtil.getObjException(incomeContractResponse, ModuleBizState.DATE_ERROR.message());
        Response<BaseCustomerBilling> baseCustomerBillingResponse = baseInfoRemoteServer.getBillingsInfoById(incomeContract.getCustomerBillingId(), pageInfoUtil.getToken());
        BaseCustomerBilling baseCustomerBilling = RpcUtil.getObjException(baseCustomerBillingResponse, ModuleBizState.CUSTOMER_BILLING_ID_NOT_EXIST.message());
        CfTaxInvoiceCommonPreVo cfTaxInvoiceCommonPreVo = new CfTaxInvoiceCommonPreVo();
        BeanUtils.copyProperties(baseCustomerBilling, cfTaxInvoiceCommonPreVo);
        cfTaxInvoiceCommonPreVo.setChargeCommonVoList(PageInfoUtil.copyPropertiesList(cfChargeCommons, CfChargeCommonVo.class, null));
        cfTaxInvoiceCommonPreVo.setSplitType(cfChargeCommon.getSplitType());
        cfTaxInvoiceCommonPreVo.setSplitVos(chargeCommonService.splitChargeInfo(cfChargeCommon));
        cfTaxInvoiceCommonPreVo.setBalance(cfChargeCommon.getBalance());
        cfTaxInvoiceCommonPreVo.setInvoiceTitle(cfChargeCommon.getInvoiceTitle());
        return cfTaxInvoiceCommonPreVo;
    }


    public CfTaxInvoiceHeader getCfTaxInvoiceHeader(String taxInvoiceNo) {
        return cfTaxInvoiceHeaderMapper.selectOne(Wrappers.lambdaQuery(CfTaxInvoiceHeader.class)
                .eq(CfTaxInvoiceHeader::getTaxInvoiceNo, taxInvoiceNo)
                .notIn(CfTaxInvoiceHeader::getTaxInvoiceStatus, TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_EIGHT.getCode()));
    }

    public List<CfTaxInvoiceDetail> getCfTaxInvoiceDetail(Long taxInvoiceId) {
        return cfTaxInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfTaxInvoiceDetail.class)
                .eq(CfTaxInvoiceDetail::getTaxInvoiceId, taxInvoiceId));
    }

    public List<String> fieldsBalances(String value) {
        List<CfTaxInvoiceHeader> chargeCommons = cfTaxInvoiceHeaderMapper.selectList(Wrappers.lambdaQuery(CfTaxInvoiceHeader.class)
                .like(StringUtils.isNotBlank(value), CfTaxInvoiceHeader::getBalance, value));
        return PageInfoUtil.lambdaToList(chargeCommons, CfTaxInvoiceHeader::getBalance);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer addAndSubmit(TaxInvoiceAddDto taxInvoiceAddDto) {
        Long taxInvoiceId = add(taxInvoiceAddDto);
        Integer updateState = taxInvoiceStateService.updateState(taxInvoiceId, TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_TWO.getCode());
        return updateState;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer updateAndSubmit(TaxInvoiceCommonUpdateDto taxInvoiceCommonUpdateDto) {
        update(taxInvoiceCommonUpdateDto);
        Integer updateState = taxInvoiceStateService.updateState(taxInvoiceCommonUpdateDto.getTaxInvoiceId(), TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_TWO.getCode());
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_TAX, OperationTypeEnum.OPERATION_SUBMIT,null,taxInvoiceCommonUpdateDto.getTaxInvoiceId());
        return updateState;
    }

    @Transactional(rollbackFor = Exception.class)
    public String importInvoice(MultipartFile multipartFile) {
        List<TaxInvoiceImportDTO> taxInvoiceImportList = PageInfoUtil.importExcel(multipartFile, 0, 1, TaxInvoiceImportDTO.class);
        if (CollectionUtil.isEmpty(taxInvoiceImportList)) {
            throw new BusinessException(ModuleBizState.IMPORT_CONTENT_EMPTY);
        }
        List<TaxInvoiceImportDTO> notEmptyList =
                taxInvoiceImportList.stream().filter(o -> StringUtils.isNotBlank(o.getInvoiceNo()) || StringUtils.isNotBlank(o.getInvoiceRemark()) || StringUtils.isNotBlank(o.getPaymentDays())
                        || StringUtils.isNotBlank(o.getTaxInvoiceNo()) || StringUtils.isNotBlank(o.getInvoiceDate())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(notEmptyList)) {
            throw new BusinessException(ModuleBizState.IMPORT_TEMPLATE_WRONG);
        }
        String fileId = verifyImportParam(taxInvoiceImportList);
        if (StringUtils.isBlank(fileId)) {
            saveImportInvoice(taxInvoiceImportList);
        }
        return fileId;
    }

    private String verifyImportParam(List<TaxInvoiceImportDTO> taxInvoiceImportList) {
        List<TaxInvoiceDownloadDTO> taxInvoiceDownloadList = new ArrayList<>();
        for (TaxInvoiceImportDTO taxInvoiceImport : taxInvoiceImportList) {
            boolean allParamEmpty = StringUtils.isEmpty(taxInvoiceImport.getInvoiceNo()) && StringUtils.isEmpty(taxInvoiceImport.getInvoiceRemark())
                    && StringUtils.isEmpty(taxInvoiceImport.getPaymentDays()) && StringUtils.isEmpty(taxInvoiceImport.getTaxInvoiceNo())
                    && StringUtils.isEmpty(taxInvoiceImport.getInvoiceDate());
            if (allParamEmpty) {
                continue;
            }
            StringBuffer failedReason = new StringBuffer();
            TaxInvoiceDownloadDTO taxInvoiceDownload = new TaxInvoiceDownloadDTO();
            BeanUtils.copyProperties(taxInvoiceImport, taxInvoiceDownload);
            if (StringUtils.isEmpty(taxInvoiceImport.getTaxInvoiceNo())) {
                failedReason.append("发票流水号必填，");
            } else {
                String taxInvoiceNo = taxInvoiceImport.getTaxInvoiceNo().trim();
                CfTaxInvoiceHeader cfTaxInvoiceHeader = cfTaxInvoiceHeaderMapper.selectOne(Wrappers.lambdaQuery(CfTaxInvoiceHeader.class)
                        .eq(CfTaxInvoiceHeader::getTaxInvoiceNo, taxInvoiceNo)
                        .ne(CfTaxInvoiceHeader::getTaxInvoiceStatus, TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_EIGHT.getCode()));
                if (cfTaxInvoiceHeader == null) {
                    failedReason.append("开票流水号错误，");
                } else if (!cfTaxInvoiceHeader.getTaxInvoiceStatus().equals(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FOUR.getCode())) {
                    failedReason.append("仅允许【待开票】状态数据导入开票信息，");
                }
                if (StringUtils.isBlank(failedReason.toString())) {
                    long sameTaxinvoiceNoCount = taxInvoiceImportList.stream().filter(o -> taxInvoiceNo.equals(o.getTaxInvoiceNo()) || taxInvoiceImport.getTaxInvoiceNo().equals(o.getTaxInvoiceNo())).count();
                    if (sameTaxinvoiceNoCount > 1) {
                        failedReason.append("开票流水号不能重复，");
                    }
                }
            }
            if (StringUtils.isEmpty(taxInvoiceImport.getInvoiceNo())) {
                failedReason.append("发票号必填，");
            }
            if (StringUtils.isEmpty(taxInvoiceImport.getInvoiceDate())) {
                failedReason.append("开票时间必填，");
            } else if (!PageInfoUtil.isDayDate(taxInvoiceImport.getInvoiceDate())) {
                failedReason.append("开票时间格式错误，");
            }
            if (StringUtils.isEmpty(taxInvoiceImport.getPaymentDays())) {
                failedReason.append("财务账期必填，");
            } else if (!PageInfoUtil.isMonthDate(taxInvoiceImport.getPaymentDays())) {
                failedReason.append("财务账期格式错误，");
            }
            if (failedReason.toString().endsWith("，")) {
                String resultReason = failedReason.substring(0, failedReason.length() - 1);
                taxInvoiceDownload.setFailedReason(resultReason);
            }
            taxInvoiceDownloadList.add(taxInvoiceDownload);
        }
        List<TaxInvoiceDownloadDTO> failedList = taxInvoiceDownloadList.stream().filter(o -> StringUtils.isNotBlank(o.getFailedReason())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(failedList)) {
            /**
             * 上传错误Excel
             * 将包含错误原因的输出内容存在redis，方便用户下载错误原因是将其导出成Excel
             */
            String fileId = INVOICE_DOWNLOAD_PREFIX + UUID.randomUUID();
            redisTemplate.opsForValue().set(fileId, taxInvoiceDownloadList, 30, TimeUnit.DAYS);
            return fileId;
        }
        return "";
    }

    private void saveImportInvoice(List<TaxInvoiceImportDTO> taxInvoiceImportList) {
        for (TaxInvoiceImportDTO taxInvoiceImport : taxInvoiceImportList) {
            boolean allParamEmpty = StringUtils.isEmpty(taxInvoiceImport.getInvoiceNo()) && StringUtils.isEmpty(taxInvoiceImport.getInvoiceRemark())
                    && StringUtils.isEmpty(taxInvoiceImport.getPaymentDays()) && StringUtils.isEmpty(taxInvoiceImport.getTaxInvoiceNo())
                    && StringUtils.isEmpty(taxInvoiceImport.getInvoiceDate());
            if (allParamEmpty) {
                continue;
            }
            TaxInvoiceCommonUpdateInvoiceDto taxInvoiceCommonUpdateInvoiceDto = new TaxInvoiceCommonUpdateInvoiceDto();
            taxInvoiceCommonUpdateInvoiceDto.setInvoiceNo(taxInvoiceImport.getInvoiceNo());
            taxInvoiceCommonUpdateInvoiceDto.setTaxInvoiceNo(taxInvoiceImport.getTaxInvoiceNo());
            taxInvoiceCommonUpdateInvoiceDto.setPaymentDays(taxInvoiceImport.getPaymentDays());
            taxInvoiceCommonUpdateInvoiceDto.setInvoiceRemark(taxInvoiceImport.getInvoiceRemark());
            LocalDateTime invoiceDate = LocalDate.parse(taxInvoiceImport.getInvoiceDate(), DateTimeFormatter.ofPattern("yyyy/MM/dd")).atStartOfDay();
            taxInvoiceCommonUpdateInvoiceDto.setInvoiceDate(invoiceDate);
            CfTaxInvoiceHeader cfTaxInvoiceHeader = cfTaxInvoiceHeaderMapper.selectOne(Wrappers.lambdaQuery(CfTaxInvoiceHeader.class)
                    .eq(CfTaxInvoiceHeader::getTaxInvoiceNo, taxInvoiceImport.getTaxInvoiceNo()));
            taxInvoiceCommonUpdateInvoiceDto.setTaxInvoiceId(cfTaxInvoiceHeader.getTaxInvoiceId());
            updateInvoice(taxInvoiceCommonUpdateInvoiceDto);
        }
    }

    public void download(HttpServletResponse response, String fileId) {
        List<TaxInvoiceDownloadDTO> taxInvoiceDownloadList = (List<TaxInvoiceDownloadDTO>) redisTemplate.opsForValue().get(fileId);
        try {
            FileUtil.exportExcel(taxInvoiceDownloadList, "批量开票失败原因", "批量开票失败原因", TaxInvoiceDownloadDTO.class, "批量开票失败原因.xls", response);
            redisTemplate.delete(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void approvalCallback(CfTaxInvoiceHeader cfTaxInvoiceHeader, ApprovalFlowDTO approvalFlowDTO, Boolean status) {
        cfTaxInvoiceHeader = cfTaxInvoiceHeaderMapper.selectById(cfTaxInvoiceHeader.getTaxInvoiceId());
        approvalFlowDTO.setSrcCode(cfTaxInvoiceHeader.getTaxInvoiceNo());
        approvalFlowDTO.setProcessId(ApprovalEnum.TAX_INVOICE_APPROVAL.getProcessId());
        if (Objects.nonNull(status)) {
            //审批通过（流程结束）或审批被拒绝发给创建人
            approvalFlowService.sendNotify(approvalFlowDTO, cfTaxInvoiceHeader.getTaxInvoiceId(),
                                           cfTaxInvoiceHeader.getTaxInvoiceNo()
                    , ApprovalEnum.TAX_INVOICE_APPROVAL, status,
                                           cfTaxInvoiceHeader.getCreateBy(),
                                           cfTaxInvoiceHeader.getCreateName());
        } else {
            for (int i = 0; i < approvalFlowDTO.getTargetUserId().size(); i++) {
                approvalFlowService.sendNotify(approvalFlowDTO, cfTaxInvoiceHeader.getTaxInvoiceId(),
                        cfTaxInvoiceHeader.getTaxInvoiceNo()
                        , ApprovalEnum.TAX_INVOICE_APPROVAL, status,
                        Long.parseLong(approvalFlowDTO.getTargetUserId().get(i)),
                        approvalFlowDTO.getTargetUserName().get(i));
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer addAndComplete(@Validated @RequestBody TaxInvoiceAddDto taxInvoiceAddDto) {
        taxInvoiceAddDto.setTaxInvoiceStatus(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FOUR.getCode());
        Long taxInvoiceId = this.add(taxInvoiceAddDto);
        TaxInvoiceCommonUpdateStatusDto taxInvoiceCommonUpdateStatusDto = new TaxInvoiceCommonUpdateStatusDto();
        taxInvoiceCommonUpdateStatusDto.setTaxInvoiceId(taxInvoiceId);
        taxInvoiceCommonUpdateStatusDto.setTaxInvoiceStatus(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FIVE.getCode());
        return this.updateStatus(taxInvoiceCommonUpdateStatusDto);
    }


    /**
     * 查询mcn收入合同关联发票
     * @param contractCode
     * @return List<McnTaxInvoiceVo>
     */
    public List<McnTaxInvoiceVo> getMcnTaxInvoice(String contractCode){
        //根据收入合同编号查询未删除的费用
        List<CfChargeCommon> chargeList = chargeCommonService.selectCfChargesBySourceCode(contractCode);
        if(CollectionUtils.isEmpty(chargeList)){
            return null;
        }
        List<Long> chargeIds = chargeList.stream().map(CfChargeCommon::getChargeId).distinct().collect(Collectors.toList());
        List<CfTaxInvoiceDetail> cfTaxInvoiceDetails = cfTaxInvoiceDetailMapper.selectList(
                Wrappers.lambdaQuery(CfTaxInvoiceDetail.class)
                        .eq(CfTaxInvoiceDetail::getChargeType,InvoiceHeaderEnum.CHARGE_TYPE_THREE.getCode())
                        .in(CfTaxInvoiceDetail::getChargeId,chargeIds));
        if(CollectionUtils.isEmpty(cfTaxInvoiceDetails)){
            return null;
        }
        List<Long> taxInvoiceIds = cfTaxInvoiceDetails.stream().map(CfTaxInvoiceDetail::getTaxInvoiceId).distinct().collect(Collectors.toList());
        List<CfTaxInvoiceHeader> cfTaxInvoiceHeaders = cfTaxInvoiceHeaderMapper.selectList(
                Wrappers.lambdaQuery(CfTaxInvoiceHeader.class)
                        //.eq(CfTaxInvoiceHeader::getJobType, InvoiceHeaderEnum.JOB_TYPE_THREE.getCode())
                        .in(CfTaxInvoiceHeader::getTaxInvoiceId,taxInvoiceIds)
        );
        if(CollectionUtils.isEmpty(cfTaxInvoiceHeaders)){
            return null;
        }
        Map<Long,CfChargeCommon> taxInvoiceIdMap = new HashMap<>(taxInvoiceIds.size());
        cfTaxInvoiceDetails.forEach(detail -> {
            Optional<CfChargeCommon> optional = chargeList.stream().filter(t -> t.getChargeId().equals(detail.getChargeId())).findFirst();
            if(optional.isPresent()){
                taxInvoiceIdMap.putIfAbsent(detail.getTaxInvoiceId(),optional.get());
            }
        });
        List<McnTaxInvoiceVo> dataList = TaxInvoiceConvertor.INSTANCE.asVoList(cfTaxInvoiceHeaders);
        dataList.forEach(data -> {
            CfChargeCommon chargeCommon = taxInvoiceIdMap.get(data.getTaxInvoiceId());
            data.setFinanceEntity(chargeCommon != null ? chargeCommon.getFinanceEntity() : null);
        });
        return dataList;
    }

}
