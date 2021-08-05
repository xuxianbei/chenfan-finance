package com.chenfan.finance.model;

import lombok.Data;

/**
 * 获取所有品牌信息 返回实体
 *
 * @author SXR
 * @date 2018/7/12
 */
@Data
public class BaseGetBrandInfoListResModel {
  /** 品牌id */
  private Integer brandId;
  /** 品牌名称 */
  private String brandName;
  /** 品牌编码 */
  private String brandCode;
  /** 英文前缀 */
  private String prefixEn;
  /** 中文前缀 */
  private String prefixCh;
  /** 销售客户 */
  private String customerName;
  /** 销售客户ID */
  private String customerId;
  /**
   * 店铺类型 1直营 2代运营
   */
  private String brandType;

  /**
   * 财务主体
   */
  private String financialBody;
}
