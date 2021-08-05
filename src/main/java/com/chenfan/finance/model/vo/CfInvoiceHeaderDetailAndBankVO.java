package com.chenfan.finance.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xiongbin
 * @date 2020-08-28
 */
@Data
public class CfInvoiceHeaderDetailAndBankVO  extends CfInvoiceHeaderDetailVO {
	/**
	 * 预付款单号
	 */
	private String advancePayNos;
	/**
	 * 采购实付金额
	 */
	private BigDecimal poMoney;
	/**
	 * 预付款总金额
	 */
	private BigDecimal advancePayMoney;
	/**
	 * 本次实收付金额
	 */
	private BigDecimal needPayMoney;
}
