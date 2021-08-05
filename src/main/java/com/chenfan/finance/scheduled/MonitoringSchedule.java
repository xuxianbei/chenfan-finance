package com.chenfan.finance.scheduled;/**
 * @Author Wen.Xiao
 * @Description // 监控费用是否正确，不正确时进行推送钉钉通知
 * @Date 2021/1/19  16:56
 * @Version 1.0
 */

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.finance.dao.*;
import com.chenfan.finance.enums.MonitoringEnum;
import com.chenfan.finance.enums.NumberEnum;
import com.chenfan.finance.model.*;
import com.chenfan.finance.utils.DingTalkUtil;
import com.chenfan.finance.utils.TimeFormatUtil;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@RefreshScope
public class MonitoringSchedule {


    @Resource
    private CfRdRecordDetailMapper cfRdRecordDetailMapper;
    @Resource
    private CfChargeMapper cfChargeMapper;
    @Resource
    private CfChargeInMapper cfChargeInMapper;
    @Resource
    private CfPoHeaderMapper cfPoHeaderMapper;


    @Value("${spring.profiles.active}")
    private String profilesActive;
      //检查到货单和财务入库单是否一致

      //检查一下生成的相关费用(采购费用、退次费用)是否与入库单是否一致
    /**
     * 检查到货单和财务入库单是否一致
     * 检查一下生成的相关费用(采购费用、退次费用)是否与入库单是否一致
     * @param param
     * @return
     */
      public ReturnT<String> monitoringTask(String param) {
          LocalDateTime endTime=LocalDateTime.now();
          LocalDateTime startTime=endTime.plusDays(-15);
          this.doCheckPoInfo(startTime,endTime);
          this.doCheckRdInfo(startTime,endTime);
          this.doCheckChargeAndChargeIn(startTime,endTime);
          this.doCheckChargeInPrice(startTime,endTime);

          return ReturnT.SUCCESS;
      }




    /**
     * 1:检查采购单是否同步 ,已经同步的合同信息是否一致，单价等是否一致
     */
    public void  doCheckPoInfo(LocalDateTime startTime,LocalDateTime endTime){
        String dateTimeToString =TimeFormatUtil.localDateTimeToString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
        List<Integer> brandIs = cfRdRecordDetailMapper.selectBrandIds();
        //检查采购单是否同步
        {
            List<String> poCodeNos = cfPoHeaderMapper.selectPoInfoDifferences(startTime, endTime, brandIs);
            if(CollectionUtils.isNotEmpty(poCodeNos)){
                StringBuffer stringBuffer = new StringBuffer();
                Integer no=0;
                for (String rdCode:poCodeNos){
                    no++;
                    stringBuffer.append(String.format("%s、采购单：%s  尚未同步到新财务系统，请检查处理！！！\n",no,rdCode));
                }
                if(stringBuffer.length()>1){
                    this.sendMessage(profilesActive+"环境告警：采购单与财务采购单存在如下差异("+dateTimeToString+"比对):\n\n",stringBuffer.toString());
                }
            }
        }
        //检查单价是否一致，数量，截至日期
        {
            List<HashMap<String, Object>> hashMapList = cfPoHeaderMapper.selectPoDetailInfoDifferences(startTime, endTime, brandIs);
            if(CollectionUtils.isNotEmpty(hashMapList)){
                StringBuffer stringBuffer = new StringBuffer();
                creatTips(hashMapList,stringBuffer,"采购单");
                if(stringBuffer.length()>1){
                    this.sendMessage(profilesActive+"环境告警：采购单与财务采购单存在如下差异("+dateTimeToString+"比对):\n\n",stringBuffer.toString());
                }
            }
        }

    }


    /**
     * 2：检查新入库的数据，数据是否同步，同步的数量是否一致
     */
    public void  doCheckRdInfo(LocalDateTime startTime,LocalDateTime endTime){
        String dateTimeToString =TimeFormatUtil.localDateTimeToString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
      //1:检查数据是否同步完成
        {
            List<String> rdInfoDifferences = cfRdRecordDetailMapper.selectRdInfoDifferences(startTime, endTime);
            if(CollectionUtils.isNotEmpty(rdInfoDifferences)){
                StringBuffer stringBuffer = new StringBuffer();
                Integer no=0;
                for (String rdCode:rdInfoDifferences){
                    no++;
                    stringBuffer.append(String.format("%s、到货通知ASN：%s  尚未同步到新财务系统，请检查处理！！！\n",no,rdCode));
                }
                if(stringBuffer.length()>1){
                    this.sendMessage(profilesActive+"环境告警：到货通知ASN数据告警("+dateTimeToString+"比对):\n\n",stringBuffer.toString());
                }
            }
        }
        //2：检查数据是否和采购数据一致
        {
            List<HashMap<String, Object>> hashMapList = cfRdRecordDetailMapper.selectRdDetailInfoDifferences(startTime, endTime);
            if(CollectionUtils.isNotEmpty(hashMapList)){
                StringBuffer stringBuffer = new StringBuffer();
                creatTips(hashMapList,stringBuffer,"入库单");
                if(stringBuffer.length()>1){
                    this.sendMessage(profilesActive+"环境告警：入库单与财务采购单存在如下差异("+dateTimeToString+"比对):\n\n",stringBuffer.toString());
                }
            }
        }
        //3: 检查是否存在尚未生成的费用
        List<CfRdRecordDetail> cfRdRecordDetails = cfRdRecordDetailMapper.selectList(Wrappers.<CfRdRecordDetail>lambdaQuery().eq(CfRdRecordDetail::getCreateChargeFlag, false).gt(CfRdRecordDetail::getQuantity, NumberEnum.ZERO.getCode()));
        if(CollectionUtils.isNotEmpty(cfRdRecordDetails)){
            StringBuffer stringBuffer = new StringBuffer();
            Integer no=0;
            for (CfRdRecordDetail cfRdRecordDetail:cfRdRecordDetails){
                no++;
                stringBuffer.append(String.format("%s、入库单：%s ，待生成数量：%s 尚未生成费用，请检查处理！！！\n",no,cfRdRecordDetail.getRdRecordDetailId(),cfRdRecordDetail.getQuantity()));
            }
            if(stringBuffer.length()>1){
                this.sendMessage(profilesActive+"环境告警：入库单据不生成费用数据告警("+dateTimeToString+"比对):\n\n",stringBuffer.toString());
            }
        }

    }

