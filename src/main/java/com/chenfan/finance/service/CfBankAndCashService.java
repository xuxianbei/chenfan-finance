package com.chenfan.finance.service;

import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.model.CfBankAndCash;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenfan.finance.model.CfBankAndCashInvoiceExtend;
import com.chenfan.finance.model.CfTaxInvoiceHeader;
import com.chenfan.finance.model.dto.ApprovalFlowDTO;
import com.chenfan.finance.model.dto.BankAndCashDto;
import com.chenfan.finance.model.dto.CreateAndClearDto;
import com.chenfan.finance.model.vo.*;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
public interface CfBankAndCashService extends IService<CfBankAndCash> {
    /**
     * 实收付款新增(不会重复)
     * @author zq
     * @date 2020/8/19 14:52
     * @param acVo acVo
     * @param userVO userVO
     * @return void
     */
    Integer create(CfBankAndCash acVo, UserVO userVO);

    /**
     * 编辑
     * @author zq
     * @date 2020/8/19 19:46
     * @param ap ap
     * @param userVO userVO
     * @return int
     */
    int update(CfBankAndCash ap, UserVO userVO);

    /**
     * 删除
     * @author zq
     * @date 2020/8/19 20:22
     * @param acVo acVo
     * @return int
     */
    int delete(CfBankAndCashShowRequestVo acVo);

    /**
     * 列表查询
     * @author zq
     * @date 2020/8/20 1:06
     * @param acVo acVo
     * @return List<CfBankAndCashListShowVo>
     */
    List<CfBankAndCashListShowVo> getList(CfBankAndCashShowRequestVo acVo);

    /**
     * 详情
     * @author zq
     * @date 2020/8/20 1:37
     * @param bankAndCashId bankAndCashId
     * @return com.chenfan.finance.model.vo.ActualPaymentInfoVo
     */
    CfBankAndCashInfoVo info(Long bankAndCashId);

    /**
     * 导出
     * @author zq
     * @date 2020/8/20 9:39
     * @param apsVo apsVo
     * @param userVO userVO
     * @param response response
     * @return void
     */
    void export(CfBankAndCashShowRequestVo apsVo, UserVO userVO, HttpServletResponse response);

    /**
     * 根据收付款单code获取info
     * @author zq
     * @date 2020/8/20 14:56
     * @param recordSeqNo recordSeqNo
     * @return com.chenfan.finance.model.vo.ActualPaymentInfoVo
     */
    CfBankAndCashInfoVo getInfoByCode(String recordSeqNo);

    /**
     * 审核流
     * @author zq
     * @date 2020/8/20 21:33
     * @param cfBankAndCash cfBankAndCash
     * @param user user
     * @return Integer
     */
    Integer review(CfBankAndCash cfBankAndCash,UserVO user);

    /**
     * 获取count
     * @author zq
     * @date 2020/8/24 14:09
     * @param acVo acVo
     * @return int
     */
    int getCount(CfBankAndCashShowRequestVo acVo);

    /**
     * createBankCashAndClear
     * @param apVo
     * @param userVO
     */
    void createBankCashAndClear(CfBankAndCashInvoiceExtend apVo, UserVO userVO);

    Integer createAndClear(CreateAndClearDto createAndClearDto);

    PageInfo<BankAndCashVo> list(BankAndCashDto bankAndCashDto);

    /**
     * 回调待办/通知消息
     *
     * @param cfBankAndCash
     * @param approvalFlowDTO
     * @param status
     */
    void approvalCallback(CfBankAndCash cfBankAndCash, ApprovalFlowDTO approvalFlowDTO, Boolean status);


}
