package com.chenfan.finance.service;

import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.exception.FinanceException;
import com.chenfan.finance.model.dto.CfChargeListQuery;
import com.chenfan.finance.model.dto.CfChargeSaveQuery;
import com.chenfan.finance.model.vo.CfChargeDetailVO;
import com.chenfan.finance.model.vo.CfChargeListVO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 费收_费用(cf_charge） 服务类
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
public interface CfChargeService {
    /**
     * 查询费用列表
     *
     * @param query
     * @return
     */
    PageInfo<CfChargeListVO> queryChargeList(CfChargeListQuery query);

    /**
     * create charge
     *
     * @param saveQuery saveQuery
     * @param userVO    userVO
     * @return long
     */
    long saveCharge(CfChargeSaveQuery saveQuery, UserVO userVO);

    /**
     * saveQuery
     *
     * @param saveQuery saveQuery
     * @param userVO    userVO
     */
    void updateCharge(CfChargeSaveQuery saveQuery, UserVO userVO);

    /**
     * chargeId
     *
     * @param chargeId chargeId
     * @return CfChargeDetailVO
     */
    CfChargeDetailVO detailCharge(Long chargeId);

    /**
     * userVO
     *
     * @param chargeId chargeId
     * @param status   status
     * @param userVO   userVO
     */
    void updateChargeCheckStatus(Long chargeId, int status, UserVO userVO);

    /**
     * chargeIds
     *
     * @param chargeIds chargeIds
     * @param status    status
     * @param userVO    userVO
     */
    void updateChargeListCheckStatus(Set<Long> chargeIds, int status, UserVO userVO);

    /**
     * getVendorList
     * @param vendors
     * @return
     */
    Map<String, String> getVendorList(List<String> vendors);

    /**
     * 获取供应商全面
     *
     * @param vendors
     * @return
     */
    Map<String, String> getVendorFullNameList(String... vendors);

    /**
     * getDicts
     * @param businessType
     * @param objects
     * @return
     */
    Map<String, String> getDicts(String businessType, Collection<?> objects);

    /**
     * chargeIds
     *
     * @param chargeIds chargeIds
     * @param invoiceNo invoiceNo
     * @return int
     * @throws FinanceException FinanceException
     */
    int delRelevance(String invoiceNo, List<Long> chargeIds) throws FinanceException;


    /**
     * 查询费用列表（以spu为维度展示） 费用来源按钮专用
     *
     * @param query
     * @return
     */
    PageInfo<CfChargeListVO> chargeListGroupByChargeSource(CfChargeListQuery query);

    /**
     * verifyChargeList
     * @param query
     * @param response
     * @return
     * @throws Exception
     */
    Object verifyChargeList(CfChargeListQuery query, HttpServletResponse response) throws Exception;

    /**
     * updateCheckStatus
     * @param chargeIds
     * @param status
     * @param userVO
     */
    void updateCheckStatus(Set<Long> chargeIds, Integer status, UserVO userVO);

    /**
     * 批量获取对应brandId对应的品牌名称
     * @param brandIds
     * @return
     */
   Map<String, String> setBrandNames(List<Integer> brandIds);
}
