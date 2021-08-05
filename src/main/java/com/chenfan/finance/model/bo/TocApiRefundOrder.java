package com.chenfan.finance.model.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * toc_api_refund_order
 * @author 
 */
@Data
@TableName("ldb.m_wdt_api_refund_order")
public class TocApiRefundOrder implements Serializable {


    private Integer platformId;

    private String refundNo;

    private String oid;

    /**
     * 平台状态：1取消退款,2已申请退款,3等待退货,4等待收货,5退款成功
     */
    private Integer status;

    /**
     * 标题
     */
    private String goodsName;

    /**
     * 规格名
     */
    private String specName;

    /**
     * 数量
     */
    private BigDecimal num;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 平台货品ID
     */
    private String goodsId;

    private String specId;

    private String goodsNo;

    private String specNo;

    /**
     * 退货时间
     */
    private Date returnTime;

    /**
     * 退款说明
     */
    private String remark;

    private Date modified;

    private Date created;



    private static final long serialVersionUID = 1L;
}