package com.chenfan.finance.service;

import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.model.CfInvoiceSettlement;
import com.chenfan.finance.model.bo.CfInvoiceSettlementBO;
import com.chenfan.finance.model.dto.InvoiceSettlementPercentDTO;
import com.chenfan.finance.model.vo.CfInvoiceHeaderDetailVO;
import com.chenfan.finance.model.vo.CfInvoiceHeaderPrintVo;
import com.chenfan.finance.model.vo.CfInvoiceSettlementInfoVo;
import com.chenfan.finance.model.vo.CfInvoiceSettlementVo;
import com.chenfan.finance.model.vo.ChargeInVO;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 账单结算表 服务类
 * </p>
 *
 * @author lizhejin
 * @since 2020-10-20
 */
public interface CfInvoiceSettlementService {

    /**
     * addSettlement
     * @param cfInvoiceSettlement
     * @param userVo
     * @return
     */
    int addSettlement(CfInvoiceSettlement cfInvoiceSettlement, UserVO userVo);

    /**
     * auditSettlement
     * @param modes
     * @param userVO
     */
    void auditSettlement(List<CfInvoiceSettlementBO> modes, UserVO userVO);

    /**
     * paymentReqPrint
     * @param invoiceSettlementId
     * @param userVo
     * @return
     */
    CfInvoiceHeaderPrintVo paymentReqPrint(Long invoiceSettlementId, UserVO userVo);

    /**
     * 打印结算单数据
     * @param invoiceSettlementId
     * @return
     */
    CfInvoiceHeaderDetailVO print(Long invoiceSettlementId);

    /**
     * updateSettle
     * @param cfInvoiceSettlement
     * @param userVO
     * @return
     */
    int updateSettle(CfInvoiceSettlement cfInvoiceSettlement, UserVO userVO);

    /**
     * getSettlementAmount
     * @param cfInvoiceSettlement
     * @return
     */
    Map<String, BigDecimal> getSettlementAmount(CfInvoiceSettlement cfInvoiceSettlement);

    /**
     * 账单结算明细导出
     * @param invoiceSettlementPercentDTO
     * @param userVO
     * @param response
     * @return
     */
    Response<Object> invoiceDetailSettlementExport(InvoiceSettlementPercentDTO invoiceSettlementPercentDTO, UserVO userVO, HttpServletResponse response);

    /**
     * updateInvoiceAndSettlementStatus
     * @param settlementId
     * @param invoiceId
     * @param userVO
     */
    void updateInvoiceAndSettlementStatus(Long settlementId, Long invoiceId, UserVO userVO);

    /**
     * getTotalSumOtherMoney
     * @param chargeInVO
     * @param one
     * @return
     */
    BigDecimal getTotalSumOtherMoney(ChargeInVO chargeInVO, BigDecimal one);

    /**
     * checkFirstSettlement
     * @param invoiceId
     * @param settlementId
     * @return
     */
    boolean checkFirstSettlement(Long invoiceId, Long settlementId);

    /**
     * 获取当前结算单的结算数据详情列表（单个结算单结算的sku的数据）
     * @param invoiceSettlementId
     * @return
     */
    CfInvoiceSettlementInfoVo getInvoiceSettlementList( Long invoiceSettlementId);

    /**
     * 修改打印付款申请状态
     * @param cfInvoiceSettlement
     * @return
     */
    void updateSettleStatus(CfInvoiceSettlement cfInvoiceSettlement);

}
