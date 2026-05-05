package com.ticket.concert.global.interceptor;

import com.ticket.concert.domain.LoginUser;
import com.ticket.concert.global.annotation.Auth;
import com.ticket.concert.global.auth.LoginUserFilter;
import com.ticket.concert.global.exception.BusinessException;
import com.ticket.concert.global.exception.constant.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod hm)) {
            return true;
        }

        Auth anno = hm.getMethodAnnotation(Auth.class);
        if (anno == null) {
            return true;
        }

        LoginUser user = LoginUserFilter.getCurrentUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        if (anno.roles().length > 0 && !Arrays.asList(anno.roles()).contains(user.role())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        return true;
    }
}
