package com.ticket.concert.domain.email;

import com.ticket.concert.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EmailVerifyToken extends BaseEntity {

    private Long id;
    private String token;
    private String email;
    private LocalDateTime expiresAt;
    private LocalDateTime consumeAt;

    public boolean isConsumable() {
        return consumeAt == null && expiresAt.isAfter(LocalDateTime.now());
    }

}
