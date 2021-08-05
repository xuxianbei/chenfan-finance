package com.chenfan.finance.server.remote.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * @author liran
 */
@Data
public class BrandDetailResModel {

	/**
	 * 页面用id
	 */
	private Integer id;
	/**
	 * 主键id
	 */
	private Integer brandId;
	/**
	 * 状态(0禁用；1启用)
	 */
	@Excel(name = "品牌状态", replace = {"禁用_0", "启用_1"})
	private Integer state;
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
	/**
	 * 企业类型
	 */
	@Excel(name = "企业类型", replace = {"企业_1", "非企业_2"})
	private String brandType;
	/**
	 * 英文前缀
	 */
	@Excel(name = "英文前缀")
	private String prefixEn;
	/**
	 * 客户id
	 */
	private Long customerId;
	/**
	 * 客户名称
	 */
	@Excel(name = "所属企业")
	private String customerName;
	/**
	 * 中文前缀
	 */
	private String prefixCh;
	/**
	 * 倍率
	 */
	private String magnification;
	/**
	 * 审核状态（0未审核1已审核）
	 */
	private Integer auditState;
	/**
	 * 溢短装
	 */
	private BigDecimal moreLess;
	/**
	 * 收货联系人
	 */
	private String receiveName;
	/**
	 * 联系电话
	 */
	private String receiveTel;
	/**
	 * 收货地址
	 */
	private String receiveAddress;
	/**
	 * 所属部门
	 */
	private String sysOrgCode;
	/**
	 * 所属公司
	 */
	private String sysCompanyCode;
	/**
	 * 流程状态
	 */
	private String bpmStatus;
	/**
	 * 旺店通仓库
	 */
	private String wdtWarehouse;
	/**
	 * 创建人名称
	 */
	@Excel(name = "登记人")
	private String createName;
	/**
	 * 创建人登录id
	 */
	private Integer createBy;
	/**
	 * 创建时间
	 */
	@Excel(name = "登记时间")
	private String createDate;
	/**
	 * 更新人名称
	 */
	private String updateName;
	/**
	 * 更新人id
	 */
	private Integer updateBy;
	/**
	 * 更新日期
	 */
	private Date updateDate;
	/**
	 * 是否删除(0:有效,1:删除)
	 */
	private Boolean isDelete;

	/**
	 * 加价倍率
	 */
	private BigDecimal markupRate;
	/**
	 * 销售税率
	 */
	private BigDecimal salesTaxRate;

	/**
	 * 联名款品牌数量
	 */
	private Integer jointBrandCount;

	/**
	 * 财务主体
	 */
	private String financialBody;

}
