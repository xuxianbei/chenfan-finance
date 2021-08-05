package com.chenfan.finance.model.dto;

import com.chenfan.finance.model.CfCharge;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author LR
 */
@Data
public class SplitChargeByInvoiceDTO {

    private BigDecimal splitMoney;

    /**
     * 直接归还费用池的+ 拆分出来的费用
     */
    private List<CfCharge> splitCharges;

    /**
     * 被拆过的费用
     */
    private List<CfCharge> partSplitCharges;

}