package com.ticket.concert.presentation.dto.auth.response;

import com.ticket.concert.domain.user.User;
import com.ticket.concert.domain.constant.Role;

import java.util.List;

public record LoginResponse(
        List<Role> roles
) {
    public static LoginResponse from(User user) {
        return new LoginResponse(List.of(user.getRole()));
    }
}
