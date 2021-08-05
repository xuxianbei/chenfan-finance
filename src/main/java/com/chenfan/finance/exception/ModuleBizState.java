package com.chenfan.finance.exception;

import com.chenfan.common.exception.ResultState;

/**
 * @author: xuxianbei
 * Date: 2021/3/1
 * Time: 10:42
 * Version:V1.0
 */
public enum ModuleBizState implements ResultState {
    DATE_ERROR(180001, "数据异常"), DATE_ERROR_DELETE(180002, "数据异常: %s 不允许删除"),
    USER_LOGIN_ERROR(180003, "用户登录失效"), SYSTEM_BUSY(180004, "业务繁忙，请稍后重试"),
    TAX_INVOICE_ADD(180005, "相同结算主体、费用种类、收付类型、来源单号才能申请开票"),
    INVOICE_BALANCE_ERROR(180006, "结算主体不一致，请重新选择"),
    INVOICE_TAX_INVOICE_ERROR(180007, "账单状态为待打款才能填写发票"),
    BANK_AND_CASH_VERTIFY_ERROR(180008, "费用【核销余额】合计必须小于等于实收付的【实收付核销余额】合计才能被核销；"),
    BANK_AND_CASH_EXCEL_OUTPUT_ERROR(180009, "不存在失败原因或者失败原因文件超过48小时过期了"),
    DATE_ERROR_BIGDECIMAL_ZERO(180010, "数据异常_金额必须大于0"),
    BANK_AND_CASH_RECORDSEQNO_ERROR(180011, "实收付流水号重复"),
    DATE_ERROR_BUSINESS(180012, "数据异常: %s"),
    DATE_ERROR_CLEAR_VERITY(1800013, "费用来源单号、结算主体、收付类型、账单抬头需一致！"),
    IMPORT_SIZE_ERROR(180014, "导入数量异常：导入数据量不得超过1000或者导入文件为空"),
    UNDEFINE_ERROR(1800015, "未定义异常"),
    IMPORT_CONTENT_EMPTY(189004, "上传内容为空"),
    IMPORT_TEMPLATE_WRONG(189005, "导入模板有误"),
    IMPORT_FAILED_WITH_REASON_DOWNLOAD(189006, "批量开票失败，请查看失败原因"),
    STATE_ERROR(189007, "状态错误，非预期的状态"),
    TARGET_DATE_ERROR(189008, "数据重复：已经是目标状态，请刷新"),
    SETTLE_STATE_ERROR(189009, "数据异常：只有【待提交】才能编辑【打印结算单】"),
    HOT_MAN_VERTIFY_ERROR(189010, "请先打印结算单"),
    BANK_CASH_BATCH_DELETE_ERROR(189011, "实收付记录有关联未作废核销单，请先作废核销单。"),
    TAX_INVOICE_LATER_FILL_ERROR(189012, "后补票需要【已核销】才能填写发票"),
    TAX_INVOICE_FILL_ERROR(189013, "发票需要审核通过才能填写发票"),
    CUSTOMER_BILLING_ID_NOT_EXIST(189014, "客户开票信息 不存在"),
    GET_BANK_MSG_ERROR(189015,"获取银行账户信息失败"),
    DONT_EDIT(189116, "非未核销与财务驳回状态下不可编辑"),
    PAYMENT_BRANCH_NOT_EQ_INVOICE_TITLE(189117, "实收付款单的交易方公司名称跟账单抬头不一致,请上传代收代付说明;"),
    CF_COMMON_BALANCE_NOT_EQ_BK_BALANCE(189118, "实收付款单的结算主体跟费用的结算主体不一致,请上传代收代付说明;"),
    CF_COMMON_NOT_FINANCEENTITY_EQ_PAY_COMPANY(189119, "实收付款单出入账公司名称跟财务主体不一致,请上传代收代付说明;"),
    CF_CLEAR_HEAD_DONT_COMMIT(189120, "包含非待提交状态核销单不可提交"),
    SPLIT_CHARGECOMMON_PART_ERROR(189121, "已核销，不支持分批开票！"),
    BANKANDCASH_INFO_IS_NULL(189122,"找不到交易流水号！"),
    BANKANDCASH_TYPE_IS_NOT_MCN(189123,"实收付业务类型错误！"),
    BANKANDCASH_TYPE_IS_NOT_AR(189124,"实收付类型错误！"),
    BANKANDCASH_STATUS_ERROR(189125,"实收付状态错误！"),
    NOWCLEARBALANCE_MORE_THAN_OVERAGE(189126,"核销金额大于费用核销余额！"),
    NOWCLEARBALANCE_MORE_THAN_BALANCEBALANCE(189127,"核销金额大于实收付核销余额！"),
    ;

    private int code;
    private String message;

    ModuleBizState(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String format(String msg) {
        return String.format(this.message, msg);
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
