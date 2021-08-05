package com.chenfan.finance.model.vo;

import com.chenfan.finance.model.CfInvoiceHeader;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiongbin
 * @date 2020-08-22
 */
@EqualsAndHashCode(callSuper = true)
@ToString
@Data
public class CfInvoiceHeaderListVO extends CfInvoiceHeader implements Serializable {

    private static final long serialVersionUID = -2391448954284123845L;

    /**
     * 总金额
     */
    private BigDecimal invoicelTotal;
    /**
     * 余额
     */
    private BigDecimal overInvoicel;


    private String createDateString;

    /**
     * 结算金额
     */
    private BigDecimal settlementAmount;
    /**
     * 结算余额
     */
    private BigDecimal balanceOfStatement;

    private  String settlementNos;

    private  String settlementStatus;
    @ApiModelProperty("入库通知单类型（0成品 1辅料）")
    private Integer  rdRecordType;

    private Integer salesType;

    private String rdRecordTypeName;
    private String brandName;

}
