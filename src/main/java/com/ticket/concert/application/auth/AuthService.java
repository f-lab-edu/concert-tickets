package com.ticket.concert.application.auth;

import com.ticket.concert.domain.LoginUser;
import com.ticket.concert.domain.user.entity.User;
import com.ticket.concert.application.dto.auth.request.LoginRequest;
import com.ticket.concert.application.dto.auth.response.LoginResponse;
import com.ticket.concert.global.exception.BusinessException;
import com.ticket.concert.global.exception.constant.ErrorCode;
import com.ticket.concert.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.ticket.concert.global.auth.SessionConst.SESSION_KEY;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = findUserOrThrow(request);
        validatePassword(request, user);
        createLoginSession(httpRequest, user);
        return LoginResponse.from(user);
    }

    private User findUserOrThrow(LoginRequest request) {
        return userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));
    }

    private void validatePassword(LoginRequest request, User user) {
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    private void createLoginSession(HttpServletRequest httpRequest, User user) {
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(SESSION_KEY, LoginUser.from(user));
    }

}
