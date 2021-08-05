package com.chenfan.finance.service.impl;

import cn.hutool.crypto.digest.MD5;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.ccp.util.start.ApplicationContextUtil;
import com.chenfan.common.exception.SystemState;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.finance.dao.*;
import com.chenfan.finance.enums.HttpStateEnum;
import com.chenfan.finance.model.*;
import com.chenfan.finance.model.dto.CreateQcChargeDto;
import com.chenfan.finance.model.dto.UpdateInvDto;
import com.chenfan.finance.model.dto.UpdatePoDetailConDto;
import com.chenfan.finance.model.dto.UpdatePoDetailPriceDto;
import com.chenfan.finance.scheduled.CfRdChargeScheduled;
import com.chenfan.finance.service.UpstreamService;
import com.chenfan.finance.utils.BeanUtilCopy;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Wen.Xiao
 * @Description // 上游数据更改下游财务数据
 * @Date 2021/5/28  16:10
 * @Version 1.0
 */
@Slf4j
@Service
public class UpstreamServiceImpl implements UpstreamService {
    @Resource
    private CfRdRecordDetailMapper cfRdRecordDetailMapper;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private CfChargeMapper cfChargeMapper;
    @Resource
    private CfChargeInMapper cfChargeInMapper;
    @Resource
    private CfPoDetailMapper cfPoDetailMapper;
    @Resource
    private CfRdChargeScheduled cfRdChargeScheduled;
    @Resource
    private CfQcRecordMapper cfQcRecordMapper;
    @Resource
    private CfQcRecordDetailMapper cfQcRecordDetailMapper;
    @Resource
    private CfQcRecordAsnDetailMapper cfQcRecordAsnDetailMapper;

