package com.chenfan.finance.model.dto;

import com.chenfan.finance.commons.utils.validategroup.Create;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author Wen.Xiao
 * @Description // 修改采购的合同日期
 * @Date 2021/5/28  15:05
 * @Version 1.0
 */
@Data
public class UpdatePoDetailConDto {
    /**
     * 采购单ID
     */
    @NotNull
    private Long poId;
    /**
     * 合同开始日期
     *
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime conStartDate;
    /**
     * 合同结束日期
     * 1：不传日期时间，检查当前采购单是否可以修改日期
     * 2：传日期时间，检查并更新对应的数据
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime conEndDate;
}
