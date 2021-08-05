package com.chenfan.finance.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

/**
 * 批量修改费用状态
 *
 * @author liran
 */
@Data
@ToString
public class UpdateChargeStatusDTO {
    private Set<Long> chargeIds;
    private Integer status;
}
