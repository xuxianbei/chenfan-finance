package com.chenfan.finance.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Wen.Xiao
 * @Description // 按周推送u8的数据导出类
 * @Date 2021/5/11  17:11
 * @Version 1.0
 */
@Data
public class TocReportByWeekExportVo {

    /**
     * 品牌id
     */
    @Excel(name = "品牌id", orderNum = "1")
    private Integer brandId;

    @Excel(name = "月份", orderNum = "2")
    private String month;
    /**
     * U8订单编号
     */
    @Excel(name = "U8单据号", orderNum = "3")
    private String orderNo;
    /**
     * 类型：收入或者支出
     */
    @Excel(name = "收付类型", orderNum = "4" ,replace = {"收入_0","支出_1"})
    private Integer type;

    /**
     * 当前月第几周
     */
    @Excel(name = "第几周", orderNum = "5")
    private Integer weeknOfMonth;

    /**
     * spu_code
     */
    @Excel(name = "商品款号", orderNum = "6")
    private String spuCode;

    /**
     * spu_name
     */
    @Excel(name = "商品名称", orderNum = "7")
    private String spuName;

    /**
     * sku_code
     */
    @Excel(name = "sku", orderNum = "8")
    private String skuCode;

    /**
     * sku_name
     */
    @Excel(name = "规格名称", orderNum = "9")
    private String skuName;
    /**
     * 本周此sku 销售或退款汇总
     */
    @Excel(name = "总数", orderNum = "10")
    private BigDecimal skuCount;
    /**
     * 此sku 对应的均价（总金额/数量）
     */
    @Excel(name = "均价", orderNum = "11")
    private BigDecimal skuPrice;

    /**
     * 总金额
     */
    @Excel(name = "总价", orderNum = "12")
    private BigDecimal totalMoney;


    /**
     * 统计开始时间
     */
    @Excel(name = "统计开始时间", orderNum = "13",isColumnHidden=true)
    private LocalDateTime countStartTime;

    /**
     * 统计结束时间
     */
    @Excel(name = "统计结束时间", orderNum = "14" ,isColumnHidden=true)
    private LocalDateTime countEndTime;
    /**
     * 月份 eg202001
     */

    @Excel(name = "到账时间", orderNum = "15" ,isColumnHidden=true)
    private LocalDateTime daozhangt;


    /**
     * 关联的原始销售单子单
     */
    @Excel(name = "关联的原始销售单子单编号", orderNum = "16" ,isColumnHidden=true)
    private String oids;









}
