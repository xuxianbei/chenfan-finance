package com.chenfan.finance.service.common.state;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.dao.CfTaxInvoiceDetailMapper;
import com.chenfan.finance.dao.CfTaxInvoiceHeaderMapper;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.CfChargeCommon;
import com.chenfan.finance.model.CfTaxInvoiceDetail;
import com.chenfan.finance.model.CfTaxInvoiceHeader;
import com.chenfan.finance.server.McnRemoteServer;
import com.chenfan.finance.service.common.ChargeCommonService;
import com.chenfan.finance.utils.OperateUtil;
import com.chenfan.finance.utils.RpcUtil;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.chenfan.finance.utils.pageinfo.TimeThreadSafeUtils;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.chenfan.finance.enums.InvoiceHeaderEnum.CHARGE_TYPE_THREE;


/**
 * 发票状态服务
 * 感觉写崩了：虽然解决了状态变化对代码影响，增加了扩展性。感觉还是不够灵活，需要进一步增加扩张性
 * 定位是状态更新，未定位状态绑定事件
 *
 * @author: xuxianbei
 * Date: 2021/3/4
 * Time: 10:07
 * Version:V1.0
 */
@Slf4j
@Service
public class TaxInvoiceStateService extends AbstractStateService {


    public static final String BUSINESS_LOCK = "TaxInvoiceState:";
    /**
     * 审批流Id
     */
    public static final Long APPROVAL_PROCESS_ID = 1366992970401710080L;

    @Resource
    private CfTaxInvoiceHeaderMapper cfTaxInvoiceHeaderMapper;

    @Resource
    private CfTaxInvoiceDetailMapper cfTaxInvoiceDetailMapper;

    @Autowired
    private ChargeCommonService chargeCommonService;

    @Autowired
    private PageInfoUtil pageInfoUtil;

    @Autowired
    private McnRemoteServer mcnRemoteServer;

    /**
     * 开票状态流程
     */
    private List<List<GetCode>> taxInvoiceStatussTaxInvoice = new ArrayList<>();

    {
        taxInvoiceStatussTaxInvoice.add(Arrays.asList(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_ONE, TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SEVEN));
        taxInvoiceStatussTaxInvoice.add(Arrays.asList(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_TWO, TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_THREE));
        taxInvoiceStatussTaxInvoice.add(Arrays.asList(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FOUR));
        taxInvoiceStatussTaxInvoice.add(Arrays.asList(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FIVE, TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FIVE));
        taxInvoiceStatussTaxInvoice.add(Arrays.asList(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SIX, TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SIX));
    }

    /**
     * 无票，后补票
     */
    private List<List<GetCode>> taxInvoiceStatussTaxInvoiceNone = new ArrayList<>();

    {
        taxInvoiceStatussTaxInvoiceNone.add(Arrays.asList(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_ONE, TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SEVEN));
        taxInvoiceStatussTaxInvoiceNone.add(Arrays.asList(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_TWO, TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_THREE));
        taxInvoiceStatussTaxInvoiceNone.add(Arrays.asList(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FOUR));
        taxInvoiceStatussTaxInvoiceNone.add(Arrays.asList(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FIVE));
        taxInvoiceStatussTaxInvoiceNone.add(Arrays.asList(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SIX, TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SIX));
    }

    /**
     * 作废
     */
    private List<GetCode> cancelTaxInvoiceStatus = new ArrayList<>();

    {
        cancelTaxInvoiceStatus.add(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_ONE);
        cancelTaxInvoiceStatus.add(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_TWO);
        cancelTaxInvoiceStatus.add(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_THREE);
        cancelTaxInvoiceStatus.add(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FOUR);
        cancelTaxInvoiceStatus.add(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FIVE);
        cancelTaxInvoiceStatus.add(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SIX);
        cancelTaxInvoiceStatus.add(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SEVEN);
    }

    /**
     * 撤销
     */
    private List<GetCode> revokeTaxInvoicStatus = new ArrayList<>();

    {
        revokeTaxInvoicStatus.add(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_ONE);
        revokeTaxInvoicStatus.add(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_TWO);
        revokeTaxInvoicStatus.add(TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_THREE);
    }


