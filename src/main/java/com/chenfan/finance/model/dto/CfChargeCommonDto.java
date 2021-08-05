package com.chenfan.finance.model.dto;

import com.chenfan.common.dto.PagerDTO;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * @author: xuxianbei
 * Date: 2021/3/1
 * Time: 10:04
 * Version:V1.0
 */
@Data
public class CfChargeCommonDto extends PagerDTO {

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 创建开始时间
     */
    private LocalDateTime BeginDate;

    /**
     * 创建结束时间
     */
    private LocalDateTime endDate;

    /**
     * 收付类型 AR=收、AP=付；
     */
    private String arapType;

    /**
     * 是否结算 1：是 0： 否
     */
    @Min(0)
    @Max(1)
    private Integer settled;

    /**
     * 1= MCN
     */
    private Integer chargeSourceType;

    /**
     * 费用种类 1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费
     */
    @Range(min = 1, max = 6)
    private Integer chargeType;

    /**
     * 来源单号
     */
    private String chargeSourceCode;

    /**
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    private String financeEntity;

    /**
     * 费用编号
     */
    private String chargeCode;


    /**
     * 0：未核销；1：已核销；2：部分核销
     */
    private Integer clearState;

    /**
     * 0：未提交申请；1：已提交申请
     */
    private Integer submitApplicationState;
}
