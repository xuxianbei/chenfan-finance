package com.chenfan.finance.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chenfan.finance.model.bo.TocU8Header;
import com.chenfan.finance.producer.U8Produce;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author lr
 */
@Service
@Slf4j
public class TocU8ProduceService extends U8Produce {

    @Value("${cf.rocketmq.topicTocDataToU8}")
    private String springTopic ;

    @Async
    public void syncTocMq(TocU8Header heder) {
        Long businessCode = heder.getMappingId();
        try {
            String s = JSON.toJSONStringWithDateFormat(heder,"yyyy-MM-dd HH:mm:ss");
            log.info("开始推送TOCU8信息： {}", s);
            SendResult sendResult = send(businessCode.toString(), s, springTopic);
            //  这里修改消息表
            insertMqMsg(sendResult, businessCode.toString());
        } catch (Exception e) {
            log.error("账单toc u8", e);
            throw new RuntimeException("账单toc u8 fail:" + businessCode);
        }
    }
}
