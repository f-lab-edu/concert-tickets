package com.ticket.concert.dto.auth.response;

import com.ticket.concert.domain.User;
import com.ticket.concert.domain.constant.Role;

import java.util.List;

public record LoginResponse(
        List<Role> roles
) {
    public LoginResponse(User user) {
        this(List.of(user.getRole()));
    }
}
