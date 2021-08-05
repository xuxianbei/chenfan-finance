package com.chenfan.finance.model.dto;

import com.chenfan.finance.model.CfRdRecordDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * @author liran
 */
@Data
public class RdRecordDetailPrePay {

    /**
     * 款号
     */
    private String productCode;
    /**
     * 成本价
     */
    private BigDecimal costsPrice;

    /**
     * 采购税率
     */
    private BigDecimal taxRate;

    private List<CfRdRecordDetail> details;

    private List<Long> poIds;
}
