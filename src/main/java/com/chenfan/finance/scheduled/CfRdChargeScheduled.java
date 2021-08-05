package com.chenfan.finance.scheduled;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.ccp.util.start.ApplicationContextUtil;
import com.chenfan.finance.config.BillNoConstantClassField;
import com.chenfan.finance.constant.CfFinanceConstant;
import com.chenfan.finance.dao.*;
import com.chenfan.finance.enums.ChargeEnum;
import com.chenfan.finance.enums.MonitoringEnum;
import com.chenfan.finance.enums.NumberEnum;
import com.chenfan.finance.model.*;
import com.chenfan.finance.model.vo.CfPoDetailExtendVo;
import com.chenfan.finance.producer.U8Produce;
import com.chenfan.finance.server.BaseRemoteServer;
import com.chenfan.finance.service.RuleBillingHeaderService;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Wen.Xiao
 * @Description // 入库通知单生成费用(使用两位，并且执行)
 * @Date 2021/4/23  16:42
 * @Version 1.0
 */
@Slf4j
@Component
public class CfRdChargeScheduled   {

    @Resource
    private CfRdRecordMapper cfRdRecordMapper;
    @Resource
    private CfRdRecordDetailMapper cfRdRecordDetailMapper;
    @Resource
    private CfChargeMapper cfChargeMapper;
    @Resource
    private CfChargeInMapper cfChargeInMapper;
    @Resource
    private CfChargeHistoryMapper cfChargeHistoryMapper;

    @Resource
    private PageInfoUtil pageInfoUtil;
    @Resource
    private CfPoHeaderMapper cfPoHeaderMapper;
    @Resource
    private CfPoDetailMapper cfPoDetailMapper;

    @Resource
    private RuleBillingDetailMapper ruleBillingDetailMapper;
    @Resource
    RejectionRetiredDetailMapper rejectionRetiredDetailMapper;
    @Resource
    private RuleBillingHeaderMapper ruleBillingHeaderMapper;
    @Resource
    public MonitoringSchedule monitoringSchedule;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private CfQcRecordDetailMapper cfQcRecordDetailMapper;
    @Resource
    CfQcRecordMapper cfQcRecordMapper;
    @Resource
    private RuleBillingHeaderService ruleBillingHeaderService;
    @Resource
    private CfQcRecordAsnDetailMapper cfQcRecordAsnDetailMapper;

    @Async
    public void asyncCfRdCalculateTask( Set<Long> poDetailIdList, Long poId,Long recordId ){
        try {
            RLock interLock = redissonClient.getLock("47973F152C14077EDF80D3C3CF37CBBE");
            if(interLock.tryLock()){
                try {
                    LambdaQueryWrapper<CfRdRecordDetail> lambdaQueryWrapper = Wrappers.<CfRdRecordDetail>lambdaQuery();
                    if(Objects.nonNull(poId)){
                        lambdaQueryWrapper.eq(CfRdRecordDetail::getPoId,poId);
                    }
                    if(CollectionUtils.isNotEmpty(poDetailIdList)){
                        lambdaQueryWrapper.in(CfRdRecordDetail::getPoDetailId, poDetailIdList);
                    }
                    if(Objects.nonNull(recordId)){
                        lambdaQueryWrapper.eq(CfRdRecordDetail::getRdRecordId,recordId);
                    }
                    List<RuleBillingDetail> ruleBillingDetailList = this.queryValidCfWdtRdRuleBilling();
                    List<CfRdRecordDetail> cfRdRecordDetails = cfRdRecordDetailMapper.selectList(lambdaQueryWrapper.eq(CfRdRecordDetail::getCreateChargeFlag, false));
                    if (CollectionUtils.isNotEmpty(cfRdRecordDetails)) {
                        // 计费方案
                        CfRdChargeScheduled bean = ApplicationContextUtil.getContext().getBean(CfRdChargeScheduled.class);
                        Map<Long, List<CfRdRecordDetail>> groups = cfRdRecordDetails.stream().collect(Collectors.groupingBy(CfRdRecordDetail::getRdRecordId));
                        groups.forEach((rdRecordId, v) -> {
                            try {
                                CfRdRecord cfRdRecord = cfRdRecordMapper.selectById(rdRecordId);
                                bean.doCreateCharge(cfRdRecord, ruleBillingDetailList, v);
                            } catch (Exception e) {
                                log.error("触发修改任务生成费用异常", e);
                            }
                        });
                    }
                }finally {
                    interLock.unlock();
                }
            }
        }catch (Exception r){
            log.error("异步调用生成费用",r);
        }

    }
    public ReturnT<String> cfRdCalculateTask(String param) {
        log.info("生成费用的参数：{}",param);
        //对费用生成的方法进行枷锁，目的是使避免重复
        RLock interLock = redissonClient.getLock("47973F152C14077EDF80D3C3CF37CBBE");
        if(interLock.tryLock()){
            try {
                List<RuleBillingDetail> ruleBillingDetailList = this.queryValidCfWdtRdRuleBilling();
                // 查询未计费的标识为红的退次单集合
                List<CfRdRecord> cfRdRecordList = cfRdRecordMapper.selectList(Wrappers.<CfRdRecord>lambdaQuery().eq(CfRdRecord::getCreateChargeFlag, false));
                CfRdChargeScheduled bean = ApplicationContextUtil.getContext().getBean(CfRdChargeScheduled.class);
                if (CollectionUtils.isNotEmpty(cfRdRecordList)) {
                    // 计费方案

                    // 查询状态正常且在有效期内的货品采购计费方案明细
                    for (CfRdRecord cfRdRecord : cfRdRecordList) {
                        try {
                            List<Long> rdRecordIds = new ArrayList<>();
                            rdRecordIds.add(cfRdRecord.getRdRecordId());
                            List<CfRdRecordDetail> cfRdDetailList = getCfRdDetailData(rdRecordIds);
                            bean.doCreateCharge(cfRdRecord, ruleBillingDetailList, cfRdDetailList);
                        } catch (Exception e) {
                            log.error("定时计算入库单费用出错", e);
                        }
                    }
                }
                // 明细数据未生成单据的要生成
                List<CfRdRecordDetail> cfRdDetailZeroData = getCfRdDetailZeroData();
                if (CollectionUtils.isNotEmpty(cfRdDetailZeroData)) {
                    // 计费方案
                    Map<Long, List<CfRdRecordDetail>> groups = cfRdDetailZeroData.stream().collect(Collectors.groupingBy(CfRdRecordDetail::getRdRecordId));
                    groups.forEach((rdRecordId, v) -> {
                        try {
                            CfRdRecord cfRdRecord = cfRdRecordMapper.selectById(rdRecordId);
                            bean.doCreateCharge(cfRdRecord, ruleBillingDetailList, v);
                        } catch (Exception e) {
                            log.error("定时计算入库单明细费用出错（计算到货数量为0的单据）", e);
                        }
                    });
                }
                return ReturnT.SUCCESS;
            }finally {
                interLock.unlock();
            }
        }
        return ReturnT.SUCCESS;
    }

