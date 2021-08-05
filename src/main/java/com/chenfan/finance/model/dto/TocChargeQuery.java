package com.chenfan.finance.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author liran
 */
@Data
public class TocChargeQuery {
    private LocalDateTime start;

    private LocalDateTime end;

    private Integer shopId;




}
