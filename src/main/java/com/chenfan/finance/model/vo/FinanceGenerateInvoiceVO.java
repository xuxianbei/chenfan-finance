package com.chenfan.finance.model.vo;

import com.chenfan.common.vo.UserVO;
import lombok.Data;

/**
 * @author 2062
 */
@Data
public class FinanceGenerateInvoiceVO {


    /**
     * 用户信息
     */
    private UserVO userVO;

    /**
     * 账单id
     */
    private Long invoiceId;
}
