package com.ticket.concert.global.auth.filter;

import com.ticket.concert.domain.LoginUser;
import com.ticket.concert.global.auth.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.ticket.concert.global.auth.SessionConst.SESSION_KEY;

public class AuthenticationFilter extends OncePerRequestFilter {

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
