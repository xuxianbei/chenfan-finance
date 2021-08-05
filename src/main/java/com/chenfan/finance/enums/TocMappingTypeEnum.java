package com.chenfan.finance.enums;

/**
 * @Author Wen.Xiao
 * @Description // 匹配类型
 * @Date 2021/4/9  16:12
 * @Version 1.0
 */
public enum TocMappingTypeEnum {
    /**
     * 11：收入流水=子单金额
     * 12: 收入流水=子单金额+收入总邮费
     * 3：收入流水=子单总金额-对应子单退款金额
     *      1：销售多个 退款部分（|单价*数量-退款金额|<=0.01）
     *      2：销售一个 退差价等 （|单价*数量-退款金额| >0.01）
     * 4：收入流水=子单总金额-对应子单退款金额+总邮费
     *      1：销售多个 退款部分 （|单价*数量-退款金额|<=0.01）
     *      2：销售一个 退差价等 （|单价*数量-退款金额| >0.01）
     * 5：收入流水=子单总金额*权益金
     * 6：收入流水=子单总金额*权益金+总邮费
     * 7：收入流水=（子单总金额-退款金额）*权益金
     * 8：收入流水=（子单总金额-退款金额）*权益金+总邮费
     */

    INCOME_SHARE_AMOUNT(1101,"INCOME_SHARE_AMOUNT","收入流水=子单金额 "),
    INCOME_SHARE_AMOUNT_POST(1102,"INCOME_SHARE_AMOUNT_POST","收入流水=子单金额+收入总邮费"),

    INCOME_SHARE_AMOUNT_REFUND(1301,"INCOME_SHARE_AMOUNT_REFUND","收入流水=子单总金额-对应子单退款金额 1：销售多个 退款部分（|单价*数量-退款金额|<=0.01） "),
    INCOME_SHARE_AMOUNT_REFUND_OTHER(1302,"INCOME_SHARE_AMOUNT_REFUND_OTHER","收入流水=子单总金额-对应子单退款金额 2：销售一个 退差价等 （|单价*数量-退款金额| >0.01） 需要生成"),
    INCOME_SHARE_AMOUNT_REFUND_POST(1311,"INCOME_SHARE_AMOUNT_REFUND_POST","收入流水=子单总金额-对应子单退款金额+总邮费 1：销售多个 退款部分 （|单价*数量-退款金额|<=0.01）"),
    INCOME_SHARE_AMOUNT_REFUND_POST_OTHER(1312,"INCOME_SHARE_AMOUNT_REFUND_POST_OTHER","收入流水=子单总金额-对应子单退款金额+总邮费 2：销售一个 退差价等 （|单价*数量-退款金额| >0.01）"),


    INCOME_SHARE_AMOUNT_EQUITY(1501,"INCOME_SHARE_AMOUNT_EQUITY","子单总金额*（1-权益金比例） =收入流水金额1501"),
    INCOME_SHARE_AMOUNT_EQUITY_REFUND(1502,"INCOME_SHARE_AMOUNT_EQUITY_REFUND","(子单总金额-退款金额)*（1-权益金比例）=收入流水金额1502"),
    INCOME_SHARE_AMOUNT_EQUITY_POST(1511,"INCOME_SHARE_AMOUNT_EQUITY_POST","子单总金额*（1-权益金比例） +全部邮费=收入流水金额1502"),
    INCOME_SHARE_AMOUNT_EQUITY_POST_REFUND(1512,"INCOME_SHARE_AMOUNT_EQUITY_POST_REFUND","(子单总金额-退款金额)*（1-权益金比例） +全部邮费=收入流水金额1502"),


    EXPEND_SHARE_AMOUNT_ALL(2101,"EXPEND_SHARE_AMOUNT_ALL","子单金额等于退款单金额=流水金额  2101"),
    EXPEND_SHARE_AMOUNT_NUM(2201,"EXPEND_SHARE_AMOUNT_NUM","子单金额大于退款单金额=流水金额 （ 1：退款金额=单价* 退款数量） 2201"),
    EXPEND_SHARE_AMOUNT_SUIT(2202,"EXPEND_SHARE_AMOUNT_SUIT","子单金额大于退款单金额=流水金额 （2:商品数量为1但是退款金额等于订单金额的一半：可能是套装）"),
    EXPEND_SHARE_AMOUNT_OTHER(2203,"EXPEND_SHARE_AMOUNT_OTHER","子单金额大于退款单金额=流水金额 （3：红包，差价等） 2203"),

    ;
    private Integer no;
    private String code;
    private String msg;

    TocMappingTypeEnum(Integer no, String code, String msg) {
        this.no = no;
        this.code = code;
        this.msg = msg;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
