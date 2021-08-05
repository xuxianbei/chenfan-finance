package com.chenfan.finance.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeRange {

    /**
     * 每月第一天 00:00:00
     */
    private LocalDateTime start;

    /**
     * 每月最后一天 23:59:59
     */
    private LocalDateTime end;
}
