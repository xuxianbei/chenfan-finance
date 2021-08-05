package com.chenfan.finance.model;

import lombok.Data;

import java.util.Date;

/**
 * @author 2062
 */
@Data
public class MqMsg {
    /**
     * mq msgId
     */
    private String msgId;

    /**
     * 业务code
     */
    private String businessCode;

    /**
     * 发送状态（取mq sendResult）
     */
    private String sendStatus;

    /**
     *
     */
    private String topic;

    /**
     * 服务名
     */
    private String applicationName;

    /**
     *
     */
    private String errorMsg;

    /**
     *
     */
    private String message;

    /**
     *
     */
    private Date sendDate;

    /**
     *
     */
    private Date consumeDate;

}