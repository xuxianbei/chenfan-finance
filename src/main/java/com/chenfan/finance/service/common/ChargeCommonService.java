package com.chenfan.finance.service.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.chenfan.ccp.plug.rpc.service.NotifyRemoteService;
import com.chenfan.ccp.util.start.ApplicationContextUtil;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.config.BillNoConstantClassField;
import com.chenfan.finance.dao.CfChargeCommonMapper;
import com.chenfan.finance.enums.ChargeCheckStatusEnum;
import com.chenfan.finance.enums.ChargeCommonEnum;
import com.chenfan.finance.enums.ChargeEnum;
import com.chenfan.finance.enums.ClearHeaderEnum;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.exception.FinanceBizState;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.CfChargeCommon;
import com.chenfan.finance.model.dto.CfChargeCommonDto;
import com.chenfan.finance.model.dto.CfChargeListQuery;
import com.chenfan.finance.model.dto.ChargeCommonUpdateDto;
import com.chenfan.finance.model.dto.SplitChargeCommonDto;
import com.chenfan.finance.model.vo.CfChargeCommonBrotherVo;
import com.chenfan.finance.model.vo.CfChargeCommonVo;
import com.chenfan.finance.model.vo.McnChargeCommonVO;
import com.chenfan.finance.model.vo.SplitVo;
import com.chenfan.finance.producer.U8Produce;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author: xuxianbei
 * Date: 2021/3/1
 * Time: 9:58
 * Version:V1.0
 */
@Slf4j
@Service
public class ChargeCommonService {

    public static final String BUSINESS_LOCK = "ChargeCommon:";

    @Resource
    private CfChargeCommonMapper cfChargeCommonMapper;


    @Autowired
    private PageInfoUtil pageInfoUtil;

    public CfChargeCommon selectOne(Long chargeId) {
        return cfChargeCommonMapper.selectOne(Wrappers.lambdaQuery(CfChargeCommon.class).eq(CfChargeCommon::getChargeId, chargeId).ne(CfChargeCommon::getCheckStatus, 0).ne(CfChargeCommon::getCheckStatus, 5));
    }

    public CfChargeCommon selectById(Long chargeId) {
        return cfChargeCommonMapper.selectById(chargeId);
    }


    public Integer create(CfChargeCommon cfChargeCommon) {
        cfChargeCommon.setCurrencyCode("RMB");
        cfChargeCommon.setExchangeRate(BigDecimal.ONE);
        cfChargeCommon.setTaxRate(BigDecimal.ZERO);
        cfChargeCommon.setChargeUnit("次");
        cfChargeCommon.setChargeQty(1);
        cfChargeCommon.setChargeCode(pageInfoUtil.generateBusinessNum(BillNoConstantClassField.CRG));
        return cfChargeCommonMapper.insert(cfChargeCommon);
    }


