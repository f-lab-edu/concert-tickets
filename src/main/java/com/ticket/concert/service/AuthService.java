package com.ticket.concert.service;

import com.ticket.concert.dto.auth.reqeust.LoginRequest;
import com.ticket.concert.dto.auth.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {

        return null;
    }
}
