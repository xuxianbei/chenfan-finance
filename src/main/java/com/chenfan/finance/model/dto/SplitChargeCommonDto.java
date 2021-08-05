package com.chenfan.finance.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * 拆分费用
 *
 * @author: xuxianbei
 * Date: 2021/4/16
 * Time: 15:45
 * Version:V1.0
 */
@Data
public class SplitChargeCommonDto {

    /**
     * 费用ID
     */
    @NotNull
    private Long masterChargeId;

    /**
     * 选中费用
     */
    @Range(min = 0, max = 9)
    @NotNull
    private Integer selectId;

    /**
     * 拆分列表
     */
    @Size(max = 10, min = 2)
    private List<BigDecimal> list;

}
