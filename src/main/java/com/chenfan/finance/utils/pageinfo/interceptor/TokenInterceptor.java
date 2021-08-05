package com.chenfan.finance.utils.pageinfo.interceptor;

import com.chenfan.common.config.Constant;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: xuxianbei
 * Date: 2021/4/5
 * Time: 17:44
 * Version:V1.0
 */
@Component
public class TokenInterceptor implements RequestInterceptor {

    @Autowired
    private PageInfoUtil pageInfoUtil;

    @Override
    public void apply(RequestTemplate template) {
        template.header(Constant.AUTHORIZATION_TOKEN, pageInfoUtil.getToken());
    }
}
