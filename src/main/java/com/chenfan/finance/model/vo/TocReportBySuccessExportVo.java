package com.chenfan.finance.model.vo;
import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Wen.Xiao
 * @Description // mapping的流水加订单
 * @Date 2021/5/11  16:20
 * @Version 1.0
 */
@Data
public class TocReportBySuccessExportVo {



    @Excel(name = "关联支付宝流水号", orderNum = "1")
    private String financeNo;
    @Excel(name = "淘宝原始订单号", orderNum = "2")
    private String tid;

    /**
     * U8订单编号
     */
    @ApiModelProperty("U8订单编号")
    @Excel(name = "U8单据号", orderNum = "3")
    private String orderNo;

    @Excel(name = "子订单号", orderNum = "4")
    private String oid;
    @Excel(name = "店铺支付宝账户", orderNum = "5")
    private String shopAlipayAccount;

    @Excel(name = "订单创建时间", orderNum = "6")
    private String createOrderDate;

    @Excel(name = "商品款号", orderNum = "7")
    private String saleGoodsNo;
    @Excel(name = "商品名称", orderNum = "8")
    private String saleGoodsName;
    @Excel(name = "SKU", orderNum = "9")
    private String saleSpecNo;
    @Excel(name = "店铺ID", orderNum = "10")
    private Integer shopId;
    @Excel(name = "出库数量", orderNum = "11")
    private BigDecimal stockOutQyt;
    @Excel(name = "订单分摊金额", orderNum = "12")
    private BigDecimal totalAmtAfterShare;
    @Excel(name = "关联流水支付宝到账金额", orderNum = "13")
    private BigDecimal detailReceived;
    @Excel(name = "关联出库单号", orderNum = "14")
    private String stockOutNos;
    @Excel(name = "关联订单号", orderNum = "15")
    private String tradeNo;
    @ApiModelProperty("是否推送到u8")
    @Excel(name = "是否推送到u8", orderNum = "16")
    private String pushState;








    @Excel(name = "类型", orderNum = "17" ,isColumnHidden=true)
    private String type;
    @Excel(name = "子单分摊后总价（不包含邮费）", orderNum = "18" ,isColumnHidden=true)
    private BigDecimal totalShareAmount;
    @Excel(name = "关联退货数量", orderNum = "19" ,isColumnHidden=true)
    private Integer refundQyt;
    @Excel(name = "消费者支付时间", orderNum = "20" ,isColumnHidden=true)
    private String payDate;
    @Excel(name = "流水到账时间", orderNum = "21" ,isColumnHidden=true)
    private String receivedDate;
    @Excel(name = "出库时间", orderNum = "22" ,isColumnHidden=true)
    private String stockOutDate;

    @Excel(name = "SKUNAME", orderNum = "23" ,isColumnHidden=true)
    private String saleSpecName;






}
