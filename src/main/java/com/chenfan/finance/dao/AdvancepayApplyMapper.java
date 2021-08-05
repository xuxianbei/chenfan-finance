package com.chenfan.finance.dao;

import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.model.Advancepay;
import com.chenfan.finance.model.Pay;
import com.chenfan.finance.model.bo.AdvancePayBo;
import com.chenfan.finance.model.dto.AdvancepayApply;
import com.chenfan.finance.model.vo.AdvancePayVo;
import com.chenfan.finance.model.vo.AdvanceVo;
import com.chenfan.finance.model.vo.AdvancepayApplyVo;
import com.chenfan.finance.model.vo.PaymentVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/** @author mbji
 * @date 2018/7/17 15:45
 */
@Repository
public interface AdvancepayApplyMapper {

  /**
   * 删除
   * @param advancePayId
   * @return
   */
  int deleteByPrimaryKey(Integer advancePayId);

  /**
   * 新增
   * @param record
   * @return
   */
  int insert(AdvancepayApply record);

  /**
   * 新增
   * @param record
   * @return
   */
  int insertSelective(AdvancepayApply record);

  /**
   * 根据id查询条数
   * @param poId
   * @return
   */
  int selectCountByPoId(Long poId);

  /**
   * 根据id和type查询条数
   * @param poId
   * @param paymentType
   * @return
   */
  int selectCountByPoIdAndState(
      @Param("poId") Long poId, @Param("paymentType") Integer paymentType);

  /**
   * 修改
   * @param record
   * @return
   */
  int updateByPrimaryKeySelective(AdvancepayApply record);

  /**
   * 修改
   * @param record
   * @return
   */
  int updateByPrimaryKeySelectiveWithenclosure(AdvancepayApply record);

  /**
   * 修改
   * @param record
   * @return
   */
  int updateByPrimaryKey(AdvancepayApply record);

  /**
   * 查询全部
   * @param bo
   * @return
   */
  List<AdvancePayVo> selectAll(AdvancePayBo bo);

  /**
   * 根据id查询
   * @param payId
   * @return
   */
  AdvancePayVo selectByPayId(@Param("payId")Integer payId);

  /**
   * 根据id查询
   * @param payId
   * @param user
   * @return
   */
  AdvancePayVo selectByPayIdAndUser(@Param("payId")Integer payId,@Param("user")UserVO user);
  /**
   * 查询付款合计
   *
   * @param bo
   * @return
   */
  BigDecimal selectTotalCount(AdvancePayBo bo);

  /**
   * 导出
   * @param bo
   * @return
   */
  List<AdvancepayApplyVo> exportExcel(AdvancePayBo bo);

  /**
   * 预付款驳回操作
   *
   * @param advancepayApply 请求参数
   * @return int
   */
  int reject(AdvancepayApply advancepayApply);


  /**
   * 根据id查询
   * @param advancePayId
   * @return
   */
  List<PaymentVo> getProductCode(Integer advancePayId);

  /**
   * 根据id查询支付类型
   * @param poId
   * @return
   */
    List<String> selectPayType(Long poId);

  /**
   * 通过poCode获取
   * @param poCode 采购单code
   * @return AdvanceVo对象
   */
    AdvanceVo selectByPoId(String poCode);

  /**
   * 查询advancePayId是否存在
   * @param paymentType
   * @param poCode
   * @param materialType
   * @return
   */
    Integer findAdvancePayId(String paymentType, String poCode, Integer materialType);

  /**
   * 获取金额
   * @param poId 采购id
   * @return 获取金额
   */
    BigDecimal findMoneyByPoId(Long poId);

  /**
   * 获取金额
   * @param poCode 采购code
   * @return 获取金额
   */
    BigDecimal findMoneyByPoCode(String poCode);

  /**
   * 修改详情
   * @param advancepay 修改对象
   * @return 执行行数
   */
    int updateAdvancepayDetails(Advancepay advancepay);

  /**
   * 查看是否已存在预付款
   * @param poIds
   * @return
   */
  Integer checkIfExists(@Param("poIds") List<Integer> poIds);
}
