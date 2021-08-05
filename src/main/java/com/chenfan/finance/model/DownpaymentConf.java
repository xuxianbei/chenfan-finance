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
 * 定金配置
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("downpayment_conf")
public class DownpaymentConf implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "payment_conf_id", type = IdType.AUTO)
    private Integer paymentConfId;

    /**
     * 定金名称
     */
    private String paymentName;

    /**
     * 定金比例
     */
    private BigDecimal proportion;

    /**
     * 尾款名称
     */
    private String tailName;

    /**
     * 尾款比例
     */
    private BigDecimal tailProportion;

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

    private Long companyId;


}
