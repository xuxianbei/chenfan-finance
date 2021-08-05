package com.chenfan.finance.server.remote.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName(value = "s_company_bank")
public class SCompanyBank implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "detail_id", type = IdType.AUTO)
    private Long detailId;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    private String companyName;

    /**
     * 户名
     */
    private String accountName;

    /**
     * 卡号
     */
    private String cardNumber;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人id
     */
    private Long createBy;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 创建时间
     */
    private Date createTime;

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
    private Date updateTime;

}