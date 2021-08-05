package com.chenfan.finance.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 预付款申请
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("advancepay_application")
public class AdvancepayApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "advance_pay_id", type = IdType.AUTO)
    private Integer advancePayId;

    /**
     * 预付款单号
     */
    private String advancePayCode;

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 申请角色id
     */
    private String firstRoleId;

    /**
     * 采购订单ID
     */
    private Long poId;

    /**
     * 采购订单号
     */
    private String poCode;

    /**
     * 品牌ID
     */
    private Integer brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 供应商主键ID
     */
    private Integer vendorId;

    /**
     * 供应商编码
     */
    private String vendorCode;

    /**
     * 审核状态 待确认_0,已确认_1,已审核_2,已提交_3,已付款_4,已完成_5,已驳回_6,已关闭_7,已打款_8
     */
    private Integer state;

    /**
     * 定金配置id
     */
    private Integer paymentConfId;

    /**
     * 付款类型
     */
    private String paymentType;

    /**
     * 任务人
     */
    private String taskPerson;

    /**
     * 职务
     */
    private String duties;

    /**
     * 收款单位
     */
    private String receiptDepartment;

    /**
     * 开户行
     */
    private String bank;

    /**
     * 付款方式
     */
    private String payment;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 发票是否已到
     */
    private Integer isArrive;

    /**
     * 附件/张
     */
    private Integer enclosure;

    /**
     * 付款用途
     */
    private String paymentUse;

    /**
     * 付款金额大写
     */
    private String moneyCapital;

    /**
     * 付款金额小写
     */
    private BigDecimal money;

    /**
     * 申请人确认
     */
    private String applyConfirmName;

    /**
     * 申请人确认时间
     */
    private LocalDateTime applyConfirmDate;

    /**
     * 理单组长
     */
    private String confirmName;

    /**
     * 审核日期
     */
    private LocalDateTime confirmDate;

    /**
     * 供应链实习助理
     */
    private String supplychainInternName;

    /**
     * 审核日期
     */
    private LocalDateTime supplychainInternDate;

    /**
     * 财务审核员
     */
    private String finaceName;

    /**
     * 审核日期
     */
    private LocalDateTime finaceDate;

    /**
     * 财务主管
     */
    private String finaceGmName;

    /**
     * 审核日期
     */
    private LocalDateTime finaceGmDate;

    /**
     * COO或者COO助理
     */
    private String cooOrHelperName;

    /**
     * 审核日期
     */
    private LocalDateTime cooOrHelperDate;

    /**
     * 部门负责人
     */
    private String department;

    /**
     * 分管总监
     */
    private String managDirector;

    /**
     * 会计
     */
    private String accountant;

    /**
     * 财务负责人
     */
    private String financechief;

    /**
     * 总经理
     */
    private String generalmanager;

    /**
     * 出纳
     */
    private String cashier;

    private Long verifyBy;

    /**
     * 审核人
     */
    private String verifyName;

    /**
     * 审核日期
     */
    private LocalDateTime verifyDate;

    /**
     * 创建人名称
     */
    private String createName;

    private Long createBy;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 更新人名称
     */
    private String updateName;

    private Long updateBy;

    /**
     * 更新日期
     */
    private LocalDateTime updateDate;

    /**
     * 改价原因
     */
    private String updatePriceReason;

    /**
     * 是否删除(0:有效；1:删除)
     */
    private Boolean isDelete;

    /**
     * 出纳已打款时间
     */
    private LocalDateTime cashierDate;

    /**
     * 打款截图
     */
    private String imgUrls;

    /**
     * 物料类型 0成衣1辅料
     */
    private Integer materialType;

    private Long companyId;

    /**
     * 费用记录 主键
     */
    private Long chargeInId;

    private String accName;
    private String invoiceNo;
}
