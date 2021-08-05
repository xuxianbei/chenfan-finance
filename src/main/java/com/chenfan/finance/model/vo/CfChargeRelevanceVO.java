package com.chenfan.finance.model.vo;

import lombok.Data;
import java.util.List;


/**
 * @author 2062
 */
@Data
public class CfChargeRelevanceVO {
	private static final long serialVersionUID = 6835039296289928327L;

	private List<Long> chargeIds;

	private String invoiceNo;
}