package com.ticket.concert.dto.user.mapper;

import com.ticket.concert.domain.User;
import com.ticket.concert.dto.user.request.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toUser(JoinRequest request) {
        return User.create(request.email(),
                request.password(),
                request.name(),
                request.phone(),
                passwordEncoder);
    }
}
