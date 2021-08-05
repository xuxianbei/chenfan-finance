package com.chenfan.finance.model.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

/**
 * author:   tangwei
 * Date:     2021/5/17 16:50
 * Description: 开票导入失败原因下载
 */
@Data
public class TaxInvoiceDownloadDTO implements Serializable {

    private static final long serialVersionUID = -3744395728627846252L;

    /**
     * 开票流水号
     */
    @Excel(name = "开票流水号")
    private String taxInvoiceNo;

    /**
     * 发票号
     */
    @Excel(name = "发票号")
    private String invoiceNo;

    /**
     * 发票日期
     */
    @Excel(name = "开票时间")
    private String invoiceDate;

    /**
     * 发票财务账期；精确到月
     */
    @Excel(name = "财务账期")
    private String paymentDays;

    /**
     * 发票备注
     */
    @Excel(name = "发票备注")
    private String invoiceRemark;

    /**
     * 失败原因
     */
    @Excel(name = "失败原因")
    private String failedReason;
}