    public void doCreateCharge(CfRdRecord cfRdRecord, List<RuleBillingDetail> ruleBillingDetailList, List<CfRdRecordDetail> cfRdDetailList) {
        List<Integer> idsOfSuccess=new ArrayList<>();
        if(Objects.isNull(cfRdRecord)){
            log.info("当前入库单详情：{} 无法关联到入库单,结算数量：{}",cfRdDetailList.stream().map(CfRdRecordDetail::getRdRecordDetailId).collect(Collectors.toList()));
            return;
        }
        Integer bredVouch = cfRdRecord.getBredVouch();
        Boolean isTc=false;
        List<CfPoDetailExtendVo> cfPoDetailExtendList =null;
        List<RejectionRetiredDetail> rejectionRetiredDetails =null;
        if(bredVouch==1){
            List<Long> poDetailIds = cfRdDetailList.stream().map(x -> x.getPoDetailId()).collect(Collectors.toList());
             cfPoDetailExtendList = cfPoDetailMapper.queryCfPoDetailExtends(poDetailIds);
        }else {
            List<Long> rjRetiredDetailIds = cfRdDetailList.stream().map(x -> x.getRjRetiredDetailId()).collect(Collectors.toList());
            rejectionRetiredDetails = rejectionRetiredDetailMapper.selectListByPrimaryKeys(rjRetiredDetailIds);
            isTc=true;
        }
        // 退次单明细集合
        for (CfRdRecordDetail cfRdRecordDetail : cfRdDetailList) {
            RLock lock = redissonClient.getLock("doCreateCharge" + cfRdRecordDetail.getId());
            if(lock.tryLock()){
                try {
                    Boolean aBoolean = this.buildAndInsertOfCharge(isTc, cfRdRecordDetail, cfRdRecord, ruleBillingDetailList, rejectionRetiredDetails, cfPoDetailExtendList);
                    if(aBoolean){
                        idsOfSuccess.add(cfRdRecordDetail.getId());
                    }
                }catch (Exception e){
                    log.error("生成费用异常",e);
                }
                finally {
                    lock.unlock();
                }
            }
        }
        if(CollectionUtils.isNotEmpty(idsOfSuccess)){
            cfRdRecordMapper.batchUpdateChargeFlagByDetailIds(idsOfSuccess);
            if(Objects.nonNull(cfRdRecord)) {
                cfRdRecordMapper.batchUpdateChargeFlagByIds(Arrays.asList(cfRdRecord.getRdRecordId()));
            }
        }
    }
    /**
     * 根据具体的生成费用并插入数据库
     * @param isTc
     * @param cfRdRecordDetail
     * @param cfRdRecord
     * @param ruleBillingDetailList
     * @param rejectionRetiredDetails
     * @param cfPoDetailExtendList
     * @return
     */
    public Boolean buildAndInsertOfCharge(Boolean isTc , CfRdRecordDetail cfRdRecordDetail, CfRdRecord cfRdRecord,
                                          List<RuleBillingDetail> ruleBillingDetailList, List<RejectionRetiredDetail> rejectionRetiredDetails, List<CfPoDetailExtendVo> cfPoDetailExtendList ){

        BigDecimal taxMoney=cfRdRecordDetail.getTaxUnitPrice().subtract(cfRdRecordDetail.getUnitPrice()).multiply(new BigDecimal(cfRdRecordDetail.getQuantity()));
        BigDecimal unitPrice= null;
        BigDecimal markupUnitPrice= null;
        BigDecimal markupRate= null;
        CfChargeIn cfChargeIn =new CfChargeIn();
        CfCharge delayChCharge = null;
        List<CfCharge> qaChargeList=new ArrayList<>();
        List<CfQcRecordDetail> cfQcRecordDetailList =null;
        if(!isTc){
            Long poDetailId = cfRdRecordDetail.getPoDetailId();
            Optional<CfPoDetailExtendVo> first = cfPoDetailExtendList.stream().filter(x-> Objects.equals(x.getPoDetailId(),poDetailId)).findFirst();
            Assert.isTrue(first.isPresent(), StringUtils.format("入库通知单:(%s)没匹配到对应的采购单详情",cfRdRecordDetail.getRdRecordDetailId()));
            CfPoDetailExtendVo cfPoDetailExtendVo = first.get();
            //TODO 当供应商 品牌，价格等
            Boolean aBoolean = checkDiff(cfRdRecordDetail, cfRdRecord, cfPoDetailExtendVo);
            if(!aBoolean){
                log.info("入库单：{}，验证不通过,不能生成费用",cfRdRecordDetail.getRdRecordDetailId());
                return false;
            }
            unitPrice=cfPoDetailExtendVo.getUnitPrice();
            markupUnitPrice=cfPoDetailExtendVo.getMarkupUnitPrice();
            markupRate=cfPoDetailExtendVo.getMarkupRate();
            delayChCharge = creatCfChargeByRuleBillingData(cfChargeIn,ruleBillingDetailList,cfPoDetailExtendVo, cfRdRecord, cfRdRecordDetail);
            cfQcRecordDetailList = creatQcChargeByRuleBillingData(cfRdRecord, cfRdRecordDetail, qaChargeList);
        }else {
            Long rjRetiredDetailId = cfRdRecordDetail.getRjRetiredDetailId();
            Optional<RejectionRetiredDetail> first = rejectionRetiredDetails.stream().filter(x -> Objects.equals(x.getRjRetiredDetailId(), rjRetiredDetailId)).findFirst();
            Assert.isTrue(first.isPresent(), StringUtils.format("退次入库通知单:%s没匹配到对应的退次单详情",cfRdRecordDetail.getRdRecordDetailId()));
            RejectionRetiredDetail rejectionRetiredDetail = first.get();
            unitPrice=rejectionRetiredDetail.getUnitPrice();
            markupUnitPrice=rejectionRetiredDetail.getMarkupUnitPrice();
            markupRate=rejectionRetiredDetail.getMarkupRate();
        }
        CfCharge cfCharge = installCfCharge(cfRdRecordDetail, cfRdRecord);
        this.installCfChargeIn(isTc,cfChargeIn,cfRdRecordDetail,cfRdRecord,unitPrice,markupUnitPrice,markupRate,taxMoney,qaChargeList);
        CfChargeHistory cfChargeHistory = installCfChargeHistory(cfCharge);
        // 统一记录费收费用、费用生成、费用历史
        ApplicationContextUtil.getContext().getBean(CfRdChargeScheduled.class).recordCfChargeAndChargeInAndHistory(cfCharge, delayChCharge, qaChargeList,cfChargeIn, cfChargeHistory,cfQcRecordDetailList);
        return true;
    }
    public Boolean checkDiff(CfRdRecordDetail cfRdRecordDetail,CfRdRecord cfRdRecord,CfPoDetailExtendVo cfPoDetailExtendVo){
        StringBuffer oneObjBuffer = new StringBuffer();
        if(!Objects.equals(cfRdRecord.getBrandId(),cfPoDetailExtendVo.getBrandId())){
            oneObjBuffer.append(String.format("'%s' 存在差异，入库通知的品牌ID：%s,采购单的品牌ID：%s\n", "品牌不一致",cfRdRecord.getBrandId(),cfPoDetailExtendVo.getBrandId()));
        }
        if(!Objects.equals(cfRdRecord.getVendorId(),cfPoDetailExtendVo.getVendorId())){
            oneObjBuffer.append(String.format("'%s' 存在差异，入库通知单的供应商ID：%s,采购单的供应商ID：%s\n", "供应商不一致",cfRdRecord.getVendorId(),cfPoDetailExtendVo.getVendorId()));
        }
        if(cfRdRecordDetail.getUnitPrice()==null||cfRdRecordDetail.getUnitPrice().compareTo(cfPoDetailExtendVo.getUnitPrice())!=0){
            oneObjBuffer.append(String.format("'%s' 存在差异，入库通知的不含税单价：%s,采购单的不含税单价：%s\n", "不含税单价",cfRdRecordDetail.getUnitPrice(),cfPoDetailExtendVo.getUnitPrice()));
        }
        if(cfRdRecordDetail.getTaxUnitPrice()==null||cfRdRecordDetail.getTaxUnitPrice().compareTo(cfPoDetailExtendVo.getTaxUnitPrice())!=0){
            oneObjBuffer.append(String.format("'%s' 存在差异，入库通知的含税单价：%s,采购单的含税单价：%s\n", "含税单价",cfRdRecordDetail.getTaxUnitPrice(),cfPoDetailExtendVo.getTaxUnitPrice()));
        }
        if(oneObjBuffer.length()>1){
            monitoringSchedule.sendMessage(String.format("%s:%s(%s) 存在差异：\n","入库单生成费用数据异常",cfRdRecord.getRdRecordCode(),cfRdRecordDetail.getRdRecordDetailId()),oneObjBuffer.toString());
        }
        return oneObjBuffer.length()<1;

    }


