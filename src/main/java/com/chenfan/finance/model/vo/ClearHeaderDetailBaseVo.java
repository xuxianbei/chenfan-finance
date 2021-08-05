package com.chenfan.finance.model.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author: xuxianbei
 * Date: 2020/8/25
 * Time: 15:59
 * Version:V1.0
 */
@Data
@ApiModel(value = "财务-核销基本信息")
public class ClearHeaderDetailBaseVo {

    /**
     * 主键id
     */
    private Long clearId;

    /**
     * "核销状态：(1=未核销， 2=已核销,0=已删除)新建都是1，选择实收付款单后变为2。1状态下可以直接删除，变为0，3：审批中，4：审批拒绝，5：已撤回，6：已作废"
     */
    private Integer clearStatus;

    /**
     * 核销编号
     */
    private String clearNo;

    /**
     * 收入
     */
    private BigDecimal nowClearDebit;

    /**
     * 支出
     */
    private BigDecimal nowClearCredit;

    /**
     * 核销方式(0=转帐; 1=现金; 2=支票
     */
    private String clearMethod;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 结算主体code
     */
    private String balanceCode;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 银行名称
     */
    private String bank;

    /**
     * 银行帐号
     */
    private String bankNo;

    /**
     * 财务人员
     */
    private String fiUser;

    /**
     * 核销日期
     */
    private LocalDateTime clearDate;

    /**
     * 核销人
     */
    private String clearName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 账单号
     */
    private String invoiceNo;

    /**
     * 费用总计类型
     */
    private String clearType;

    /**
     * v1.4.0
     * 交易流水号
     */
    private String recordSeqNo;

    /**
     * v1.4.0
     * 汇款截图
     */
    private String receiptScreenshot;

    /**
     * v1.4.0
     * 代收代付说明
     */
    private String collectionAndPayRemark;

    /**
     * v1.4.0
     * 驳回理由
     */
    private String rejectReason;

    /**
     * v4.9.0
     * 平台订单号
     */
    private String platformOrderNumber;

    /**
     * v4.9.0
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    private String financeEntity;

    /**
     * v4.9.0
     * 创建人名称
     */
    private String createName;


}
