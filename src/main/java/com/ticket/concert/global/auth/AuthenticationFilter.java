package com.ticket.concert.global.auth;

import com.ticket.concert.domain.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthenticationFilter extends OncePerRequestFilter {

    public static final String SESSION_KEY = "LOGIN_USER";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute(SESSION_KEY) instanceof LoginUser user) {
                UserContextHolder.set(user);
            }
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.remove();
        }
    }
}
