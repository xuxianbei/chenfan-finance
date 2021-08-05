package com.chenfan.finance.service;

import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.model.bo.AdvancePayBo;
import com.chenfan.finance.model.bo.CfPoHeaderBo;
import com.chenfan.finance.model.vo.CfPoHeaderListVo;
import com.chenfan.finance.model.vo.PayApplyInfo;

import java.math.BigDecimal;

/**
 * <p>
 *  财务_业务单据_采购单 (cf_po_header)  服务类
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */

public interface CfPoHeaderService {

    /**
     * selectPurchaseOrder
     * @param bo bo
     * @param user user
     * @return CfPoHeaderListVo
     */
    CfPoHeaderListVo selectPurchaseOrder(CfPoHeaderBo bo, UserVO user);

    /**
     * getAmountOfMoney
     * @param poId poId
     * @param paymentConfId paymentConfId
     * @param payValue payValue
     * @param payType payType
     * @param firstOrLastPay firstOrLastPay
     * @return PayApplyInfo
     */
    PayApplyInfo getAmountOfMoney(Long poId, Integer paymentConfId,BigDecimal payValue,String payType,Integer firstOrLastPay);

    /**
     * queryAdvance
     * @param poCode poCode
     * @return BigDecimal
     */
    BigDecimal queryAdvance(String poCode);
}
