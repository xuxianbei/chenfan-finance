package com.chenfan.finance.model.dto;

import com.chenfan.finance.model.ApprovalFlow;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 2062
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApprovalFlowDTO extends ApprovalFlow {

    private List<String> targetUserId;

    private List<String> targetUserName;

    private String srcCode;
}
