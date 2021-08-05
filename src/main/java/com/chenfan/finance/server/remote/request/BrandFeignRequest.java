package com.chenfan.finance.server.remote.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author liran
 */
@Data
public class BrandFeignRequest implements Serializable {

    private static final long serialVersionUID = 7181576213149776611L;

    /**品牌ID集合*/
    private List<Integer> brandIdList;

    /**品牌Name集合*/
    private List<String> brandNameList;

    /**品牌名称*/
    private String brandName;
}
