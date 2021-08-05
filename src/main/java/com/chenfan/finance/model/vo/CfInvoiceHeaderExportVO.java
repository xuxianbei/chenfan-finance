package com.chenfan.finance.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
/**
 * @author liran
 */
@Data
public class CfInvoiceHeaderExportVO {

    @Excel(name = "账单状态",
            replace = {"草稿_1", "待结算_2", "部分结算_3", "全部结算_4" , "已作废_7" ,"已删除_0","业务待审核_5","待提交财务_8","已核销_9","全部付款_12"},
            orderNum = "1")
    private Integer invoiceStatus;

    @Excel(name = "账单编号",
            orderNum = "2")
    private String invoiceNo;

    @Excel(name = "结算主体",
            orderNum = "3")
    private String balance;

    @Excel(name = "业务类型",
            orderNum = "4")
    private String jobType;

    @Excel(name = "收付类型",
            replace = {"红字_AR", "蓝字_AP"},
            orderNum = "5")
    private String invoiceType;

    @Excel(name = "账单金额",
            orderNum = "6")
    private BigDecimal invoiceAmount;

    @Excel(name = "结算金额",
            orderNum = "7")
    private BigDecimal settleAmount;

    @Excel(name = "结算余额",
            orderNum = "8")
    private BigDecimal settleLeftAmount;

    @Excel(name = "预付款",
            orderNum = "9")
    private BigDecimal advancePayAmount;

    @Excel(name = "结算起始日期",
            orderNum = "10")
    private String dateStart;

    @Excel(name = "结算截止日期",
            orderNum = "11")
    private String dateEnd;

    @Excel(name = "已核销金额",
            orderNum = "12")
    private BigDecimal clearedAmount;

    @Excel(name = "核销余额",
            orderNum = "13")
    private BigDecimal clearedLeftAmount;

    @Excel(name = "核销状态",
            replace = {"未核销_0", "部分核销_1", "全部核销_2"},
            orderNum = "14")
    private Integer clearedStatus;

    @Excel(name = "创建人",
            orderNum = "15")
    private String createBy;

    @Excel(name = "创建日期",
            orderNum = "16")
    private String createDate;

    @Excel(name = "备注",
            orderNum = "17")
    private String remark;

    /**
     * 销售类型
     */
    @Excel(name = "销售类型",
            replace = {" _null", "直播款_1", "订单款_2"},
            orderNum = "18")
    private Integer salesType;
}
