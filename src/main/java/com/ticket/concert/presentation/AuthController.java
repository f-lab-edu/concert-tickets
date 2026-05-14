package com.ticket.concert.presentation;

import com.ticket.concert.application.dto.auth.request.LoginRequest;
import com.ticket.concert.application.dto.auth.response.LoginResponse;
import com.ticket.concert.global.common.ApiResponse;
import com.ticket.concert.application.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/v1/auth/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        LoginResponse loginResponse = authService.login(request, httpRequest);
        return ApiResponse.success(loginResponse);
    }
}