    /**
     * 更新状态
     *
     * @param taxInvoiceNo
     * @param invoiceStatus
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateState(String taxInvoiceNo, Integer invoiceStatus, Object object) {
        CfTaxInvoiceHeader cfTaxInvoiceHeader = cfTaxInvoiceHeaderMapper.selectOne(Wrappers.lambdaQuery(CfTaxInvoiceHeader.class)
                .eq(CfTaxInvoiceHeader::getTaxInvoiceNo, taxInvoiceNo));
        return updateState(cfTaxInvoiceHeader.getTaxInvoiceId(), invoiceStatus, object);
    }

    /**
     * 更新状态
     *
     * @param taxInvoiceId
     * @param invoiceStatus
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateState(Long taxInvoiceId, Integer invoiceStatus) {
        return updateState(taxInvoiceId, invoiceStatus, null);
    }

    /**
     * 更新状态
     *
     * @param taxInvoiceId
     * @param invoiceStatus
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateState(Long taxInvoiceId, Integer invoiceStatus, Object object) {
        pageInfoUtil.tryLockBusinessTip(BUSINESS_LOCK, taxInvoiceId);
        try {
            CfTaxInvoiceHeader cfTaxInvoiceHeader = cfTaxInvoiceHeaderMapper.selectById(taxInvoiceId);
            CFRequestHolder.setUserThreadLocal(null);
            switch (invoiceStatus) {
                //作废
                case 8:
                    judgeInList(cancelTaxInvoiceStatus, cfTaxInvoiceHeader.getTaxInvoiceStatus());
                    UserVO userVO = new UserVO();
                    userVO.setRealName(cfTaxInvoiceHeader.getCreateName());
                    userVO.setUserId(cfTaxInvoiceHeader.getCreateBy());
                    CFRequestHolder.setUserThreadLocal(userVO);
                    break;
                //撤销
                case 7:
                    judgeInList(revokeTaxInvoicStatus, cfTaxInvoiceHeader.getTaxInvoiceStatus());
                    break;
                //正常状态跳转
                default:
                    Assert.isTrue(judgeStatus(cfTaxInvoiceHeader.getTaxInvoiceStatus(), invoiceStatus, cfTaxInvoiceHeader.getTaxInvoiceWay()), ModuleBizState.DATE_ERROR.message());
            }
            CfTaxInvoiceHeader cfInvoiceHeaderUpdate = new CfTaxInvoiceHeader();
            cfInvoiceHeaderUpdate.setTaxInvoiceId(taxInvoiceId);
            cfInvoiceHeaderUpdate.setTaxInvoiceStatus(invoiceStatus);
            cfInvoiceHeaderUpdate.setUpdateDate(LocalDateTime.now());
            //状态之后
            afterStatus(invoiceStatus, cfInvoiceHeaderUpdate, object, cfTaxInvoiceHeader, cfTaxInvoiceHeader.getTaxInvoiceWay());
            return cfTaxInvoiceHeaderMapper.updateById(cfInvoiceHeaderUpdate);
        } finally {
            pageInfoUtil.tryUnLock(BUSINESS_LOCK, taxInvoiceId);
        }
    }

    /**
     * 因为业务复杂度不够，这里就简单处理了
     * 如果复杂度增加：考虑状态绑定事件
     *
     * @param invoiceStatus
     * @param cfTaxInvoiceHeaderUpdate
     */
    private void afterStatus(Integer invoiceStatus, CfTaxInvoiceHeader cfTaxInvoiceHeaderUpdate, Object object,
                             CfTaxInvoiceHeader cfTaxInvoiceHeaderOld, int taxInvoiceWay) {
        List<CfTaxInvoiceDetail> cfTaxInvoiceDetails =
                cfTaxInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfTaxInvoiceDetail.class).eq(CfTaxInvoiceDetail::getTaxInvoiceId, cfTaxInvoiceHeaderUpdate.getTaxInvoiceId()));

        //开启审批流
        if (TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_TWO.getCode().equals(invoiceStatus)) {
            cfTaxInvoiceHeaderUpdate.setFlowId(pageInfoUtil.startProcess(cfTaxInvoiceHeaderUpdate, cfTaxInvoiceHeaderUpdate.getClass(), APPROVAL_PROCESS_ID, cfTaxInvoiceHeaderUpdate.getTaxInvoiceId()));
        } else if (TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_SIX.getCode().equals(invoiceStatus)) {
            //已核销
            doClear(cfTaxInvoiceHeaderUpdate, object, cfTaxInvoiceHeaderOld, cfTaxInvoiceDetails);
        } else if (TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_EIGHT.getCode().equals(invoiceStatus)) {
            //已作废
            pageInfoUtil.stopProcess(cfTaxInvoiceHeaderOld.getFlowId());
            cfTaxInvoiceDetails.forEach(cfTaxInvoiceDetail -> {
                chargeCommonService.updateByInvoice(cfTaxInvoiceDetail.getChargeId(), "", null);
                mcnFillInvoice(cfTaxInvoiceHeaderOld, cfTaxInvoiceDetail, false);
            });
        } else if (TaxInvoiceHeaderEnum.TAX_INVOICE_STATUS_FIVE.getCode().equals(invoiceStatus)) {
            //已开票
            cfTaxInvoiceHeaderUpdate.setInvoiceNo(FA_PIAO + cfTaxInvoiceHeaderUpdate.getTaxInvoiceId());
            cfTaxInvoiceHeaderUpdate.setInvoiceDate(LocalDateTime.now());
            cfTaxInvoiceHeaderUpdate.setPaymentDays(TimeThreadSafeUtils.dateTimeFormat(cfTaxInvoiceHeaderUpdate.getInvoiceDate(),
                    TimeThreadSafeUtils.YYYY_MM));

            cfTaxInvoiceDetails.forEach(cfTaxInvoiceDetail -> {
                mcnFillInvoice(cfTaxInvoiceHeaderOld, cfTaxInvoiceDetail, true);
            });

            //无票
            if (InvoiceHeaderEnum.CUSTOMER_INVOICE_WAY_TAX_INVOICE_NONE.getCode() == taxInvoiceWay) {
                //已开票
                cfTaxInvoiceDetails.forEach(cfInvoiceDetail1 -> {
                    //反写费用
                    chargeCommonService.updateByInvoiceSetInvoiceNo(cfInvoiceDetail1.getChargeId(), cfTaxInvoiceHeaderUpdate.getInvoiceNo(), cfTaxInvoiceHeaderUpdate.getInvoiceDate());
                });

            }
        }
        OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_TAX, CFRequestHolder.getIsUpdateThreadLocal()?OperationTypeEnum.OPERATION_UPDATE:OperationTypeEnum.getTypeByTaxInvoiceHeaderEnum(TaxInvoiceHeaderEnum.getByCode(invoiceStatus)),cfTaxInvoiceHeaderOld.getTaxInvoiceNo(),cfTaxInvoiceHeaderOld.getTaxInvoiceId());

    }

    private void mcnFillInvoice(CfTaxInvoiceHeader cfTaxInvoiceHeaderOld, CfTaxInvoiceDetail cfTaxInvoiceDetail, boolean positive) {
        if (ChargeCommonEnum.CHARGE_TYPE_HOT_MAN.getCode().equals(cfTaxInvoiceDetail.getChargeType())) {
            mcnRemoteServer.invoicedCallBack(cfTaxInvoiceHeaderOld.getTaxInvoiceNo(), positive ? cfTaxInvoiceDetail.getInvoiceDebit() : cfTaxInvoiceDetail.getInvoiceDebit().negate(),
                    cfTaxInvoiceDetail.getChargeSourceCode());
        }
    }

    private void doClear(CfTaxInvoiceHeader cfTaxInvoiceHeaderUpdate, Object object, CfTaxInvoiceHeader cfTaxInvoiceHeaderOld, List<CfTaxInvoiceDetail> cfTaxInvoiceDetails) {
        if (object instanceof InvoiceStateService.UpdateStateData) {
            InvoiceStateService.UpdateStateData updateStateData = (InvoiceStateService.UpdateStateData) object;
            /**
             * 第一种已核销金额等于开票的总金额
             * 第二种已核销金额小于开票的总金额
             */
            //处理不可重复读场景
            List<CfChargeCommon> needs = new ArrayList<>();
            List<CfChargeCommon> chargeCommons =
                    chargeCommonService.selectList(cfTaxInvoiceDetails.stream().map(CfTaxInvoiceDetail::getChargeId).collect(Collectors.toList()));

            for (CfChargeCommon chargeCommon : chargeCommons) {
                if (!updateStateData.getClearCharges().stream().filter(t -> t.getChargeId().equals(chargeCommon.getChargeId())).findFirst().isPresent()) {
                    needs.add(chargeCommon);
                }
            }
            //如果剩余的费用的发票号全部不会空，那么全部核销了
            cfTaxInvoiceHeaderUpdate.setClearStatus(
                    needs.stream().filter(t -> StringUtils.isNotBlank(t.getInvoiceNo())).collect(Collectors.toList()).size() == needs.size() ? 2 : 1);
        }
        //MCN 如果是非拆分费用，核销的信息的费用一定是一条。因为一个收入合同一定对应一个来源单号
        CfTaxInvoiceDetail cfTaxInvoiceDetail = cfTaxInvoiceDetails.get(0);
        //MCN收入
        if (CHARGE_TYPE_THREE.getCode().equals(cfTaxInvoiceDetail.getChargeType())) {
            List<CfChargeCommon> chargeCommons =
                    chargeCommonService.selectList(Wrappers.lambdaQuery(CfChargeCommon.class).eq(CfChargeCommon::getChargeSourceCode, cfTaxInvoiceDetail.getChargeSourceCode()));

            List<CfChargeCommon> needs = new ArrayList<>();
            //处理不可重复读场景
            for (CfChargeCommon chargeCommon : chargeCommons) {
                if (!cfTaxInvoiceDetails.stream().filter(t -> t.getChargeId().equals(chargeCommon.getChargeId())).findFirst().isPresent()) {
                    needs.add(chargeCommon);
                }
            }
            //汇总核销金额
            BigDecimal totalActualAmount = BigDecimal.ZERO;
            if (!CollectionUtils.isEmpty(needs)) {
                for (CfChargeCommon t : needs) {
                    totalActualAmount = totalActualAmount.add(t.getActualAmount());
                }
            }
            //如果存在核销号为空，判定部分付款
            /*RpcUtil.getObjNoException(mcnRemoteServer.changePayBackStatus(needs.stream().filter(t -> StringUtils.isBlank(t.getClearNo())).findFirst().isPresent() ? 1 : 2,
                    cfTaxInvoiceDetail.getChargeSourceCode()));*/
            //根据核销金额跟合同金额比较，自行判断回款状态
            RpcUtil.getObjNoException(mcnRemoteServer.payBackCallBack(totalActualAmount, cfTaxInvoiceDetail.getChargeSourceCode()));
        }
    }


    /**
     * 这个状态判断，扩展性太差了。
     * 只能顺时针切换状态
     *
     * @param oldInvoiceStatus
     * @param invoiceStatus
     * @param customerInvoiceWay
     * @return
     */
    @Override
    protected boolean judgeStatus(Integer oldInvoiceStatus, Integer invoiceStatus, Integer customerInvoiceWay) {
        switch (customerInvoiceWay) {
            case 1:
                Integer newStatus = getInvoiceStatusEnumFromList(oldInvoiceStatus, taxInvoiceStatussTaxInvoice, invoiceStatus);
                return newStatus.equals(invoiceStatus);
            case 2:
                Integer newStatus2 = getInvoiceStatusEnumFromList(oldInvoiceStatus, taxInvoiceStatussTaxInvoiceNone, invoiceStatus);
                return newStatus2.equals(invoiceStatus);
            default:
                throw new IllegalStateException("Unexpected value: " + customerInvoiceWay);
        }
    }
}