    /**
     * 生成延期扣款费用,配置的计费方案不能重叠
     *  4-7  4<= x  <=7
     * @return
     */
    public CfCharge creatCfChargeByRuleBillingData(CfChargeIn cfChargeIn,List<RuleBillingDetail> ruleBillingDetailList,CfPoDetailExtendVo cfPoDetailExtendVo, CfRdRecord rdRecord, CfRdRecordDetail recordDetail){
        CfPoHeader cfPoHeader = cfPoHeaderMapper.selectByPrimaryKey(recordDetail.getPoId());
        Assert.notNull(cfPoHeader, "入库通知单没匹配到对应的采购单");
        CfCharge delayChCharge = null;
        //是否有扣费方案和采购类型是否为1和2
        Boolean check= CollectionUtils.isNotEmpty(ruleBillingDetailList)&&(Objects.equals(cfPoHeader.getOrderType(), NumberEnum.ONE.getCode())||Objects.equals(cfPoHeader.getOrderType(), NumberEnum.TWO.getCode())||Objects.equals(cfPoHeader.getPoType(),NumberEnum.ONE.getCode()));
        if(check){
            // 判断入库单明细是否延期
            boolean isDelay = rdRecord.getCreateDate().isAfter(cfPoDetailExtendVo.getConEndDate());
            if (isDelay) {
                // 延期天数
                long daysNum = rdRecord.getCreateDate().toLocalDate().toEpochDay() - cfPoDetailExtendVo.getConEndDate().toLocalDate().toEpochDay();
                for (RuleBillingDetail ruleBillingDetail : ruleBillingDetailList) {
                    //判断采购单是否符合此计费方案
                    if (isDelay && CfFinanceConstant.RULE_TYPE_FOR_DELAY.equals(ruleBillingDetail.getRuleType())
                            && ruleBillingDetail.getGoodsRange().equals(cfPoHeader.getPoType())) {
                        String[] ruleLevel = ruleBillingDetail.getRuleLevelCondition().split("-");
                        //延期时间是不是在此计费方案的时间范围内
                        Boolean c=(ruleLevel.length==1&&daysNum>= Long.parseLong(ruleLevel[0]))||(ruleLevel.length==2&&daysNum>= Long.parseLong(ruleLevel[0])&&daysNum<= Long.parseLong(ruleLevel[1]));
                        if(c){
                            delayChCharge = installCfCharge(rdRecord, recordDetail, ruleBillingDetail);
                            if (Objects.nonNull(delayChCharge)) {
                                supplyCfChargeInRuleData(cfChargeIn, ruleBillingDetail, delayChCharge);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return delayChCharge;
    }

    /**
     * 组装质检扣费
     */
    public List<CfQcRecordDetail> creatQcChargeByRuleBillingData(CfRdRecord rdRecord, CfRdRecordDetail recordDetail,List<CfCharge> qaChargeList){
        List<CfQcRecordDetail> cfQcRecordDetailList=new ArrayList<>();
        List<CfQcRecord> cfQcRecords = cfQcRecordMapper.selectList(Wrappers.<CfQcRecord>lambdaQuery().eq(CfQcRecord::getRdRecordId, rdRecord.getRdRecordId()));
        if(CollectionUtils.isNotEmpty(cfQcRecords)){
            //总数据
            Integer quantity = recordDetail.getQuantity();
            List<RuleBillingDetailQcTask> ruleBillingDetailQcTasks = this.buildHashMapBean();
            CfQcRecord cfQcRecord = cfQcRecords.stream().findFirst().get();
            Integer qcTaskId = cfQcRecord.getQcTaskId();
            List<CfQcRecordDetail> cfQcRecordDetails = cfQcRecordDetailMapper.selectList(Wrappers.<CfQcRecordDetail>lambdaQuery().eq(CfQcRecordDetail::getQcTaskId, qcTaskId));
            a:for (CfQcRecordDetail cfQcRecordDetail:cfQcRecordDetails){
                StringBuffer errorMsg=new StringBuffer();
                RuleBillingDetailQcTask match = matchRule(cfQcRecord.getQcTaskCode(),cfQcRecordDetail, cfQcRecord.getBrandId(), ruleBillingDetailQcTasks,errorMsg);
                if (match == null) {
                    log.warn("没匹配到对应的扣费规则  : {}", cfQcRecordDetail);
                    monitoringSchedule.sendMessage("入库质检数据无扣费规则",errorMsg.toString());
                    break a;
                }
                Integer todoQty=0;
                if(quantity>=(cfQcRecordDetail.getQcChargingQty()-cfQcRecordDetail.getQcQty())){
                    todoQty=cfQcRecordDetail.getQcChargingQty()-cfQcRecordDetail.getQcQty();
                }else {
                    todoQty=quantity;
                }
                qaChargeList.add(this.installQcCfCharge(todoQty,recordDetail,match,cfQcRecordDetail,cfQcRecord));
                cfQcRecordDetail.setQcQty(cfQcRecordDetail.getQcQty()+todoQty);
                cfQcRecordDetailList.add(cfQcRecordDetail);
            }
        }
        return cfQcRecordDetailList;
    }


    /**
     * 组装费收费用
     *
     * @param ruleBillingDetail
     * @return
     */
    private CfCharge installCfCharge(CfRdRecord rdRecord, CfRdRecordDetail recordDetail, RuleBillingDetail ruleBillingDetail) {

        BigDecimal baseAmountPp = recordDetail.getTaxUnitPrice().multiply(new BigDecimal(recordDetail.getQuantity()));
        String res = pageInfoUtil.generateBusinessNum(BillNoConstantClassField.CRG);
        return CfCharge.builder()
                .chargeCode(res)
                // 1货品采购, 8延期费用
                .chargeType(ruleBillingDetail == null ? "1" : "8")
                // 状态自动创建的费用为已提交
                .checkStatus(2)
                .salesType(recordDetail.getSalesType()!=null?recordDetail.getSalesType():rdRecord.getRdRecordType()==1?2:null)
                // 应收/应付类型
                .arapType(ruleBillingDetail == null ? ChargeEnum.ARAP_TYPE_AP.getCode() : ChargeEnum.ARAP_TYPE_AR.getCode())
                // 货品采购
                .chargeSource(CfFinanceConstant.CHARGE_SOURCE_FOR_PO)
                .chargeSourceCode(rdRecord.getRdRecordCode())
                .chargeSourceDetailCode(recordDetail.getInventoryCode())
                .chargeSourceDetailId(recordDetail.getRdRecordDetailId())
                .brandId(rdRecord.getBrandId())
                .productCode(recordDetail.getProductCode())
                .currencyCode("RMB")
                .exchangeRate(new BigDecimal("1.0"))
                .chargeUnit("1")
                // @lizhejin 2020-11-11 计算延期费用时 要减掉溢短装  (质检 MlQuantity 默认为0)
                // @ wen.xiao 2021-06-30  溢短装也计算延期数量中去
                .chargeQty(recordDetail.getQuantity() )
                .pricePp(ruleBillingDetail == null ? recordDetail.getTaxUnitPrice() : recordDetail.getTaxUnitPrice().multiply(ruleBillingDetail.getDeductionRate()))
                // 总价
                .amountPp(ruleBillingDetail == null ? baseAmountPp :
                        recordDetail.getTaxUnitPrice().multiply(ruleBillingDetail.getDeductionRate())
                                .multiply(new BigDecimal(recordDetail.getQuantity())))
                // 结算主体
                .balance(rdRecord.getVendorCode())
                // 费用所属年月
                .chargeMonthBelongTo(LocalDate.now().format(DateTimeFormatter.ofPattern(CfFinanceConstant.FORMAT_YM)))
                .remark(ruleBillingDetail == null ? "" : "满足扣费类型：" + ruleBillingDetail.getRuleType() +
                        ", 级别条件：" + ruleBillingDetail.getRuleLevelCondition() +
                        ", 扣费比例：" + ruleBillingDetail.getDeductionRate() +
                        ", 扣费方案明细id：" + ruleBillingDetail.getRuleBillingDetailId())
                .chargeHistoryChecked(1)
                .chargeEdited(1)
                .taxRate(recordDetail.getTaxRate())
                .companyId(rdRecord.getCompanyId())
                .companyId(rdRecord.getCompanyId())
                .createBy(rdRecord.getCreateBy())
                .createName(rdRecord.getCreateName())
                .createDate(rdRecord.getCreateDate())
                .updateBy(rdRecord.getUpdateBy())
                .updateName(rdRecord.getUpdateName())
                .updateDate(rdRecord.getUpdateDate())
                .build();
    }

    /**
     * 组装费收费用
     *
     * @param rdDetail
     * @param cfRdRecord
     * @return
     */
    private CfCharge installCfCharge(CfRdRecordDetail rdDetail, CfRdRecord cfRdRecord) {
        String res = pageInfoUtil.generateBusinessNum(BillNoConstantClassField.CRG);
        return CfCharge.builder()
                .chargeCode(res)
                // 普通采购
                .chargeType(cfRdRecord.getBredVouch() == 1 ? CfFinanceConstant.CHARGE_SOURCE_FOR_PO : CfFinanceConstant.CHARGE_SOURCE_FOR_RD)
                // 状态自动创建的费用为已提交
                .checkStatus(2)
                // 应收
                .arapType(cfRdRecord.getBredVouch() == 1 ? ChargeEnum.ARAP_TYPE_AP.getCode() : ChargeEnum.ARAP_TYPE_AR.getCode())
                .salesType(rdDetail.getSalesType()!=null?rdDetail.getSalesType():cfRdRecord.getRdRecordType()==1?2:null)
                .taxRate(rdDetail.getTaxRate())
                // 普通采购
                .chargeSource(CfFinanceConstant.CHARGE_SOURCE_FOR_PO)
                .chargeSourceCode(cfRdRecord.getRdRecordCode())
                .chargeSourceDetailCode(rdDetail.getInventoryCode())
                .chargeSourceDetailId(rdDetail.getRdRecordDetailId().longValue())
                .brandId(cfRdRecord.getBrandId())
                .productCode(rdDetail.getProductCode())
                .currencyCode("RMB")
                .exchangeRate(new BigDecimal("1.0"))
                .chargeUnit("1")
                .chargeQty(rdDetail.getQuantity())
                .pricePp(rdDetail.getTaxUnitPrice())
                // 总价
                .amountPp(rdDetail.getTaxUnitPrice().multiply(new BigDecimal(rdDetail.getQuantity())))
                // 结算主体
                .balance(cfRdRecord.getVendorCode())
                // 财务期间
                .chargeMonthBelongTo(LocalDate.now().format(DateTimeFormatter.ofPattern(CfFinanceConstant.FORMAT_YM)))
                .remark("")
                .chargeHistoryChecked(1)
                .chargeEdited(1)
                .companyId(cfRdRecord.getCompanyId())
                .createBy(cfRdRecord.getCreateBy())
                .createName(cfRdRecord.getCreateName())
                .createDate(cfRdRecord.getCreateDate())
                .updateBy(cfRdRecord.getUpdateBy())
                .updateName(cfRdRecord.getUpdateName())
                .updateDate(cfRdRecord.getUpdateDate())
                .build();
    }

    private CfCharge installQcCfCharge(Integer qcChargingQty,CfRdRecordDetail cfRdRecordDetail,RuleBillingDetailQcTask rule, CfQcRecordDetail cfQcRecordDetail, CfQcRecord cfQcRecord) {
        String res = pageInfoUtil.generateBusinessNum(BillNoConstantClassField.CRG);

        return CfCharge.builder()
                .chargeCode(res)
                .chargeType(String.valueOf(NumberEnum.NINE.getCode()))
                .checkStatus(2)
                .arapType(ChargeEnum.ARAP_TYPE_AR.getCode())
                .salesType(cfQcRecordDetail.getSalesType())
                .chargeSource(CfFinanceConstant.CHARGE_SOURCE_FOR_PO)
                .chargeSourceCode(cfQcRecord.getQcTaskCode())
                .chargeSourceDetailCode(cfQcRecordDetail.getInventoryCode())
                .chargeSourceDetailId(cfQcRecordDetail.getQcChargingId())
                .brandId(cfQcRecord.getBrandId().longValue())
                .productCode(cfQcRecordDetail.getProductCode())
                .currencyCode("RMB")
                .exchangeRate(new BigDecimal("1.0"))
                .chargeUnit("1")
                .chargeQty(qcChargingQty)
                .pricePp(rule.getDeductionAmount())
                // 总价
                .amountPp(rule.getDeductionAmount().multiply(new BigDecimal(qcChargingQty)))
                // 结算主体
                .balance(cfQcRecord.getVendorCode())
                // 费用所属年月
                .chargeMonthBelongTo(LocalDate.now().format(DateTimeFormatter.ofPattern(CfFinanceConstant.FORMAT_YM)))
                .remark("满足扣费类型：" + rule.getRuleType() +
                        ", 级别条件：" + rule.getRuleLevelCondition() +
                        ", 扣费金额：" + rule.getDeductionAmount() +
                        ", 扣费方案明细id：" + rule.getRuleBillingDetailId())
                .chargeHistoryChecked(1)
                .chargeEdited(1)
                .companyId(1L)
                .createBy(cfQcRecord.getCreateBy())
                .createName(cfQcRecord.getCreateName())
                .createDate(cfQcRecord.getCreateDate())
                .updateBy(cfQcRecord.getCreateBy())
                .updateName(cfQcRecord.getCreateName())
                .updateDate(cfQcRecord.getCreateDate())
                .taxRate(cfRdRecordDetail.getTaxRate())
                .build();
    }

    /**
     * 组装费用生成记录
     *
     * @param rdDetail
     * @param cfRdRecord
     * @return
     */
    private void installCfChargeIn(Boolean isTc,CfChargeIn cfChargeIn,CfRdRecordDetail rdDetail, CfRdRecord cfRdRecord,
                                   BigDecimal unitPrice, BigDecimal markupUnitPrice, BigDecimal markupRate,BigDecimal taxMoney,List<CfCharge> qaChargeList) {

        if (!isTc) {
            cfChargeIn.setArrivalQty(rdDetail.getArrivalQty());
            cfChargeIn.setRejectionQty(rdDetail.getRejectionQty());
            cfChargeIn.setActualQty(rdDetail.getActualQty());
            cfChargeIn.setDefectiveRejectionQty(0);
        } else {
            cfChargeIn.setArrivalQty(0);
            cfChargeIn.setRejectionQty(0);
            cfChargeIn.setActualQty(0);
            cfChargeIn.setDefectiveRejectionQty(rdDetail.getQuantity());
        }
        cfChargeIn.setUnitPrice(unitPrice);
        cfChargeIn.setMarkupRate(markupRate);
        cfChargeIn.setMarkupUnitPrice(markupUnitPrice);
        cfChargeIn.setTaxMoney(taxMoney);
        cfChargeIn.setBrandId(cfRdRecord.getBrandId());
        cfChargeIn.setVendorId(cfRdRecord.getVendorId());
        cfChargeIn.setInventoryCode(rdDetail.getInventoryCode());
        cfChargeIn.setProductCode(rdDetail.getProductCode());
        cfChargeIn.setGoodsName(rdDetail.getInventoryName());
        cfChargeIn.setSalesType(rdDetail.getSalesType()!=null?rdDetail.getSalesType():cfRdRecord.getRdRecordType()==1?2:null);
        cfChargeIn.setCostsPrice(rdDetail.getTaxUnitPrice());
        cfChargeIn.setTaxRate(rdDetail.getTaxRate());
        cfChargeIn.setCompanyId(cfRdRecord.getCompanyId());
        cfChargeIn.setCreateBy(cfRdRecord.getCreateBy());
        cfChargeIn.setCreateName(cfRdRecord.getCreateName());
        cfChargeIn.setCreateDate(cfRdRecord.getCreateDate());
        cfChargeIn.setUpdateBy(cfRdRecord.getUpdateBy());
        cfChargeIn.setUpdateName(cfRdRecord.getUpdateName());
        cfChargeIn.setUpdateDate(cfRdRecord.getUpdateDate());
        // 预付款预付金额(采购单里未结算 尾款+定金)
        cfChargeIn.setAdvancepayAmount(BigDecimal.ZERO);
        // 预付款实付金额（预付款申请单 已打款金额和）
        cfChargeIn.setAdvancepayAmountActual(BigDecimal.ZERO);
        cfChargeIn.setQaDeductions(qaChargeList.stream().map(x->x.getAmountPp()).reduce(BigDecimal.ZERO,BigDecimal::add));
    }

    /**
     * 补充费用生成记录计费数据
     *
     * @param cfChargeIn
     * @param ruleBillingDetail
     * @param delayChCharge
     */
    private void supplyCfChargeInRuleData(CfChargeIn cfChargeIn, RuleBillingDetail ruleBillingDetail, CfCharge delayChCharge) {
        JSONObject json = new JSONObject();
        json.put(ruleBillingDetail.getRuleLevelCondition(), delayChCharge.getChargeQty());
        cfChargeIn.setRuleBillingId(ruleBillingDetail.getRuleBillingId());
        // 延期明细
        cfChargeIn.setPostponeDetail(json.toString());
        // 延期扣款总金额
        cfChargeIn.setPostponeDeductionsTotal(delayChCharge.getAmountPp());
        cfChargeIn.setRemark(delayChCharge.getRemark());
    }

    /**
     * 组装费用修改历史
     *
     * @param cfCharge
     * @return
     */
    private CfChargeHistory installCfChargeHistory(CfCharge cfCharge) {
        CfChargeHistory cfChargeHistory = CfChargeHistory.builder().build();
        BeanUtils.copyProperties(cfCharge, cfChargeHistory);
        return cfChargeHistory;
    }
    /**
     * 记录费收费用、费用生成、费用历史
     *
     * @param cfCharge
     * @param cfChargeIn
     * @param cfChargeIn
     */
    @Transactional(rollbackFor = Exception.class)
    public void recordCfChargeAndChargeInAndHistory(CfCharge cfCharge, CfCharge delayChCharge,List<CfCharge> qaChargeList, CfChargeIn cfChargeIn, CfChargeHistory cfChargeHistory,List<CfQcRecordDetail> cfQcRecordDetailList) {
        //插入基本费用
        cfChargeMapper.insert(cfCharge);
        // 插入费用生成记录
        cfChargeIn.setChargeId(cfCharge.getChargeId());
        cfChargeInMapper.insert(cfChargeIn);
        // 插入费用修改历史
        cfChargeHistoryMapper.insert(cfChargeHistory);
        if (delayChCharge != null) {
            // 插入延期费用
            cfChargeMapper.insert(delayChCharge);
        }
        for (CfCharge qaCfCharge: qaChargeList) {
            cfChargeMapper.insert(qaCfCharge);
            CfQcRecordAsnDetail cfQcRecordAsnDetail=new CfQcRecordAsnDetail();
            cfQcRecordAsnDetail.setQcChargingId(qaCfCharge.getChargeSourceDetailId());
            cfQcRecordAsnDetail.setRdRecordDetailId(cfCharge.getChargeSourceDetailId());
            cfQcRecordAsnDetail.setQcQty(qaCfCharge.getChargeQty());
            cfQcRecordAsnDetailMapper.insert(cfQcRecordAsnDetail);
        }
        for (CfQcRecordDetail cfQcRecordDetail : cfQcRecordDetailList) {
            cfQcRecordDetailMapper.updateById(cfQcRecordDetail);
        }
    }
    /**
     * 查询退次单明细集合
     *
     * @param rdRecordIds
     * @return
     */
    private List<CfRdRecordDetail> getCfRdDetailData(List<Long> rdRecordIds) {
        return cfRdRecordDetailMapper.selectList(
                Wrappers.<CfRdRecordDetail>lambdaQuery().in(CfRdRecordDetail::getRdRecordId, rdRecordIds).eq(CfRdRecordDetail::getCreateChargeFlag, false)
        );
    }

    private List<CfRdRecordDetail> getCfRdDetailZeroData() {
        return cfRdRecordDetailMapper.selectList(Wrappers.<CfRdRecordDetail>lambdaQuery().eq(CfRdRecordDetail::getCreateChargeFlag, false)
        );
    }

    /**
     * 查询有效计费方案明细
     *
     * @return
     */
    public List<RuleBillingDetail> queryValidCfWdtRdRuleBilling() {
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
                    .eq(RuleBillingDetail::getIsDelete, 0)
            );
        }
        return ruleBillingDetailList;
    }

    /**
     * 讲对应的数据转换成对应的唯一的计费方案
     * @param qcTaskCode
     * @param detail
     * @param brandId
     * @param actRules
     * @return
     */
    public RuleBillingDetailQcTask matchRule(String qcTaskCode,CfQcRecordDetail detail, Integer brandId, List<RuleBillingDetailQcTask> actRules,StringBuffer errorMsg) {

        List<RuleBillingDetailQcTask> matchBrands = actRules.stream().filter((a) -> a.getBrandIdsMap().containsKey(brandId + "")).collect(Collectors.toList());
        if (org.springframework.util.CollectionUtils.isEmpty(matchBrands)) {
            log.info("根据品牌:{} 没有匹配的计费方案:{}",brandId,actRules);
            errorMsg.append(StringUtils.format("根据品牌:%s 没有匹配的计费方案",brandId)) ;
            return null;
        }
        List<RuleBillingDetailQcTask> matchTypes = matchBrands.stream().filter((a) -> a.getQcDeductionType().equals(detail.getQcChargingType() )).collect(Collectors.toList());
        if (org.springframework.util.CollectionUtils.isEmpty(matchTypes)) {
            log.info("根据质检项目:{} 没有匹配的计费方案:{}",detail.getQcChargingType(),matchBrands);
            errorMsg.append(StringUtils.format("根据质检项目:%s 没有匹配的计费方案",detail.getQcChargingType()));
            return null;
        }
        List<RuleBillingDetailQcTask> matchMiddles = matchTypes.stream().filter((a) -> a.getMiddleClassIdsMap().containsKey(detail.getMiddleClassId()+"")).collect(Collectors.toList());
        if (org.springframework.util.CollectionUtils.isEmpty(matchMiddles)) {
            log.info("根据中类:{} 没有匹配的计费方案:{}",detail.getMiddleClassId(),matchTypes);
            errorMsg.append(StringUtils.format("根据中类:%s 没有匹配的计费方案",detail.getMiddleClassId()));
            return null;
        }
        List<RuleBillingDetailQcTask> matchSeason = matchMiddles.stream().filter((a) -> a.getSeasonMap().containsKey(detail.getSeason())).collect(Collectors.toList());
        if (org.springframework.util.CollectionUtils.isEmpty(matchSeason)) {
            log.info("根据季节:{} 没有匹配的计费方案:{}",detail.getSeason(),matchMiddles);
            errorMsg.append(StringUtils.format("根据季节:%s 没有匹配的计费方案",detail.getSeason()));
            return null;
        }
        if(matchSeason.size()>1){
            log.info("获取到多个可用计费方案，不符合系统原设计逻辑，暂不生成费用， 通知相关人员，待修复后在生成");
            errorMsg.append("获取到多个可用计费方案，不符合系统原设计逻辑，暂不生成费用， 通知相关人员，待修复后在生成");
            List<Long> collect = matchSeason.stream().map(RuleBillingDetailQcTask::getRuleBillingDetailId).collect(Collectors.toList());
            monitoringSchedule.sendMessage("告警：质检扣费匹配到多条计费方案：",String.format("质检单：%s(%s),匹配到的计费方案条数：%s,涉及的计费方案：%s",qcTaskCode,detail.getQcChargingId(),matchSeason.size(),collect));
            return null;
        }
        return matchSeason.get(0);
    }

    /**
     * 将计费方案生成可方便匹配的数据
     * @return
     */
    public List<RuleBillingDetailQcTask> buildHashMapBean() {
        List<RuleBillingDetailQcTask> list = new ArrayList<>();
        List<RuleBillingDetail> ruleBillingDetails = ruleBillingHeaderService.queryRuleBilling(String.valueOf(NumberEnum.TWO.getCode()));
        if(org.springframework.util.CollectionUtils.isEmpty(ruleBillingDetails)){
            log.info("当前无可用的质检扣费方案");
            return list;
        }

        for (RuleBillingDetail ruleBillingDetail : ruleBillingDetails) {
            RuleBillingDetailQcTask t = new RuleBillingDetailQcTask();
            BeanUtils.copyProperties(ruleBillingDetail, t);
            list.add(t);
            t.setBrandIdsMap(createMapByIds(t.getBrandIds()));
            t.setSeasonMap(createMapByIds(t.getSeason()));
            t.setMiddleClassIdsMap(createMapByIds(t.getMiddleClassIds()));
            // 替换中文名
            Map<String, String> middleClassIdsMap = t.getMiddleClassIdsMap();
            String str = "1_大衣、2_羽绒、3_棉衣、4_皮草、5_风衣、6_其他";
            String[] split = str.split("、");
            for (String s : split) {
                String[] split1 = s.split("_");
                String s1 = split1[0];
                String s2 = split1[1];
                if (!middleClassIdsMap.containsKey(s1)) {
                    middleClassIdsMap.put(s1, s2);
                }
            }
        }
        return list;
    }
    /**
     * 对字符串（eg 1,2,3,4）切割为[{1,1},{2,2},{3,3}]
     * @param ids
     * @return
     */
    private Map<String, String> createMapByIds(String ids) {
        Map<String, String> map = new HashMap<>(8);
        if (org.springframework.util.StringUtils.isEmpty(ids)) {
            return map;
        }
        String[] split = ids.split(",");
        for (String s : split) {
            map.put(s, s);
        }
        return map;
    }


}