    /**
     * 通过账单/开票 反写发票
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateByInvoiceSetInvoiceNo(Long chargeId, String taxInvoiceNo, LocalDateTime taxInvoiceDate) {
        //暂未找到mybatis框架对时间的条件约束
        CfChargeCommon oldCfChargeCommon = selectOne(chargeId);
        LocalDateTime invoiceDate = oldCfChargeCommon.getInvoiceDate();
        oldCfChargeCommon.setTaxInvoiceNo(taxInvoiceNo);
        oldCfChargeCommon.setTaxInvoiceDate(taxInvoiceDate);
        oldCfChargeCommon.setInvoiceDate(invoiceDate);
        updateSynchronize(oldCfChargeCommon);
    }


    /**
     * 通过账单/开票 反写 开票流水号，账单流水号
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateByInvoice(Long chargeId, String invoiceNo, LocalDateTime invoiceDate, Integer status) {
        CfChargeCommon cfChargeCommonUpdate = cfChargeCommonMapper.selectById(chargeId);
        cfChargeCommonUpdate.setInvoiceNo(invoiceNo);
        cfChargeCommonUpdate.setInvoiceDate(invoiceDate);
        if (StringUtils.isEmpty(invoiceNo)) {
            cfChargeCommonUpdate.setTaxInvoiceNo(invoiceNo);
            cfChargeCommonUpdate.setTaxInvoiceDate(null);
            cfChargeCommonUpdate.setCheckStatus(status);
        }
        updateSynchronize(cfChargeCommonUpdate);
    }

    /**
     * 通过账单/开票 反写 开票流水号，账单流水号
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateByInvoice(Long chargeId, String invoiceNo, LocalDateTime invoiceDate) {
        CfChargeCommon cfChargeCommonUpdate = cfChargeCommonMapper.selectById(chargeId);
        cfChargeCommonUpdate.setChargeId(chargeId);
        cfChargeCommonUpdate.setInvoiceNo(invoiceNo);
        cfChargeCommonUpdate.setInvoiceDate(invoiceDate);
        if (StringUtils.isEmpty(invoiceNo)) {
            cfChargeCommonUpdate.setTaxInvoiceNo(invoiceNo);
            cfChargeCommonUpdate.setTaxInvoiceDate(null);
        }
        updateSynchronize(cfChargeCommonUpdate);
    }

    /**
     * 通过核销反写  不支持多次核销
     *
     * @param chargeId
     * @param clearNo
     * @param actualDate
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateByClear(Long chargeId, String clearNo, LocalDateTime actualDate, BigDecimal overage) {
        CfChargeCommon cfChargeCommonOld = cfChargeCommonMapper.selectById(chargeId);
        Assert.isTrue(Objects.nonNull(cfChargeCommonOld), ModuleBizState.DATE_ERROR.message());
        cfChargeCommonOld.setChargeId(chargeId);
        cfChargeCommonOld.setClearNo(PageInfoUtil.stringAdd(cfChargeCommonOld.getClearNo(), clearNo));
        cfChargeCommonOld.setActualAmount(cfChargeCommonOld.getAmountPp().subtract(overage));
        cfChargeCommonOld.setActualDate(actualDate);
        cfChargeCommonOld.setTaxInvoiceDate(cfChargeCommonOld.getTaxInvoiceDate());
        cfChargeCommonOld.setInvoiceDate(cfChargeCommonOld.getInvoiceDate());
        cfChargeCommonOld.setOverage(overage);
        updateSynchronize(cfChargeCommonOld);
    }

    /**
     * 通过核销驳回反写
     * @param chargeId
     * @param overage
     */
    @Transactional(rollbackFor = Exception.class)
    public void backUpByClear(Long chargeId, BigDecimal overage,String clearNo) {
        CfChargeCommon cfChargeCommonOld = cfChargeCommonMapper.selectById(chargeId);
        Assert.isTrue(Objects.nonNull(cfChargeCommonOld), ModuleBizState.DATE_ERROR.message());
        //驳回后核销号清空
        cfChargeCommonOld.setClearNo(PageInfoUtil.stringSubtract(cfChargeCommonOld.getClearNo(), clearNo));
        cfChargeCommonOld.setActualAmount(cfChargeCommonOld.getActualAmount().subtract(overage));
        //实收付时间清空
        cfChargeCommonOld.setOverage(cfChargeCommonOld.getOverage().add(overage));
        updateSynchronize(cfChargeCommonOld);
    }

    /**
     * 通过核销反写  不支持多次核销
     *
     * @param chargeId
     * @param clearNo
     * @param actualAmount
     * @param actualDate
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateByClear(Long chargeId, String clearNo, BigDecimal actualAmount, LocalDateTime actualDate) {
        CfChargeCommon cfChargeCommonOld = selectOne(chargeId);
        Assert.isTrue(Objects.nonNull(cfChargeCommonOld) && StringUtils.isEmpty(cfChargeCommonOld.getClearNo())
                && cfChargeCommonOld.getAmountPp().compareTo(actualAmount) <= 0, ModuleBizState.DATE_ERROR.message());
        cfChargeCommonOld.setClearNo(PageInfoUtil.stringAdd(cfChargeCommonOld.getClearNo(), clearNo));
        cfChargeCommonOld.setActualAmount(actualAmount);
        cfChargeCommonOld.setActualDate(actualDate);
        cfChargeCommonOld.setOverage(cfChargeCommonOld.getAmountPp().subtract(actualAmount));
        updateSynchronize(cfChargeCommonOld);
    }

    /**
     * 同步更新
     */
    private void updateSynchronize(CfChargeCommon cfChargeCommon) {
        updateSynchronizeBase(cfChargeCommon.getChargeId(), () -> cfChargeCommonMapper.updateById(cfChargeCommon));
    }

