package com.chenfan.finance.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.common.config.BillNoConstantClassField;
import com.chenfan.common.utils.DateTimeUtils;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.exception.FinanceException;
import com.chenfan.finance.constant.CfFinanceConstant;
import com.chenfan.finance.dao.RuleBillingDetailMapper;
import com.chenfan.finance.dao.RuleBillingHeaderMapper;
import com.chenfan.finance.enums.StateEnum;
import com.chenfan.finance.model.RuleBillingDetail;
import com.chenfan.finance.model.RuleBillingHeader;
import com.chenfan.finance.model.bo.RuleBillingHeaderBo;
import com.chenfan.finance.model.bo.RuleBillingHeaderListBo;
import com.chenfan.finance.model.vo.RuleBillingHeaderListVo;
import com.chenfan.finance.model.vo.RuleBillingHeaderVo;
import com.chenfan.finance.server.BaseRemoteServer;
import com.chenfan.finance.service.RuleBillingHeaderService;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;


/**
 * @author lqliu
 * @date 2020/8/19 15:00
 */
@Service
public class RuleBillingHeaderServiceImpl implements RuleBillingHeaderService {
    private static  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static Logger LOGGER = LoggerFactory.getLogger(RuleBillingHeaderServiceImpl.class);
    @Autowired
    private RuleBillingHeaderMapper ruleBillingHeaderMapper;

    @Autowired
    private RuleBillingDetailMapper ruleBillingDetailMapper;

    @Autowired
    private PageInfoUtil pageInfoUtil;

    @Autowired
    private BaseRemoteServer baseRemoteServer;
  /**
   * 计费方案列表
   * @author llq
   * @date 2020/8/20 14:12
   * @param ruleBillingHeaderBo
   * @return java.util.List<com.chenfan.finance.model.RuleBillingHeader>
   */
    @Override
    public PageInfo<RuleBillingHeaderListVo> list(RuleBillingHeaderListBo ruleBillingHeaderBo){
        PageHelper.startPage(ruleBillingHeaderBo.getPageNum(),ruleBillingHeaderBo.getPageSize());
        List<RuleBillingHeaderListVo> ruleBillingHeaders = ruleBillingHeaderMapper.selectAll(ruleBillingHeaderBo);
        return new PageInfo<>(ruleBillingHeaders);
    }

    /**
     * 根据id修改费用状态[1;启用，2;停用]
     * @author llq
     * @date 2020/8/20 14:11
     * @param state, ruleBillingId
     * @return void
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateState(Integer state,Long ruleBillingId)
            throws FinanceException,ParseException{
        if (state==1){
            RuleBillingHeader ruleBillingHeader1 = ruleBillingHeaderMapper.selectById(ruleBillingId);
            List<RuleBillingHeader> ruleBillingHeader = ruleBillingHeaderMapper.selectByState(ruleBillingHeader1.getBusinessType(),ruleBillingId);
            if (Objects.nonNull(ruleBillingHeader)) {
                for (RuleBillingHeader r : ruleBillingHeader) {
                    long time = sdf.parse(sdf.format(r.getBeginDate())).getTime();
                    long time1 = sdf.parse(sdf.format(r.getEndDate())).getTime();
                    long time2 = sdf.parse(sdf.format(ruleBillingHeader1.getBeginDate())).getTime();
                    long time3 = sdf.parse(sdf.format(ruleBillingHeader1.getEndDate())).getTime();
                    if (time2 >= time && time2 <= time1) {
                        throw new FinanceException("该时间段已存在相同类型的计费方案,不能启用!");
                    }
                    if (time3 >= time && time3 <= time1) {
                        throw new FinanceException("该时间段已存在相同类型的计费方案,不能启用!");
                    }
                    if (time2 <= time && time <= time3) {
                        throw new FinanceException("该时间段已存在相同类型的计费方案,不能启用!");
                    }
                    if (time2 <= time1 && time1 <= time3) {
                        throw new FinanceException("该时间段已存在相同类型的计费方案,不能启用!");

                    }
                }
            }
        }
        return ruleBillingHeaderMapper.updateState(state,ruleBillingId);
    }

    /**
     * 计费方案详情
     * @author llq
     * @date 2020/8/20 14:12
     * @param ruleBillingId 计费方案id
     * @return com.chenfan.finance.model.vo.RuleBillingHeaderVo
     */
    @Override
    public RuleBillingHeaderVo detail(Long ruleBillingId){
        RuleBillingHeaderVo ruleBillingHeaderVo = new RuleBillingHeaderVo();
        RuleBillingHeader ruleBillingHeader = ruleBillingHeaderMapper.selectByRuleBillingId(ruleBillingId);
        ruleBillingHeaderVo.setBeginDate(DateTimeUtils.toDateTime(ruleBillingHeader.getBeginDate()));
        ruleBillingHeaderVo.setEndDate(DateTimeUtils.toDateTime(ruleBillingHeader.getEndDate()));
        BeanUtils.copyProperties(ruleBillingHeader,ruleBillingHeaderVo);
        List<RuleBillingDetail> ruleBillingDetails = ruleBillingDetailMapper.selectByPrimaryKey(ruleBillingId,StateEnum.DELAY.getMsg());
        ruleBillingHeaderVo.setDelayList(ruleBillingDetails);
        List<RuleBillingDetail> ruleBillingDetails1 = ruleBillingDetailMapper.selectByPrimaryKey(ruleBillingId,StateEnum.QC.getMsg());
        ruleBillingHeaderVo.setQcList(ruleBillingDetails1);
        return  ruleBillingHeaderVo;
    }

