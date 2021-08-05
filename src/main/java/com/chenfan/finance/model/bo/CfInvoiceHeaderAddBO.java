package com.chenfan.finance.model.bo;

import com.chenfan.finance.model.CfInvoiceDetail;
import com.chenfan.finance.model.CfInvoiceHeader;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiongbin
 * @date 2020-08-20
 */
@ToString
@Data
public class CfInvoiceHeaderAddBO implements Serializable {

    private static final long serialVersionUID = 6319775941365524565L;

    private CfInvoiceHeader invoiceHeader;
    private List<CfInvoiceDetail> chargeList;

    private List<CfInvoiceDetail> invoiceDetailList;

}
