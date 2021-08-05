package com.chenfan.finance.enums;


/**
 * @author mbji
 */
public enum ErrorMessageEnum {
    /**
     * 打开明细失败
     * */
    OPENDETAIL_ERROR("打开明细失败"),
    /**
     *仅允许理单员操作
     * */
    ONLY_OM("仅允许理单员操作"),
    /**
     *已经结算过了，不能申请付款
     * */
    ONLY_NO_DOWN_ORDER("已经结算过了，不能申请付款"),
    /**
     *失败
     * */
    FAIL("失败"),
    /**
     *审核过的采购单才能弃审
     * */
    ABANDON_NOT_AUDIT("审核过的采购单才能弃审"),
    /**
     *只能关闭开立的单据
     * */
    CLOSE_ONLY_OPEN("只能关闭开立的单据"),
    /**
     *只能完成审核过的单据
     * */
    ONLY_AUDIT("只能完成审核过的单据"),
    /**
     *采购单已有下游单据，不允许关闭或弃审
     * */
    DOWN_ORDER_ERROR("采购单已有下游单据，不允许关闭或弃审"),
    /**
     *开立或关闭状态的采购单，明细不可完成
     * */
    COMPLETE_ERROR("开立或关闭状态的采购单，明细不可完成"),
    /**
     *开始日期不能大于结束日期
     * */
    DATE_ERROR("开始日期不能大于结束日期"),
    /**
     *日期格式不对
     * */
    DATA_PARSE_ERROE("日期格式不对"),
    /**
     *采购单含已完成的明细或者已申请付款，不允许关闭或弃审
     * */
    REFUSE_CLOSE("采购单含已完成的明细或者已申请付款，不允许关闭或弃审"),
    /**
     *采购单id没有找到
     * */
    ID_NOT_FOUND("采购单id没有找到"),
    /**
     *只能审核开立状态下的采购单
     * */
    ONLY_OPEN("只能审核开立状态下的采购单"),
    /**
     *审核失败
     * */
    AUDIT_ERROR("审核失败"),
    /**
     *修改失败
     * */
    UPDATE_ERROR("修改失败"),
    /**
     *删除失败
     * */
    DELETE_ERROR("删除失败"),
    /**
     *已超出请购单订购数量，无法打开，请仔细确认
     * */
    PONUM_BIGGERTHAN_PUAPPNUM("已超出请购单订购数量，无法打开，请仔细确认！"),
    /**
     *没有找到对应的主单id
     * */
    NOT_FIND_POID("没有找到对应的主单id"),
    /**
     *没有找到对应的明细id
     * */
    NOT_FIND_DETAILID("没有找到对应的明细id"),
    /**
     *创建采购单失败
     * */
    CREATE_FAIL("创建失败"),
    /**
     *仅允许角色为理单员的用户才可以操作
     * */
    ONLY_ORDER_MANAGEER("仅允许角色为理单员的用户才可以操作！"),
    /**
     *仅允许相应任务人可以操作
     * */
    ONLY_TASKMAN("仅允许相应任务人可以操作！"),
    /**
     *已经申请过了，正在处理中！
     * */
    ALREADY_APPLIED("已经申请过了，正在处理中！"),
    /**
     *申请失败
     * */
    APPLY_ERROR("申请失败"),
    /**
     *你无权进行此操作
     * */
    NO_POWER("你无权进行此操作"),
    /**
     *未查到此用户
     * */
    NO_USER("未查到此用户"),
    /**
     *改价原因不能为空
     * */
    REASON_NULL("改价原因不能为空"),
    /**
     *金额只能编辑比原先小的值
     * */
    MONEY_BIGGER("金额只能编辑比原先小的值"),
    /**
     *请先关闭对应的结算单后再修改付款金额
     * */
    STATEMENTS_ERROR("请先关闭对应的结算单后再修改付款金额"),
    /**
     *操作成功
     * */
    SUCCESS("操作成功"),

    /**
     * 付款方式不能为空
     */
    PAYMENT_ERROR("付款方式不能为空"),
    /**
     *已完成或关闭的跟单任务不能进行操作
     * */
    TRACK_UPDATE_ERROR("已完成或关闭的跟单任务不能进行操作"),

    /**
     * 已经生成了分期付款的预付款
     */
    ALREADY_EXIST_HIRE_PURCHASE("已经生成了分期付款方式的预付款"),

    /**
     * 已经生成了定金付款方式的预付款
     */
    ALREADY_EXIST_BARGAIN("已经生成了定金付款方式的预付款");


    private String msg;

    ErrorMessageEnum(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
