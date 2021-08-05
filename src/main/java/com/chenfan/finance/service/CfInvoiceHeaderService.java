package com.chenfan.finance.service;

import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.model.CfClearHeader;
import com.chenfan.finance.model.CfInvoiceHeader;
import com.chenfan.finance.model.CfInvoiceSettlement;
import com.chenfan.finance.model.CfPoDetail;
import com.chenfan.finance.model.bo.CfInvoiceHeaderAddBO;
import com.chenfan.finance.model.bo.CfInvoiceHeaderBO;
import com.chenfan.finance.model.bo.InvoiceSwitchBanBO;
import com.chenfan.finance.model.dto.*;
import com.chenfan.finance.model.dto.ApprovalFlowDTO;
import com.chenfan.finance.model.dto.CfInvoiceHeaderListDTO;
import com.chenfan.finance.model.dto.InvoiceHeaderAddDTO;
import com.chenfan.finance.model.vo.CfInvoiceHeaderDetailVO;
import com.chenfan.finance.model.vo.CfInvoiceHeaderListVO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
public interface CfInvoiceHeaderService{

	/**
	 * 账单保存
	 *
	 * @param invoiceHeaderAddBO invoiceHeaderAddBO
	 * @param userVO userVO
	 * @return Response<Object>
	 */
	Response<Object> save(CfInvoiceHeaderAddBO invoiceHeaderAddBO, UserVO userVO);

	/**
	 * goAdd
	 * @param chargeIds chargeIds
	 * @param no no
	 * @return Response<Object>
	 */
	Response<CfInvoiceHeaderBO> goAdd(List<Long> chargeIds, String no);

	/**
	 * 直接创建账单
	 * @param invoiceHeaderAddDTO
	 * @param user
	 * @return
	 */
	Response<Object> addInvoices(InvoiceHeaderAddDTO invoiceHeaderAddDTO, UserVO user);

	/**
	 * 账单列表
	 * @param dto dto
	 * @return PageInfo<CfInvoiceHeaderListVO>
	 */
	PageInfo<CfInvoiceHeaderListVO> invoiceHeaderList(CfInvoiceHeaderListDTO dto);

	/**
	 * 账单列表（红字抵充待选列表）
	 * @param dto
	 * @return
	 */
	PageInfo<CfInvoiceHeaderListVO> invoiceHeaderListOfAssociated(CfInvoiceHeaderListOfAssociatedDTO dto);

	/**
	 * 批量账单审核/驳回
	 * @param modes modes
	 * @param userVO userVO
	 */
	void auditInvoices(List<InvoiceSwitchBanBO> modes, UserVO userVO);

	/**
	 * 账单详情
	 * @param invoiceId invoiceId
	 * @return CfInvoiceHeaderDetailVO
	 */
	CfInvoiceHeaderDetailVO detail(Long invoiceId,Boolean isMvc);


	/**
	 * 账单编辑
	 *
	 * @param invoiceHeaderAddBO invoiceHeaderAddBO
	 * @param userVO userVO
	 * @return Response<Object>
	 */
	Response<Object> update(CfInvoiceHeaderAddBO invoiceHeaderAddBO, UserVO userVO);

	/**
	 * 填写发票信息
	 *
	 * @param settlement
	 * @param userVO userVO
	 * @return Response<Object>
	 */
	Response<Object> updateInvoiceInfo(CfInvoiceSettlement settlement, UserVO userVO);

	/**
	 * 修改发票信息
	 * @param settlement
	 * @param userVO
	 * @return
	 */
	Response<Object>  editInvoiceInfo(CfInvoiceSettlement settlement, UserVO userVO);

	/**
	 * 发票校验
	 * @param invoiceId
	 * @param settlementId
	 * @param userVO
	 */
	void invoiceVerify(Long invoiceId,Long settlementId, UserVO userVO);

	/**
	 * 登记人
	 * @return Response<Object>
	 */
	Response<Object> createNameList();

	/**
	 * createBankAndCash
	 * @param invoiceId
	 * @param settlementId
	 * @param userVO
	 * @return
	 */
	Response<Object> createBankAndCash(Long invoiceId, Long settlementId, UserVO userVO);

	/**
	 * 新增结算前检查
	 * @param invoiceId 账单id
	 * @param amount 本次结算金额
	 * @return true：可以新增结算，false：不可新增结算
	 */
	boolean settlementCheck(Long invoiceId, BigDecimal amount);

	/**
	 * 更新账单结算状态
	 * @param invoiceId
	 * @param userVO
	 */
	 void updateInvoiceStatus(Long invoiceId, UserVO userVO,CfInvoiceSettlement cfInvoiceSettlement);

	/**
	 * updateInvoiceDelayMoney
	 * @param cfInvoiceUpdateMoneyDto
	 * @param userVO
	 */
	Response<Object> updateInvoiceDelayMoney(CfInvoiceUpdateMoneyDto cfInvoiceUpdateMoneyDto, UserVO userVO);


	/**
	 * 账单列表导出
	 * @param dto
	 * @param userVO
	 * @param response
	 * @return
	 */
	 Response<Object> invoiceExport(CfInvoiceHeaderListDTO dto,UserVO userVO, HttpServletResponse response);

	/**
	 * getPrePayMoneyByInvoiceNo
	 * @param invoiceNo
	 * @param sb
	 * @return
	 */
	BigDecimal getPrePayMoneyByInvoiceNo(String invoiceNo, StringBuilder sb);

	/**
	 * getAllPoDetails
	 * @param invoiceNo
	 * @return
	 */
    List<CfPoDetail> getAllPoDetails(String invoiceNo);

	/**
	 * splitInvoice
	 * @param invoiceNo
	 * @return
	 */
	Response<Object> splitInvoice(String invoiceNo);

	/**
	 * fixAdjustInvoice
	 * @param invoiceNo
	 * @return
	 */
	Response<Object> fixAdjustInvoice(String invoiceNo);

	/**
	 * u8fix
	 * @param no
	 */
	void u8fix(String no);

	/**
	 * 对账单进行强制驳回（账单状态在待提交财务、待结算、部分结算（账单下结算单为待开票）、全部结算（账单下全部结算单为待开票）时显示强制驳回按钮）
	 * @param invoiceIds
	 * @param userVO
	 */
	void forcedToDismissInvoices(List<Long> invoiceIds, UserVO userVO);


	/**
	 * 全新尾款释放
	 * @param invoiceNo
	 * @return
	 */
	Response<Object> balanceRelease(String invoiceNo);

	/**
	 * 获取结算单列表
	 * @param invoiceId
	 * @return
	 */
	List<CfInvoiceSettlement> getAllSettlement(Long invoiceId);


	/**
	 * 计算实付的红字抵充调整后金额
	 * @param calculateRedRealMoneyDto
	 * @return
	 */
	Response<BigDecimal> getRedRealMoney(CalculateRedRealMoneyDto calculateRedRealMoneyDto);

	/**
	 * 回调待办/通知消息
	 *
	 * @param cfClearHeader
	 * @param approvalFlowDTO
	 * @param status
	 */
	void approvalCallback(CfInvoiceHeader cfClearHeader, ApprovalFlowDTO approvalFlowDTO, Boolean status);


}
