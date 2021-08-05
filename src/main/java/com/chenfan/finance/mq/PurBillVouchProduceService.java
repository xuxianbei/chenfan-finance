package com.chenfan.finance.mq;

import com.alibaba.fastjson.JSONObject;
import com.chenfan.finance.model.PurchaseInvoice;
import com.chenfan.finance.model.PurchaseInvoiceDetail;
import com.chenfan.finance.producer.U8Produce;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author 2062
 */
@Service
@Slf4j
public class PurBillVouchProduceService extends U8Produce {

    @Value("${cf.rocketmq.topicInvoiceToFinanceToU8}")
    private String springTopic;

    @Async
    public void syncSendWdtRecordToMq(PurchaseInvoice purchaseInvoice, List<PurchaseInvoiceDetail> detailList, String invoiceNo) {
        try {
            String businessCode = purchaseInvoice.getPurchaseInvoiceCode();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("purchase_invoice", purchaseInvoice);
            jsonObject.put("purchase_invoice_detail", detailList);
            jsonObject.put("msg_id", businessCode);
            log.info("推送u8发票信息： {}", jsonObject.toJSONString());
            SendResult sendResult = send(businessCode, jsonObject.toJSONString(), springTopic);
            //  这里修改消息表
            insertMqMsg(sendResult, businessCode);
        } catch (Exception e) {
            log.error("账单生成发票失败", e);
            throw new RuntimeException("账单生成发票失败:" + invoiceNo);
        }
    }
}
