package com.chenfan.finance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenfan.finance.model.CfInvoiceHeader;
import com.chenfan.finance.model.dto.CfInvoiceHeaderListDTO;
import com.chenfan.finance.model.dto.CfInvoiceHeaderListOfAssociatedDTO;
import com.chenfan.finance.model.vo.CfInvoiceHeaderListVO;
import com.chenfan.finance.model.vo.PriceDetailInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Repository
public interface CfInvoiceHeaderMapper extends BaseMapper<CfInvoiceHeader> {


	/**
	 * 查找蓝字账单关联的红字账单详情
	 * @param associatedInvoiceNo 蓝字账单编号
	 * @return
	 */
	List<CfInvoiceHeaderListVO> selectRedListByAssociatedNo(String associatedInvoiceNo);
	/**
	 * 账单列表（红字抵充待选列表）
	 * @param dto
	 * @return
	 */
	List<CfInvoiceHeaderListVO> invoiceHeaderListOfAssociated(CfInvoiceHeaderListOfAssociatedDTO dto);

	/**
	 * 账单列表
	 * @param reqModel
	 * @return
	 */
	List<CfInvoiceHeaderListVO> invoiceHeaderList(CfInvoiceHeaderListDTO reqModel);

	/**
	 * 登记人接口
	 * @return
	 */
	List<CfInvoiceHeader> createNameList();

	/**
	 * 根据账单id查询到来源单号
	 * @param invoiceId
	 * @return
	 */
	List<String> getSourceCodesById(Long invoiceId);

	/**
	 * 根据单号获取成本价等详情
	 * @author zq
	 * @date 2020/9/15 10:44
	 * @param codes
	 * @return java.util.List<com.chenfan.finance.model.vo.PriceDetailInfo>
	 */
	List<PriceDetailInfo> getPriceDetailInfos(@Param("codes") List<String> codes);

	/**
	 * getPoType
	 * @param invoiceId
	 * @return
	 */
	List<Integer> getPoType(Long invoiceId);

	/**
	 * 初始化账单的对冲值
	 * @param invoiceId
	 * @return
	 */
	int updateAdjustById(Long invoiceId);

	/**
	 * 根据抵充和将要抵充的查询相关数据
	 * @param invoiceNo
	 * @param invoiceIdsOfAssociated
	 * @return
	 */
	List<CfInvoiceHeader> selectByAssociated(String invoiceNo,List<Long> invoiceIdsOfAssociated );

	/**
	 * 解除红字账单已经关联的蓝字相关信息
	 * @param associatedInvoiceNo
	 * @param associatedInvoiceSettlementNo
	 * @return
	 */
	int unAssociated(@Param("associatedInvoiceNo") String associatedInvoiceNo,@Param("associatedInvoiceSettlementNo") String  associatedInvoiceSettlementNo );

	/**
	 * 绑定结算单到红字
	 * @param invoiceNo 蓝字账单id
	 * @param associatedInvoiceSettlementNo 蓝字结算单号
	 * @return
	 */
	int associated(@Param("invoiceNo") String invoiceNo,@Param("associatedInvoiceSettlementNo") String  associatedInvoiceSettlementNo);
}
