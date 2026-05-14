package com.ticket.concert.domain;

import com.ticket.concert.domain.constant.Role;
import com.ticket.concert.domain.user.User;

import java.util.List;

public record LoginUser(
        Long id,
        String username,
        List<Role> roles
) {

    public static LoginUser from(User user){
        return new LoginUser(user.getId(), user.getName(), List.of(user.getRole()));
    }
}
