package com.chenfan.finance.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("PurBillVouchs")
@Accessors(chain = true)
public class PurBillVouchs implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer pbvid;

    @TableField("cInvCode")
    private String cInvCode;

    @TableField("bExBill")
    private Boolean bExBill = false;

    @TableField("dInDate")
    private LocalDateTime dInDate;

    @TableField("iPBVQuantity")
    private Double iPBVQuantity;

    @TableField("iNum")
    private Double iNum = 0.00;

    @TableField("iOriCost")
    private Double iOriCost;

    @TableField("iOriMoney")
    private BigDecimal iOriMoney;

    @TableField("iOriTaxPrice")
    private BigDecimal iOriTaxPrice;

    @TableField("iOriSum")
    private BigDecimal iOriSum;

    @TableField("iCost")
    private Double iCost;

    @TableField("iMoney")
    private BigDecimal iMoney;

    @TableField("iTaxPrice")
    private BigDecimal iTaxPrice;

    @TableField("iSum")
    private BigDecimal iSum;

    @TableField("iExMoney")
    private BigDecimal iExMoney;

    @TableField("iLostQuan")
    private Double iLostQuan;

    @TableField("iNLostQuan")
    private Double iNLostQuan;

    @TableField("iNLostMoney")
    private BigDecimal iNLostMoney;

    @TableField("iOriTotal")
    private BigDecimal iOriTotal;

    @TableField("iTotal")
    private BigDecimal iTotal;

    @TableField("cDebitHead")
    private String cDebitHead;

    @TableField("cTaxHead")
    private String cTaxHead;

    @TableField("cClue")
    private String cClue;

    @TableField("dSignDate")
    private LocalDateTime dSignDate;

    @TableField("cCorInvCode")
    private String cCorInvCode;

    @TableField("cFree1")
    private String cFree1;

    @TableField("cFree2")
    private String cFree2;

    @TableField("iTaxRate")
    private Double iTaxRate;

    @TableField("cDefine22")
    private String cDefine22;

    @TableField("cDefine23")
    private String cDefine23;

    @TableField("cDefine24")
    private String cDefine24;

    @TableField("cDefine25")
    private String cDefine25;

    @TableField("cDefine26")
    private Float cDefine26;

    @TableField("cDefine27")
    private Float cDefine27;

    @TableField("iPOsID")
    private Integer iPOsID;

    @TableField("cItemCode")
    private String cItemCode;

    @TableField("cItem_class")
    private String citemClass;

    @TableField("cNLostType")
    private String cNLostType;

    @TableField("mNLostTax")
    private BigDecimal mNLostTax;

    @TableField("cItemName")
    private String cItemName;

    @TableField("cFree3")
    private String cFree3;

    @TableField("cFree4")
    private String cFree4;

    @TableField("cFree5")
    private String cFree5;

    @TableField("cFree6")
    private String cFree6;

    @TableField("cFree7")
    private String cFree7;

    @TableField("cFree8")
    private String cFree8;

    @TableField("cFree9")
    private String cFree9;

    @TableField("cFree10")
    private String cFree10;

    @TableField("dSDate")
    private LocalDateTime dSDate;

    @TableField("cUnitID")
    private String cUnitID;

    @TableField("cDefine28")
    private String cDefine28;

    @TableField("cDefine29")
    private String cDefine29;

    @TableField("cDefine30")
    private String cDefine30;

    @TableField("cDefine31")
    private String cDefine31;

    @TableField("cDefine32")
    private String cDefine32;

    @TableField("cDefine33")
    private String cDefine33;

    @TableField("cDefine34")
    private Integer cDefine34;

    @TableField("cDefine35")
    private Integer cDefine35;

    @TableField("cDefine36")
    private LocalDateTime cDefine36;

    @TableField("cDefine37")
    private LocalDateTime cDefine37;

    @TableField("iSBsID")
    private Integer iSBsID;

    @TableField("RdsId")
    private Integer RdsId;

    @TableField("ContractRowGUID")
    private String ContractRowGUID;

    @TableField("iOriTaxCost")
    private Double iOriTaxCost;

    @TableField("UpSoType")
    private String UpSoType;

    @TableField("cBAccounter")
    private String cBAccounter;

    @TableField("bCosting")
    private Boolean bCosting = false;

    @TableField("iInvMPCost")
    private Double iInvMPCost;

    @TableField("ContractCode")
    private String ContractCode;

    @TableField("ContractRowNo")
    private String ContractRowNo;

    private String copcode;

    private String cdescription;

    @TableField("bTaxCost")
    private Boolean bTaxCost = false;

    private String chyordercode;

    private Integer ihyorderdid;

    private Double inattaxprice;

    private Double iinvexchrate = 0.00;

    private String opseq;

    @TableField("cBG_ItemCode")
    private String cbgItemcode;

    @TableField("cBG_ItemName")
    private String cbgItemname;

    @TableField("cBG_CaliberKey1")
    private String cbgCaliberkey1;

    @TableField("cBG_CaliberKeyName1")
    private String cbgCaliberkeyname1;

    @TableField("cBG_CaliberKey2")
    private String cbgCaliberkey2;

    @TableField("cBG_CaliberKeyName2")
    private String cbgCaliberkeyname2;

    @TableField("cBG_CaliberKey3")
    private String cbgCaliberkey3;

    @TableField("cBG_CaliberKeyName3")
    private String cbgCaliberkeyname3;

    @TableField("cBG_CaliberCode1")
    private String cbgCalibercode1;

    @TableField("cBG_CaliberName1")
    private String cbgCalibername1;

    @TableField("cBG_CaliberCode2")
    private String cbgCalibercode2;

    @TableField("cBG_CaliberName2")
    private String cbgCalibername2;

    @TableField("cBG_CaliberCode3")
    private String cbgCalibercode3;

    @TableField("cBG_CaliberName3")
    private String cbgCalibername3;

    @TableField("iBG_Ctrl")
    private Integer ibgCtrl;

    @TableField("cBG_Auditopinion")
    private String cbgAuditopinion;

    @TableField("cConExecID")
    private String cConExecID;

    @TableField("cConRowID")
    private String cConRowID;

    private Integer brettax = 0;

    private Double fretquantity;

    private LocalDateTime dretdate;

    private LocalDateTime dlastretdate;

    private BigDecimal fretmoney;

    @TableField("iOriZbjMoney")
    private BigDecimal iOriZbjMoney;

    @TableField("iOriNoRateZbjMoney")
    private BigDecimal iOriNoRateZbjMoney;

    @TableField("iZbjMoney")
    private BigDecimal iZbjMoney;

    @TableField("iNoRateZbjMoney")
    private BigDecimal iNoRateZbjMoney;

    private Integer ivouchrowno;

    @TableField("cPZNum")
    private String cPZNum;

    private LocalDateTime dkeepdate;

    @TableField("cBG_CaliberKey4")
    private String cbgCaliberkey4;

    @TableField("cBG_CaliberKeyName4")
    private String cbgCaliberkeyname4;

    @TableField("cBG_CaliberKey5")
    private String cbgCaliberkey5;

    @TableField("cBG_CaliberKeyName5")
    private String cbgCaliberkeyname5;

    @TableField("cBG_CaliberKey6")
    private String cbgCaliberkey6;

    @TableField("cBG_CaliberKeyName6")
    private String cbgCaliberkeyname6;

    @TableField("cBG_CaliberCode4")
    private String cbgCalibercode4;

    @TableField("cBG_CaliberName4")
    private String cbgCalibername4;

    @TableField("cBG_CaliberCode5")
    private String cbgCalibercode5;

    @TableField("cBG_CaliberName5")
    private String cbgCalibername5;

    @TableField("cBG_CaliberCode6")
    private String cbgCalibercode6;

    @TableField("cBG_CaliberName6")
    private String cbgCalibername6;

    @TableField("cbMemo")
    private String cbMemo;

    private String cbsysbarcode;

    private Integer bgift = 0;

    private String rowguid;


}
