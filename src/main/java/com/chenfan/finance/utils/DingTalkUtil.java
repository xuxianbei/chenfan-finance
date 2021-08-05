package com.chenfan.finance.utils;

import com.chenfan.ccp.util.start.ApplicationContextUtil;
import com.chenfan.finance.producer.U8Produce;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;

import java.util.Arrays;

/**
 * @Author Wen.Xiao
 * @Description // 发送钉钉消息
 * @Date 2021/4/2  13:42
 * @Version 1.0
 */
public class DingTalkUtil {

    public static  OapiRobotSendResponse sendTextToDingTalk(String textString){

        DingTalkClient client = ApplicationContextUtil.getContext().getBean(DingTalkClient.class);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent("告警："+textString);
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setIsAtAll(true);
        request.setAt(at);
        try {
            OapiRobotSendResponse response = client.execute(request);
        return response;
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

}
