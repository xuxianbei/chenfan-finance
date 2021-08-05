package com.chenfan.finance.dao;

import com.chenfan.finance.model.CfPoHeader;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.bo.CfPoHeaderBo;
import com.chenfan.finance.model.dto.SPoHeader;
import com.chenfan.finance.model.vo.CfPoHeaderVo;
import org.springframework.stereotype.Repository;
import com.chenfan.finance.model.dto.PoHeader;
import com.chenfan.finance.model.vo.PayApplyInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 财务_业务单据_采购单 (cf_po_header)  Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Repository
public interface CfPoHeaderMapper extends BaseMapper<CfPoHeader> {

    /**
     * selectAllCode
     *
     * @return List<CfPoHeader>
     */
    List<CfPoHeader> selectAllCode();

    /**
     * 根据poId查询
     *
     * @param poId poId
     * @return CfPoHeader
     */
    CfPoHeader selectByPrimaryKey(Long poId);

    /**
     * 修改采购单
     *
     * @param record record
     * @return int
     */
    int updateByPrimaryKeySelective(CfPoHeader record);

    /**
     * 修改定金
     *
     * @param poId     poId
     * @param payValue payValue
     * @param type     type
     * @return int
     */
    int updateHirePurchase(@Param("poId") Long poId, @Param("payValue") BigDecimal payValue, @Param("type") int type);

    /**
     * 根据采购单号查询含税金额总计、供应商、存货编码、采购类型
     *
     * @param poId poId
     * @return PayApplyInfo
     */
    PayApplyInfo selectPayInfo(Long poId);

    /**
     * 根据采购单号修改采购单定金结算状态
     *
     * @param poIds    poIds
     * @param hsStatus hsStatus
     */
    void updateHsStatusByPoIds(@Param("poIds") List<Long> poIds, @Param("hsStatus") Integer hsStatus);

    /**
     * 选择采购单号
     *
     * @param bo bo
     * @return List<CfPoHeaderVo>
     */
    List<CfPoHeaderVo> selectPurchaseOrder(CfPoHeaderBo bo);


    /**
     * selectListToInitPay
     *
     * @param poCode poCode
     * @return List<CfPoHeaderVo>
     */
    List<CfPoHeaderVo> selectListToInitPay(String poCode);

    /**
     * 根据chargeId 查询到采购单号
     * @param invoiceId
     * @return
     */
    List<SPoHeader> selectPoCodeByChargeId(Long invoiceId);

    /**
     * 查询当前采购单是否同步到财务
     * @param startTime
     * @param endTime
     * @param brandIds
     * @return
     */
    List<String> selectPoInfoDifferences(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("brandIds") List<Integer> brandIds);


    /**
     * 查询当前财务采购单与业务采购单得差异性
     * @param startTime
     * @param endTime
     * @param brandIds
     * @return
     */
    List<HashMap<String,Object>> selectPoDetailInfoDifferences(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("brandIds") List<Integer> brandIds);
}
