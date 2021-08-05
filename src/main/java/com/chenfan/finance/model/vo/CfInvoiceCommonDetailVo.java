package com.chenfan.finance.model.vo;

import com.chenfan.finance.server.remote.vo.ExcutionSettleInfoVO;
import com.chenfan.finance.server.remote.vo.ProcessDetailVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 详情
 *
 * @author: xuxianbei
 * Date: 2021/3/3
 * Time: 10:41
 * Version:V1.0
 */
@Data
public class CfInvoiceCommonDetailVo extends CfInvoiceCommonPreVo {
    /**
     * 普通帐单内部编号
     */
    private Long invoiceId;

    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 账单状态（账单状态 1待提交；2待结算；3部分结算；4全部结算；7已作废，0已删除,5 业务待审核,8 待提交财务,9已核销,10审批中,11待打款,12已开票，13已撤回, 14审批拒绝
     */
    private Integer invoiceStatus;

    /**
     * 全部审批流
     */
    private List<Long> flowIds;

    /**
     * 当前审批流
     */
    private Long flowId;

    /**
     * 开票核销详情
     */
    private CfInvoiceClearDetailVo cfInvoiceClearDetailVo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 发票备注
     */
    private String customerInvoiceRemark;

    /**
     * 对方发票号
     */
    private String customerInvoiceNo;

    /**
     * 对方发票日期
     */
    private LocalDateTime customerInvoiceDate;

    /**
     * 财务账期；精确到月
     */
    private String paymentDays;

    /**
     * 打款方式
     */
    private Long accountId;

    /**
     * 结算单模板(1, "内部红人执行单模板";2, "外部红人执行单模板";3, "红人采购费/年度返点/客户返点模板")
     */
    private Integer settleTemplate;

}
