package com.chenfan.finance.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author 2062
 */
@Data
@ApiModel(value = "财务-核销列表实体")
public class ClearHeaderVo {

    /**
     * 核销id
     */
    private Long clearId;

    @ApiModelProperty(value = "核销状态 1=未核销， 2=已核销,0=已删除")
    @Excel(name = "核销方式", replace = {"已删除_0", "未核销_1", "已核销_2"}, width = 30)
    private Integer clearStatus;

    @ApiModelProperty(value = "核销单号")
    @Excel(name = "核销单号",width = 30)
    private String clearNo;

    @ApiModelProperty(value = "核销类型：AR：收，AP：付")
    @Excel(name = "核销方式", replace = {"收_AR", "付_AP"}, width = 30)
    private String clearType;

    @ApiModelProperty(value = "核销方式(0=转帐; 1=现金; 2=支票")
    @Excel(name = "核销方式", replace = {"转帐_0", "现金_1", "支票_2"}, width = 30)
    private String clearMethod;

    /**
     * 业务类型(1货品采购; 2销售订单;3MCN)
     */
    private Integer jobType;

    @ApiModelProperty(value = "品牌名称")
    @Excel(name = "品牌名称",width = 30)
    private String brandName;

    @ApiModelProperty(value = "结算主体")
    @Excel(name = "结算主体",width = 30)
    private String balance;

    @ApiModelProperty(value = "结算主体名称")
    @Excel(name = "结算主体名称",width = 30)
    private String balanceName;

    @ApiModelProperty(value = "本次核销总计")
    @Excel(name = "本次核销总计",width = 30)
    private BigDecimal nowClearBalance;

    @ApiModelProperty(value = "本次核销余额")
    @Excel(name = "本次核销余额",width = 30)
    private BigDecimal nowBalanceBalance;

    @ApiModelProperty(value = "核销人")
    @Excel(name = "核销人",width = 30)
    private String clearName;

    @ApiModelProperty("财务人员")
    @Excel(name = "财务人员",width = 30)
    private String fiUser;

    @ApiModelProperty(value = "核销时间")
    @Excel(name = "核销时间",width = 30)
    private LocalDateTime clearDate;

    @ApiModelProperty(value = "创建日期")
    private LocalDateTime createDate;

    /**
     * v1.4.0
     * 驳回理由
     */
    @ApiModelProperty(value = "驳回理由")
    private String rejectReason;

    /**
     * v4.9.0
     * 出账公司  交易方-公司名称
     */
    private String paymentBranch;

    /**
     * v4.9.0
     * 入账公司--出入账公司信息
     */
    private String payCompany;

    /**
     * v4.9.0
     * 实收付总金额
     */
    private BigDecimal amount;

    /**
     * v4.9.0
     * 交易流水号
     */
    private String recordSeqNo;
}
