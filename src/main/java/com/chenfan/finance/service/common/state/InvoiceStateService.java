package com.chenfan.finance.service.common.state;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.dao.CfInvoiceDetailMapper;
import com.chenfan.finance.dao.CfInvoiceHeaderMapper;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.exception.FinanceBizState;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.CfBankAndCash;
import com.chenfan.finance.model.CfChargeCommon;
import com.chenfan.finance.model.CfInvoiceDetail;
import com.chenfan.finance.model.CfInvoiceHeader;
import com.chenfan.finance.server.McnRemoteServer;
import com.chenfan.finance.server.dto.ExcutionSetPaidDTO;
import com.chenfan.finance.service.common.ChargeCommonService;
import com.chenfan.finance.utils.OperateUtil;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.chenfan.finance.utils.pageinfo.TimeThreadSafeUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.chenfan.finance.enums.InvoiceHeaderEnum.*;


/**
 * 账单状态服务
 * 感觉写崩了：虽然解决了状态变化对代码影响，增加了扩展性。感觉还是不够灵活，需要进一步增加扩张性
 *
 * @author: xuxianbei
 * Date: 2021/3/4
 * Time: 10:07
 * Version:V1.0
 */
@Service
public class InvoiceStateService extends AbstractStateService {

    public static final String BUSINESS_LOCK = "InvoiceState:";

    @Autowired
    private McnRemoteServer mcnRemoteServer;
    /**
     * 审批流Id
     */
    public static final Long APPROVAL_PROCESS_ID = 1371280750556479488L;


    @Resource
    private CfInvoiceHeaderMapper cfInvoiceHeaderMapper;

    @Resource
    private CfInvoiceDetailMapper cfInvoiceDetailMapper;

    @Autowired
    private ChargeCommonService chargeCommonService;

    /**
     * 开票状态流程
     * 优点：统一管理了状态，这样只要考虑状态是否组织正确即可。
     * 加强点：把状态和执行内容进行绑定，这样方便后续代码维护
     */
    private List<List<GetCode>> invoiceStatussTaxInvoice = new ArrayList<>();

