package com.chenfan.finance.producer;

import com.chenfan.common.vo.Response;
import com.chenfan.finance.model.MqMsg;
import com.chenfan.finance.server.BaseRemoteServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * @author 2062
 */
@Slf4j
@Component
public class U8Produce implements ApplicationContextAware {

    @Resource
    private RocketMQTemplate rocketTemplate;
    @Resource
    private BaseRemoteServer baseRemoteServer;

    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        U8Produce.applicationContext = applicationContext;
    }

    @Async
    public SendResult send(String businessCode, String ms, String springTopic) throws Exception {
        //生成message类型
        Message<byte[]> message = MessageBuilder.withPayload(ms.getBytes(StandardCharsets.UTF_8)).build();
        SendResult sendResult = rocketTemplate.syncSendOrderly(springTopic, message, springTopic);
        log.info("{},mq send business_code: {}", springTopic, businessCode);
        log.info("mq send message: {}", ms);
        log.info("mq send result: {}", sendResult);
        return sendResult;
    }

    protected void insertMqMsg(SendResult sendResult, String businessCode) {
        MqMsg msg = new MqMsg();
        msg.setMsgId(sendResult.getOffsetMsgId());
        msg.setBusinessCode(businessCode);
        msg.setApplicationName("chenfan-cloud-finance");
        msg.setSendStatus(sendResult.getSendStatus().toString());
        msg.setTopic(sendResult.getMessageQueue().getTopic());
        Response<Object> send = baseRemoteServer.send(msg);
        Assert.isTrue(send != null && send.getCode() == HttpStatus.OK.value(), "保存消息失败");
    }
}
