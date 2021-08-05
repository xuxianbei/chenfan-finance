package com.chenfan.finance.server.remote.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("base_customer_billing")
public class BaseCustomerBilling implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "billing_id", type = IdType.AUTO)
    private Long billingId;

    /**
     * 客户代码
     */
    private String customerCode;

    /**
     * 顺序栏
     */
    private String customerOrder;

    /**
     * 开票抬头
     */
    @Excel(name = "开票抬头")
    private String invoiceTitle;

    /**
     * 开票抬头状态
     */
    @Excel(name = "开票抬头状态")
    private String invoiceTitleType;

    /**
     * 纳税人识别号
     */
    @Excel(name = "纳税人识别号")
    private String taxpayerIdentificationNumber;

    /**
     * 开票地址
     */
    @Excel(name = "开票地址")
    private String billingAddress;

    /**
     * 开票电话
     */
    @Excel(name = "开票电话")
    private String billingTel;

    /**
     * 开户账户
     */
    @Excel(name = "开户账户")
    private String billingAccount;

    /**
     * 开户银行
     */
    @Excel(name = "开户银行")
    private String billingBank;
    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

    /**
     * 删除标识 0：否 1：是
     */
    private Integer isDeleted;

    /**
     * 修改人id
     */
    private Long updateBy;

    /**
     * 修改人
     */
    private String updateName;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;


    /**
     * 公司id
     */
    private Long companyId;


    /**
     * 租户id
     */
    private Long tenantId;

}
