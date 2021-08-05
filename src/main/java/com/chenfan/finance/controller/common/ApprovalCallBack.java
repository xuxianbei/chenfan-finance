package com.chenfan.finance.controller.common;

import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/3/4
 * Time: 17:29
 * Version:V1.0
 */
@Data
public class ApprovalCallBack {
    /**
     * 审批实例ID
     */
    private Long approvalId;

    /**
     * 回调参数, 发起审批时传递的json字符串
     */
    private String param;

    /**
     * 事件类型
     */
    private Integer eventType;

    /**
     * 本次审批结果
     */
    private Boolean approvalFlag;


    /**
     * 审批是否结束
     */
    private Boolean approvalFinished;
    /**
     * 审批信息，可能有多个用户
     */
    private String nextApprovalUserId;

    /**
     * 审批用户名称, 可能有多个用户
     */
    private String nextApprovalUserName;

    /**
     * 拒绝原因（审批拒绝备注）
     */
    private String remark;
}
