package com.ticket.concert.domain;

import com.ticket.concert.domain.constant.Role;

import java.util.List;

public record LoginUser(
        Long id,
        String username,
        List<Role> roles
) {
    public LoginUser(User user) {
        this(user.getId(), user.getName(), List.of(user.getRole()));
    }
}
