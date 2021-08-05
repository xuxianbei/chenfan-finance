package com.chenfan.finance.server.remote.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * author:   tangwei
 * Date:     2021/4/16 14:29
 * Description: 新增评论
 */
@ApiModel(value = "新增评论")
@Data
@Builder
public class CommentAddDTO implements Serializable {

    private static final long serialVersionUID = -2579851380565784939L;

    /**
     * 业务id（如执行单id）
     */
    @ApiModelProperty("业务id（如执行单id）")
    @NotNull
    private Long businessId;

    /**
     * 业务单号（如执行单号）
     */
    @ApiModelProperty("业务单号（如执行单号）")
    @NotNull
    private String businessCode;

    /**
     * 业务类型（1收入合同，2执行单，3年框合同，4核销单，5开票单）
     */
    @NotNull
    @ApiModelProperty("业务类型（1收入合同，2执行单，3年框合同，4核销单，5开票单）")
    private Integer businessType;

    /**
     * 评论内容
     */
    @ApiModelProperty("评论内容")
    private String commentContent;

    /**
     * 评论附件
     */
    @ApiModelProperty("评论附件")
    private String commentFile;

    /**
     * @人集合JSON串
     */
    @ApiModelProperty("@人集合-JSON串")
    private String atJson;


}