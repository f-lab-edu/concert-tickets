package com.ticket.concert.domain;

public record LoginUser(
        Long id,
        String username,
        String[] role
) {
}
