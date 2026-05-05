package com.ticket.concert.domain;

import java.io.Serializable;

// 세션에 담기는 객체를 Serializable을 활용해 직렬화
public record LoginUser(
        Long id,
        String username,
        String role
) implements Serializable {
}
