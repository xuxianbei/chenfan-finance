package com.chenfan.finance.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author chenguopeng
 * @Date 2021/7/9 10:15
 */
@Data
@ApiModel(value = "财务-核销批量执行返回结果")
public class ClearHeaderCommitVo {

	/**
	 * 失败条数
	 */
	private Integer failList;

	/**
	 * 成功条数
	 */
	private Integer succssList;

	/**
	 * 驳回条数
	 */
	private Integer rejectList;

	/**
	 * 批量提交返回结果
	 * @param failList
	 * @param succssList
	 * @param rejectList
	 */
	public ClearHeaderCommitVo(Integer failList, Integer succssList, Integer rejectList) {
		this.failList = failList;
		this.succssList = succssList;
		this.rejectList = rejectList;
	}

	/**
	 * 批量审批返回结果
	 * @param failList
	 * @param succssList
	 */
	public ClearHeaderCommitVo(Integer failList, Integer succssList) {
		this.failList = failList;
		this.succssList = succssList;
	}
}
