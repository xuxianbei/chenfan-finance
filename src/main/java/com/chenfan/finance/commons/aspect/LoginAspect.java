package com.chenfan.finance.commons.aspect;

import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.annotation.Login;
import com.chenfan.finance.commons.exception.FinanceException;
import com.chenfan.finance.config.UserVoConstextHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

/**
 * token获取参数值，获取User赋值到形参
 *
 * @author liran
 */
@Component
@Aspect
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class LoginAspect {

    private final static String TOKEN = "token";

    private final static String USER_VO = "userVO";

    @Autowired
    HttpServletRequest request;
    @Autowired
    HttpSession session;

    @Pointcut("@within(com.chenfan.finance.commons.annotation.Login)||@annotation(com.chenfan.finance.commons.annotation.Login)")
    public void pointcut() {
    }

    @Around("pointcut()")
    @SneakyThrows
    public Object around(ProceedingJoinPoint point) {
        Object target = point.getThis();
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Login login = AnnotationUtils.findAnnotation(method, Login.class);
        if (login == null) {
            login = AnnotationUtils.findAnnotation(target.getClass(), Login.class);
        }

        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = point.getArgs();

        if (login != null && login.check()) {
            String token = request.getHeader(Constant.AUTHORIZATION_TOKEN);
            UserVO userVO = UserVoConstextHolder.getUserVo();
            if (userVO == null) {
                throw new FinanceException("用户未登录");
            }
            for (int i = 0; i < parameterNames.length; i++) {
                if (TOKEN.equals(parameterNames[i]) && args[i].getClass() == String.class) {
                    args[i] = token;
                    continue;
                }
                if (USER_VO.equals(parameterNames[i]) && args[i].getClass() == UserVO.class) {
                    args[i] = userVO;
                }
            }
        }
        try {
            return point.proceed(args);
        } catch (IllegalArgumentException e) {
            log.warn("参数错误 {}", e.getMessage());
            return new Response<>(ResponseCode.PARAMETER_ERROR.getCode(), e.getMessage());
        }
    }
}
