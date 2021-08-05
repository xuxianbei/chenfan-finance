package com.chenfan.finance.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: xuxianbei
 * Date: 2020/9/1
 * Time: 17:21
 * Version:V1.0
 */
@Data
public class BrandDetailResModelRpc {


    /**
     * 主键id
     */
    private Integer brandId;

    /**
     * 品牌编码
     */
    @Excel(name = "品牌编号")
    private String brandCode;
    /**
     * 品牌名称
     */
    @Excel(name = "品牌名称")
    private String brandName;
}