    /**
     * 更改采购日期
     * 1：已存在入库单采购单
     * 2：不存在入库单只有采购单
     * 3：不存在采购单
     * @param updatePoDetailConDto
     * @return
     */
    @Override
    public Response<Boolean> updatePoCon(UpdatePoDetailConDto updatePoDetailConDto) {
        //根据类型验证数据 todo
        LocalDateTime conEndDate = updatePoDetailConDto.getConEndDate();
        Long poId = updatePoDetailConDto.getPoId();
        List<CfPoDetail> cfPoDetails = cfPoDetailMapper.selectList(Wrappers.<CfPoDetail>lambdaQuery().eq(CfPoDetail::getPoId, poId));
        if(CollectionUtils.isEmpty(cfPoDetails)){
            return Response.success(true);
        }

        List<Long> rdDetailIdsByPoId = cfRdRecordDetailMapper.selectRdDetailIdsByPoId(poId);
        List<CfCharge> cfCharges =new ArrayList<>();
        //无入库通知单，检查
        if(CollectionUtils.isNotEmpty(rdDetailIdsByPoId)){
            cfCharges = cfChargeMapper.selectList(Wrappers.<CfCharge>lambdaQuery().in(CfCharge::getChargeSourceDetailId, rdDetailIdsByPoId));
        }
        Optional<CfCharge> first = cfCharges.stream().filter(x -> Objects.nonNull(x.getInvoiceNo())).findFirst();
        if(first.isPresent()){
            return Response.error(SystemState.BUSINESS_ERROR.code(),"当前采购已生成账单，不支持在修改时间！！！");
        }
        if(Objects.nonNull(conEndDate)){
            List<Long> chargeIds = cfCharges.stream().map(x -> x.getChargeId()).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(chargeIds)){
                chargeIds.add(-1L);
            }
            RLock multiLock = getMultiLock(chargeIds,Boolean.TRUE);
            if(multiLock.tryLock()){
                try {
                    //第一步删除cf_charge
                    cfChargeMapper.delete(Wrappers.<CfCharge>lambdaQuery().in(CfCharge::getChargeId, chargeIds));
                    //第二步删除cf_charge_in
                     cfChargeInMapper.delete(Wrappers.<CfChargeIn>lambdaQuery().in(CfChargeIn::getChargeId, chargeIds));
                    //第三步更新采购单相关的合同日期
                     CfPoDetail cfPoDetail=new CfPoDetail();
                     cfPoDetail.setConEndDate(conEndDate);
                     cfPoDetailMapper.update(cfPoDetail, Wrappers.<CfPoDetail>lambdaQuery().eq(CfPoDetail::getPoId, poId));
                    //第四步更新入库通知单信息为未生成费用
                     CfRdRecordDetail cfRdRecordDetail = new CfRdRecordDetail();
                     cfRdRecordDetail.setCreateChargeFlag(false);
                     cfRdRecordDetailMapper.update(cfRdRecordDetail, Wrappers.<CfRdRecordDetail>lambdaQuery().eq(CfRdRecordDetail::getPoId, poId));
                }catch (Exception e){
                    log.error("异常：",e);
                    return new Response(ResponseCode.SAVE_ERROR, e.getMessage());
                }finally {
                    multiLock.unlock();
                }
            }else {
                return Response.error(SystemState.BUSINESS_ERROR.code(),"当前采购单，正在被财务的其他业务引用，请稍后重试");
            }
            cfRdChargeScheduled.asyncCfRdCalculateTask(null,poId,null);
        }
        return Response.success(true);
    }
    @Override
    public Response<Boolean> updateInv(UpdateInvDto updateInvDto) {
        List<Integer>  inventoryIdList=new ArrayList(){{add(-1);}};
        List<String> productCodeList=new ArrayList(){{add("-1");}};
        if(Objects.nonNull(updateInvDto.getInventoryId())){
            inventoryIdList.add(updateInvDto.getInventoryId());
        }
        if(Objects.isNull(updateInvDto.getInventoryId())&&StringUtils.isNotBlank(updateInvDto.getProductCode())){
            productCodeList.add(updateInvDto.getProductCode());
        }
        List<CfRdRecordDetail> cfRdRecordDetails = cfRdRecordDetailMapper.selectList(Wrappers.<CfRdRecordDetail>lambdaQuery().in(CfRdRecordDetail::getInventoryId, inventoryIdList).or().in(CfRdRecordDetail::getProductCode, productCodeList));
        if(CollectionUtils.isEmpty(cfRdRecordDetails)){
            return Response.success(true);
        }
        CfCharge cfCharge = new CfCharge();
        cfCharge.setSalesType(updateInvDto.getSalesType());
        //更新费用表
        cfChargeMapper.update(cfCharge,Wrappers.<CfCharge>lambdaQuery().in(CfCharge::getChargeSourceDetailId,cfRdRecordDetails.stream().map(x -> x.getRdRecordDetailId()).collect(Collectors.toList())).isNull(CfCharge::getInvoiceNo));
        List<CfCharge> cfCharges = cfChargeMapper.selectList(Wrappers.<CfCharge>lambdaQuery().in(CfCharge::getChargeSourceDetailId, cfRdRecordDetails.stream().map(x -> x.getRdRecordDetailId()).collect(Collectors.toList())).eq(CfCharge::getSalesType,updateInvDto.getSalesType()));

        //更新 cf_charge_in
        CfChargeIn cfChargeIn=new CfChargeIn();
        cfChargeIn.setSalesType(updateInvDto.getSalesType());
        cfChargeIn.setGoodsName(updateInvDto.getInventoryName());
        cfChargeInMapper.update(cfChargeIn,Wrappers.<CfChargeIn>lambdaQuery().in(CfChargeIn::getChargeId,cfCharges.stream().map(x->x.getChargeId()).collect(Collectors.toList())));
        //更新入库单
        CfRdRecordDetail cfRdRecordDetail = new CfRdRecordDetail();
        cfRdRecordDetail.setSalesType(updateInvDto.getSalesType());
        cfRdRecordDetail.setInventoryName(updateInvDto.getInventoryName());
        cfRdRecordDetailMapper.update(cfRdRecordDetail,Wrappers.<CfRdRecordDetail>lambdaQuery().in(CfRdRecordDetail::getRdRecordDetailId,cfCharges.stream().map(x->x.getChargeSourceDetailId()).collect(Collectors.toList())));
        return Response.success(true);
    }

    /**
     * 更改采购价格
     * @param updatePoDetailPriceDtoList
     * @return
     */
    @Override
    public Response<Boolean> updatePriceOfPo(List<UpdatePoDetailPriceDto> updatePoDetailPriceDtoList) {
        List<Long> chargeIds =new ArrayList<>();
        Response<Boolean> booleanResponse = this.checkPoStateOfPrice(updatePoDetailPriceDtoList.stream().map(x->x.getPoDetailId()).collect(Collectors.toList()), chargeIds);
        if(!Objects.equals(booleanResponse.getCode(),SystemState.SUCCESS.code())){
            return booleanResponse;
        }
        if(CollectionUtils.isEmpty(chargeIds)){
            chargeIds.add(-2L);
        }
        RLock multiLock = getMultiLock(chargeIds,Boolean.TRUE);
        if(multiLock.tryLock()){
           try {
               //删除费用等，更新采购和入库单的价格
               cfChargeMapper.delete(Wrappers.<CfCharge>lambdaQuery().in(CfCharge::getChargeId,chargeIds));
               cfChargeInMapper.delete(Wrappers.<CfChargeIn>lambdaQuery().in(CfChargeIn::getChargeId,chargeIds));
               //跟新采购的价格
               //第三步更新采购单相关的合同日期
               for (UpdatePoDetailPriceDto updatePoDetailPriceDto :updatePoDetailPriceDtoList) {
                   CfPoDetail cfPoDetail = BeanUtilCopy.copyPropertiesIgnoreType(updatePoDetailPriceDto, CfPoDetail.class);
                   cfPoDetailMapper.updateById(cfPoDetail);
                   CfRdRecordDetail cfRdRecordDetail = BeanUtilCopy.copyPropertiesIgnoreType(updatePoDetailPriceDto, CfRdRecordDetail.class);
                   cfRdRecordDetail.setCreateChargeFlag(false);
                   cfRdRecordDetailMapper.update(cfRdRecordDetail, Wrappers.<CfRdRecordDetail>lambdaQuery().eq(CfRdRecordDetail::getPoDetailId, updatePoDetailPriceDto.getPoDetailId()));
               }
           }catch (Exception e){
               log.error("异常：",e);
               return new Response(ResponseCode.SAVE_ERROR, e.getMessage());
           }finally {
               multiLock.unlock();
           }
        }else {
            return Response.error(SystemState.BUSINESS_ERROR.code(),"当前采购单，正在被财务的其他业务引用，请稍后重试");
        }
        cfRdChargeScheduled.asyncCfRdCalculateTask(updatePoDetailPriceDtoList.stream().map(x->x.getPoDetailId()).collect(Collectors.toSet()),null,null);
        return Response.success(true);
    }

    /**
     * 检查采购单是否允许改价
     * @param poDetailIdList
     * @param chargeIds
     * @return
     */
    @Override
    public Response<Boolean> checkPoStateOfPrice(List<Long> poDetailIdList,List<Long> chargeIds) {
        List<CfRdRecordDetail> cfRdRecordDetails = cfRdRecordDetailMapper.selectList(Wrappers.<CfRdRecordDetail>lambdaQuery().in(CfRdRecordDetail::getPoDetailId, poDetailIdList));
        List<Long> rdReDetailIds = cfRdRecordDetails.stream().map(x -> x.getRdRecordDetailId()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(rdReDetailIds)){
            rdReDetailIds.add(-3L);
        }
        List<CfCharge> cfCharges = cfChargeMapper.selectList(Wrappers.<CfCharge>lambdaQuery().in(CfCharge::getChargeSourceDetailId, rdReDetailIds));
        //检查相关数据是否生成账单
        List<Long> rdRetailIdsOfInv =cfCharges.stream().filter(x->StringUtils.isNotBlank(x.getInvoiceNo())).map(CfCharge::getChargeSourceDetailId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(rdRetailIdsOfInv)){
            Set<Long> poIdsOfUsedInv = cfRdRecordDetails.stream().filter(x -> rdRetailIdsOfInv.contains(x.getRdRecordDetailId())).map(CfRdRecordDetail::getPoId).collect(Collectors.toSet());
            return Response.error(SystemState.BUSINESS_ERROR.code(),"采购单详情ID:"+poIdsOfUsedInv+"已经生成账单，不允许更改价格");
        }
        if(Objects.nonNull(chargeIds)){
            chargeIds.addAll(cfCharges.stream().map(x -> x.getChargeId()).collect(Collectors.toList()));
        }
        return Response.success(true);
    }

    /**
     * 由于财务这边的入库信息同步较晚，所以存在数量小于质检数量的情况，所以财务这边没法限制登记的数量，需质检入库那边限制单个质检的总数量要小于商品入库数量
     * @param createQcChargeDto
     * @return
     */
    @Override
    public Response<Boolean> createQcChargeByQcTask(CreateQcChargeDto createQcChargeDto) {
        List<CfCharge> cfCharges = cfChargeMapper.selectList(Wrappers.<CfCharge>lambdaQuery().eq(CfCharge::getChargeSourceCode, createQcChargeDto.getQcTask().getRdRecordCode()).or().in(CfCharge::getChargeSourceDetailId,createQcChargeDto.getQcTaskDetails().stream().map(x->x.getQcChargingId()).collect(Collectors.toList())));
        List<Long> chargeIds = cfCharges.stream().map(x -> x.getChargeId()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(chargeIds)){
            chargeIds.add(-4L);
        }
        RLock multiLock = getMultiLock(chargeIds,Boolean.TRUE);
        if(multiLock.tryLock()){
            try {
                //第一步：检查是否存在已结算的数据
                List<CfCharge> chargesOfInv = cfCharges.stream().filter(x -> StringUtils.isNotBlank(x.getInvoiceNo())).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(chargesOfInv)){
                    return Response.error(SystemState.BUSINESS_ERROR.code(),"当前入库单已经生成账单:"+chargesOfInv.stream().map(x->x.getInvoiceNo()).collect(Collectors.toSet())+"，暂不支持登记质检扣费");
                }
                // 第二步： 检查当前是否有符合的计费方案
                for (CreateQcChargeDto.QcTaskDetailsBean qcTaskDetailsBean:createQcChargeDto.getQcTaskDetails()) {
                    StringBuffer errorMsg=new StringBuffer();
                    RuleBillingDetailQcTask match = cfRdChargeScheduled.matchRule(createQcChargeDto.getQcTask().getQcTaskCode(),BeanUtilCopy.copyPropertiesIgnoreType(qcTaskDetailsBean,CfQcRecordDetail.class), createQcChargeDto.getQcTask().getBrandId(), cfRdChargeScheduled.buildHashMapBean(),errorMsg);
                    if(Objects.isNull(match)){
                      return Response.error(ResponseCode.SAVE_ERROR.getCode(),errorMsg.toString());
                    }
                }
                ApplicationContextUtil.getContext().getBean(UpstreamServiceImpl.class).doDatabaseDataOfQc(createQcChargeDto,chargeIds);
                // 第四步： 关联数据
            }catch (Exception e){
                log.error("异常：",e);
                return new Response(ResponseCode.SAVE_ERROR, e.getMessage());
            }finally {
                multiLock.unlock();
            }
        }else {
            return Response.error(SystemState.BUSINESS_ERROR.code(),"当前入库，正在被财务的其他业务引用，请稍后重试");
        }
        cfRdChargeScheduled.asyncCfRdCalculateTask(null,null,createQcChargeDto.getQcTask().getRdRecordId());
        return Response.success(true);
    }


    /**
     * 处理QC的数据库数据
     * @param createQcChargeDto
     * @param chargeIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void doDatabaseDataOfQc(CreateQcChargeDto createQcChargeDto,List<Long> chargeIds){
        CfQcRecord cfQcRecord = cfQcRecordMapper.selectById(createQcChargeDto.getQcTask().getQcTaskId());
        if(Objects.isNull(cfQcRecord)){
            cfQcRecordMapper.insert(BeanUtilCopy.copyPropertiesIgnoreType(createQcChargeDto.getQcTask(),CfQcRecord.class));
        }else {
            cfQcRecordMapper.updateById(BeanUtilCopy.copyPropertiesIgnoreType(createQcChargeDto.getQcTask(),CfQcRecord.class));
        }
        for (CreateQcChargeDto.QcTaskDetailsBean qcTaskDetailsBean:createQcChargeDto.getQcTaskDetails()) {
            CfQcRecordDetail cfQcRecordDetail = cfQcRecordDetailMapper.selectById(qcTaskDetailsBean.getQcChargingId());
            if(Objects.isNull(cfQcRecordDetail)){
                cfQcRecordDetailMapper.insert(BeanUtilCopy.copyPropertiesIgnoreType(qcTaskDetailsBean,CfQcRecordDetail.class));
            }else {
                CfQcRecordDetail cfQcRecordDetail1 = BeanUtilCopy.copyPropertiesIgnoreType(qcTaskDetailsBean, CfQcRecordDetail.class);
                cfQcRecordDetail1.setQcQty(0);
                cfQcRecordAsnDetailMapper.delete(Wrappers.<CfQcRecordAsnDetail>lambdaQuery().eq(CfQcRecordAsnDetail::getQcChargingId,cfQcRecordDetail1.getQcChargingId()));
                cfQcRecordDetailMapper.updateById(cfQcRecordDetail1);
            }
        }
        cfChargeInMapper.delete(Wrappers.<CfChargeIn>lambdaQuery().in(CfChargeIn::getChargeId,chargeIds));
        cfChargeMapper.delete(Wrappers.<CfCharge>lambdaQuery().in(CfCharge::getChargeId,chargeIds));
        CfRdRecordDetail cfRdRecordDetail = new CfRdRecordDetail();
        cfRdRecordDetail.setCreateChargeFlag(false);
        cfRdRecordDetailMapper.update(cfRdRecordDetail,Wrappers.<CfRdRecordDetail>lambdaQuery().eq(CfRdRecordDetail::getRdRecordId,createQcChargeDto.getQcTask().getRdRecordId()));
    }

    /**
     * Returns MultiLock instance associated with specified <code>chargeIds</code>
     * @param chargeIds
     * @param isSchedule
     * @return
     */
    private  RLock getMultiLock(List<Long> chargeIds,Boolean isSchedule){
        List<RLock> rLockList = new ArrayList<>();
        if(isSchedule){
            rLockList.add(redissonClient.getLock("47973F152C14077EDF80D3C3CF37CBBE"));
        }
        for (Long chargeId: chargeIds) {
            rLockList.add(redissonClient.getLock(MD5.create().digestHex("/invoice/add"+chargeId)));
        }
        return redissonClient.getMultiLock(rLockList.toArray(new RLock[rLockList.size()]));
    }
}
