package com.chenfan.finance.scheduled;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.finance.dao.*;
import com.chenfan.finance.enums.NumberEnum;
import com.chenfan.finance.enums.TocMappingTypeEnum;

import com.chenfan.finance.model.bo.*;
import com.chenfan.finance.scheduled.bo.TocCacheBo;
import com.chenfan.finance.scheduled.bo.TocOriginBo;

import com.chenfan.finance.utils.BatchInsertUtil;
import com.chenfan.finance.utils.TimeFormatUtil;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Wen.Xiao
 * @Description // TOC 出库mapping
 * @Date 2021/4/7  10:06
 * @Version 1.0
 */
@Slf4j
@Component
@RefreshScope
public class TocOriginBatchStockOutMappingScheduled {

    @Resource
    private TocAlipayOriginMapper tocAlipayOriginMapper;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private TocApiTradeMapper tocApiTradeMapper;
    @Resource
    private TocApiTradeOrderMapper tocApiTradeOrderMapper;
    @Resource
    private TocApiRefundMapper tocApiRefundMapper;
    @Resource
    private TocSalesTradeOrderMapper tocSalesTradeOrderMapper;
    @Resource
    private TocSalesTradeMapper tocSalesTradeMapper;
    @Resource
    private TocStockoutOrderMapper tocStockoutOrderMapper;
    @Resource
    private TocStockoutOrderDetailMapper tocStockoutOrderDetailMapper;
    @Resource
    private TocIncomeOrderMapper tocIncomeOrderMapper;
    @Resource
    private TocExpendOrderMapper tocExpendOrderMapper;


    /**
     * 退款查询支付宝流水的sql
     */
    @Value("${toc.refund.sqlString: remark like '%退款%' or remark like '%TBBAIL%'   or remark like '%退差价%'  or remark like '%已扣减红包%'   or remark like '%花呗-买A就返A售中%'  or remark like '%商家权益红包-红包退回%' or remark like '%花呗-A买就返A用户%' or remark like '%售后支付%'   or opposite_account like '%集分宝南京企业%'  or goods_name like '%服务费(内容创作者/服务机构)退款%' }")
    private String sqlString;

    /**
     * 权益金额比例
     */
    @Value("${toc.equityRatio:0.9524}")
    private BigDecimal equityRatio;

    @Value("${toc.poorAmount:0.01}")
    private BigDecimal poorAmount;
    @Value("${toc.poorSecond:3}")
    private Integer poorSecond;

    @Async
    public  void mappingBatchEntrance(){
        this.mappingBatchEntrance("上传任务触发mapping");
    }
    public ReturnT<String> mappingBatchEntrance(String param){
        RLock interLock = redissonClient.getLock("C69EE00C169C50DA0C82A0EC6BEE6299");
        if(interLock.tryLock()){
            try {
                LocalDateTime minTime = LocalDateTime.of(LocalDate.of(2020,05,01),  LocalTime.MIN);
                this.entranceOfMappingIncome(minTime);
                this.entranceOfMappingExpend(minTime);
            }catch (Throwable throwable){
                log.error("数据mapping逻辑异常",throwable);
            }finally {
                interLock.unlock();
            }
        }
        log.info("执行完成");
        return ReturnT.SUCCESS;
    }

    /**
     * 收入数据处理入口
     * @param minTime
     */
    private void entranceOfMappingIncome(LocalDateTime minTime){
        for (int i=0;i<1;i++){
            int size=1000;
            int count=0;
            long total=0;
        //匹配失败的流水
            List<TocAlipayOrigin> totalFailOrigin =new ArrayList<>();
            List<TocAlipayOrigin> successTocOrigins=new ArrayList<>();
        //匹配成功的流水
            while (size!=0){
                List<TocOriginBo> tocOriginBoList=new ArrayList<>();
                List<String> failFinanceNos = totalFailOrigin.parallelStream().map(x -> x.getFinanceNo()).collect(Collectors.toList());
                if(CollectionUtils.isEmpty(failFinanceNos)){
                    failFinanceNos.add("-1");
                }

                List<TocAlipayOrigin> allIncomeAmounts =new ArrayList<>();
                if(i==0){
                    allIncomeAmounts = tocAlipayOriginMapper.selectList(Wrappers.<TocAlipayOrigin>lambdaQuery()
                            .gt(TocAlipayOrigin::getAccountDate, minTime)
                            .likeRight(TocAlipayOrigin::getTid,"T200P")
                            .notIn(TocAlipayOrigin::getFinanceNo,failFinanceNos)
                            .in(TocAlipayOrigin::getCheckFlag,0,9)
                            .gt(TocAlipayOrigin::getIncomeAmount,BigDecimal.ZERO)
                            .groupBy(TocAlipayOrigin::getTid)
                            .last("limit 1000")
                    );
                   // allIncomeAmounts=tocAlipayOriginMapper.selectListByBeanKey("326679744058711");
                }else {
                    List<String> failTids = totalFailOrigin.parallelStream().map(x -> x.getTid()).collect(Collectors.toList());
                    if(CollectionUtils.isEmpty(failTids)){
                        failTids.add("-1");
                    }
                    allIncomeAmounts=tocAlipayOriginMapper.selectListForGroup(failTids);
                }
                size=allIncomeAmounts.size();
                log.info("已经处理流水：{}，本次待处理流水数量：{}",total,size);
                total=total+size;
                if(CollectionUtils.isNotEmpty(allIncomeAmounts)){
                    this.logicOfMappingIncome(allIncomeAmounts,tocOriginBoList);
                    totalFailOrigin.addAll(allIncomeAmounts);
                    //TODO 生成相关数据并插入DB
                    this.buildIncomeAndInsertDb(tocOriginBoList,successTocOrigins);
                    this.insertDataToDB(i==1,successTocOrigins,null);
                    successTocOrigins.clear();
                }
            }
            this.insertDataToDB(false,null,totalFailOrigin);
        }

    }