    /**
     * 计费方案新增
     * @author llq
     * @date 2020/8/20 14:14
     * @param ruleBillingHeaderBo, userVO
     * @return int
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addRule(RuleBillingHeaderBo ruleBillingHeaderBo, UserVO userVO)
            throws FinanceException,ParseException{
        List<RuleBillingHeader> ruleBillingHeader = ruleBillingHeaderMapper.selectByState(ruleBillingHeaderBo.getBusinessType(),null);
        if (Objects.nonNull(ruleBillingHeader)) {
            for (RuleBillingHeader r : ruleBillingHeader) {
                long time = sdf.parse(sdf.format(r.getBeginDate())).getTime();
                long time1 = sdf.parse(sdf.format(r.getEndDate())).getTime();
                long time2 = sdf.parse(sdf.format(ruleBillingHeaderBo.getBeginDate())).getTime();
                long time3 = sdf.parse(sdf.format(ruleBillingHeaderBo.getEndDate())).getTime();
                if (time2 >= time && time2 <= time1) {
                    throw new FinanceException("该时间段已存在相同类型的计费方案,请修改!");
                }
                if (time3 >= time && time3 <= time1) {
                    throw new FinanceException("该时间段已存在相同类型的计费方案,请修改!");
                }
                if (time2 <= time && time <= time3) {
                    throw new FinanceException("该时间段已存在相同类型的计费方案,请修改!");
                }
                if (time2 <= time1 && time1 <= time3) {
                    throw new FinanceException("该时间段已存在相同类型的计费方案,请修改!");

                    }
                }
            }
            ruleBillingHeaderBo.setCreateBy(userVO.getUserId());
            ruleBillingHeaderBo.setCreateDate(new Date());
            ruleBillingHeaderBo.setCreateName(userVO.getRealName());
            ruleBillingHeaderBo.setUpdateDate(new Date());
            ruleBillingHeaderBo.setUpdateBy(userVO.getUserId());
            ruleBillingHeaderBo.setUpdateName(userVO.getRealName());
            ruleBillingHeaderBo.setCompanyId(userVO.getCompanyId());
            ruleBillingHeaderBo.setRuleBillingStatus(StateEnum.CHILD_OPEN.getCode());

            String poCode = pageInfoUtil.generateBusinessNum(com.chenfan.finance.config.BillNoConstantClassField.RULEBILL);
            ruleBillingHeaderBo.setRuleBillingNo(poCode);
            int i = ruleBillingHeaderMapper.insertSelective(ruleBillingHeaderBo);
            List<RuleBillingDetail> detailList = ruleBillingHeaderBo.getDelayList();
            List<RuleBillingDetail> qcList = ruleBillingHeaderBo.getQcList();
            qcList.forEach(x->{detailList.add(x);});
            detailList.forEach(
                            x -> {
                                x.setRuleBillingId(ruleBillingHeaderBo.getRuleBillingId());
                                ruleBillingDetailMapper.insertSelective(x);
                            });
        return  ruleBillingHeaderBo.getRuleBillingId();
    }
    /**
     * 计费方案修改
     * @param ruleBillingHeaderBo
     * @param userVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateRule(RuleBillingHeaderBo ruleBillingHeaderBo, UserVO userVO)
             throws FinanceException,ParseException{
            List<RuleBillingHeader> ruleBillingHeader1 = ruleBillingHeaderMapper.selectByState(ruleBillingHeaderBo.getBusinessType(),ruleBillingHeaderBo.getRuleBillingId());
            if (Objects.nonNull(ruleBillingHeader1)) {
                for (RuleBillingHeader r : ruleBillingHeader1) {
                    long time = sdf.parse(sdf.format(r.getBeginDate())).getTime();
                    long time1 = sdf.parse(sdf.format(r.getEndDate())).getTime();
                    long time2 = sdf.parse(sdf.format(ruleBillingHeaderBo.getBeginDate())).getTime();
                    long time3 = sdf.parse(sdf.format(ruleBillingHeaderBo.getEndDate())).getTime();
                    if (time2 >= time && time2 <= time1) {
                        throw new FinanceException("该时间段已存在相同类型的计费方案,请修改!");
                    }
                    if (time3 >= time && time3 <= time1) {
                        throw new FinanceException("该时间段已存在相同类型的计费方案,请修改!");
                    }
                    if (time2 <= time && time <= time3) {
                        throw new FinanceException("该时间段已存在相同类型的计费方案,请修改!");
                    }
                    if (time2 <= time1 && time1 <= time3) {
                        throw new FinanceException("该时间段已存在相同类型的计费方案,请修改!");

                    }
                }
            }
            RuleBillingHeader ruleBillingHeader = new RuleBillingHeader();
            BeanUtils.copyProperties(ruleBillingHeaderBo, ruleBillingHeader);
            ruleBillingHeader.setUpdateBy(userVO.getUserId());
            ruleBillingHeader.setUpdateName(userVO.getRealName());
            ruleBillingHeader.setUpdateDate(new Date());
            int i = ruleBillingHeaderMapper.updateByPrimaryKeySelective(ruleBillingHeader);
            if (i > 0) {
                List<RuleBillingDetail> detailList = ruleBillingHeaderBo.getDelayList();
                List<RuleBillingDetail> qcList = ruleBillingHeaderBo.getQcList();
                qcList.forEach(x -> {
                    detailList.add(x);
                });
                detailList.forEach(x -> {
                    x.setRuleBillingDetailId(null);
                    x.setRuleBillingId(ruleBillingHeaderBo.getRuleBillingId());
                });
                if (Objects.nonNull(detailList) && detailList.size() > 0) {
                    //删除已经存在的数据
                    int i1 = ruleBillingDetailMapper.updateById(ruleBillingHeaderBo.getRuleBillingId());
                    if (i1 > 0) {
                        //新增修改的数据
                        detailList.forEach(x->{
                            ruleBillingDetailMapper.insertSelective(x);
                        });
                    }
                }
            }
        return ruleBillingHeaderBo.getRuleBillingId();
    }

    @Override
    public List<RuleBillingDetail> queryRuleBilling(String ruleType) {
        List<RuleBillingDetail> ruleBillingDetailList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        List<RuleBillingHeader> ruleBillingHeaders = ruleBillingHeaderMapper.selectList(Wrappers.<RuleBillingHeader>lambdaQuery()
                // 货品采购
                .eq(RuleBillingHeader::getBusinessType, CfFinanceConstant.RULE_BILLING_TYPE_FOR_PO)
                .eq(RuleBillingHeader::getRuleBillingStatus, 1)
                .le(RuleBillingHeader::getBeginDate, now)
                .ge(RuleBillingHeader::getEndDate, now)
        );
        RuleBillingHeader ruleBillingHeader = ruleBillingHeaders.size() == 0 ? null : ruleBillingHeaders.get(0);
        if (ruleBillingHeader != null) {
            // 获取对应计费方案明细数据
            ruleBillingDetailList = ruleBillingDetailMapper.selectList(Wrappers.<RuleBillingDetail>lambdaQuery()
                    .eq(RuleBillingDetail::getRuleBillingId, ruleBillingHeader.getRuleBillingId())
                    .eq(RuleBillingDetail::getRuleType,ruleType)
                    .eq(RuleBillingDetail::getIsDelete, 0)
            );
        }
        return ruleBillingDetailList;
    }
}
