package com.chenfan.finance.controller;

import com.chenfan.common.config.Constant;
import org.springframework.http.HttpHeaders;

/**
 * @author Eleven.Xiao
 * @description //单元测试配置
 * @date 2020/12/9  14:01
 */
public class BaseTestConfig {
    private static final HttpHeaders httpHeaders = new HttpHeaders();
    static {
        httpHeaders.add(Constant.AUTHORIZATION_TOKEN, "2DCBA7B44DCD1DD098DE5F1FC4AAB8F3A3B87F3F2FC88BF44B12707B251613716DAD1293E63E1F948D8BC846CA2A5A41");
        httpHeaders.add(Constant.DATA_TYPE, "qy");
    }
    public static HttpHeaders getHttpHeaders(){
         return httpHeaders;
    }
}
