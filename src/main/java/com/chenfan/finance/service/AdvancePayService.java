package com.chenfan.finance.service;

import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.purchaseexceptions.PurchaseException;
import com.chenfan.finance.model.CfPoHeader;
import com.chenfan.finance.model.bo.AdvancePayBo;
import com.chenfan.finance.model.dto.DownpaymentConfig;
import com.chenfan.finance.model.vo.*;

import java.util.List;

/**
 * @author mbji
 */
public interface AdvancePayService {

    /**
     * 获取采购单号
     * @return
     */
    List<CfPoHeader> selectAllCode();

    /**
     * 预付款申请
     *
     * @param bo 参数对象
     * @param user 用户信息
     * @throws PurchaseException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    void advancePayApply(AdvancePayBo bo,UserVO user) throws PurchaseException, InstantiationException, IllegalAccessException;

    /**
     * 修改付款申请
     *
     * @param bo 参数对象
     * @param user 用户信息
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws PurchaseException
     */
    void updateAdvancePay(AdvancePayBo bo, UserVO user) throws InstantiationException, IllegalAccessException, PurchaseException;

    /**
     * 预付款申请单列表
     *
     * @param bo 参数对象
     * @return AdvancePayListVo 对象
     */
    AdvancePayListVo advancePayList(AdvancePayBo bo);

    /**
     * 查询详情
     *
     * @param payId payId
     * @param user user
     * @return AdvancePayVo 对象
     */
    AdvancePayVo advancePayInfo(Integer payId, UserVO user);

    /**
     * 审核喝关闭操作
     *
     * @param bo bo
     * @param user user
     * @throws PurchaseException
     */
    void auditOrClose(AdvancePayBo bo,UserVO user) throws PurchaseException;

    /**
     * 查询定金配置
     * @return
     */
    List<DownpaymentConfig> paymentConfigList();

    /**
     * 导出
     * @param bo
     * @return
     */
    List<AdvancepayApplyVo> exportExcel(AdvancePayBo bo);

    /**
     * 根据poId获取预付款申请单新建信息
     * @param poCode 采购单code
     * @param user 用户
     * @return AdvanceVo
     */
    AdvanceVo createList(String poCode,UserVO user);


    /**
     * 从采购那边的数据来生成新预付款
     * @param vo
     * @throws PurchaseException
     */
    void createFromPurchase(AdvancePayToNewFinanceVO vo) throws PurchaseException;

    /**
     * 根据采购单code获取生成到新预付款的定金配置id
     * @param poCode
     * @return
     */
    Integer getAdvanceByPoCode(String poCode);

    /**
     * 查看是否已经生成了预付款
     * @param bo
     * @return
     */
    Integer checkIfExistAdvance(AdvancePayBo bo);

    /**
     * 查看有没有采购单已经定金百分百的数据
     * @param poId
     * @param firstOrLastPay
     * @return
     */
    Integer checkIfExistAdvanceByPoId(Integer poId,Integer firstOrLastPay);

    /**
     * 创建实收付
     * @param advancePayVo
     * @param userVo
     */
   void createBankAndCash(AdvancePayVo advancePayVo, UserVO userVo) ;
}
