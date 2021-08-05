package com.chenfan.finance.utils;


import com.alibaba.fastjson.JSONObject;
import com.chenfan.common.config.Constant;
import com.chenfan.common.exception.BusinessException;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author 2062
 */
@Component
public class AuthorizationUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public UserVO getUser() {
        String token =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader(Constant.AUTHORIZATION_TOKEN);
        UserVO user = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
        return user;
    }

    public UserVO getUserOfRequired() {
        UserVO user = this.getUser();
        if (Objects.isNull(user)) {
            throw new BusinessException(Constant.UNAUTHORIZED,"用户登录失效");
        }
        return user;
    }

    public String getUserName(){
        UserVO user = this.getUser();
        return user==null?null:user.getRealName();
    }

    /**
     * 注意： 创建子线程下，值为空，需要额外编码进行子线程传递
     * @return
     */
    public String getToken() {
        if(Objects.isNull(RequestContextHolder.getRequestAttributes())){
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if(Objects.isNull(request)){
            return null;
        }
        return request.getHeader(Constant.AUTHORIZATION_TOKEN);
    }
}
