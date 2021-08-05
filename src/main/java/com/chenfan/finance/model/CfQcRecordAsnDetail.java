package com.chenfan.finance.model;

import java.io.Serializable;
import lombok.Data;

/**
 * cf_qc_record_asn_detail
 * @author 
 */
@Data
public class CfQcRecordAsnDetail implements Serializable {
    private Long id;

    private Long qcChargingId;

    private Long rdRecordDetailId;

    private Integer qcQty;

    private static final long serialVersionUID = 1L;
}