    /**
     * 基础同步更新
     */
    private void updateSynchronizeBase(Long chargeId, Supplier supplier) {
        if (pageInfoUtil.tryLock(BUSINESS_LOCK, chargeId)) {
            try {
                supplier.get();
            } finally {
                pageInfoUtil.tryUnLock(BUSINESS_LOCK, chargeId);
            }
        } else {
            throw FinanceBizState.SYSTEM_BUSY;
        }
    }

    /**
     * 根据Ids返回结果
     *
     * @param chargeIds
     * @return
     */
    public List<CfChargeCommon> selectList(List<Long> chargeIds) {
        chargeIds = chargeIds.stream().distinct().collect(Collectors.toList());
        return cfChargeCommonMapper.selectBatchIds(chargeIds);
    }

    /**
     * 根据Ids返回结果
     *
     * @param queryWrapper
     * @return
     */
    public List<CfChargeCommon> selectList(LambdaQueryWrapper<CfChargeCommon> queryWrapper) {
        queryWrapper = queryWrapper.ne(CfChargeCommon::getCheckStatus, ChargeCommonEnum.CHECK_STSTUS_ZERO.getCode());
        return cfChargeCommonMapper.selectList(queryWrapper);
    }


    /**
     * 费用支持多次核销，因此核销号为（多次核销的集合，按逗号分隔）
     * 根据核销号返回结果
     *
     * @param clearNos
     * @return
     */
    @Deprecated
    public List<CfChargeCommon> selectListByClearNos(List<String> clearNos) {
        clearNos = clearNos.stream().distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(clearNos)) {
            return new ArrayList<>();
        }
        return cfChargeCommonMapper.selectList(Wrappers.lambdaQuery(CfChargeCommon.class)
                .in(CfChargeCommon::getClearNo, clearNos));
    }

    /**
     * 此版本暂时无费用状态，前端无法判断当前单据是否可用 所以已作废的单据也不显示
     * @param cfChargeCommonDto
     * @return
     */
    /**
     * 根据核销id返回结果
     *
     * @param clearIds
     * @return
     */
    public List<McnChargeCommonVO> selectListByClearids(List<Long> clearIds) {
        clearIds = clearIds.stream().distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(clearIds)) {
            return new ArrayList<>();
        }
        return cfChargeCommonMapper.getBalanceByClearids(clearIds);
    }

    /**
     * 此版本暂时无费用状态，前端无法判断当前单据是否可用 所以已作废的单据也不显示
     * @param cfChargeCommonDto
     * @return
     */
    public PageInfo<CfChargeCommonVo> list(CfChargeCommonDto cfChargeCommonDto) {
        LambdaQueryWrapper<CfChargeCommon> lambdaQueryWrapper = condition(cfChargeCommonDto);
        PageInfoUtil.startPage(cfChargeCommonDto);
        List<CfChargeCommon> list = cfChargeCommonMapper.selectList(lambdaQueryWrapper);
        return PageInfoUtil.toPageInfo(list, CfChargeCommonVo.class, (old, newItem) ->
                newItem.setClearState(getClearStatus(old.getClearNo(), old.getOverage())));
    }


    /**
     * 核销单号合余额
     *
     * @param clearNo
     * @param overage
     * @return
     */
    private Integer getClearStatus(String clearNo, BigDecimal overage) {
        if (StringUtils.isBlank(clearNo)) {
            //未核销
            return 0;
        } else {
            if (overage == null || BigDecimal.ZERO.compareTo(overage) == 0) {
                return 1;
            } else {
                return 2;
            }
        }
    }


    private LambdaQueryWrapper<CfChargeCommon> condition(CfChargeCommonDto cfChargeCommonDto) {
        LambdaQueryWrapper<CfChargeCommon> queryWrapper = Wrappers.lambdaQuery(CfChargeCommon.class);
        queryWrapper.eq(StringUtils.isNotBlank(cfChargeCommonDto.getBalance()), CfChargeCommon::getBalance, cfChargeCommonDto.getBalance())
                .between(Objects.nonNull(cfChargeCommonDto.getBeginDate()) && Objects.nonNull(cfChargeCommonDto.getEndDate()),
                        CfChargeCommon::getCreateDate, cfChargeCommonDto.getBeginDate(), cfChargeCommonDto.getEndDate())
                .eq(StringUtils.isNotBlank(cfChargeCommonDto.getArapType()), CfChargeCommon::getArapType, cfChargeCommonDto.getArapType())
                .eq(Objects.nonNull(cfChargeCommonDto.getChargeSourceType()), CfChargeCommon::getChargeSourceType, cfChargeCommonDto.getChargeSourceType())
                .eq(Objects.nonNull(cfChargeCommonDto.getChargeType()), CfChargeCommon::getChargeType, cfChargeCommonDto.getChargeType())
                .like(StringUtils.isNotBlank(cfChargeCommonDto.getChargeSourceCode()), CfChargeCommon::getChargeSourceCode, cfChargeCommonDto.getChargeSourceCode())
                .eq(StringUtils.isNotBlank(cfChargeCommonDto.getFinanceEntity()), CfChargeCommon::getFinanceEntity, cfChargeCommonDto.getFinanceEntity())
                .eq(Objects.nonNull(cfChargeCommonDto.getSubmitApplicationState()) && cfChargeCommonDto.getSubmitApplicationState() == 0, CfChargeCommon::getInvoiceNo, "")
                .ne(Objects.nonNull(cfChargeCommonDto.getSubmitApplicationState()) && cfChargeCommonDto.getSubmitApplicationState() == 1, CfChargeCommon::getInvoiceNo, "")
                .like(StringUtils.isNotBlank(cfChargeCommonDto.getChargeCode()), CfChargeCommon::getChargeCode, cfChargeCommonDto.getChargeCode());
        if (Objects.nonNull(cfChargeCommonDto.getClearState())) {
            if (cfChargeCommonDto.getClearState() == 0) {
                queryWrapper.and(queryWrapper1 -> {
                    queryWrapper1.eq(CfChargeCommon::getClearNo, Strings.EMPTY).or();
                    queryWrapper1.isNull(CfChargeCommon::getClearNo);
                });
            } else {
                if (cfChargeCommonDto.getClearState() == 1) {
                    queryWrapper.and(queryWrapper1 -> {
                        queryWrapper1.ne(CfChargeCommon::getClearNo, Strings.EMPTY);
                        queryWrapper1.eq(CfChargeCommon::getOverage, 0);
                    });
                } else {
                    queryWrapper.and(queryWrapper1 -> {
                        queryWrapper1.ne(CfChargeCommon::getClearNo, Strings.EMPTY);
                        queryWrapper1.gt(CfChargeCommon::getOverage, 0);
                    });
                }
            }
        }

        if (Objects.nonNull(cfChargeCommonDto.getSettled())) {
            if (cfChargeCommonDto.getSettled() == 1) {
                queryWrapper.and((t) -> t.ne(CfChargeCommon::getInvoiceNo, "").or((x) -> x.ne(CfChargeCommon::getTaxInvoiceNo, "")));
            } else if (cfChargeCommonDto.getSettled() == 0) {
                queryWrapper.eq(CfChargeCommon::getInvoiceNo, "").eq(CfChargeCommon::getTaxInvoiceNo, "");
            }
        }

        //过滤公司权限
        if (Objects.nonNull(cfChargeCommonDto.getCompanyIds())) {
            queryWrapper.in(CfChargeCommon::getCompanyId, cfChargeCommonDto.getCompanyIds());
        }
        //过滤用户权限
        if (Objects.nonNull(cfChargeCommonDto.getUserIds())) {
            queryWrapper.in(CfChargeCommon::getCreateBy, cfChargeCommonDto.getUserIds());
        }
        //过滤品牌
        if (Objects.nonNull(cfChargeCommonDto.getBrandIds())) {
            queryWrapper.in(CfChargeCommon::getBrandId, cfChargeCommonDto.getBrandIds());
        }

        queryWrapper.ne(CfChargeCommon::getCheckStatus, ChargeCommonEnum.CHECK_STSTUS_ZERO.getCode());
        queryWrapper.ne(CfChargeCommon::getCheckStatus, ChargeCheckStatusEnum.ZF.getCode());
        queryWrapper.orderByDesc(CfChargeCommon::getCreateDate);
        return queryWrapper;
    }

    /**
     * 删除
     * 这里删除是需要账单，发票作废时候反向把账单，发票清空
     *
     * @param chargeCommonUpdateDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateDelete(ChargeCommonUpdateDto chargeCommonUpdateDto) {
        chargeCommonUpdateDto.setCheckStatus(ChargeCommonEnum.CHECK_STSTUS_ZERO.getCode());
        List<CfChargeCommon> cfChargeCommons = cfChargeCommonMapper.selectBatchIds(chargeCommonUpdateDto.getChargeIds());
        if (CollectionUtils.isEmpty(cfChargeCommons)) {
            throw FinanceBizState.DATE_ERROR;
        }
        cfChargeCommons.forEach(cfChargeCommon -> {
            Assert.isTrue(StringUtils.isBlank(cfChargeCommon.getInvoiceNo()), ModuleBizState.DATE_ERROR_DELETE.format("已经生成发票或者账单"));
            Assert.isTrue(cfChargeCommon.getParentId() == -1, ModuleBizState.DATE_ERROR_DELETE.format("拆分费用"));
            Assert.isTrue(StringUtils.isBlank(cfChargeCommon.getClearNo()), ModuleBizState.DATE_ERROR_DELETE.format("费用已经关联核销单"));
            CfChargeCommon chargeCommonUpdate = new CfChargeCommon();
            chargeCommonUpdate.setChargeId(cfChargeCommon.getChargeId());
            chargeCommonUpdate.setCheckStatus(chargeCommonUpdateDto.getCheckStatus());
            chargeCommonUpdate.setUpdateBy(pageInfoUtil.getUser().getUserId());
            chargeCommonUpdate.setUpdateName(pageInfoUtil.getUser().getRealName());
            chargeCommonUpdate.setUpdateDate(LocalDateTime.now());
            cfChargeCommonMapper.updateById(chargeCommonUpdate);
            try {
                ApplicationContextUtil.getContext().getBean(NotifyRemoteService.class).saveNotify(cfChargeCommon.getChargeId(), MessageEnum.NOTIFY_MESSAGE.getNotifyType(), 1, -1L, "MCN费用", null, null, String.format("您有一笔%s【%s】被作废", "费用",cfChargeCommon.getChargeCode()), cfChargeCommon.getCreateBy(), cfChargeCommon.getCreateName());
            }catch (Exception e){
                log.error("发送消息通知异常",e);
            }
        });
        return 1;
    }

    /**
     * 作废操作
     * 这里删除是需要账单，发票作废时候反向把账单，发票清空
     *
     * @param chargeId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Response updateInvalid(Long chargeId) {
        CfChargeCommon cfChargeCommon = cfChargeCommonMapper.selectById(chargeId);
        if (Objects.isNull(cfChargeCommon)) {
            throw FinanceBizState.DATE_ERROR;
        }
        {
            List<CfChargeCommon> cfChargeCommonsOfParent =new ArrayList<>();
            List<Long> chargeIds = new ArrayList<>();
            this.invalidCheck(chargeId,cfChargeCommonsOfParent,chargeIds);
            CfChargeCommon chargeCommonUpdate = new CfChargeCommon();
            chargeCommonUpdate.setCheckStatus(ChargeCheckStatusEnum.ZF.getCode());
            cfChargeCommonMapper.update(chargeCommonUpdate, Wrappers.<CfChargeCommon>lambdaQuery().in(CfChargeCommon::getChargeId, chargeIds));
            try {
                for (CfChargeCommon cfC:cfChargeCommonsOfParent) {
                    ApplicationContextUtil.getContext().getBean(NotifyRemoteService.class).saveNotify(cfC.getChargeId(), MessageEnum.NOTIFY_MESSAGE.getNotifyType(), 1, -1L, "MCN费用", null, null, String.format("您有一笔%s【%s】被作废", "费用", cfC.getChargeCode()), cfC.getCreateBy(), cfC.getCreateName());
                }
            } catch (Exception e) {
                log.error("发送消息通知异常", e);
            }

        }
        return Response.success(1);
    }

    /**
     * 作废前查询检查
     * @param chargeId
     * @return
     */
    public Response invalidCheck(Long chargeId,List<CfChargeCommon> cfChargeCommonsOfParent,List<Long> chargeIds){
        CfChargeCommon cfChargeCommon = cfChargeCommonMapper.selectById(chargeId);
        if (Objects.isNull(cfChargeCommon)) {
            throw FinanceBizState.DATE_ERROR;
        }
        Assert.isTrue(StringUtils.isBlank(cfChargeCommon.getClearNo()), "数据异常：该费用已关联核销单不允许作废");
        {
            chargeIds.add(chargeId);
            Assert.isTrue(StringUtils.isBlank(cfChargeCommon.getInvoiceNo()),"数据异常：费用已经生成发票或者账单不允许作废");
            Long parentId = cfChargeCommon.getParentId();
            if (!Objects.equals(parentId, -1L)) {
                cfChargeCommonsOfParent = cfChargeCommonMapper.selectList(Wrappers.<CfChargeCommon>lambdaQuery().eq(CfChargeCommon::getParentId, parentId).notIn(CfChargeCommon::getChargeId,cfChargeCommon.getChargeId()));
                cfChargeCommonsOfParent.forEach(x -> {
                    Assert.isTrue(StringUtils.isBlank(x.getInvoiceNo()), "数据异常：该费用关联的费用:"+x.getChargeCode()+"已经生成发票或者账单不允许作废");
                    Assert.isTrue(StringUtils.isBlank(cfChargeCommon.getClearNo()), "数据异常：该费用关联的费用:"+x.getChargeCode()+"已关联核销单不允许作废");
                    chargeIds.add(x.getChargeId());
                });
            }
            cfChargeCommonsOfParent.add(cfChargeCommon);
            if(CollectionUtils.isNotEmpty(cfChargeCommonsOfParent)&&cfChargeCommonsOfParent.size()>1){
                return  new Response(200,"该费用已经关联其他单据，将会一同作废关联单据：",cfChargeCommonsOfParent.stream().map(x->x.getChargeCode()).collect(Collectors.toSet()));
            }

        }
        return  new Response(200,null);
    }
    public Integer getRelatedCount(String chargeSourceCode, String chargeSourceDetail) {
        return cfChargeCommonMapper.selectCount(Wrappers.lambdaQuery(CfChargeCommon.class)
                .eq(StringUtils.isNotBlank(chargeSourceCode), CfChargeCommon::getChargeSourceCode, chargeSourceCode)
                .eq(StringUtils.isNotBlank(chargeSourceDetail), CfChargeCommon::getChargeSourceDetail, chargeSourceDetail)
                .ne(CfChargeCommon::getCheckStatus, ChargeCheckStatusEnum.SC.getCode()));
    }

    public Integer getInvalid(String chargeSourceCode, String chargeSourceDetail) {
        return cfChargeCommonMapper.selectCount(Wrappers.lambdaQuery(CfChargeCommon.class)
                .eq(StringUtils.isNotBlank(chargeSourceCode), CfChargeCommon::getChargeSourceCode, chargeSourceCode)
                .eq(StringUtils.isNotBlank(chargeSourceDetail), CfChargeCommon::getChargeSourceDetail, chargeSourceDetail)
                .ne(CfChargeCommon::getCheckStatus, ChargeCheckStatusEnum.SC.getCode())
                .ne(CfChargeCommon::getCheckStatus, ChargeCheckStatusEnum.ZF.getCode()));
    }

    public List<McnChargeCommonVO> getMcnCharge(String chargeSourceCode) {
        return cfChargeCommonMapper.getMcnCharge(chargeSourceCode);
    }

    public List<String> getBalance(String balance) {
        List<CfChargeCommon> chargeCommons = cfChargeCommonMapper.selectList(Wrappers.lambdaQuery(CfChargeCommon.class)
                .like(StringUtils.isNotBlank(balance), CfChargeCommon::getBalance, balance)
                .groupBy(CfChargeCommon::getBalance).select(CfChargeCommon::getBalance));
        return chargeCommons.stream().map(CfChargeCommon::getBalance).collect(Collectors.toList());
    }

    public List<String> getFinanceEntity(String entity) {
        return getAppointField(entity, CfChargeCommon::getFinanceEntity);
    }

    /**
     * 取目标字段去重
     *
     * @param entity
     * @param sFunction
     * @return
     */
    private List<String> getAppointField(String entity, SFunction<CfChargeCommon, ? extends String> sFunction) {
        List<CfChargeCommon> chargeCommons = cfChargeCommonMapper.selectList(Wrappers.lambdaQuery(CfChargeCommon.class)
                .like(StringUtils.isNotBlank(entity), sFunction, entity)
                .groupBy(sFunction).select(sFunction));
        return chargeCommons.stream().map(sFunction).collect(Collectors.toList());
    }

    public PageInfo<CfChargeCommonVo> select(CfChargeListQuery cfChargeListQuery) {
        PageInfoUtil.startPage(cfChargeListQuery);
        List<CfChargeCommon> chargeCommons = cfChargeCommonMapper.selectList(Wrappers.lambdaQuery(CfChargeCommon.class)
                .like(StringUtils.isNotEmpty(cfChargeListQuery.getChargeSourceCode()), CfChargeCommon::getChargeSourceCode, cfChargeListQuery.getChargeSourceCode())
                .like(StringUtils.isNotEmpty(cfChargeListQuery.getInvoiceTitle()), CfChargeCommon::getInvoiceTitle, cfChargeListQuery.getInvoiceTitle())
                .in(CollectionUtils.isNotEmpty(cfChargeListQuery.getChargeIds()), CfChargeCommon::getChargeId, cfChargeListQuery.getChargeIds())
                .eq(StringUtils.isNotEmpty(cfChargeListQuery.getBalance()), CfChargeCommon::getBalance, cfChargeListQuery.getBalance())
                .eq(StringUtils.isNotEmpty(cfChargeListQuery.getArapType()), CfChargeCommon::getArapType, cfChargeListQuery.getArapType())
                .ne(1 == cfChargeListQuery.getTaxInvoiceNoFilled(), CfChargeCommon::getTaxInvoiceNo, Strings.EMPTY)
                .like(StringUtils.isNotEmpty(cfChargeListQuery.getTaxInvoiceNo()), CfChargeCommon::getTaxInvoiceNo, cfChargeListQuery.getTaxInvoiceNo())
                .ne(CfChargeCommon::getCheckStatus, ChargeCommonEnum.CHECK_STSTUS_ZERO.getCode())
                .orderByDesc(CfChargeCommon::getCreateDate));
        List<CfChargeCommonVo> list = chargeCommons.stream().map(cfChargeCommon -> {
            CfChargeCommonVo cfChargeCommonVo = new CfChargeCommonVo();
            PageInfoUtil.copyProperties(cfChargeCommon, cfChargeCommonVo);
            cfChargeCommonVo.setAmountBalance(cfChargeCommonVo.getAmountPp().subtract(cfChargeCommonVo.getActualAmount()));
            return cfChargeCommonVo;
        }).collect(Collectors.toList());
        return PageInfoUtil.toPageInfo(chargeCommons, list);
    }


    public CfChargeCommonVo detail(Long chargeId) {
        CfChargeCommon cfChargeCommon = selectOne(chargeId);
        Assert.isTrue(Objects.nonNull(cfChargeCommon), ModuleBizState.DATE_ERROR.message());
        CfChargeCommonVo result = PageInfoUtil.initEntity(CfChargeCommonVo.class, cfChargeCommon);

        if (cfChargeCommon.getSplitType() == 1) {
            List<CfChargeCommon> chargeCommons = cfChargeCommonMapper.selectList(Wrappers.lambdaQuery(CfChargeCommon.class).eq(CfChargeCommon::getParentId, cfChargeCommon.getParentId()));
            result.setBrothercharges(PageInfoUtil.listToCustomList(chargeCommons, CfChargeCommonBrotherVo.class));
            result.setSplitVos(chargeCommons.stream().map(cfChargeCommon1 -> {
                SplitVo splitVo = new SplitVo();
                BeanUtils.copyProperties(cfChargeCommon1, splitVo);
                splitVo.setApplyTaxInvoice(StringUtils.isEmpty(cfChargeCommon1.getInvoiceNo()) ? 0 : 1);
                return splitVo;
            }).collect(Collectors.toList()));
        }
        return result;
    }

    /**
     * 费用拆分信息展示
     *
     * @param cfChargeCommon
     * @return
     */
    public List<SplitVo> splitChargeInfo(CfChargeCommon cfChargeCommon) {
        if (cfChargeCommon.getParentId() == -1) {
            return new ArrayList<>();
        }

        List<CfChargeCommon> chargeCommons = cfChargeCommonMapper.selectList(Wrappers.lambdaQuery(CfChargeCommon.class).eq(CfChargeCommon::getParentId, cfChargeCommon.getParentId()));

        return (chargeCommons.stream().map(cfChargeCommon1 -> {
            SplitVo splitVo = new SplitVo();
            BeanUtils.copyProperties(cfChargeCommon1, splitVo);
            splitVo.setApplyTaxInvoice(StringUtils.isEmpty(cfChargeCommon1.getInvoiceNo()) ? 0 : 1);
            return splitVo;
        }).collect(Collectors.toList()));
    }

    @Transactional(rollbackFor = Exception.class)
    public Long splitChargeCommon(SplitChargeCommonDto splitChargeCommonDto) {
        CfChargeCommon cfChargeCommon = selectOne(splitChargeCommonDto.getMasterChargeId());
        verity(splitChargeCommonDto, cfChargeCommon);

        //因为lambda
        AtomicInteger count = new AtomicInteger(0);
        List<CfChargeCommon> cfChargeCommons = splitChargeCommonDto.getList().stream().map(value -> {
            CfChargeCommon son = PageInfoUtil.initEntity(CfChargeCommon.class, cfChargeCommon);
            if (StringUtils.isNotBlank(cfChargeCommon.getClearNo())) {
                throw FinanceBizState.SPLIT_CHARGECOMMON_PART_ERROR;
            }
            son.setChargeId(null);
            son.setAmountPp(value);
            son.setPricePp(value);
            son.setOverage(value);
            son.setSplitId(count.incrementAndGet());
            son.setParentId(cfChargeCommon.getChargeId());
            son.setSplitType(1);
            son.setChargeCode(pageInfoUtil.generateBusinessNum(BillNoConstantClassField.CRG));
            pageInfoUtil.baseInfoFill(son);
            cfChargeCommonMapper.insert(son);
            return son;
        }).collect(Collectors.toList());

        CfChargeCommon updateCfChargeCommon =cfChargeCommonMapper.selectById(cfChargeCommon.getChargeId());
        updateCfChargeCommon.setCheckStatus(ChargeCommonEnum.CHECK_STSTUS_ZERO.getCode());
        updateCfChargeCommon.setParentId(0L);
        updateSynchronize(updateCfChargeCommon);

        return cfChargeCommons.get(splitChargeCommonDto.getSelectId()).getChargeId();
    }

    private void verity(SplitChargeCommonDto splitChargeCommonDto, CfChargeCommon cfChargeCommon) {
        Assert.isTrue(splitChargeCommonDto.getList().size() > splitChargeCommonDto.getSelectId(), ModuleBizState.DATE_ERROR.message());
        Assert.isTrue(Objects.nonNull(cfChargeCommon) && cfChargeCommon.getParentId() == -1 && ChargeEnum.ARAP_TYPE_AR.getCode().equals(cfChargeCommon.getArapType()), ModuleBizState.DATE_ERROR.message());
        Assert.isTrue(ChargeCommonEnum.CHARGE_SOURCE_TYPE_MCN.getCode().equals(cfChargeCommon.getChargeSourceType()), ModuleBizState.DATE_ERROR.message());
        splitChargeCommonDto.getList().forEach(value ->
                Assert.isTrue(value.compareTo(BigDecimal.ZERO) > 0, ModuleBizState.DATE_ERROR_BIGDECIMAL_ZERO.message()));
        BigDecimal sum = splitChargeCommonDto.getList().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        Assert.isTrue(cfChargeCommon.getAmountPp().compareTo(sum) == 0, ModuleBizState.DATE_ERROR.message());
        Assert.isTrue(StringUtils.isEmpty(cfChargeCommon.getInvoiceNo()), ModuleBizState.DATE_ERROR.message());
    }

    public List<String> fieldsInvoiceTitle(String value) {
        return getAppointField(value, CfChargeCommon::getInvoiceTitle);
    }

    /**
     * 账单物理删除
     *
     * @param chargeId
     * @param invoiceNo
     */
    @Transactional(rollbackFor = Exception.class)
    public void invoiceNoPhysicsDelete(Long chargeId, String invoiceNo) {
        CfChargeCommon cfChargeCommon = cfChargeCommonMapper.selectById(chargeId);
        Assert.isTrue(cfChargeCommon.getInvoiceNo().equals(invoiceNo) && cfChargeCommon.getChargeSourceCode().equals(invoiceNo), ModuleBizState.DATE_ERROR.message());
        updateSynchronizeBase(chargeId, () -> cfChargeCommonMapper.deleteById(chargeId));
    }

    /**
     * 根据来源单据号查询费用(排除已删除)
     *
     * @param sourceCode
     * @return
     */
    public List<CfChargeCommon> selectCfChargesBySourceCode(String sourceCode) {
        return cfChargeCommonMapper.selectList(Wrappers.lambdaQuery(CfChargeCommon.class)
                .eq(CfChargeCommon::getChargeSourceCode, sourceCode)
                .ne(CfChargeCommon::getCheckStatus, ChargeCommonEnum.CHECK_STSTUS_ZERO)
        );
    }
}
