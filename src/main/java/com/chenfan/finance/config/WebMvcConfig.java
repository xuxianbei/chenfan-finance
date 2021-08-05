package com.chenfan.finance.config;

import com.alibaba.fastjson.JSONObject;
import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.UserVO;
import com.chenfan.privilege.common.config.SearchAuthorityHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 公用拦截器 监听数据源切换
 *
 * @author lizhejin
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                new HandlerInterceptorAdapter() {
                    @Override
                    public boolean preHandle(
                            HttpServletRequest request, HttpServletResponse response, Object handler)
                            throws Exception {
                        String token = request.getHeader(Constant.AUTHORIZATION_TOKEN);
                        if (!StringUtils.isEmpty(token)) {
                            UserVO user = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
                            UserVoConstextHolder.setUserVo(user);
                        }
                        return true;
                    }

                    @Override
                    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                                Object handler, Exception ex) throws Exception {
                        super.afterCompletion(request, response, handler, ex);
                        //移除用户信息
                        UserVoConstextHolder.remove();
                    }
                }).addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SearchAuthorityHandler(stringRedisTemplate));
    }

}
