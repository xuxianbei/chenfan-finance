package com.chenfan.finance.service.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author: xuxianbei
 * Date: 2021/3/12
 * Time: 10:14
 * Version:V1.0
 */
@RefreshScope
@Configuration
@Getter
public class McnConfiguration {

    /**
     * 品牌Id：mcn
     */
    @Value("${mcn.default.brand.id:1}")
    private Integer brandIdMcn;


}
