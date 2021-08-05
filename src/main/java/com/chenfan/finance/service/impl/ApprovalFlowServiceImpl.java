package com.chenfan.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenfan.ccp.plug.rpc.service.NotifyRemoteService;
import com.chenfan.ccp.plug.rpc.service.impl.DefaultNotifyRemoteServiceImpl;
import com.chenfan.finance.constant.McnConstant;
import com.chenfan.finance.dao.ApprovalFlowMapper;
import com.chenfan.finance.enums.ApprovalEnum;
import com.chenfan.finance.enums.MessageEnum;
import com.chenfan.finance.model.ApprovalFlow;
import com.chenfan.finance.model.dto.ApprovalFlowDTO;
import com.chenfan.finance.service.ApprovalFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @Author chenguopeng
 * @Date 2021/5/24 15:02
 */
@Service
public class ApprovalFlowServiceImpl extends ServiceImpl<ApprovalFlowMapper, ApprovalFlow> implements ApprovalFlowService {
	@Bean
	public NotifyRemoteService notifyRemoteService() {
		return new DefaultNotifyRemoteServiceImpl();
	}

	@Autowired
	private NotifyRemoteService notifyRemoteService;

	@Override
	public void sendNotify(ApprovalFlowDTO approvalFlowDTO, Long srcId, String srcCode, ApprovalEnum approvalEnum,
	                       Boolean status, Long targetUserId, String targetUserName) {
		String message = Objects.isNull(status) ? null : String.format((status ? McnConstant.NOTIFY_SUCCESS_TEMPLATE : McnConstant.NOTIFY_FAILURE_TEMPLATE), approvalEnum.getProcessName(), srcCode);
		Integer notifyType = Objects.isNull(status) ? MessageEnum.TO_DO_MESSAGE.getNotifyType() : MessageEnum.NOTIFY_MESSAGE.getNotifyType();
		notifyRemoteService.saveNotify(srcId, notifyType, 1, approvalEnum.getProcessId(), approvalEnum.getProcessName()
				, approvalEnum.getUrl(), approvalEnum.getType(), message, targetUserId, targetUserName
				, approvalFlowDTO.getApprovalId(), approvalFlowDTO.getProcessId().toString(), approvalFlowDTO.getSrcCode());
	}

}
