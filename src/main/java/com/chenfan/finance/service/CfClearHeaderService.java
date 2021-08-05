package com.chenfan.finance.service;

import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.model.CfClearHeader;
import com.chenfan.finance.model.dto.ApprovalFlowDTO;
import com.chenfan.finance.model.dto.CfClearAddAndUpdateDto;
import com.chenfan.finance.model.dto.CfClearListDto;
import com.chenfan.finance.model.vo.ClearHeaderDetailVo;
import com.chenfan.finance.model.vo.ClearHeaderVo;
import com.chenfan.finance.model.vo.ClearUserVo;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 财务_核销(cf_clear_header） 服务类
 * </p>
 *
 * @author lywang
 * @since 2020-08-22
 */
public interface CfClearHeaderService {

    /**
     * create
     * @param cfClearAddAndUpdateDto cfClearAddAndUpdateDto
     * @param user user
     * @return String
     */
    String create(CfClearAddAndUpdateDto cfClearAddAndUpdateDto, UserVO user);

    /**
     * cfClearAddAndUpdateDto
     * @param cfClearAddAndUpdateDto cfClearAddAndUpdateDto
     * @param user user
     * @return String
     */
    String update(CfClearAddAndUpdateDto cfClearAddAndUpdateDto, UserVO user);

    /**
     * customList
     * @param cfClearListDto cfClearListDto
     * @return PageInfo<ClearHeaderVo>
     */
    PageInfo<ClearHeaderVo> customList(CfClearListDto cfClearListDto);

    /**
     * detail
     * @param clearNo clearNo
     * @return ClearHeaderDetailVo
     */
    ClearHeaderDetailVo detail(String clearNo);

    /**
     * clearNos
     * @param clearNos clearNos
     * @return Integer
     */
    Integer delete(List<String> clearNos);

    /**
     * clearUsers
     * @return List<ClearUserVo>
     */
    List<ClearUserVo> clearUsers();

    /**
     * export
     * @param cfClearListDto cfClearListDto
     * @param userVO userVO
     * @param response response
     */
    void export(CfClearListDto cfClearListDto, UserVO userVO, HttpServletResponse response);

    /**
     * 回调待办/通知消息
     *
     * @param cfClearHeader
     * @param approvalFlowDTO
     * @param status
     */
    void approvalCallback(CfClearHeader cfClearHeader, ApprovalFlowDTO approvalFlowDTO, Boolean status);

}
