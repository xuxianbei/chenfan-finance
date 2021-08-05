package com.chenfan.finance.model.vo;

import com.chenfan.finance.model.RuleBillingDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
/**
 * @author liran
 */
@Data
public class RuleBillingHeaderVo {
    @ApiModelProperty(value="计费方案id")
    private Long ruleBillingId;
    @ApiModelProperty(value="计费方案No")
    private String ruleBillingNo;
    @ApiModelProperty(value="计费方案名称")
    private String ruleBillingName;
    @ApiModelProperty(value="业务类型(1=货品采购,2=销售单,3=入库费,4,出库费,5 ,仓储费,6,行政费用,7，其他)")
    private String businessType;
    @ApiModelProperty(value="状态,初始为1，(0=已删除, 1=正常, 2=已停用）")
    private Integer ruleBillingStatus;
    @ApiModelProperty(value="方案失效日")
    private LocalDateTime beginDate;
    @ApiModelProperty(value="方案生效日")
    private LocalDateTime endDate;
    @ApiModelProperty(value="备注")
    private String remark;

    private Long companyId;

    private Long createBy;

    private String createName;

    private Date createDate;

    private Long updateBy;

    private String updateName;

    private Date updateDate;

    private List<RuleBillingDetail> delayList;
    private List<RuleBillingDetail> qcList;

}