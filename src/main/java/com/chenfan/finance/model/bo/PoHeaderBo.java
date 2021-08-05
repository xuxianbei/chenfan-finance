package com.chenfan.finance.model.bo;

import com.chenfan.common.dto.PagerDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author llq
 */
@Data
@ApiModel
public class PoHeaderBo extends PagerDTO {
    /**
     * 1是导出 2 是列表
     */
    private Integer export;
    /**
     * 导出全部 0 为部分 1 为全部
     */
    private Integer exportAll;
    /**
     * 货号
     */
    @ApiModelProperty(value = "货号")
    private String productCode;
    /**
     * 存货编码
     */
    @ApiModelProperty(value = "存货编码")
    private String inventoryCode;
    /**
     * 品牌
     */
    @ApiModelProperty(value = "品牌")
    private Integer brandId;
    /**
     * 供应商
     */
    @ApiModelProperty(value = "供应商")
    private Integer vendorId;
    /**
     * 上新日期
     */
    @ApiModelProperty(value = "上新日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date newDate;
    /**
     * 单据状态
     */
    @ApiModelProperty(value = "单据状态(0开立，1审核，2关闭，3完成)")
    private Integer state;

    /**
     * 采购单号
     */
    @ApiModelProperty(value = "采购单号")
    private String poCode;
    /**
     * 下单日期start
     */
    @ApiModelProperty(value = "下单日期start")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date poDateBegin;
    /**
     * 下单日期end
     */
    @ApiModelProperty(value = "下单日期end")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date poDateEnd;
    /**
     * 制单人
     */
    @ApiModelProperty(value = "制单人")
    private String createName;
    /**
     * 合同初始日期start
     */
    @ApiModelProperty(value = "合同初始日期start")
    private String constartDateBegin;
    /**
     * 合同初始日期end
     */
    @ApiModelProperty(value = "合同初始日期end")
    private String constartDateEnd;
    /**
     * 合同截至日期开始
     */
    @ApiModelProperty(value = "合同截至日期开始")
    private String conEndDateStart;
    /**
     * 合同截至日期结束
     */
    @ApiModelProperty(value = "合同截至日期结束")
    private String conEndDateEnd;

    @ApiModelProperty(value = "来源订单编号")
    private String sourceNumber;

    @ApiModelProperty(value = "订单类型")
    private Integer sourceType;

    /**
     * 税率
     */
    @ApiModelProperty(value = "税率")
    private String taxRate;

    /**
     * 辅料需求单编号
     * */
    private String accessoryRequisitionsCode;
    /**
     * 当前页
     *//*
    private int pageNum = 1;

    *//**
     * pageSize
     *//*
    private int pageSize = 10;*/

    private int start;

    private Long currentUserId;

    private String vendorLevel;

    private Date newDateEnd;

    /**
     * 是否供应商后台
     */
    private boolean vendorWeb;

    private String dataType;
    @ApiModelProperty(value = "首返单")
    private Integer orderType;

    private Integer childOrderType;

    @ApiModelProperty(value = "采购类型0：成衣；1：辅料")
    private Integer poType;

    /**
     * 品牌权限ID列表
     */
    private List<Integer> brandIds;

    private Long poId;

    public Date getNewDateEnd() {
        Calendar c = Calendar.getInstance();
        c.setTime(this.newDate);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

}
