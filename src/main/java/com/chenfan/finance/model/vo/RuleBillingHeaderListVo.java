package com.chenfan.finance.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rule_billing_header")
public class RuleBillingHeaderListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计费方案id
     */
    @TableId(value = "rule_billing_id", type = IdType.AUTO)
    private Long ruleBillingId;

    /**
     * 计费方案号
     */
    private String ruleBillingNo;

    /**
     * 计费方案名称
     */
    private String ruleBillingName;

    /**
     * 业务类型；新增时选择，（1=货品采购,2=销售单,3=入库费,4,出库费,5 ,仓储费,6,行政费用,7，其他）
     */
    private String businessType;

    /**
     * 状态,初始为1，(0=已删除, 1=正常, 2=已停用）
     */
    private Integer ruleBillingStatus;

    /**
     * 方案生效日
     */
    private LocalDateTime beginDate;

    /**
     * 方案失效日
     */
    private LocalDateTime endDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 创建人id
     */
    private Long createBy;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新人id
     */
    private Long updateBy;

    /**
     * 更新人名称
     */
    private String updateName;

    /**
     * 更新时间
     */
    private Date updateDate;


}
