package com.chenfan.finance.model.bo;

import com.baomidou.mybatisplus.annotation.TableField;

import com.chenfan.finance.enums.TocMappingTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * toc_toc_alipay_origin
 * @author 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TocAlipayOrigin implements Serializable {
    /**
     * 账务流水号
     */
    @ApiModelProperty("账务流水号")
    private String financeNo;

    /**
     * 业务流水号
     */
    @ApiModelProperty("业务流水号")
    private String businessNo;

    /**
     * 主订单号(商户订单号)
     */
    @ApiModelProperty("主订单号(商户订单号)")
    private String tid;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String goodsName;

    /**
     * 到账时间(发生时间)
     */
    @ApiModelProperty("到账时间(发生时间)")
    private LocalDateTime accountDate;

    /**
     * 对方账户
     */
    @ApiModelProperty("对方账户")
    private String oppositeAccount;

    /**
     * 收入金额
     */
    @ApiModelProperty("收入金额")
    private BigDecimal incomeAmount;

    /**
     * 支出金额
     */

    @ApiModelProperty("支出金额")
    private BigDecimal expendAmount;

    /**
     * 账户余额
     */
    @ApiModelProperty("账户余额")
    private BigDecimal accountLeftAmount;

    /**
     * 交易渠道 0支付宝
     */
    @ApiModelProperty("交易渠道 0支付宝")
    private Integer payWay;

    /**
     * 业务类型"交易付款_0", "在线支付_1", "交易退款_2", "交易分账_3", "其它_4", "收费_5", "提现_6", "转账_7", "红包退回_8","保证金_9","退款_10","退款（交易退款）_11","提现_12","分账_13"
     */
    @ApiModelProperty("业务类型\"交易付款_0\", \"在线支付_1\", \"交易退款_2\", \"交易分账_3\", \"其它_4\", \"收费_5\", \"提现_6\", \"转账_7\", \"红包退回_8\",\"保证金_9\",\"退款_10\",\"退款（交易退款）_11\",\"提现_12\",\"分账_13")
    private Integer businessType;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 商家账户
     */
    @ApiModelProperty("商家账户")
    private String shopAlipayAccount;

    @ApiModelProperty(value = "是否包含订单数据 0：否，9：是")
    private Integer checkFlag;

    /**
     * @see TocMappingTypeEnum
     */
    @ApiModelProperty(value = "匹配类型",hidden = true)
    private Integer checkType;


    private static final long serialVersionUID = 1L;

}