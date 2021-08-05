package com.chenfan.finance.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @Author Wen.Xiao
 * @Description // 支付宝流水原始数据导出
 * @Date 2021/5/11  13:46
 * @Version 1.0
 */
@Data
public class AlipayOriginExportVo {

    @Excel(name = "账务流水号", orderNum = "1")
    private String financeNo;

    @Excel(name = "业务流水号", orderNum = "2")
    private String businessNo;

    @Excel(name = "商户订单号", orderNum = "3")
    private String tid;

    @Excel(name = "商品名称", orderNum = "4")
    private String goodsName;

    @Excel(name = "发生时间", orderNum = "5")
    private String accountDate;

    @Excel(name = "对方账号", orderNum = "6")
    private String oppositeAccount;


    @Excel(name = "收入金额（+元）", orderNum = "7")
    private String incomeAmount;

    @Excel(name = "支出金额（-元）", orderNum = "8")
    private String expendAmount;

    @Excel(name = "账户余额（元）", orderNum = "9")
    private String accountLeftAmount;


    @Excel(name = "交易渠道", replace = {"支付宝_0"}, orderNum = "10")
    private String payWay;


    @Excel(name = "业务类型", orderNum = "11", replace = {"交易付款_0", "在线支付_1", "交易退款_2", "交易分账_3", "其它_4", "收费_5", "提现_6", "转账_7", "红包退回_8","保证金_9","退款_10","退款（交易退款）_11","提现_12","分账_13","批量代发到账户_14"})
    private String businessType;

    @Excel(name = "备注", orderNum = "12")
    private String remark;
}
