package com.chenfan.finance.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 发票表
 * </p>
 *
 * @author lizhejin
 * @since 2020-10-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("purchase_invoice")
public class PurchaseInvoice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "purchase_invoice_id")
    private Long purchaseInvoiceId;

    /**
     * 发票号
     */
    private String purchaseInvoiceCode;

    /**
     * 对方发票号：业务自填发票号
     */
    private  String customerInvoiceNo;

    /**
     * 供应商主键ID
     */
    private Integer vendorId;

    /**
     * 供应商编码
     */
    private String vendorCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 结算ID
     */
    private Integer accountBillId;

    /**
     * 结算单号
     */
    private String accountBillCode;

    /**
     * 发票类型
     */
    private String invoiceType;

    /**
     * 所属部门
     */
    private String sysOrgCode;

    /**
     * 所属公司
     */
    private String sysCompanyCode;

    /**
     * 创建人名称
     */
    private String createName;

    private Long createBy;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 更新人名称
     */
    private String updateName;

    private Long updateBy;

    /**
     * 更新日期
     */
    private Date updateDate;

    /**
     * 是否删除(0:有效；1:删除)
     */
    private Boolean isDelete;

    private Long companyId;

    @TableField(exist = false)
    private Long brandId;

    @TableField(exist = false)
    private String accountScreen;

    /**
     * 销售类型 1直播2正常
     */
    private Integer inventoryType;
}
