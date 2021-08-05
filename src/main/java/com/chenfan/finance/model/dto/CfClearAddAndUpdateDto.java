package com.chenfan.finance.model.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author:lywang
 * @Date:2020/8/22
 */
@ApiModel(value = "财务-核销新增和更新")
@Data
public class CfClearAddAndUpdateDto {
    /**
     * 核销单号
     */
    @ApiModelProperty("核销单号")
    @NotBlank(groups = {update.class})
    private String clearNo;

    /**
     * 主键id
     */
    @NotNull(groups = {McnUpdate.class})
    private Long clearId;

    /**
     * 核销方式(0=转帐; 1=现金; 2=支票，）
     */
    @ApiModelProperty("核销方式(0=转帐; 1=现金; 2=支票，）")
    private String clearMethod;

    @ApiModelProperty("核销类型：0：收，1：付")
    private String clearType;

    /**
     * 品牌
     */
    @ApiModelProperty("品牌id")
    private Long brandId;

    /**
     * 结算主体
     */
    @ApiModelProperty("结算主体")
    private String balance;

    /**
     * 财务人员
     */
    @ApiModelProperty("财务人员")
    private String fiUser;

    /**
     * 核销时间
     */
    @ApiModelProperty("核销日期")
    private LocalDateTime clearDate;

    /**
     * 核销人id  当前用户
     */
    @ApiModelProperty("核销人id")
    private Long clearBy;

    @ApiModelProperty("费用id列表")
    @Size(min = 1, groups = {update.class, Mcn.class})
    @NotNull(groups = Mcn.class)
    private List<Long> chargeIds;

    @ApiModelProperty("实收付流水号")
    @Size(min = 1, max = 10, groups = {update.class, Mcn.class}, message = "实收付流水号不能超过10个")
    private List<String> recordSeqNos;

    @ApiModelProperty("备注")
    @Length(max = 500, message = "输入长度不能超过500个字符")
    private String remark;

    /**
     * v1.4.0
     * 交易流水号
     */
    @ApiModelProperty("交易流水号")
    @NotNull(groups = {Mcn.class,McnUpdate.class})
    private String recordSeqNo;

    /**
     * v1.4.0
     * 核销金额
     */
    @ApiModelProperty("核销金额")
    @NotNull(groups = {McnUpdate.class})
    private BigDecimal nowClearBalance;

    /**
     * v1.4.0
     * 汇款截图
     */
    @ApiModelProperty("汇款截图")
    private String receiptScreenshot;

    /**
     * v1.4.0
     * 代收代付说明
     */
    @ApiModelProperty("代收代付说明")
    private String collectionAndPayRemark;

    /**
     * v1.4.0
     * 核销单号
     */
    @ApiModelProperty("核销单ids")
    private List<Long> clearIds;



    public interface update {

    }

    public interface Mcn {

    }

    public interface McnUpdate {

    }

    public interface batchCommit {

    }
}
