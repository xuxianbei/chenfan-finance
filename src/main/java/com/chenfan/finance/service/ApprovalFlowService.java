package com.chenfan.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chenfan.ccp.plug.rpc.service.NotifyRemoteService;
import com.chenfan.finance.constant.McnConstant;
import com.chenfan.finance.enums.ApprovalEnum;
import com.chenfan.finance.enums.MessageEnum;
import com.chenfan.finance.model.ApprovalFlow;
import com.chenfan.finance.model.dto.ApprovalFlowDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author chenguopeng
 * @Date 2021/5/24 13:51
 */
public interface ApprovalFlowService extends IService<ApprovalFlow> {

	 void sendNotify(ApprovalFlowDTO approvalFlowDTO, Long srcId, String srcCode, ApprovalEnum approvalEnum,
	                       Boolean status, Long targetUserId, String targetUserName);


}