    /**
     * 检查费用表和费用记录表是否存在差异性
     * @param startTime
     * @param endTime
     */
    public void doCheckChargeAndChargeIn(LocalDateTime startTime,LocalDateTime endTime){
        String timeTips = TimeFormatUtil.localDateTimeToString(startTime)+"~"+TimeFormatUtil.localDateTimeToString(endTime);
        String dateTimeToString =TimeFormatUtil.localDateTimeToString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
        List<String> chargeCodeList = cfChargeMapper.selectDiffChargeInAndCharge();
        StringBuffer stringBuffer = new StringBuffer();
        if(CollectionUtils.isNotEmpty(chargeCodeList)){
            for (String chargeCode:chargeCodeList){
                stringBuffer.append(String.format("cf_charge_in：%s 对应得部分价格缺失，请检查数据!!!!",chargeCode));
            }
            this.sendMessage(profilesActive+"环境告警："+timeTips+ "日数据预警监测("+dateTimeToString+"比对):\n\n",stringBuffer.toString());
        }
    }


    /**
     * 检查当前cf_charge_in 里面的含税单据
     * @param startTime
     * @param endTime
     */
    public void doCheckChargeInPrice(LocalDateTime startTime,LocalDateTime endTime){
        List<CfChargeIn> cfChargeIns = cfChargeInMapper.selectList(Wrappers.<CfChargeIn>lambdaQuery()
                .apply("(unit_price is null or markup_unit_price is null or markup_rate is null) and create_date between {0} and {1}", startTime, endTime));
        StringBuffer stringBuffer = new StringBuffer();
        if(CollectionUtils.isNotEmpty(cfChargeIns)){
            String timeTips = TimeFormatUtil.localDateTimeToString(startTime)+"~"+TimeFormatUtil.localDateTimeToString(endTime);
            String dateTimeToString =TimeFormatUtil.localDateTimeToString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
            for (CfChargeIn cfChargeIn:cfChargeIns){
                stringBuffer.append(String.format("费用：%s 对应得cf_charge_in数据缺失，请检查数据!!!!",cfChargeIn.getChargeInId()));
            }
            this.sendMessage(profilesActive+"环境告警："+timeTips+ "日数据预警监测("+dateTimeToString+"比对):\n\n",stringBuffer.toString());
        }
    }

    /**
     * 创建提示语TIPS
     * @param hashMapList
     * @param stringBuffer
     */
    private static  void   creatTips(List<HashMap<String, Object>> hashMapList, StringBuffer stringBuffer,String orderType){
        Integer no=0;
        for (HashMap<String, Object> hashMap:hashMapList) {
            StringBuffer oneObjBuffer = new StringBuffer();
            Set<String> strings = hashMap.keySet();
            for (String key:strings) {
                if(key.startsWith("cf")){
                    Object cfObj = hashMap.get(key);
                    Object plmObj = hashMap.get(key.substring(2));
                    if(!Objects.equals(cfObj,plmObj)){
                        oneObjBuffer.append(String.format("'%s' 存在差异，财务使用的单据：%s,原始单的单据：%s\n",MonitoringEnum.getEnumNotifyNameByFile(key),cfObj,plmObj));
                    }
                }
            }
            if(oneObjBuffer.length()>1){
                no++;
                Object header=null;
                Object detail=null;
                switch (orderType){
                    case "采购单":
                        header=hashMap.get("po_code");
                        detail=hashMap.get("po_detail_id");
                        break;
                    case "入库单":
                        header=hashMap.get("rd_record_code");
                        detail=hashMap.get("rd_record_detail_id");
                        break;
                    default:
                        break;
                }
                stringBuffer.append(String.format("%s:%s:%s(%s) 存在差异：（\n",no,orderType,header,detail)).append(oneObjBuffer).append(")\n");
            }
        }

    }









     public void sendMessage(String tips,String appendString){
         List<String> strList = getStrList(appendString, 5000);
         for (int i = 0; i < strList.size(); i++) {
             String as =null;
             if(i==0){
                 as=tips+strList.get(i);
             }else {
                 as=strList.get(i);
             }
             DingTalkUtil.sendTextToDingTalk(as);
         }
     }

    public static List<String> getStrList(String inputString, int length) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < Integer.MAX_VALUE; index++) {
            String childStr = substring(inputString, index * length, (index + 1) * length);
            if(StringUtils.isEmpty(childStr)){
                break;
            }
            list.add(childStr);
        }
        return list;
    }
    public static String substring(String str, int f, int t) {
        if(f > str.length()){
            return null;
        }
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }





}
