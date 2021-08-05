package com.chenfan.finance.config;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Wen.Xiao
 * @Description // 发送钉钉消息配置
 * @Date 2021/4/2  14:00
 * @Version 1.0
 */

@Configuration
@RefreshScope
public class DingTalkConfig {

    @Value("${dingTalk.serverurl:https://oapi.dingtalk.com/robot/send?access_token=08569ff9ff529ee868f3c5716b921b21daaa0d7ad2d1aaa5f5d1beb56db8cf74}")
    private String serverUrl;
    @Bean
    public DingTalkClient dingTalkClient(){
         return new DefaultDingTalkClient(serverUrl);
    }
}
