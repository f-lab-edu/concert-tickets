package com.ticket.concert.domain.email.repository;

import com.ticket.concert.domain.email.entity.EmailVerifyToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerifyTokenRepository {
    Long save(String token,  String email, LocalDateTime tokenTtl);
    Optional<EmailVerifyToken> findByTokenAndStatus(String token);
    void updateConsumeAt(Long id);

}
