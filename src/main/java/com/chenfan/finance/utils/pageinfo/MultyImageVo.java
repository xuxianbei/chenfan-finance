package com.chenfan.finance.utils.pageinfo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author: xuxianbei
 * Date: 2021/4/4
 * Time: 16:50
 * Version:V1.0
 */
@Data
public class MultyImageVo {

    /**
     * 名字
     */
    @NotEmpty
    private String fileName;

    /**
     * url
     */
    @NotEmpty
    private String id;
}
