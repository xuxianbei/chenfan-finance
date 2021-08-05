package com.chenfan.finance.model.bo;

import com.chenfan.finance.model.CfInvoiceHeader;
import com.chenfan.finance.model.vo.CfChargeVO;
import com.chenfan.finance.model.vo.ChargeInVO;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiongbin
 * @date 2020-08-20
 */
@ToString
public class CfInvoiceHeaderBO implements Serializable {

	private static final long serialVersionUID = 6319775941365524565L;

	private CfInvoiceHeader invoiceHeader;
	private List<CfChargeVO> chargeList;
	private List<ChargeInVO> chargeInList;
	private Integer chargeListSize;

	public Integer getChargeListSize() {
		return chargeListSize;
	}

	public void setChargeListSize(Integer chargeListSize) {
		this.chargeListSize = chargeListSize;
	}

	public CfInvoiceHeader getInvoiceHeader() {
		return invoiceHeader;
	}

	public void setInvoiceHeader(CfInvoiceHeader invoiceHeader) {
		this.invoiceHeader = invoiceHeader;
	}

	public List<CfChargeVO> getChargeList() {
		return chargeList;
	}

	public void setChargeList(List<CfChargeVO> chargeList) {
		this.chargeList = chargeList;
	}

	public List<ChargeInVO> getChargeInList() {
		return chargeInList;
	}

	public void setChargeInList(List<ChargeInVO> chargeInList) {
		this.chargeInList = chargeInList;
	}
}
