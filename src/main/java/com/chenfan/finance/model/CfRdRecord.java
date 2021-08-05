package com.chenfan.finance.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 入库单
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_rd_record")
public class CfRdRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 入库单ID
     */
    @TableId(value = "rd_record_id", type = IdType.AUTO)
    private Long rdRecordId;

    /**
     * 入库单号
     */
    private String rdRecordCode;

    /**
     * 业务类型(普通采购)
     */
    private String cbusType;

    /**
     * 仓库编码
     */
    private String cwhCode;

    /**
     * 到货单主键id
     */
    private Long puArrivalId;

    /**
     * 到货单号
     */
    private String puArrivalCode;

    /**
     * 退次单ID
     */
    private Long rjRetiredId;

    /**
     * 退次单号
     */
    private String rjRetiredCode;

    /**
     * 供应商主键ID
     */
    private Long vendorId;

    /**
     * 供应商编码
     */
    private String vendorCode;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 首返单
     */
    private Integer orderType;

    /**
     * 审核状态
     */
    private Boolean state;

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
     * 备注
     */
    private String remark;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 发票号
     */
    private String saleBillid;

    /**
     * 红蓝标识（1蓝字 -1红字） 1：采购  不等于1的都是退次
     */
    private Integer bredVouch;

    /**
     * 审核时是否已推送到旺店通
     */
    private Integer reachWangdiantong;

    /**
     * 发货单状态(0未成功1成功)
     */
    private Integer deliveryStatus;

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
     * 是否删除(0:有效；1:删除)
     */
    private Boolean isDelete;

    /**
     * 入库通知单类型（0成品 1辅料）
     */
    private Integer rdRecordType;

    /**
     * 推送仓库
     */
    private String pushWarehouse;

    private Long companyId;

    /**
     * 是否生成过费用,0未生成 1 已生成
     */
    private Boolean createChargeFlag;

}