    {
        //优化思路：定义source 和 target 数据结构都是List。
        invoiceStatussTaxInvoice.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_ONE, InvoiceHeaderEnum.INVOICE_STATUS_THIRTEEN));
        invoiceStatussTaxInvoice.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_TEN, InvoiceHeaderEnum.INVOICE_STATUS_FOUTTEEN));
        invoiceStatussTaxInvoice.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_ELEVNE));
        invoiceStatussTaxInvoice.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_TWELVE, InvoiceHeaderEnum.INVOICE_STATUS_FIVETEEN));
        invoiceStatussTaxInvoice.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_NINE));
    }

    /**
     * 无票
     */
    private List<List<GetCode>> invoiceStatussTaxInvoiceNone = new ArrayList<>();

    {
        invoiceStatussTaxInvoiceNone.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_ONE, InvoiceHeaderEnum.INVOICE_STATUS_THIRTEEN));
        invoiceStatussTaxInvoiceNone.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_TEN, InvoiceHeaderEnum.INVOICE_STATUS_FOUTTEEN));
        invoiceStatussTaxInvoiceNone.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_ELEVNE));
        invoiceStatussTaxInvoiceNone.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_TWELVE, InvoiceHeaderEnum.INVOICE_STATUS_FIVETEEN));
        invoiceStatussTaxInvoiceNone.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_NINE));
    }

    /**
     * 后补票
     */
    private List<List<GetCode>> invoiceStatussTaxInvoiceLater = new ArrayList<>();

    {
        invoiceStatussTaxInvoiceLater.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_ONE, InvoiceHeaderEnum.INVOICE_STATUS_THIRTEEN));
        invoiceStatussTaxInvoiceLater.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_TEN, InvoiceHeaderEnum.INVOICE_STATUS_FOUTTEEN));
        invoiceStatussTaxInvoiceLater.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_TWELVE, InvoiceHeaderEnum.INVOICE_STATUS_FIVETEEN));
        invoiceStatussTaxInvoiceLater.add(Arrays.asList(InvoiceHeaderEnum.INVOICE_STATUS_NINE));
    }

    /**
     * 作废
     */
    private List<GetCode> cancelInvoiceStatus = new ArrayList<>();

    {
        cancelInvoiceStatus.add(InvoiceHeaderEnum.INVOICE_STATUS_ONE);
        cancelInvoiceStatus.add(InvoiceHeaderEnum.INVOICE_STATUS_THIRTEEN);
        cancelInvoiceStatus.add(InvoiceHeaderEnum.INVOICE_STATUS_TEN);
        cancelInvoiceStatus.add(InvoiceHeaderEnum.INVOICE_STATUS_FOUTTEEN);
        cancelInvoiceStatus.add(InvoiceHeaderEnum.INVOICE_STATUS_ELEVNE);
        cancelInvoiceStatus.add(InvoiceHeaderEnum.INVOICE_STATUS_TWELVE);
    }

    /**
     * 撤销
     */
    private List<GetCode> revokeInvoicStatus = new ArrayList<>();

    {
        revokeInvoicStatus.add(InvoiceHeaderEnum.INVOICE_STATUS_ONE);
        revokeInvoicStatus.add(InvoiceHeaderEnum.INVOICE_STATUS_TEN);
        revokeInvoicStatus.add(InvoiceHeaderEnum.INVOICE_STATUS_FOUTTEEN);
    }


    /**
     * 强制驳回
     */
    private List<GetCode> forceCallbackInvoicStatus = new ArrayList<>();

    {
        forceCallbackInvoicStatus.add(InvoiceHeaderEnum.INVOICE_STATUS_ELEVNE);
    }

    @Data
    public static class UpdateStateData {
        /**
         * 备注
         */
        private String remark;

        /**
         * 金额
         */
        private BigDecimal amount;

        /**
         * 收付时间
         */
        private CfBankAndCash cfBankAndCash;

        /**
         * 核销的费用
         */
        private List<CfChargeCommon> clearCharges;

        /**
         * 额外状态判断
         */
        private JudgeStateExt judgeStateExt;
    }

    /**
     * 更新状态
     *
     * @param invoiceNo
     * @param invoiceStatus
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateState(String invoiceNo, Integer invoiceStatus, Object object) {
        CfInvoiceHeader cfInvoiceHeader = cfInvoiceHeaderMapper.selectOne(Wrappers.lambdaQuery(CfInvoiceHeader.class)
                .eq(CfInvoiceHeader::getInvoiceNo, invoiceNo));
        return updateState(cfInvoiceHeader.getInvoiceId(), invoiceStatus, object);
    }

    /**
     * 更新状态
     *
     * @param invoiceId
     * @param invoiceStatus
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateState(Long invoiceId, Integer invoiceStatus) {
        return updateState(invoiceId, invoiceStatus, null);
    }

    /**
     * 更新状态
     *
     * @param invoiceId
     * @param invoiceStatus
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateState(Long invoiceId, Integer invoiceStatus, Object object) {
        pageInfoUtil.tryLockBusinessTip(BUSINESS_LOCK, invoiceId);
        try {
            CfInvoiceHeader cfInvoiceHeader = cfInvoiceHeaderMapper.selectById(invoiceId);
            CFRequestHolder.setUserThreadLocal(null);
            //效率嘛，相当的一般的啊
            switch (InvoiceHeaderEnum.valueOfInvoiceStatus(invoiceStatus)) {
                //作废
                case INVOICE_STATUS_SEVEN:
                    judgeInList(cancelInvoiceStatus, cfInvoiceHeader.getInvoiceStatus());
                    UserVO userVO = new UserVO();
                    userVO.setRealName(cfInvoiceHeader.getCreateName());
                    userVO.setUserId(cfInvoiceHeader.getCreateBy());
                    CFRequestHolder.setUserThreadLocal(userVO);
                    break;
                //撤销
                case INVOICE_STATUS_THIRTEEN:
                    judgeInList(revokeInvoicStatus, cfInvoiceHeader.getInvoiceStatus());
                    break;
                case UNDEFINE:
                    throw FinanceBizState.DATE_ERROR;
                    //正常状态跳转
                default: {
                    //强制驳回 又写崩了
                    if (forceBack(invoiceStatus, cfInvoiceHeader)) {
                        //
                     OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_INVOICE, OperationTypeEnum.OPERATION_FORCE_BACK,cfInvoiceHeader.getInvoiceNo(),cfInvoiceHeader.getInvoiceId());
                    } else {
                        JudgeStateExt judgeStateExt = null;
                        if (object instanceof UpdateStateData) {
                            UpdateStateData updateStateData = (UpdateStateData) object;
                            judgeStateExt = updateStateData.getJudgeStateExt();
                        }
                        Assert.isTrue(judgeStatus(cfInvoiceHeader.getInvoiceStatus(), invoiceStatus, cfInvoiceHeader.getCustomerInvoiceWay(),
                                judgeStateExt), ModuleBizState.DATE_ERROR.message());
                    }
                }
            }
            CfInvoiceHeader cfInvoiceHeaderUpdate = new CfInvoiceHeader();
            cfInvoiceHeaderUpdate.setInvoiceId(invoiceId);
            cfInvoiceHeaderUpdate.setInvoiceStatus(invoiceStatus);
            cfInvoiceHeaderUpdate.setUpdateDate(LocalDateTime.now());
            //状态之后  这里就尴尬了，状态和事件分离了 差不多彻底写崩了
            afterStatus(invoiceStatus, cfInvoiceHeaderUpdate, object, cfInvoiceHeader, cfInvoiceHeader.getCustomerInvoiceWay());
            OperateUtil.insertOperationLog(OperationBsTypeEnum.OPERATION_BS_MCN_INVOICE, OperationTypeEnum.getTypeByInvoiceHeaderEnum(InvoiceHeaderEnum.valueOfInvoiceStatus(invoiceStatus)),cfInvoiceHeader.getInvoiceNo(),cfInvoiceHeader.getInvoiceId());


            return cfInvoiceHeaderMapper.updateById(cfInvoiceHeaderUpdate);
        } finally {
            pageInfoUtil.tryUnLock(BUSINESS_LOCK, invoiceId);
        }
    }

    private boolean forceBack(Integer invoiceStatus, CfInvoiceHeader cfInvoiceHeader) {
        return forceCallbackInvoicStatus.stream().filter(t -> t.getCode().equals(cfInvoiceHeader.getInvoiceStatus())).findFirst().isPresent()
                && invoiceStatus.equals(InvoiceHeaderEnum.INVOICE_STATUS_ONE.getCode());
    }


    /**
     * 因为业务复杂度不够，这里就简单处理了
     * 如果复杂度增加：考虑状态绑定事件
     *
     * @param invoiceStatus
     * @param cfInvoiceHeaderUpdate
     */
    private void afterStatus(Integer invoiceStatus, CfInvoiceHeader cfInvoiceHeaderUpdate, Object object,
                             CfInvoiceHeader cfInvoiceHeader, int customerInvoiceWay) {
        List<CfInvoiceDetail> cfInvoiceDetail =
                cfInvoiceDetailMapper.selectList(Wrappers.lambdaQuery(CfInvoiceDetail.class).eq(CfInvoiceDetail::getInvoiceId, cfInvoiceHeaderUpdate.getInvoiceId()));
        List<CfInvoiceDetail> needInvoiceDetails =
                cfInvoiceDetail.stream().filter(t -> !t.getChargeType().equals(InvoiceHeaderEnum.CHARGE_TYPE_SIX.getCode())).collect(Collectors.toList());

        //开启审批流
        if (InvoiceHeaderEnum.INVOICE_STATUS_TEN.getCode().equals(invoiceStatus)) {

            //红人校验
            hotManVertify(cfInvoiceHeader);

            cfInvoiceHeaderUpdate.setFlowId(pageInfoUtil.startProcess(cfInvoiceHeaderUpdate, cfInvoiceHeaderUpdate.getClass(), APPROVAL_PROCESS_ID,
                    cfInvoiceHeaderUpdate.getInvoiceId()));
        }

        //强制驳回
        if (forceBack(invoiceStatus, cfInvoiceHeader)) {
            chargeCallBack(cfInvoiceHeader, cfInvoiceDetail, cfInvoiceHeaderUpdate, 1);
        }

        //已作废
        if (InvoiceHeaderEnum.INVOICE_STATUS_SEVEN.getCode().equals(invoiceStatus)) {
            chargeCallBack(cfInvoiceHeader, cfInvoiceDetail, cfInvoiceHeaderUpdate, 0);
        }

        //已撤回
        if (InvoiceHeaderEnum.INVOICE_STATUS_THIRTEEN.getCode().equals(invoiceStatus)) {
            chargeCallBack(cfInvoiceHeader, cfInvoiceDetail, cfInvoiceHeaderUpdate, 1);
        }

        //已核销
        if (InvoiceHeaderEnum.INVOICE_STATUS_NINE.getCode().equals(invoiceStatus)) {
            StringBuilder remark = new StringBuilder("");
            if (object instanceof UpdateStateData) {
                UpdateStateData updateStateData = (UpdateStateData) object;
                cfInvoiceHeaderUpdate.setClearAmount(cfInvoiceHeader.getInvoicelCredit());
                cfInvoiceHeaderUpdate.setClearStatus(InvoiceHeaderEnum.CLEAR_STATUS_ALL.getCode());
                Assert.isTrue(cfInvoiceHeaderUpdate.getClearAmount().compareTo(cfInvoiceHeader.getInvoicelCredit()) >= 0, ModuleBizState.DATE_ERROR.message());

                remark.append(updateStateData.getRemark());
                cfInvoiceHeaderUpdate.setArapDate(PageInfoUtil.dateToLocalDateTime(updateStateData.getCfBankAndCash().getArapDate()));

                List<String> chargeSouceCodes = PageInfoUtil.lambdaToList(needInvoiceDetails, CfInvoiceDetail::getChargeSourceCode);
                List<ExcutionSetPaidDTO> excutionSetPaidDtos = chargeSouceCodes.stream().map(value -> {
                    ExcutionSetPaidDTO excutionSetPaidDTO = new ExcutionSetPaidDTO();
                    excutionSetPaidDTO.setExcuteCode(value);
                    excutionSetPaidDTO.setPaidDate(PageInfoUtil.dateToLocalDateTime(updateStateData.getCfBankAndCash().getArapDate()));
                    excutionSetPaidDTO.setPaidRemark(updateStateData.getCfBankAndCash().getRemark());
                    excutionSetPaidDTO.setPaidWay(cfInvoiceHeader.getAccountName());
                    return excutionSetPaidDTO;
                }).collect(Collectors.toList());

                int chargeType = Integer.valueOf(needInvoiceDetails.get(0).getChargeType());
                //客户返点费不调用
                if (chargeType != CHARGE_TYPE_TWO.getCode()) {
                    mcnRemoteServer.setPaid(excutionSetPaidDtos);
                }
                //年度返点费 或者 客户返点费
                if (chargeType == CHARGE_TYPE_FIVE.getCode() || chargeType == CHARGE_TYPE_TWO.getCode()) {
                    mcnRemoteServer.financeStatusChange(chargeSouceCodes, 5);
                }
            }
        }

        //这里彻底写崩了，GOOD LUCK
        if (InvoiceHeaderEnum.INVOICE_STATUS_TWELVE.getCode().equals(invoiceStatus)) {
            //无票
            if (InvoiceHeaderEnum.CUSTOMER_INVOICE_WAY_TAX_INVOICE_NONE.getCode() == customerInvoiceWay) {
                //已开票
                cfInvoiceDetail.forEach(cfInvoiceDetail1 -> {
                    cfInvoiceDetail1.setCustomerInvoiceNo(FA_PIAO + cfInvoiceHeaderUpdate.getInvoiceId());
                    cfInvoiceDetail1.setCustomerInvoiceDate(LocalDateTime.now());
                    cfInvoiceDetail1.setPaymentDays(TimeThreadSafeUtils.dateTimeFormat(cfInvoiceDetail1.getCustomerInvoiceDate(),
                            TimeThreadSafeUtils.YYYY_MM));
                    cfInvoiceDetailMapper.updateById(cfInvoiceDetail1);

                    //反写费用
                    chargeCommonService.updateByInvoiceSetInvoiceNo(cfInvoiceDetail1.getChargeId(), FA_PIAO + cfInvoiceHeaderUpdate.getInvoiceId(), cfInvoiceDetail1.getCustomerInvoiceDate());
                });

            }
        }
    }

    /**
     * 红人校验
     *
     * @param cfInvoiceHeader
     */
    private void hotManVertify(CfInvoiceHeader cfInvoiceHeader) {
        if (InvoiceHeaderEnum.SETTLE_TEMPLATE_IN_RED.getCode().equals(cfInvoiceHeader.getSettleTemplate()) ||
                (SETTLE_TEMPLATE_OUT_RED.getCode().equals(cfInvoiceHeader.getSettleTemplate()))) {
            if (StringUtils.isBlank(cfInvoiceHeader.getAccountName())) {
                throw FinanceBizState.HOT_MAN_VERTIFY_ERROR;
            }
        }
    }

    /**
     * 费用回写
     *
     * @param cfInvoiceHeader
     * @param cfInvoiceDetail
     * @param cfInvoiceHeaderUpdate
     * @param dealWay               0 解除关联。
     */
    private void chargeCallBack(CfInvoiceHeader cfInvoiceHeader, List<CfInvoiceDetail> cfInvoiceDetail, CfInvoiceHeader cfInvoiceHeaderUpdate,
                                Integer dealWay) {
        cfInvoiceDetail.forEach(cfInvoiceDetail1 -> {
            if (String.valueOf(CHARGE_TYPE_SIX.getCode()).equals(cfInvoiceDetail1.getChargeType())) {
                if (cfInvoiceDetail1.getChargeSourceCode().equals(cfInvoiceHeader.getInvoiceNo())) {
                    chargeCommonService.invoiceNoPhysicsDelete(cfInvoiceDetail1.getChargeId(), cfInvoiceHeader.getInvoiceNo());
                }
                cfInvoiceDetailMapper.deleteById(cfInvoiceDetail1.getInvoiceDetailId());
                cfInvoiceHeaderUpdate.setInvoicelCredit(cfInvoiceHeader.getInvoicelCredit().subtract(cfInvoiceDetail1.getInvoiceCredit()));
                cfInvoiceHeaderUpdate.setAccountName(Strings.EMPTY);
                cfInvoiceHeaderUpdate.setSettleTitle(Strings.EMPTY);
                cfInvoiceHeaderUpdate.setSettleDescription(Strings.EMPTY);
            } else {
                if (dealWay == 0) {
                    chargeCommonService.updateByInvoice(cfInvoiceDetail1.getChargeId(), "", null);
                }
            }
        });
        if (InvoiceHeaderEnum.SETTLE_TEMPLATE_IN_RED.getCode().equals(cfInvoiceHeader.getSettleTemplate()) ||
                (SETTLE_TEMPLATE_OUT_RED.getCode().equals(cfInvoiceHeader.getSettleTemplate()))) {
           cfInvoiceHeaderMapper.updateById(cfInvoiceHeaderUpdate);
        }
        pageInfoUtil.stopProcess(cfInvoiceHeader.getFlowId());
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
    protected boolean judgeStatus(Integer oldInvoiceStatus, Integer invoiceStatus, Integer customerInvoiceWay, JudgeStateExt judgeStateExt) {
        super.judgeStatus(oldInvoiceStatus, invoiceStatus, customerInvoiceWay);
        Integer newStatus;
        switch (InvoiceHeaderEnum.valueOfCustomerInvoiceStatus(customerInvoiceWay)) {
            case CUSTOMER_INVOICE_WAY_TAX_INVOICE:
                newStatus = getInvoiceStatusEnumFromList(oldInvoiceStatus, invoiceStatussTaxInvoice, invoiceStatus);
                break;
            case CUSTOMER_INVOICE_WAY_TAX_INVOICE_NONE:
                newStatus = getInvoiceStatusEnumFromList(oldInvoiceStatus, invoiceStatussTaxInvoiceNone, invoiceStatus);
                break;
            case CUSTOMER_INVOICE_WAY_TAX_INVOICE_LATER:
                newStatus = getInvoiceStatusEnumFromList(oldInvoiceStatus, invoiceStatussTaxInvoiceLater, invoiceStatus);
                break;
            default:
                throw FinanceBizState.STATE_ERROR;
        }

        boolean result = newStatus.equals(invoiceStatus);
        if (Objects.nonNull(judgeStateExt)) {
            result = result || judgeStateExt.apply(oldInvoiceStatus, invoiceStatus);
        }
        return result;
    }

}
