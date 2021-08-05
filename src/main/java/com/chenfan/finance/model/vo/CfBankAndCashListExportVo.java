package com.chenfan.finance.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * @author liran
 */
@Data
public class CfBankAndCashListExportVo {

    @Excel(name = "状态",
            replace = {"草稿_1", "已入账（待核销）_2", "部分核销_3", "已核销_4" , "作废_5" ,"已删除_0"},
            orderNum = "1")
    private Integer bankAndCashStatus;

    @Excel(name = "收付流水号", orderNum = "2")
    private String recordSeqNo;

    @Excel(name = "记录类型",
            replace = {"支付宝_1", "微信支付_2", "现金_3", "支票_4", "汇款_5"},
            orderNum = "3")
    private String recordType;

    @Excel(name = "收/付日期", orderNum = "4")
    private String arapDate;

    @Excel(name = "单据收付类型",
            replace = {"实收_1","实付_2","预收_3","预付_4"},
            orderNum = "5")
    private String arapType;

    @Excel(name = "结算主体", orderNum = "6")
    private String balance;

    @Excel(name = "总金额", orderNum = "7")
    private BigDecimal amount;

    @Excel(name = "收付款单位", orderNum = "8")
    private String collectionUnit;

    @Excel(name = "核销状态", orderNum = "9")
    private String clearStatus;

    @Excel(name = "经办人", orderNum = "10")
    private String recordUser;

    @Excel(name = "支票号", orderNum = "11")
    private String checkNo;



}
