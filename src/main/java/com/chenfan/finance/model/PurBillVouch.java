package com.chenfan.finance.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("PurBillVouch")
@Accessors(chain = true)
public class PurBillVouch implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pbvid;

    @TableField("cPBVBillType")
    private String cPBVBillType = "01";

    @TableField("cPBVCode")
    private String cPBVCode;

    @TableField("cPTCode")
    private String cPTCode;

    @TableField("dPBVDate")
    private LocalDateTime dPBVDate;

    @TableField("cVenCode")
    private String cVenCode;

    @TableField("cUnitCode")
    private String cUnitCode = "00001";

    @TableField("cDepCode")
    private String cDepCode;

    @TableField("cPersonCode")
    private String cPersonCode;

    @TableField("cPayCode")
    private String cPayCode;

    private String cexchName = "人民币";

    @TableField("cExchRate")
    private Float cExchRate = 1F;

    @TableField("iPBVTaxRate")
    private Float iPBVTaxRate;

    @TableField("cPBVMemo")
    private String cPBVMemo;

    @TableField("cOrderCode")
    private String cOrderCode;

    @TableField("cInCode")
    private String cInCode;

    @TableField("cBusType")
    private String cBusType;

    @TableField("dSDate")
    private LocalDateTime dSDate;

    @TableField("cPBVMaker")
    private String cPBVMaker;

    @TableField("cPBVVerifier")
    private String cPBVVerifier;

    @TableField("bNegative")
    private Boolean bNegative = false;

    @TableField("bOriginal")
    private Boolean bOriginal = false;

    @TableField("bFirst")
    private Boolean bFirst = false;

    private String citemClass;

    private String citemcode;

    @TableField("cHeadCode")
    private String cHeadCode;

    @TableField("iNetLock")
    private Float iNetLock = 0F;

    @TableField("cDefine1")
    private String cDefine1;

    @TableField("cDefine2")
    private String cDefine2;

    @TableField("cDefine3")
    private String cDefine3;

    @TableField("cDefine4")
    private LocalDateTime cDefine4;

    @TableField("cDefine5")
    private Integer cDefine5;

    @TableField("cDefine6")
    private LocalDateTime cDefine6;

    @TableField("cDefine7")
    private Float cDefine7;

    @TableField("cDefine8")
    private String cDefine8;

    @TableField("cDefine9")
    private String cDefine9;

    @TableField("cDefine10")
    private String cDefine10;

    @TableField("bPayment")
    private String bPayment;

    @TableField("dVouDate")
    private LocalDateTime dVouDate;

    @TableField("iVTid")
    private Integer iVTid = 8163;

    private LocalDateTime ufts;

    @TableField("cAccounter")
    private String cAccounter;

    @TableField("cSource")
    private String cSource = "采购";

    @TableField("cDefine11")
    private String cDefine11;

    @TableField("cDefine12")
    private String cDefine12;

    @TableField("cDefine13")
    private String cDefine13;

    @TableField("cDefine14")
    private String cDefine14;

    @TableField("cDefine15")
    private Integer cDefine15;

    @TableField("cDefine16")
    private Float cDefine16;

    @TableField("bIAFirst")
    private Boolean bIAFirst = false;

    @TableField("iDiscountTaxType")
    private Integer iDiscountTaxType = 0;

    @TableField("cVenPUOMProtocol")
    private String cVenPUOMProtocol;

    @TableField("dCreditStart")
    private LocalDateTime dCreditStart;

    @TableField("iCreditPeriod")
    private Integer iCreditPeriod;

    @TableField("dGatheringDate")
    private LocalDateTime dGatheringDate;

    @TableField("cModifyDate")
    private LocalDateTime cModifyDate;

    @TableField("cReviser")
    private String cReviser;

    @TableField("bCredit")
    private Integer bCredit;

    @TableField("iBG_OverFlag")
    private Integer ibgOverflag;

    @TableField("cBG_Auditor")
    private String cbgAuditor;

    @TableField("cBG_AuditTime")
    private String cbgAudittime;

    @TableField("ControlResult")
    private Integer ControlResult;

    private Integer iflowid = 0;

    private LocalDateTime dverifydate;

    private LocalDateTime dverifysystime;

    @TableField("cVenAccount")
    private String cVenAccount;

    @TableField("iPrintCount")
    private Integer iPrintCount = 0;

    private String ccleanver;

    @TableField("cContactCode")
    private String cContactCode = "0000100000001";

    @TableField("cVenPerson")
    private String cVenPerson = "demo";

    @TableField("cVenBank")
    private String cVenBank;

    @TableField("cVerifier")
    private String cVerifier;

    @TableField("cAuditDate")
    private LocalDateTime cAuditDate;

    @TableField("cAuditTime")
    private LocalDateTime cAuditTime;

    private Integer iverifystateex;

    private Integer ireturncount;

    @TableField("IsWfControlled")
    private Boolean IsWfControlled = false;

    private String csysbarcode;

    @TableField("cCurrentAuditor")
    private String cCurrentAuditor;

    private LocalDateTime cmaketime;

    private LocalDateTime cmodifytime;

    @TableField("bMerger")
    private Integer bMerger;


}
