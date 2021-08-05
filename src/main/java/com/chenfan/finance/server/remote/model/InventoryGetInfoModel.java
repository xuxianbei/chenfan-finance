package com.chenfan.finance.server.remote.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author SXR
 * @date 2018/7/11
 */
@Data
public class InventoryGetInfoModel {
    /**
     * 货号(旧:procode)
     */
    private String productCode;
    /**
     * 存货名称(旧:cinvname)
     */
    private String inventoryName;


    private Integer inventoryType;
    /**
     * 品牌id(旧:brand)
     */
    private Integer brandId;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 中文前缀（旧undefined2）
     */
    private String prefixCh;
    /**
     * 上新日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date newDate;
    /**
     * 品类
     */
    private String inventoryCategory;

    /**
     * 存货分类名称
     */
    private String invCName;
    /**
     * 款式来源
     */
    private String styleSource;
    /**
     * 季节
     */
    private String season;
    /**
     * 年份
     */
    private Integer years;

    private Integer spuState;
    /**
     * 波段
     */
    private String waveBand;

    /**
     * 等级
     */
    private String level;
    /**
     * 安全技术类别
     */
    private String safetyTechnology;
    /**
     * 面料名称
     */
    private String compositionName;
    /**
     * 面料成分
     */
    private String composition;
    /**
     * 洗唛类目
     */
    private String wmcategory;
    /**
     * 主图片
     */
    private String procodeCover;
    /**
     * 附图
     */
    private String procodeImgs;
    /**
     * 建档人
     */
    private String ccreatePerson;
    /**
     * 创建人名称
     */
    private String createName;
    /**
     * 创建人id
     */
    private Long createBy;
    /**
     * 更新人名称
     */
    private String updateName;
    /**
     * 更新人id
     */
    private Long updateBy;
    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
    /**
     * sku列表
     */
    //private List<InventorySkuinfoList> skuList;

    private String colors;

    private String sizes;

    private String sizeGroup;

    /**
     * 单品套装分类
     */
    private Integer categoryType;
    /**
     * 联名款系列id
     */
    private Integer jointSeriesId;
    /**
     * 是否联名(0是1否)
     */
    private Integer isJoint;
    /**
     * 联名款系列名称
     */
    private String jointSeriesName;
    /**
     * 联名款品牌id
     */
    private Integer jointBrandId;
    /**
     * 联名款品牌名称
     */
    private String jointBrandName;
    /**
     * 配件
     */
    private String accessories;

    private String nationalCode;

    /**
     * 销售类型（1：直播款；2：正常款）
     */
    private Integer salesType;

    /**
     * 分类id
     */
    private Integer classifyId;

    /**
     * 分类名称
     */
    private String classifyName;

    /**
     * 大类id
     */
    private Integer bigClassId;

    /**
     * 大类名称
     */
    private String bigClassName;

    /**
     * 中类id
     */
    private Integer middleClassId;

    /**
     * 中类名称
     */
    private String middleClassName;

    /**
     * 小类id
     */
    private Integer smallClassId;

    /**
     * 小类名称
     */
    private String smallClassName;

    /**
     * 类目id
     */
    private Integer categoryId;

    /**
     * 类目名称
     */
    private String categoryName;

    /**
     * 分类id
     */
    private Integer sortId;

    /**
     * 分类名称
     */
    private String sortName;

    /**
     * 执行标准编id
     */
    private Integer standardId;

    /**
     * 执行标准编名称
     */
    private String standardName;

    private String productCodeSuffix;

    private Integer modelType;
    private Integer trimmingSchemeId;
    private String trimmingSchemeName;
    /**
     * 销售类型（1：普通直播款 2：直播代发；3：直播款-部分承担库存）
     */
    private Integer liveType;
    /**
     * 商品来源：自有（1）、外采（2）
     */
    private Integer inventorySource;
    /**
     * 卖点
     */
    private String sellingPoint;
    /**
     * 是否授权商商品：是（1）、否（2）
     */
    private Integer licensedGoods;
}
