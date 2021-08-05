package com.chenfan.finance.model.vo;

import com.chenfan.finance.server.remote.vo.ExcutionSettleInfoVO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/3/25
 * Time: 16:48
 * Version:V1.0
 */
@Data
public class CfInvoiceCommonDetailSettleVo {

    /**
     * 结算单信息
     */
    private ExcutionSettleInfoVO excutionSettleInfoVO;

    /**
     * 全部审批流
     */
    private List<Long> flowIds;

    /**
     * 当前审批流
     */
    private Long flowId;


    /**
     * 打款方式
     */
    private Long accountId;

    /**
     * 打款名称
     */
    private String accountName;

    /**
     * 打款类型：1, "红人收款账户" 2, "客户收款账户" 3, "公司账户" 4, "第三方账户"
     */
    private Integer accountType;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 帐单号
     */
    private String invoiceNo;
}
