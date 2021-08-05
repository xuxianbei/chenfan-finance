package com.chenfan.finance.service;

import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.exception.FinanceException;
import com.chenfan.finance.model.RuleBillingDetail;
import com.chenfan.finance.model.bo.RuleBillingHeaderBo;
import com.chenfan.finance.model.bo.RuleBillingHeaderListBo;
import com.chenfan.finance.model.vo.RuleBillingHeaderListVo;
import com.chenfan.finance.model.vo.RuleBillingHeaderVo;
import com.github.pagehelper.PageInfo;

import java.text.ParseException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
public interface RuleBillingHeaderService{
        /**
         * 列表
         * @param ruleBillingHeaderBo ruleBillingHeaderBo
         * @return PageInfo<RuleBillingHeaderListVo>
         */
        PageInfo<RuleBillingHeaderListVo> list(RuleBillingHeaderListBo ruleBillingHeaderBo);

        /**
         * 根据id修改状态
         * @param ruleBillingId  ruleBillingId
         * @param state  state
         * @return int
         * @throws  FinanceException FinanceException
         * @throws  ParseException ParseException
         */
        int updateState(Integer state,Long ruleBillingId) throws FinanceException,ParseException;
        /**
         * 根据id查看详情
         * @param ruleBillingId  ruleBillingId
         * @return RuleBillingHeaderVo
         */
        RuleBillingHeaderVo detail(Long ruleBillingId);

        /**
         * 新增
         * @param ruleBillingHeaderBo  ruleBillingHeaderBo
         * @param userVO  userVO
         * @return Long
         * @throws  FinanceException FinanceException
         * @throws  ParseException ParseException
         */
        Long addRule(RuleBillingHeaderBo ruleBillingHeaderBo, UserVO userVO) throws FinanceException, ParseException;

        /**
         * 修改
         * @param user  user
         * @param headerBo  headerBo
         * @return Long
         * @throws  FinanceException FinanceException
         * @throws  ParseException ParseException
         */
        Long updateRule(RuleBillingHeaderBo headerBo, UserVO user)throws FinanceException,ParseException;


        /**
         * 查询当前系统有效的计费方案
         * @param ruleType 扣费类型（1.延期扣款;2.质检扣款）
         * @return
         */
        List<RuleBillingDetail> queryRuleBilling( String ruleType);
}
