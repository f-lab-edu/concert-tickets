package com.ticket.concert.domain.email;

import com.ticket.concert.domain.constant.Status;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerifyTokenRepository {
    Long save(String token,  String email, LocalDateTime tokenTtl);
    Optional<EmailVerifyToken> findByTokenAndStatus(String token, Status status);
    void updateConsumeAt(String token, Status status);

}
