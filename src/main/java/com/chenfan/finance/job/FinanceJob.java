package com.chenfan.finance.job;

import com.chenfan.finance.scheduled.*;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
/**
 * @author liran
 */
@Component
public class FinanceJob {

    @Autowired
    private CfRdChargeScheduled cfRdChargeScheduled;


    @Resource
    private MonitoringSchedule monitoringSchedule;
    @Resource
    private TocOriginBatchStockOutMappingScheduled tocOriginBatchStockOutMappingScheduled;


    @XxlJob("cfRdCalculateTaskJob")
    public ReturnT<String> cfRdCalculateTaskJob(String param) {
        return cfRdChargeScheduled.cfRdCalculateTask(param);
    }



    /**
     * 财务数据错误告警监控
     * @param param
     * @return
     */
    @XxlJob("monitoringTask")
    public ReturnT<String>   monitoringTask(String param) {
        //先生成一波费用，避免错误提示
        cfRdChargeScheduled.cfRdCalculateTask(param);
        return monitoringSchedule.monitoringTask(param);
    }


    /**
     * 支付宝流水数据和原始订单进行mapping 定时任务
     * @param param
     * @return
     */
    @XxlJob("tocDataMapping")
    public ReturnT<String>   tocDataMapping(String param) {
        return tocOriginBatchStockOutMappingScheduled.mappingBatchEntrance(param);
    }


}
