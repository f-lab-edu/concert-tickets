package com.ticket.concert.global.auth;

import com.ticket.concert.domain.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class LoginUserFilter extends OncePerRequestFilter {

    public static final String SESSION_KEY = "LOGIN_USER";
    private static final ThreadLocal<LoginUser> CURRENT = new ThreadLocal<>();

    public static LoginUser getCurrentUser() {
        return CURRENT.get();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute(SESSION_KEY) instanceof LoginUser u) {
                CURRENT.set(u);
            }
            filterChain.doFilter(request, response);
        } finally {
            CURRENT.remove();
        }
    }
}
