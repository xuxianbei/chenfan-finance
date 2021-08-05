package com.chenfan.finance.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单
 *
 * @author: xuxianbei
 * Date: 2021/3/3
 * Time: 9:46
 * Version:V1.0
 */
@Data
public class CfInvoiceCommonVo {

    /**
     * 普通帐单内部编号
     */
    private Long invoiceId;

    /**
     * 账单状态（1待提交；2待结算；3部分结算；4全部结算；7已作废，0已删除,5 业务待审核,8 待提交财务,9已核销,10审批中,11待打款,12已开票，13已撤回
     */
    private Integer invoiceStatus;

    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    private String financeEntity;

    /**
     * 业务类型(货品采购1; 销售订单2; 3:MCN)
     */
    private String jobType;

    /**
     * 应收/应付类型  AR=收；AP=付；
     */
    private String invoiceType;

    /**
     * 帐单金额-应付总金额
     */
    private BigDecimal invoicelCredit;

    /**
     * 当前审批人
     */
    private String approvalName;

    /**
     * 核销金额
     */
    private BigDecimal clearAmount;

    /**
     * 核销余额
     */
    private BigDecimal clearAmountBalance;


    /**
     * 对方发票号
     */
    private String customerInvoiceNo;

    /**
     * 开票方式(1=开票、2=无票、3=后补票)
     */
    private Integer customerInvoiceWay;

    /**
     * 打款名称
     */
    private String accountName;

    /**
     * 费用名称
     * 费用种类 1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费
     */
    private String chargeType;

    /**
     * 费用来源单号
     */
    private String chargeSourceCode;

    /**
     * 核销状态(0=未核销,1=部分核销,2=全部核销)
     */
    private Integer clearStatus;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 打款类型：1, "红人收款账户" 2, "客户收款账户" 3, "公司账户" 4, "第三方账户"
     */
    private Integer accountType;


}
