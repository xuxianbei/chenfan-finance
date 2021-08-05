package com.chenfan.finance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 获取所有品牌信息 请求实体
 *
 * @author SXR
 * @date 2018-7-12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseGetBrandInfoListReqModel {
  /** 用户ID */
  private Long userId;
  /** 状态 */
  private Integer state;
  /** id */
  private Integer brandId;
  /** 品牌编码 */
  private String brandCode;
  /** 名称 */
  private String brandName;
  /** 英文前缀 */
  private String prefixEn;
  /** 中文前缀 */
  private String prefixCh;
  private List<Integer> brandIds;
}
