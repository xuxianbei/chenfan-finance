package com.chenfan.finance.model.bo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author mbji
 */
@Data
public class PoMainBo {
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
    private String productCode;
    /**
     * 存货编码
     */
    private String inventoryCode;
    /**
     * 品牌
     */
    private Integer brandId;
    /**
     * 供应商
     */
    private Integer vendorId;
    /**
     * 上新日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date newDate;
    /**
     * 单据状态
     */
    private Integer state;
    /**
     * 请购单号
     */
    private String appvouchCode;
    /**
     * 来源单号
     */
    private String sourceNumber;
    /**
     * 订单类型
     */
    private Integer sourceType;
    /**
     * 采购类型
     */
    private Integer poType;
    /**
     * 采购单号
     */
    private String poCode;
    /**
     * 下单日期start
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date poDateBegin;
    /**
     * 下单日期end
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date poDateEnd;
    /**
     * 制单人
     */
    private String createName;
    /**
     * 合同初始日期start
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date constartDateBegin;
    /**
     * 合同初始日期end
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date constartDateEnd;
    /**
     * 合同截至日期开始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date conEndDateStart;
    /**
     * 合同截至日期结束
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date conEndDateEnd;

    private String preOrderCode;

    private Integer preOrderId;

    /**
     * 税率
     */
    private String taxRate;

    /**
     * 当前页
     */
    private int pageNum = 1;

    /**
     * pageSize
     */
    private int pageSize = 10;

    private int start;

    private Long currentUserId;

    private String vendorLevel;

    private Date newDateEnd;

    /**
     * 是否供应商后台
     */
    private boolean vendorWeb;

    private String dataType;

    private Integer orderType;

    /**
     * 品牌权限ID列表
     */
    private List<Integer> brandIds;

    public int getStart() {
        return (this.pageNum - 1) * this.pageSize;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public Date getNewDateEnd() {
        Calendar c = Calendar.getInstance();
        c.setTime(this.newDate);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

}
