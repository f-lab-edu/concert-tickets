package com.ticket.concert.application.dto.auth.response;

import com.ticket.concert.domain.user.entity.User;
import com.ticket.concert.domain.user.constant.Role;

import java.util.List;

public record LoginResponse(
        List<Role> roles
) {
    public static LoginResponse from(User user) {
        return new LoginResponse(List.of(user.getRole()));
    }
}
