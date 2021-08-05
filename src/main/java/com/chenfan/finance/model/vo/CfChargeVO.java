package com.chenfan.finance.model.vo;

import com.chenfan.finance.model.CfCharge;
import lombok.Data;

import java.util.List;

/**
 * @author xiongbin
 * @date 2020-09-07
 */
@Data
public class CfChargeVO extends CfCharge {
	/**
	 * 结算主体简称
	 */
	private String balanceName;
	/**
	 * 费用种类名称
	 */
	private String chargeTypeName;
	/**
	 * 费用来源类型-前端展示
	 */
	private String chargeSourceName;

	private List<CfChargeSkuVO> cfChargeSkuVOList;

	private String chargeIds;
}
