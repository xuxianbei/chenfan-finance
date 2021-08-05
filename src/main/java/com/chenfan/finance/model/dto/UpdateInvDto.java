package com.chenfan.finance.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author Wen.Xiao
 * @Description // 更改商品档案关联的数据
 * @Date 2021/5/28  15:59
 * @Version 1.0
 */
@Data
public class UpdateInvDto {

    /**
     * 存货档案编码（和productCode 任意传一个就好，两个都传时 以inventoryId为主）
     */
    private  Integer  inventoryId;
    /**
     * 商品编码 （和inventoryId 任意传一个就好，两个都传时 以inventoryId为主）
     */
    private  String productCode;
    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private  String  inventoryName;
    /**
     * 销售类型
     */
    @NotNull(message = "销售类型不能为空")
    private  Integer salesType;
}
