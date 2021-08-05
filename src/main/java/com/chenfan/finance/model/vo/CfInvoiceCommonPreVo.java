package com.chenfan.finance.model.vo;

import com.chenfan.finance.model.CfBsOperationLog;
import com.chenfan.finance.utils.pageinfo.MultyImageVo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 账单详情-预新增
 *
 * @author: xuxianbei
 * Date: 2021/3/1
 * Time: 15:37
 * Version:V1.0
 */
@Data
public class CfInvoiceCommonPreVo {

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 业务类型(货品采购1; 销售订单2; 3:MCN)
     */
    private String jobType;

    /**
     * 帐单金额-应收总金额
     */
    private BigDecimal invoicelDebit;

    /**
     * 帐单金额-应付总金额
     */
    private BigDecimal invoicelCredit;

    /**
     * 应收/应付类型  AR=收；AP=付；
     */
    private String invoiceType;

    /**
     * 帐单抬头
     */
    private String invoiceTitle;

    /**
     * 帐单抬头名称
     */
    private String invoiceTitleName;

    /**
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    private String financeEntity;

    /**
     * 银行
     */
    private String bank;

    /**
     * 银行帐号
     */
    private String bankAccounts;

    /**
     * 帐单日期
     */
    private LocalDateTime invoiceDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 开票方式(1=开票、2=无票、3=后补票)
     */
    private Integer customerInvoiceWay;

    /**
     * 合同列表
     */
    private List<MultyImageVo> contractUrls;

    /**
     * 费用列表
     */
    private List<CfChargeCommonVo> chargeCommonVos = new ArrayList<>();

    /**
     * 操作日字记录
     */
    private List<CfBsOperationLog> cfBsOperationLogList;

}
