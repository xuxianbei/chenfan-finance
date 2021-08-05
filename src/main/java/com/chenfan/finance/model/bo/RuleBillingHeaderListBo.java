package com.chenfan.finance.model.bo;

import com.chenfan.common.dto.PagerDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author lqliu
 */
@Data
public class RuleBillingHeaderListBo extends PagerDTO {
    @ApiModelProperty(value="计费方案id")
    private Long ruleBillingId;
    @ApiModelProperty(value="计费方案No")
    private String ruleBillingNo;
    @ApiModelProperty(value="计费方案名称")
    private String ruleBillingName;
    @ApiModelProperty(value="业务类型(1=货品采购,2=销售单,3=入库费,4=出库费,5=仓储费,6=行政费用,7=其他)")
    private String businessType;
    @ApiModelProperty(value="状态,初始为1，(0=已删除, 1=正常, 2=已停用)")
    private Integer ruleBillingStatus;
    @ApiModelProperty(value="方案失效日")
    private Date beginDate;
    @ApiModelProperty(value="方案生效日")
    private Date endDate;
    @ApiModelProperty(value="备注")
    private String remark;

    private Long companyId;

    private Long createBy;

    private String createName;

    @ApiModelProperty(value = "创建时间Start")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateStart;

    @ApiModelProperty(value = "创建时间End")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateEnd;

    private Date createDate;

    private Long updateBy;

    private String updateName;

    private Date updateDate;

}