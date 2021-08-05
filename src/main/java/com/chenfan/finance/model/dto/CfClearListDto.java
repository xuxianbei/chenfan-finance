package com.chenfan.finance.model.dto;

import com.chenfan.common.dto.PagerDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 2062
 */
@ApiModel(value = "财务-核销列表")
@Data
public class CfClearListDto extends PagerDTO {

    @ApiModelProperty("核销单号")
    private String clearNo;

    @ApiModelProperty(value = "核销人id")
    private Long clearBy;

    @ApiModelProperty(value = "核销类型：0：收，1：付")
    private String clearType;

    @ApiModelProperty(value = "核销方式(0=转帐; 1=现金; 2=支票")
    private String clearMethod;

    @ApiModelProperty(value = "结算主体")
    private String balance;

    /**
     * 品牌id
     */
    private Integer brandId;

    /**
     * 核销状态 1=未核销， 2=已核销,0=已删除
     */
    private Integer clearStatus;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 业务类型(1货品采购; 2销售订单;3MCN)
     */
    @Range(min = 1, max = 3)
    private Integer jobType;
}
