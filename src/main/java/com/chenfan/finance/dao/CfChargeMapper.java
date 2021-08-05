package com.chenfan.finance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.CfCharge;
import com.chenfan.finance.model.CfInvoiceDetail;
import com.chenfan.finance.model.CfInvoiceHeader;
import com.chenfan.finance.model.dto.CfChargeListQuery;
import com.chenfan.finance.model.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 费收_费用(cf_charge） Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Repository
public interface CfChargeMapper extends BaseMapper<CfCharge> {

    /**
     * query
     *
     * @param query query
     * @return List<CfCharge>
     */
    List<CfCharge> queryChargeList(CfChargeListQuery query);

    /**
     * 查询可生成账单的费用
     *
     * @param chargeIds chargeIds
     * @param no        no
     * @return List<CfChargeVO>
     */
    List<CfChargeVO> queryChargeListByChargeIds(@Param("chargeIds") List<Long> chargeIds, @Param("no") String no);

    /**
     * invoiceId
     *
     * @param invoiceId invoiceId
     * @return List<ChargeInvoiceDetailVO>
     */
    List<ChargeInvoiceDetailVO> queryChargeListByInvoiceId(Long invoiceId);

    /**
     * invoiceDetail
     *
     * @param invoiceDetail invoiceDetail
     * @return int
     */
    int updateInvoiceNo(CfInvoiceDetail invoiceDetail);

    /**
     * invoiceHeader
     *
     * @param invoiceHeader invoiceHeader
     * @param chargeIds     chargeIds
     * @return int
     */
    int updateInvoiceInfo(@Param("invoiceHeader") CfInvoiceHeader invoiceHeader, @Param("chargeIds") List<Long> chargeIds);

    /**
     * chargeIds
     *
     * @param chargeIds chargeIds
     */
    void updateInvoiceEntranceDateByChargeIds(List<Long> chargeIds);

    /**
     * invoiceNo
     *
     * @param invoiceNo invoiceNo
     * @param chargeIds chargeIds
     * @return int
     */
    int delRelevance(@Param("invoiceNo") String invoiceNo, @Param("chargeIds") List<Long> chargeIds);

    /**
     * chargeIds
     *
     * @param chargeIds chargeIds
     * @return List<String>
     */
    List<String> selectRelevance(@Param("chargeIds") List<Long> chargeIds);

    /**
     * chargeIds
     *
     * @param chargeIds chargeIds
     * @return List<ChargeInQuantityMatchVO>
     */
    List<ChargeInQuantityMatchVO> getAllReferenceQuantity(@Param("chargeIds") List<Long> chargeIds);

    /**
     * delayChargeIds
     *
     * @param delayChargeIds delayChargeIds
     * @param money          money
     */
    void updateEpricePpByChargeIds(List<Long> delayChargeIds, BigDecimal money);

    /**
     * chargeIds
     *
     * @param chargeIds chargeIds
     * @return List<Long>
     */
    List<Long> getAllRefPoIds(@Param("chargeIds") List<Long> chargeIds);

    /**
     * getChargeSkuList
     * @param salesType
     * @param productCode
     * @param chargeSourceCode
     * @param chargeType
     * @param query
     * @return
     */
    List<CfChargeSkuListVO> getChargeSkuList(@Param("salesType") Integer salesType, @Param("productCode") String productCode, @Param("chargeSourceCode") String chargeSourceCode, @Param("chargeType") String chargeType, @Param("query") CfChargeListQuery query);

    /**
     * 费用来源按钮专用查询列表
     *
     * @param query
     * @return
     */
    List<CfChargeListVO> chargeListChargeSourceGroup(CfChargeListQuery query);

    /**
     * 同一spu、同一来源单号、同一费用种类的sku
     *
     * @param invoiceId
     * @param productCode
     * @param chargeType
     * @param chargeSourceCode
     * @return
     */
    List<ChargeInvoiceSkuDetailVO> queryChargeSkuListByInvoiceId(Long invoiceId, String productCode, String chargeType, String chargeSourceCode);

    /**
     * selectByChargeId
     * @param chargeId
     * @return
     */
    CfCharge selectByChargeId(Long chargeId);

    /**
     * queryChargeSkuListByChargeIds
     * @param chargeIds
     * @param no
     * @param productCode
     * @param chargeSourceCode
     * @param chargeType
     * @return
     */
    List<CfChargeSkuVO> queryChargeSkuListByChargeIds(List<Long> chargeIds, String no, String productCode, String chargeSourceCode, String chargeType);

    /**
     * selectByChargeIds
     * @param chargeIds
     * @return
     */
    List<CfCharge> selectByChargeIds(List<Long> chargeIds);

    /**
     * 查询ID
     * @param query
     * @return
     */
    List<Long> selectRdRecodeDetailIdsByCharge(CfChargeListQuery query);
    /**
     * verifyChargeListPre
     * @param query
     * @return
     */
    List<CfChargeListVO> verifyChargeListPre(CfChargeListQuery query);

    /**
     * 获取销售类型根据账单号
     * @param invoiceNo
     * @return
     */
    Integer getSaleTypeWithInvoiceNo(String invoiceNo);

    /**
     * 逻辑删除费用
     * @param invoiceNo
     *  @param  isOffset '是否是抵消的费用  0否 /1是'
     * @return
     */
    int  deleteOfLogic(@Param("invoiceNo") String invoiceNo,@Param("isOffset") Integer isOffset);


    /**
     * 根据已选的费用，去获取它的采购类型（成衣或者辅料）
     * @param chargeIds
     * @return
     */
    List<Integer> checkRdTypeByChargeId(@Param("chargeIds") List<Long> chargeIds);

    /**
     * 查询当前账单的税率
     * @param invoiceNo
     * @return
     */
    BigDecimal selectTaxRateOfInvoiceNo(String invoiceNo);


    /**
     * 检查不一致得费用
     * @return
     */
    List<String> selectDiffChargeInAndCharge();

    /**
     * 检查当前费用下的charge_in 是否存在
     * @param chargeIds
     * @return
     */
    List<Long> checkChargeAndChargeInIsHaving(List<Long> chargeIds);

}