    private void entranceOfMappingExpend(LocalDateTime minTime){
        int size=1000;
        int count=0;
        int total=0;
        //匹配失败的流水
        List<TocAlipayOrigin> totalFailOrigin =new ArrayList<>();
        //匹配成功的流水
        List<TocAlipayOrigin> successTocOrigins=new ArrayList<>();
        while (size!=0){
            List<TocOriginBo> tocOriginBoList=new ArrayList<>();
            count++;
            List<String> failFinanceNos = totalFailOrigin.parallelStream().map(x -> x.getFinanceNo()).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(failFinanceNos)){
                failFinanceNos.add("-1");
            }

            List<TocAlipayOrigin> allExpendAmounts = tocAlipayOriginMapper.selectListOfExpend(failFinanceNos,sqlString);
            //List<TocAlipayOrigin> allExpendAmounts=tocAlipayOriginMapper.selectListByBeanKey("326038280636540");
            size=allExpendAmounts.size();
            ArrayList<TocAlipayOrigin> objects = new ArrayList<>();
            aaa:for (Iterator<TocAlipayOrigin> it = allExpendAmounts.iterator(); it.hasNext();){
                TocAlipayOrigin next = it.next();
                String tidForOrigin = getTidForOrigin(next, false);
                if(tidForOrigin!=null){
                    objects.add(next);

                }else {
                    totalFailOrigin.add(next);
                }
            }
            while (CollectionUtils.isNotEmpty(objects)){
                HashMap<String, TocAlipayOrigin> objectObjectHashMap = new HashMap<>();
                a:for (Iterator<TocAlipayOrigin> it = objects.iterator(); it.hasNext();){
                    TocAlipayOrigin next = it.next();
                    String tidForOrigin = getTidForOrigin(next, false);
                    if(!objectObjectHashMap.containsKey( tidForOrigin)){
                        objectObjectHashMap.put(tidForOrigin,next);
                        it.remove();
                    }
                }

                log.info("支出：已经处理流水：{}，正在处理第 {} 次收入流水，本次流水数量：{}",total,count,size);
                total=total+size;
                if(CollectionUtils.isNotEmpty(objectObjectHashMap)){
                    List<TocAlipayOrigin> collect = objectObjectHashMap.values().stream().collect(Collectors.toList());
                    this.logicOfMappingExpend(tocOriginBoList,collect);
                    //本次匹配失败
                    totalFailOrigin.addAll(collect);
                    this.buildExpendAndInsertDb(tocOriginBoList,successTocOrigins);
                    this.insertDataToDB(false,successTocOrigins,null);
                    successTocOrigins.clear();
                    tocOriginBoList.clear();
                }
            }
        }
        this.insertDataToDB(false,null,totalFailOrigin);

    }
    /**
     * 处理mapping逻辑
     * @param allIncomeAmounts
     * @param tocOriginBoList
     */
    private void logicOfMappingIncome(List<TocAlipayOrigin> allIncomeAmounts, List<TocOriginBo> tocOriginBoList){
        Set<String> tids = allIncomeAmounts.parallelStream().map(x -> getTidForOrigin(x, true)).collect(Collectors.toSet());
        if(CollectionUtils.isNotEmpty(tids)){
            List<TocCacheBo> tocCacheBoList = this.generatingAssociationRelationshipsForTradeOrderByTids(tids);

            Map<String, List<TocCacheBo>> tocTradeGroupMapOfTid = tocCacheBoList.stream().collect(Collectors.groupingBy(x -> x.getTocApiTrade().getTid()));
            List<String> usedOidByTids = tocIncomeOrderMapper.getUsedOidByTids(tids).stream().map(TocIncomeOrder::getOid).collect(Collectors.toList());
            List<TocIncomeOrder> tocIncomeOrderList = tocIncomeOrderMapper.checkPostUsedByTids(tids);
            Map<String, List<TocIncomeOrder>> usedPostByTidCache = tocIncomeOrderList.stream().collect(Collectors.groupingBy(TocIncomeOrder::getTid));
            a:for (Iterator<TocAlipayOrigin> it = allIncomeAmounts.iterator(); it.hasNext();){
                TocAlipayOrigin tocAlipayOrigin = it.next();
                String tid = getTidForOrigin(tocAlipayOrigin, true);

                if(tocTradeGroupMapOfTid.containsKey(tid)){
                    BigDecimal incomeAmount = tocAlipayOrigin.getIncomeAmount();
                    TocCacheBo tocCacheBo = tocTradeGroupMapOfTid.get(tid).get(NumberEnum.ZERO.getCode());
                    TocApiTrade tocApiTrade = tocCacheBo.getTocApiTrade();
                    List<TocCacheBo.TocDataCacheOfTrader> tocDataCacheOfTraderList = tocCacheBo.getTocDataCacheOfTraderList();
                   //TODO 此处按照时间分组当前
                    //总邮费
                    BigDecimal postAmount = usedPostByTidCache.containsKey(tid)?BigDecimal.ZERO:tocApiTrade.getPostAmount();
                    b:for (int i = 1; i < 13; i++) {
                        List<TocCacheBo.TocDataCacheOfTrader> tocDataCacheOfTraders = this.groupByTime(usedOidByTids,tocAlipayOrigin, tocDataCacheOfTraderList,i);
                        if(CollectionUtils.isEmpty(tocDataCacheOfTraders)){
                            continue b;
                        }
                        ccb:for (TocCacheBo.TocDataCacheOfTrader tocDataCacheOfTrader:tocDataCacheOfTraders) {
                            TocApiTradeOrder tocApiTradeOrder = tocDataCacheOfTrader.getTocApiTradeOrder();
                            TocApiRefund tocApiRefund = tocDataCacheOfTrader.getTocApiRefund();
                            Boolean isOther=true;
                            BigDecimal num = tocApiTradeOrder.getNum();
                            if(tocApiRefund==null){
                                continue ccb;
                            }
                            if(tocApiRefund.getActualRefundAmount().compareTo(tocApiTradeOrder.getShareAmount())==0){
                                tocApiRefund.setNum(num.intValue());
                                isOther=false;
                            }else {
                                tocApiRefund.setNum(0);
                                //   1：退款金额=单价* 退款数量
                                //单价
                                BigDecimal price_pp = tocApiTradeOrder.getShareAmount().divide(num, 2, RoundingMode.HALF_UP);
                                //计算距离退款金额最近的数量
                                rr:for (int r = 1; r<= num.intValue(); r++) {
                                    BigDecimal multiply = price_pp.multiply(new BigDecimal(i)).add(tocApiRefund.getActualRefundAmount());
                                    if(multiply.compareTo(new BigDecimal(0.1))<1&&multiply.compareTo(new BigDecimal(-0.1))>0){
                                        log.debug("总数量：{}，单价：{}，退款金额：{}，退款数量：{}",num,price_pp,tocApiRefund.getActualRefundAmount(),i);
                                        tocApiRefund.setNum(r);
                                        isOther=false;
                                        continue rr;
                                    }
                                }
                            }
                            tocApiRefund.setIsOther(isOther);

                        }
                        //子单总金额
                        BigDecimal totalShareAmount = tocDataCacheOfTraders.parallelStream().map(x -> x.getTocApiTradeOrder().getShareAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        //总退款
                        BigDecimal totalRefundAmount = tocDataCacheOfTraders.parallelStream().filter(x->x.getTocApiRefund()!=null).map(x -> x.getTocApiRefund().getActualRefundAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);

                        if(totalRefundAmount!=null&&BigDecimal.ZERO.compareTo(totalRefundAmount)<0){
                            //包含退款
                            if(totalShareAmount.compareTo(incomeAmount)==0){
                                //不包含退款 收入流水=子单金额     INCOME_SHARE_AMOUNT(1101,"INCOME_SHARE_AMOUNT","收入流水=子单金额 "),
                                tocOriginBoList.add(TocOriginBo.builder()
                                        .tocCacheBo(
                                                TocCacheBo.builder()
                                                        .tocApiTrade(tocApiTrade)
                                                        .tocDataCacheOfTraderList(tocDataCacheOfTraders)
                                                        .build())
                                        .tocAlipayOrigin(tocAlipayOrigin)
                                        .tocMappingTypeEnum(TocMappingTypeEnum.INCOME_SHARE_AMOUNT)
                                        .postAmount(postAmount)
                                        .build());
                                it.remove();
                                continue a;
                            }else if(incomeAmount.compareTo(totalShareAmount.add(postAmount))==0){
                                //不包含退款 收入流水=子单金额+收入总邮费    INCOME_SHARE_AMOUNT_POST(1102,"INCOME_SHARE_AMOUNT_POST","收入流水=子单金额+收入总邮费"),
                                tocOriginBoList.add(TocOriginBo.builder()
                                        .tocCacheBo(
                                                TocCacheBo.builder()
                                                        .tocApiTrade(tocApiTrade)
                                                        .tocDataCacheOfTraderList(tocDataCacheOfTraders)
                                                        .build())
                                        .tocAlipayOrigin(tocAlipayOrigin)
                                        .tocMappingTypeEnum(TocMappingTypeEnum.INCOME_SHARE_AMOUNT_POST)
                                        .postAmount(postAmount)
                                        .build());
                                it.remove();
                                continue a;
                            }else if(incomeAmount.compareTo(totalShareAmount.subtract(totalRefundAmount))==0){
                                //收入流水=子单总金额-对应子单退款金额 ||  |收入流水-（子单总金额-对应子单退款金额）| <=0.01
                                tocOriginBoList.add(TocOriginBo.builder()
                                        .tocCacheBo(
                                                TocCacheBo.builder()
                                                        .tocApiTrade(tocApiTrade)
                                                        .tocDataCacheOfTraderList(tocDataCacheOfTraders)
                                                        .build())
                                        .tocAlipayOrigin(tocAlipayOrigin)
                                        .tocMappingTypeEnum(TocMappingTypeEnum.INCOME_SHARE_AMOUNT_REFUND)
                                        .postAmount(postAmount)
                                        .build());
                                it.remove();
                                continue a;
                            }else if(incomeAmount.compareTo(totalShareAmount.add(postAmount).subtract(totalRefundAmount))==0){
                                //收入流水=子单总金额-对应子单退款金额+总邮费 || |收入流水-（子单总金额-对应子单退款金额+总邮费）| <=0.01
                                tocOriginBoList.add(TocOriginBo.builder()
                                        .tocCacheBo(
                                                TocCacheBo.builder()
                                                        .tocApiTrade(tocApiTrade)
                                                        .tocDataCacheOfTraderList(tocDataCacheOfTraders)
                                                        .build())
                                        .tocAlipayOrigin(tocAlipayOrigin)
                                        .tocMappingTypeEnum(TocMappingTypeEnum.INCOME_SHARE_AMOUNT_REFUND_POST)
                                        .postAmount(postAmount)
                                        .build());
                                it.remove();
                                continue a;
                            }

                        }else {
                            /**
                             * 当前子单无对应的退款
                             */
                            if(totalShareAmount.compareTo(incomeAmount)==0){
                                //不包含退款 收入流水=子单金额   INCOME_SHARE_AMOUNT(1101,"INCOME_SHARE_AMOUNT","收入流水=子单金额 "),
                                tocOriginBoList.add(TocOriginBo.builder()
                                        .tocCacheBo(
                                                TocCacheBo.builder()
                                                        .tocApiTrade(tocApiTrade)
                                                        .tocDataCacheOfTraderList(tocDataCacheOfTraders)
                                                        .build())
                                        .tocAlipayOrigin(tocAlipayOrigin)
                                        .tocMappingTypeEnum(TocMappingTypeEnum.INCOME_SHARE_AMOUNT)
                                        .postAmount(postAmount)
                                        .build());
                                it.remove();
                                continue a;
                            }else if(incomeAmount.compareTo(totalShareAmount.add(postAmount))==0){
                                //不包含退款 收入流水=子单金额+收入总邮费    INCOME_SHARE_AMOUNT_POST(1102,"INCOME_SHARE_AMOUNT_POST","收入流水=子单金额+收入总邮费"),
                                tocOriginBoList.add(TocOriginBo.builder()
                                        .tocCacheBo(
                                                TocCacheBo.builder()
                                                        .tocApiTrade(tocApiTrade)
                                                        .tocDataCacheOfTraderList(tocDataCacheOfTraders)
                                                        .build())
                                        .tocAlipayOrigin(tocAlipayOrigin)
                                        .tocMappingTypeEnum(TocMappingTypeEnum.INCOME_SHARE_AMOUNT_POST)
                                        .postAmount(postAmount)
                                        .build());
                                it.remove();
                                continue a;
                            }

                        }
                        if(i==12){
                            //都匹配不上，那么就是采用 总金额*（1-权益金比例）=收入金额
                            BigDecimal poorAmountOfAmount = totalShareAmount.multiply(equityRatio).subtract(incomeAmount);
                            BigDecimal poorAmountOfAmountAndPost = totalShareAmount.multiply(equityRatio).add(postAmount).subtract(incomeAmount);
                            //0.005<0.01&& 0-(-0.005)<0.1
                            if(poorAmountOfAmount.compareTo(poorAmount)<0&&BigDecimal.ZERO.subtract(poorAmountOfAmount).compareTo(poorAmount)<0){
                                // |收入流水-子单金额* （1-权益金比例）|<0.01    INCOME_SHARE_AMOUNT_EQUITY(1501,"INCOME_SHARE_AMOUNT_EQUITY","子单总金额*（1-权益金比例） =收入流水金额1501"),
                                tocOriginBoList.add(TocOriginBo.builder()
                                        .tocCacheBo(
                                                TocCacheBo.builder()
                                                        .tocApiTrade(tocApiTrade)
                                                        .tocDataCacheOfTraderList(tocDataCacheOfTraders)
                                                        .build())
                                        .tocAlipayOrigin(tocAlipayOrigin)
                                        .tocMappingTypeEnum(TocMappingTypeEnum.INCOME_SHARE_AMOUNT_EQUITY)
                                        .postAmount(postAmount)
                                        .build());
                                it.remove();
                                continue a;
                            }else if(poorAmountOfAmountAndPost.compareTo(poorAmount)<0&&BigDecimal.ZERO.subtract(poorAmountOfAmountAndPost).compareTo(poorAmount)<0){
                                // |收入流水 - （ 子单金额* （1-权益金比例）+总邮费）|<0.01    INCOME_SHARE_AMOUNT_EQUITY_POST(1511,"INCOME_SHARE_AMOUNT_EQUITY_POST","子单总金额*（1-权益金比例） +全部邮费=收入流水金额1502"),
                                tocOriginBoList.add(TocOriginBo.builder()
                                        .tocCacheBo(
                                                TocCacheBo.builder()
                                                .tocApiTrade(tocApiTrade)
                                                .tocDataCacheOfTraderList(tocDataCacheOfTraders)
                                                .build())
                                        .tocAlipayOrigin(tocAlipayOrigin)
                                        .tocMappingTypeEnum(TocMappingTypeEnum.INCOME_SHARE_AMOUNT_EQUITY_POST)
                                        .postAmount(postAmount)
                                        .build());
                                it.remove();
                                continue a;
                            }
                        }


                    }

                }
            }

        }

    }


    private void logicOfMappingExpend(List<TocOriginBo> tocOriginBoList, List<TocAlipayOrigin> allIncomeAmounts){
        Set<String> tids = allIncomeAmounts.parallelStream().map(x -> getTidForOrigin(x, false)).collect(Collectors.toSet());
        if(CollectionUtils.isNotEmpty(tids)){
            //获取关联关系
            List<TocCacheBo> tocCacheBoList = this.generatingAssociationRelationshipsForTradeOrderByTids(tids);
            List<String> refundNoOfUsedByTids = tocIncomeOrderMapper.getRefundNoOfUsedByTids(tids,Arrays.asList(
                    TocMappingTypeEnum.INCOME_SHARE_AMOUNT_REFUND.getNo(),
                    TocMappingTypeEnum.INCOME_SHARE_AMOUNT_REFUND_OTHER.getNo(),
                    TocMappingTypeEnum.INCOME_SHARE_AMOUNT_REFUND_POST.getNo(),
                    TocMappingTypeEnum.INCOME_SHARE_AMOUNT_REFUND_POST_OTHER.getNo(),
                    TocMappingTypeEnum.INCOME_SHARE_AMOUNT_EQUITY_REFUND.getNo(),
                    TocMappingTypeEnum.INCOME_SHARE_AMOUNT_EQUITY_POST_REFUND.getNo()
            ));
            List<String> refundNoOfUsedByTids1 = tocExpendOrderMapper.getRefundNoOfUsedByTids(tids);
            if(CollectionUtils.isNotEmpty(refundNoOfUsedByTids1)){
                refundNoOfUsedByTids.addAll(refundNoOfUsedByTids1);
            }
            Map<String, List<TocCacheBo>> tocTradeGroupMapOfTid = tocCacheBoList.stream().collect(Collectors.groupingBy(x -> x.getTocApiTrade().getTid()));
            a:for (Iterator<TocAlipayOrigin> it = allIncomeAmounts.iterator(); it.hasNext();){
                TocAlipayOrigin tocAlipayOrigin = it.next();
                BigDecimal expendAmount = tocAlipayOrigin.getExpendAmount();
                String tid = getTidForOrigin(tocAlipayOrigin, false);
                if(tocTradeGroupMapOfTid.containsKey(tid)){
                    TocCacheBo tocCacheBo = tocTradeGroupMapOfTid.get(tid).get(NumberEnum.ZERO.getCode());
                    //只取存在退款 且尚未使用的
                    List<TocCacheBo.TocDataCacheOfTrader> tocDataCacheOfTraderList = tocCacheBo.getTocDataCacheOfTraderList().stream().filter(x -> !Objects.isNull(x.getTocApiRefund()) && !refundNoOfUsedByTids.contains(x.getTocApiTradeOrder().getRefundId())).collect(Collectors.toList());
                    LocalDateTime accountDate = tocAlipayOrigin.getAccountDate();
                    long toEpochSecond = accountDate.toEpochSecond(ZoneOffset.of("+8"));
                    List<TocCacheBo.TocDataCacheOfTrader> tocDataCacheOfTraders= new ArrayList<>();

                    if(CollectionUtils.isNotEmpty(tocDataCacheOfTraderList)){
                        b:for (int i = 1; i < 13; i++) {
                            //根据时间进行分组 如果存在当前单个退款单的数据=流水,那么取最近的一条
                            Optional<TocCacheBo.TocDataCacheOfTrader> first = tocDataCacheOfTraderList.stream().filter(x -> expendAmount.add(x.getTocApiRefund().getActualRefundAmount()).compareTo(BigDecimal.ZERO) == 0).sorted((x1, x2) -> {
                                long orderTimeSecond = x1.getTocApiRefund().getCurrentPhaseTimeout().toEpochSecond(ZoneOffset.of("+8"));
                                long originTimeSecond =x2.getTocApiRefund().getCurrentPhaseTimeout().toEpochSecond(ZoneOffset.of("+8"));
                                return Long.compare(Math.abs(orderTimeSecond - toEpochSecond), Math.abs(originTimeSecond - toEpochSecond));
                            }).findFirst();
                            if(first.isPresent()){
                                tocDataCacheOfTraders.add(first.get());
                                i=13;
                            }else {
                                tocDataCacheOfTraders=this.groupByTime(refundNoOfUsedByTids, tocAlipayOrigin, tocDataCacheOfTraderList, i);
                                if(CollectionUtils.isEmpty(tocDataCacheOfTraders)){
                                    continue b;
                                }
                            }
                            ccb:for (TocCacheBo.TocDataCacheOfTrader tocDataCacheOfTrader:tocDataCacheOfTraders) {
                                TocApiTradeOrder tocApiTradeOrder = tocDataCacheOfTrader.getTocApiTradeOrder();
                                TocApiRefund tocApiRefund = tocDataCacheOfTrader.getTocApiRefund();
                                Boolean isOther=true;
                                BigDecimal num = tocApiTradeOrder.getNum();
                                if(tocApiRefund==null){
                                    continue ccb;
                                }
                                if(tocApiRefund.getActualRefundAmount().compareTo(tocApiTradeOrder.getShareAmount())==0){
                                    tocApiRefund.setNum(num.intValue());
                                    isOther=false;
                                }else {
                                    tocApiRefund.setNum(0);
                                    //   1：退款金额=单价* 退款数量
                                    //单价
                                    BigDecimal price_pp = tocApiTradeOrder.getShareAmount().divide(num, 2, RoundingMode.HALF_UP);
                                    //计算距离退款金额最近的数量
                                    rr:for (int r = 1; r<= num.intValue(); r++) {
                                        BigDecimal multiply = price_pp.multiply(new BigDecimal(i)).add(tocApiRefund.getActualRefundAmount());
                                        if(multiply.compareTo(new BigDecimal(0.1))<1&&multiply.compareTo(new BigDecimal(-0.1))>0){
                                            log.debug("总数量：{}，单价：{}，退款金额：{}，退款数量：{}",num,price_pp,tocApiRefund.getActualRefundAmount(),i);
                                            tocApiRefund.setNum(r);
                                            isOther=false;
                                            continue rr;
                                        }
                                    }
                                }
                                tocApiRefund.setIsOther(isOther);
                            }
                            BigDecimal totalRefundAmount = tocDataCacheOfTraders.stream().map(x -> x.getTocApiRefund().getActualRefundAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            if(totalRefundAmount.add(expendAmount).compareTo(BigDecimal.ZERO)==0){
                               tocOriginBoList.add(
                                       TocOriginBo.builder()
                                               .tocAlipayOrigin(tocAlipayOrigin)
                                               .tocCacheBo(TocCacheBo.builder().tocApiTrade(tocCacheBo.getTocApiTrade()).tocDataCacheOfTraderList(tocDataCacheOfTraders).build())
                                       .build()
                               );
                               it.remove();
                               continue a;
                           }


                        }
                    }
                }

                //处理并分类
            }

        }
    }
    private void buildExpendAndInsertDb( List<TocOriginBo> tocOriginBoList, List<TocAlipayOrigin> successTocOrigins){
        List<TocExpendOrderStockOut>  tocExpendOrderStockOutList=new ArrayList<>();
        List<TocExpendOrder> tocExpendOrderList=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(tocOriginBoList)){
            for (TocOriginBo t:tocOriginBoList) {
                TocAlipayOrigin tocAlipayOrigin = t.getTocAlipayOrigin();
                val tocApiTrade = t.getTocCacheBo().getTocApiTrade();
                List<TocCacheBo.TocDataCacheOfTrader> tocDataCacheOfTraders = t.getTocCacheBo().getTocDataCacheOfTraderList();
                for (TocCacheBo.TocDataCacheOfTrader tocDataCacheOfTrader: tocDataCacheOfTraders) {
                    List<TocStockoutOrderDetail> tocStockOutOrderDetailList = tocDataCacheOfTrader.getTocStockOutOrderDetailList();
                    TocApiTradeOrder tocApiTradeOrder = tocDataCacheOfTrader.getTocApiTradeOrder();
                    TocApiRefund tocApiRefund = tocDataCacheOfTrader.getTocApiRefund();
                    if(CollectionUtils.isNotEmpty(tocStockOutOrderDetailList)){
                        for (TocStockoutOrderDetail tocStockoutOrderDetail:tocStockOutOrderDetailList) {
                            tocExpendOrderStockOutList.add(
                                    TocExpendOrderStockOut.builder()
                                            .stockOutDetailId(tocStockoutOrderDetail.getRecId())
                                            .stockOutId(tocStockoutOrderDetail.getStockoutId())
                                            .stockOutNum(tocStockoutOrderDetail.getNum())
                                            .stockOutPrice(tocStockoutOrderDetail.getPrice())
                                            .stockOutTotalAmount(tocStockoutOrderDetail.getTotalAmount())
                                            .stockOutCostPrice(tocStockoutOrderDetail.getCostPrice())
                                            .goodsName(tocStockoutOrderDetail.getGoodsName())
                                            .goodsNo(tocStockoutOrderDetail.getGoodsNo())
                                            .specName(tocStockoutOrderDetail.getSpecName())
                                            .specId(tocStockoutOrderDetail.getSpecId())
                                            .specNo(tocStockoutOrderDetail.getSpecNo())
                                            .specCode(tocStockoutOrderDetail.getSpecCode())
                                            .oid(tocApiTradeOrder.getOid())
                                            .build()
                            );
                        }
                    }
                    Integer num = tocApiRefund.getNum();
                    Integer checkType=TocMappingTypeEnum.EXPEND_SHARE_AMOUNT_ALL.getNo();
                    if(Objects.isNull(num)||num==0){
                        checkType=TocMappingTypeEnum.EXPEND_SHARE_AMOUNT_OTHER.getNo();
                    }
                    tocAlipayOrigin.setCheckType(checkType);
                    tocExpendOrderList.add(TocExpendOrder.builder()
                            .oid(tocApiTradeOrder.getOid())
                            .financeNo(tocAlipayOrigin.getFinanceNo())
                            .tid(tocApiTradeOrder.getTid())
                            .totalShareAmount(tocApiTradeOrder.getShareAmount())
                            .detailReceived(tocAlipayOrigin.getExpendAmount())
                            .totalAmtAfterShare(BigDecimal.ZERO.subtract(tocApiRefund.getActualRefundAmount()))
                            .shopAlipayAccount(tocAlipayOrigin.getShopAlipayAccount())
                            .createOrderDate(tocApiTrade.getTradeTime())
                            .payDate(tocApiTrade.getPayTime())
                            .stockOutDate( TimeFormatUtil.stringToLocalDateTime(tocApiTradeOrder.getStockoutNo()))
                            .receivedDate(tocAlipayOrigin.getAccountDate())
                            .saleQty(tocApiTradeOrder.getNum().intValue())
                            .saleSpecNo(tocApiTradeOrder.getSpecNo())
                            .saleGoodsNo(tocApiTradeOrder.getGoodsNo())
                            .saleSpecName(tocApiTradeOrder.getSpecName())
                            .saleGoodsName(tocApiTradeOrder.getGoodsName())
                            .refundQyt(num)
                            .checkType(checkType)
                            .price(tocApiTradeOrder.getPrice())
                            .refundNo(tocApiRefund.getRefundNo())
                            .shopId(tocApiTrade.getShopId())
                            .build());
                }

                successTocOrigins.add(tocAlipayOrigin);
            }

            HashMap<String,TocStockoutOrder> dateTimeMap=new HashMap();
            if(CollectionUtils.isNotEmpty(tocExpendOrderStockOutList)){
                List<TocStockoutOrder> tocStockoutOrders = tocStockoutOrderMapper.selectList(Wrappers.<TocStockoutOrder>lambdaQuery().in(TocStockoutOrder::getStockoutId, tocExpendOrderStockOutList.stream().map(x -> x.getStockOutId()).collect(Collectors.toList())));
                Map<Integer, List<TocStockoutOrder>> groupMap = tocStockoutOrders.stream().collect(Collectors.groupingBy(TocStockoutOrder::getStockoutId));
                for(TocExpendOrderStockOut tocExpendOrderStockOut:tocExpendOrderStockOutList){
                    if(groupMap.containsKey(tocExpendOrderStockOut.getStockOutId())){
                        dateTimeMap.put(tocExpendOrderStockOut.getOid(),groupMap.get(tocExpendOrderStockOut.getStockOutId()).get(0));
                        tocExpendOrderStockOut.setStockOutNo(groupMap.get(tocExpendOrderStockOut.getStockOutId()).get(0).getStockoutNo());
                    }
                }
                BatchInsertUtil.batchInsert(tocExpendOrderStockOutList, TocExpendOrderStockOutMapper.class,"insertList");
            }
            if(CollectionUtils.isNotEmpty(tocExpendOrderList)){
                tocExpendOrderList.forEach(x->{
                    TocStockoutOrder tocStockoutOrder = dateTimeMap.get(x.getOid());
                    if(Objects.nonNull(tocStockoutOrder)){
                        x.setTradeNo(tocStockoutOrder.getSrcOrderNo());
                        x.setStockOutDate(tocStockoutOrder.getConsignTime());
                    }
                });
                BatchInsertUtil.batchInsert(tocExpendOrderList, TocExpendOrderMapper.class,"insertList");
            }
        }
    }


    /**
     * 将订单和出库单以及退款单关联
     * @param tids
     * @return
     */
    private List<TocCacheBo> generatingAssociationRelationshipsForTradeOrderByTids(Set<String> tids){
        List<TocCacheBo> tocCacheBoList=new ArrayList<>();
        //当前批次下tid 对应的原始订单
        List<TocApiTrade> allTocApiTrades = tocApiTradeMapper.selectList(Wrappers.<TocApiTrade>lambdaQuery().in(TocApiTrade::getTid, tids).eq(TocApiTrade::getPlatformId, NumberEnum.ONE.getCode()));
        //当前批次下tid对应的原始订单详情
        List<TocApiTradeOrder> allTocApiTradeOrders = tocApiTradeOrderMapper.selectList(Wrappers.<TocApiTradeOrder>lambdaQuery().in(TocApiTradeOrder::getTid, tids).eq(TocApiTradeOrder::getPlatformId, NumberEnum.ONE.getCode()));
        Map<String, List<TocApiTradeOrder>> tidGroupMap = allTocApiTradeOrders.parallelStream().collect(Collectors.groupingBy(TocApiTradeOrder::getTid));


        //当前批次下的订单关联
        List<TocSalesTradeOrder> allTocSalesTradeOrders = tocSalesTradeOrderMapper.selectList(Wrappers.<TocSalesTradeOrder>lambdaQuery().in(TocSalesTradeOrder::getSrcTid, tids).eq(TocSalesTradeOrder::getPlatformId, NumberEnum.ONE.getCode()));
        Map<String, List<TocSalesTradeOrder>> orderIdGroupMap = allTocSalesTradeOrders.parallelStream().collect(Collectors.groupingBy(x -> x.getSrcTid() + ":" + x.getSrcOid()));
        //当前批次下的出库单详情
        Map<Integer, List<TocStockoutOrderDetail>> orderDetailIdGroupMap=new HashMap<>();
        if(CollectionUtils.isNotEmpty(allTocSalesTradeOrders)){
            List<TocStockoutOrderDetail> allStockOutOrderDetails=  tocStockoutOrderDetailMapper.selectList(Wrappers.<TocStockoutOrderDetail>lambdaQuery()
                    .in(TocStockoutOrderDetail::getSrcOrderDetailId,
                            allTocSalesTradeOrders.stream().map(TocSalesTradeOrder::getRecId).collect(Collectors.toList())));
            orderDetailIdGroupMap = allStockOutOrderDetails.parallelStream().collect(Collectors.groupingBy(TocStockoutOrderDetail::getSrcOrderDetailId));
        }
        //所有的退款订单
        List<String> refundNos = allTocApiTradeOrders.stream().filter(x -> StringUtils.isNotBlank(x.getRefundId())).map(x -> x.getRefundId()).collect(Collectors.toList());
        Map<String, List<TocApiRefund>> refundNoGroupMap=new HashMap<>();
        if(CollectionUtils.isNotEmpty(refundNos)){
            List<TocApiRefund> allTocApiRefunds = tocApiRefundMapper.selectList(Wrappers.<TocApiRefund>lambdaQuery().in(TocApiRefund::getRefundNo, refundNos).eq(TocApiRefund::getPlatformId, NumberEnum.ONE.getCode()));
            refundNoGroupMap = allTocApiRefunds.parallelStream().collect(Collectors.groupingBy(TocApiRefund::getRefundNo));
        }


        for (TocApiTrade tocApiTrade:allTocApiTrades) {
            String tid = tocApiTrade.getTid();
            if(tidGroupMap.containsKey(tid)){
                List<TocCacheBo.TocDataCacheOfTrader> tocDataCacheOfTraderList=new ArrayList<>();
                List<TocApiTradeOrder> tocApiTradeOrders = tidGroupMap.get(tid);

                for (TocApiTradeOrder tocApiTradeOrder: tocApiTradeOrders) {
                    TocCacheBo.TocDataCacheOfTrader tocDataCacheOfTrader=new TocCacheBo.TocDataCacheOfTrader();
                    String oid = tocApiTradeOrder.getOid();
                    //处理退款关联
                    if(StringUtils.isNotBlank(tocApiTradeOrder.getRefundId())){
                        if(refundNoGroupMap.containsKey(tocApiTradeOrder.getRefundId())){
                            Optional<TocApiRefund> first = refundNoGroupMap.get(tocApiTradeOrder.getRefundId()).parallelStream().sorted(Comparator.comparing(TocApiRefund::getStatus).reversed()).findFirst();
                            if(!Objects.isNull(first)){
                                tocDataCacheOfTrader.setTocApiRefund(first.get());
                            }
                        }
                    }
                    //处理出库关联
                    if(orderIdGroupMap.containsKey(tid + ":" + oid)){
                    List<TocStockoutOrderDetail> stockoutAllOrderDetails=new ArrayList<>();
                        List<TocSalesTradeOrder> tocSalesTradeOrders = orderIdGroupMap.get(tid + ":" + oid);
                        for (TocSalesTradeOrder tocSalesTradeOrder: tocSalesTradeOrders) {
                            Integer recId = tocSalesTradeOrder.getRecId();
                            if(orderDetailIdGroupMap.containsKey(recId)){
                                List<TocStockoutOrderDetail> stockoutOrderDetails = orderDetailIdGroupMap.get(recId);
                                if(CollectionUtils.isNotEmpty(stockoutOrderDetails)){
                                    stockoutAllOrderDetails.addAll(stockoutOrderDetails);
                                }
                            }
                        }
                    tocDataCacheOfTrader.setTocStockOutOrderDetailList(stockoutAllOrderDetails);
                    }
                    tocDataCacheOfTrader.setTocApiTradeOrder(tocApiTradeOrder);
                    tocDataCacheOfTraderList.add(tocDataCacheOfTrader);
                }

                tocCacheBoList.add(TocCacheBo.builder().tocApiTrade(tocApiTrade).tocDataCacheOfTraderList(tocDataCacheOfTraderList).build());
            }
        }

        return tocCacheBoList;
    }

    /**
     * 根据时间进行分组
     * @param tocDataCacheOfTraderList
     * @param count 第 1次：所有的； 2：时间误差在最近的一组，10:当天的
     * @return
     */
    private List<TocCacheBo.TocDataCacheOfTrader> groupByTime(List<String> usedOidByTid,TocAlipayOrigin tocAlipayOrigin, List<TocCacheBo.TocDataCacheOfTrader> tocDataCacheOfTraderList, Integer count){

        List<TocCacheBo.TocDataCacheOfTrader> tocDataCacheOfTraders = tocDataCacheOfTraderList.stream().filter(x -> org.apache.commons.lang.StringUtils.isNotEmpty(x.getTocApiTradeOrder().getStockoutNo())).collect(Collectors.toList());
        if(count<10){
            return tocDataCacheOfTraders.stream().filter(x->(!usedOidByTid.contains(x.getTocApiTradeOrder().getOid()))).filter(x->filterByTime(tocAlipayOrigin.getAccountDate(), TimeFormatUtil.stringToLocalDateTime(x.getTocApiTradeOrder().getStockoutNo()),count,true)).collect(Collectors.toList());
        }else if(count==10){
            String dateTimeToString = TimeFormatUtil.localDateTimeToString(tocAlipayOrigin.getAccountDate(), "yyyy-MM-dd");
            return tocDataCacheOfTraders.stream().filter(x->(!usedOidByTid.contains(x.getTocApiTradeOrder().getOid()))).filter(x->x.getTocApiTradeOrder().getStockoutNo().contains(dateTimeToString)).collect(Collectors.toList());
        }
        return tocDataCacheOfTraderList.stream().filter(x->!usedOidByTid.contains(x.getTocApiTradeOrder().getOid())).collect(Collectors.toList());

    }


    /**
     * 根据流水获取原始单号tid
     * @param tocAlipayOrigin
     * @param isIncome
     * @return
     */
    private static String getTidForOrigin(TocAlipayOrigin tocAlipayOrigin, boolean isIncome){
        if(isIncome){
            StringBuilder s = new StringBuilder(tocAlipayOrigin.getTid());
            int i = s.lastIndexOf("P");
            String substring = s.substring(i + 1);
            return substring;
        }else {
            String remark = tocAlipayOrigin.getRemark();
            String tid = tocAlipayOrigin.getTid();
            //带-的是退款单号
            if(remark!=null&&remark.contains("T200P")){
                StringBuilder s = new StringBuilder(remark);
                int i = s.lastIndexOf("-T200P");
                String substring = s.substring(i + 6);
                return substring;
            }
            if(tid!=null&&tid.contains("T200P")){
                StringBuilder s = new StringBuilder(tid);
                int i = s.lastIndexOf("T200P");

                String substring = s.substring(i + 5);
                return substring;
            }
            log.info("未通过：{}",tocAlipayOrigin.getFinanceNo());
        }
        return null;
    }

    private  Boolean filterByTime(LocalDateTime originTime,LocalDateTime orderTime,Integer count,Boolean isIncome){
        long orderTimeSecond = orderTime.toEpochSecond(ZoneOffset.of("+8"));
        long originTimeSecond = originTime.toEpochSecond(ZoneOffset.of("+8"));
        if(isIncome){
            //先有出库，后有入账 (所以订单的时间要小于流水到账时间)
            if(Math.abs(orderTimeSecond-originTimeSecond)<(poorSecond+count)*count){
                return true;
            }
        }else {
            //先有订单退款，后又退款完成
            if(Math.abs(orderTimeSecond-originTimeSecond)<(poorSecond+count)*count){
                return true;
            }
        }

        return false;
    }

    private void buildIncomeAndInsertDb(List<TocOriginBo> tocOriginBoList,List<TocAlipayOrigin> successTocOrigins){
        if(CollectionUtils.isNotEmpty(tocOriginBoList)){
            List<TocIncomeOrder> tocIncomeOrderList=new ArrayList<>();
            List<TocIncomeOrderStockOut> tocIncomeOrderStockOutList=new ArrayList<>();
            for (TocOriginBo tocOriginBo:tocOriginBoList) {
                TocCacheBo tocCacheBo = tocOriginBo.getTocCacheBo();
                TocMappingTypeEnum tocMappingTypeEnum = tocOriginBo.getTocMappingTypeEnum();
                TocAlipayOrigin tocAlipayOrigin = tocOriginBo.getTocAlipayOrigin();
                tocAlipayOrigin.setCheckType(tocMappingTypeEnum.getNo());
                successTocOrigins.add(tocAlipayOrigin);
                TocApiTrade tocApiTrade = tocCacheBo.getTocApiTrade();
                BigDecimal postAmount = tocOriginBo.getPostAmount();
                List<TocCacheBo.TocDataCacheOfTrader> tocDataCacheOfTraderList = tocCacheBo.getTocDataCacheOfTraderList();
                for (TocCacheBo.TocDataCacheOfTrader tocDataCacheOfTrader: tocDataCacheOfTraderList) {
                    TocApiTradeOrder tocApiTradeOrder = tocDataCacheOfTrader.getTocApiTradeOrder();
                    TocApiRefund tocApiRefund = tocDataCacheOfTrader.getTocApiRefund();

                    String refundNo=null;
                    List<TocStockoutOrderDetail> tocStockOutOrderDetailList = tocDataCacheOfTrader.getTocStockOutOrderDetailList();


                    BigDecimal totalAmtRefund=tocApiRefund==null?BigDecimal.ZERO:tocApiRefund.getActualRefundAmount();
                    BigDecimal totalAmtPost=BigDecimal.ZERO;
                    BigDecimal totalAmtEquity=BigDecimal.ZERO;
                    BigDecimal shareAmount = tocApiTradeOrder.getShareAmount();
                    BigDecimal totalAmtAfterShare=BigDecimal.ZERO;
                    BigDecimal totalStockOutAmt=BigDecimal.ZERO;
                    if(CollectionUtils.isNotEmpty(tocStockOutOrderDetailList)){
                        totalStockOutAmt=tocStockOutOrderDetailList.stream().map(x->x.getTotalAmount()).reduce(BigDecimal.ZERO,BigDecimal::add);
                        for (TocStockoutOrderDetail tocStockoutOrderDetail:tocStockOutOrderDetailList) {
                            tocIncomeOrderStockOutList.add(
                                    TocIncomeOrderStockOut.builder()
                                            .stockOutDetailId(tocStockoutOrderDetail.getRecId())
                                            .stockOutId(tocStockoutOrderDetail.getStockoutId())
                                            .stockOutNum(tocStockoutOrderDetail.getNum())
                                            .stockOutPrice(tocStockoutOrderDetail.getPrice())
                                            .stockOutTotalAmount(tocStockoutOrderDetail.getTotalAmount())
                                            .stockOutCostPrice(tocStockoutOrderDetail.getCostPrice())
                                            .goodsName(tocStockoutOrderDetail.getGoodsName())
                                            .goodsNo(tocStockoutOrderDetail.getGoodsNo())
                                            .specName(tocStockoutOrderDetail.getSpecName())
                                            .specId(tocStockoutOrderDetail.getSpecId())
                                            .specNo(tocStockoutOrderDetail.getSpecNo())
                                            .specCode(tocStockoutOrderDetail.getSpecCode())
                                            .oid(tocApiTradeOrder.getOid())
                                            .build()
                            );

                        }
                    }
                    Integer tocMappingTypeEnumNo = tocMappingTypeEnum.getNo();
                    Integer refundQyt=0;
                    switch (tocMappingTypeEnum){
                       case INCOME_SHARE_AMOUNT:
                           totalAmtAfterShare=shareAmount;
                           break;
                       case INCOME_SHARE_AMOUNT_POST:
                           if(postAmount!=null){
                               totalAmtPost=postAmount;
                               postAmount=null;
                           }
                           totalAmtAfterShare=shareAmount.add(totalAmtPost);
                           break;
                       case INCOME_SHARE_AMOUNT_REFUND:
                       case INCOME_SHARE_AMOUNT_REFUND_OTHER:
                           if(tocApiRefund!=null&&tocApiRefund.getIsOther()){
                               tocMappingTypeEnumNo=TocMappingTypeEnum.INCOME_SHARE_AMOUNT_REFUND_OTHER.getNo();
                           }
                           totalAmtAfterShare=shareAmount.subtract(totalAmtRefund);
                           if(tocApiRefund!=null){
                               refundNo=tocApiRefund.getRefundNo();
                               refundQyt=tocApiRefund.getNum();
                           }
                           break;
                       case INCOME_SHARE_AMOUNT_REFUND_POST:
                       case INCOME_SHARE_AMOUNT_REFUND_POST_OTHER:
                           if(postAmount!=null){
                               totalAmtPost=postAmount;
                               postAmount=null;
                           }
                           totalAmtAfterShare=shareAmount.subtract(totalAmtRefund).add(totalAmtPost);
                           if(tocApiRefund!=null&&tocApiRefund.getIsOther()){
                               tocMappingTypeEnumNo=TocMappingTypeEnum.INCOME_SHARE_AMOUNT_REFUND_POST_OTHER.getNo();
                           }
                           if(tocApiRefund!=null){
                               refundQyt=tocApiRefund.getNum();
                               refundNo=tocApiRefund.getRefundNo();
                           }
                           break;
                       case INCOME_SHARE_AMOUNT_EQUITY:
                           totalAmtEquity=shareAmount.multiply(BigDecimal.ONE.subtract(equityRatio));
                           totalAmtAfterShare=shareAmount.multiply(equityRatio);
                           break;
                       case INCOME_SHARE_AMOUNT_EQUITY_POST:
                           if(postAmount!=null){
                               totalAmtPost=postAmount;
                               postAmount=null;
                           }
                           totalAmtEquity=shareAmount.multiply(BigDecimal.ONE.subtract(equityRatio));
                           totalAmtAfterShare=shareAmount.multiply(equityRatio).add(totalAmtPost);
                           break;
                       default:
                           break;
                   }
                    TocIncomeOrder build = TocIncomeOrder.builder()
                            .financeNo(tocAlipayOrigin.getFinanceNo())
                            .tid(tocApiTradeOrder.getTid())
                            .oid(tocApiTradeOrder.getOid())
                            .totalAmtRefund(totalAmtRefund)
                            .totalAmtPost(totalAmtPost)
                            .detailReceived(tocAlipayOrigin.getIncomeAmount())
                            .totalAmtEquity(totalAmtEquity)
                            .totalAmtAfterShare(totalAmtAfterShare)
                            .totalShareAmount(shareAmount)
                            .createOrderDate(tocApiTrade.getTradeTime())
                            .stockOutDate( TimeFormatUtil.stringToLocalDateTime(tocApiTradeOrder.getStockoutNo()))
                            .payDate(tocApiTrade.getPayTime())
                            .receivedDate(tocAlipayOrigin.getAccountDate())
                            .saleQty(tocApiTradeOrder.getNum())
                            .checkType(tocMappingTypeEnumNo)
                            .price(shareAmount.divide(tocApiTradeOrder.getNum(), 2, RoundingMode.HALF_UP))
                            .saleSpecNo(tocApiTradeOrder.getSpecNo())
                            .saleGoodsNo(tocApiTradeOrder.getGoodsNo())
                            .saleSpecName(tocApiTradeOrder.getSpecName())
                            .saleGoodsName(tocApiTradeOrder.getGoodsName())
                            .refundQyt(refundQyt)
                            .totalStockOutAmt(totalStockOutAmt)
                            .shopAlipayAccount(tocAlipayOrigin.getShopAlipayAccount())
                            .refundNo(refundNo)
                            .shopId(tocApiTrade.getShopId())
                            .build();
                    if(StringUtils.isBlank(build.getSaleSpecNo())){
                        build.setSaleSpecNo(StringUtils.isBlank(tocApiTradeOrder.getSpecId())?null:tocApiTradeOrder.getSpecId());
                    }
                    tocIncomeOrderList.add(build);
                }

            }

            HashMap<String,TocStockoutOrder> dateTimeMap=new HashMap();
            if(CollectionUtils.isNotEmpty(tocIncomeOrderStockOutList)){
                List<TocStockoutOrder> tocStockoutOrders = tocStockoutOrderMapper.selectList(Wrappers.<TocStockoutOrder>lambdaQuery().in(TocStockoutOrder::getStockoutId, tocIncomeOrderStockOutList.stream().map(x -> x.getStockOutId()).collect(Collectors.toList())));
                Map<Integer, List<TocStockoutOrder>> groupMap = tocStockoutOrders.stream().collect(Collectors.groupingBy(TocStockoutOrder::getStockoutId));
                for(TocIncomeOrderStockOut tocIncomeOrderStockOut:tocIncomeOrderStockOutList){
                    if(groupMap.containsKey(tocIncomeOrderStockOut.getStockOutId())){
                        dateTimeMap.put(tocIncomeOrderStockOut.getOid(),groupMap.get(tocIncomeOrderStockOut.getStockOutId()).get(0));
                        tocIncomeOrderStockOut.setStockOutNo(groupMap.get(tocIncomeOrderStockOut.getStockOutId()).get(0).getStockoutNo());
                    }
                }
                BatchInsertUtil.batchInsert(tocIncomeOrderStockOutList, TocIncomeOrderStockOutMapper.class,"insertList");
            }
            if(CollectionUtils.isNotEmpty(tocIncomeOrderList)){
                tocIncomeOrderList.forEach(x->{
                    TocStockoutOrder tocStockoutOrder = dateTimeMap.get(x.getOid());
                    if(Objects.nonNull(tocStockoutOrder)){
                        x.setTradeNo(tocStockoutOrder.getSrcOrderNo());
                        x.setStockOutDate(tocStockoutOrder.getConsignTime());
                    }
                });
                BatchInsertUtil.batchInsert(tocIncomeOrderList, TocIncomeOrderMapper.class,"insertList");
            }
        }
    }

    private void  insertDataToDB(Boolean isGroup, List<TocAlipayOrigin> successTocOrigins, List<TocAlipayOrigin> failTocOrigins){
        if(CollectionUtils.isNotEmpty(successTocOrigins)){
            Map<Integer, List<TocAlipayOrigin>> collect = successTocOrigins.parallelStream().collect(Collectors.groupingBy(TocAlipayOrigin::getCheckType));
            for (Integer key:collect.keySet()) {
                List<TocAlipayOrigin> tocAlipayOrigins = collect.get(key);
                if(isGroup){
                    tocAlipayOriginMapper.update(TocAlipayOrigin.builder().checkFlag(NumberEnum.ONE.getCode()).checkType(key).build(),
                            Wrappers.<TocAlipayOrigin>lambdaQuery().in(TocAlipayOrigin::getTid, tocAlipayOrigins.stream().map(x->x.getTid()).collect(Collectors.toSet()))
                                    .in(TocAlipayOrigin::getBusinessType,0,1)
                                    .gt(TocAlipayOrigin::getIncomeAmount, BigDecimal.ZERO));
                }else {
                    tocAlipayOriginMapper.update(TocAlipayOrigin.builder().checkFlag(NumberEnum.ONE.getCode()).checkType(key).build(), Wrappers.<TocAlipayOrigin>lambdaQuery().in(TocAlipayOrigin::getFinanceNo,
                            tocAlipayOrigins.stream().map(x->x.getFinanceNo()).collect(Collectors.toSet())));
                }
            }
        }
        if(CollectionUtils.isNotEmpty(failTocOrigins)){
            tocAlipayOriginMapper.update(TocAlipayOrigin.builder().checkFlag(NumberEnum.NINE.getCode()).build(), Wrappers.<TocAlipayOrigin>lambdaQuery().in(TocAlipayOrigin::getFinanceNo,
                    failTocOrigins.stream().map(x->x.getFinanceNo()).collect(Collectors.toSet())));
        }

    }



}
