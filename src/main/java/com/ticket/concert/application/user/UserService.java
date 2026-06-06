package com.ticket.concert.application.user;

import com.ticket.concert.domain.user.entity.User;
import com.ticket.concert.application.dto.user.request.JoinRequest;
import com.ticket.concert.global.exception.BusinessException;
import com.ticket.concert.global.exception.constant.ErrorCode;
import com.ticket.concert.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(JoinRequest request) {
        validateDuplicateEmail(request.email());
        validateDuplicatePhone(request.phone());

        User user = request.toUser(passwordEncoder);

        Long generatedId = userRepository.save(user);
        log.info("[USER_JOIN] success. userId={}", generatedId);
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }
    }

    private void validateDuplicatePhone(String phone) {
        if (userRepository.existsByPhone(phone)) {
            throw new BusinessException(ErrorCode.PHONE_DUPLICATED);
        }
    }
}
