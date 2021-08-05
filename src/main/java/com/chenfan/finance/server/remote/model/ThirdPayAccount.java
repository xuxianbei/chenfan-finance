package com.chenfan.finance.server.remote.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author:   tangwei
 * Date:     2021/1/24 13:23
 * Description: 第三方打款账号
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("third_pay_account")
public class ThirdPayAccount extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 3662398532041208251L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账户编号
     */
    private String accountCode;

    /**
     * 打款户名
     */
    private String paymentAccount;

    /**
     * 打款平台名称
     */
    private String accountPlatform;

    /**
     * 打款银行
     */
    private String accountBank;

    /**
     * 账号
     */
    private String accountNumber;

    /**
     * 启用状态
     */
    private Boolean enableStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 手续费率
     */
    private BigDecimal handlingFeeRate;

    /**
     * 部门id
     */
    private Long departmentId;

    /**
     * 是否删除(0:有效；1:删除)
     */
    private Integer isDelete;

}