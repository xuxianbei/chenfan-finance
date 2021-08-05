package com.chenfan.finance.server.remote.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lizhejin
 * @since 2020-08-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BaseDicts implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典值code
     */
    @NotNull(message = "字典类型编码不能为空")
    @Length(max = 50, message = "字典类型编码长度不能超过50")
    private String dictprofileCode;

    /**
     * 字典类型code
     */
    @NotNull(message = "字典类型值不能为空")
    @Length(max = 50, message = "字典类型值长度不能超过50")
    private String dictsCode;

    /**
     * 字典值中文名
     */
    @NotNull(message = "字典类型值的中文名不能为空")
    @Length(max = 50, message = "字典类型值的中文名长度不能超过50")
    private String dictsNameC;

    /**
     * 字典值英文名
     */
    @NotNull(message = "字典类型值的英文名不能为空")
    @Length(max = 50, message = "字典类型值的英文名长度不能超过50")
    private String dictsNameE;

    /**
     * 字典值排序
     */
    @NotNull(message = "字典值排序序号不能为空")
    private Integer sortNo;

    /**
     * 状态（0：启用，1：禁用）
     */
    private Boolean dictsStatus;

    /**
     * 是否可编辑（0：可编辑，1：不可编辑）
     */
    private Boolean isFixed;

}
