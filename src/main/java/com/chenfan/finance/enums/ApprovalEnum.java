package com.chenfan.finance.enums;

import com.chenfan.finance.service.common.TaxInvoiceCommonService;
import com.chenfan.finance.service.impl.CfBankAndCashServiceImpl;
import com.chenfan.finance.service.impl.CfClearHeaderServiceImpl;
import com.chenfan.finance.service.impl.CfInvoiceHeaderServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author chenguopeng
 * @Date 2021/5/24 13:53
 */
@AllArgsConstructor
@Getter
public enum ApprovalEnum {
	/**
	 * 业务对应-流程id
	 */
	TAX_INVOICE_APPROVAL(1366992970401710080L,TaxInvoiceCommonService .class, "开票审批流", "drawer", "view/baseData/invoiceManage/detail"),

	INVOICE_APPROVAL(1371280750556479488L, CfInvoiceHeaderServiceImpl.class, "账单", "drawer", "view/baseData/mcnAccountant/comps/detail"),

	CFCLEAR_APPROVAL(1371280567689019392L, CfClearHeaderServiceImpl.class, "核销单", "page", "billVerificationMcn"),

	BANK_AND_CASH_APPROVAL(1371280443780890624L, CfBankAndCashServiceImpl.class, "新增实收付", "drawer", "view/financialSettlementManagement/actualPayManagement/mcnDetail");



	/**
	 * approvalTemplateId 流程那边的模板id改了名字，改数据比较麻烦 所以备注说明一下
	 */
	private final long processId;
	private final Class<?> clazz;
	private final String processName;
	private final String type;
	private final String url;

	public static Class<?> getServiceClass(Long processId) {
		ApprovalEnum[] approvalEnums = ApprovalEnum.values();
		for (ApprovalEnum approvalEnum : approvalEnums) {
			if (approvalEnum.getProcessId() == processId) {
				return approvalEnum.getClazz();
			}
		}
		return null;
	}
}
