package com.ticket.concert.domain;

import lombok.Getter;

@Getter
public class User extends BaseEntity {
    private Long id;
    private String email;
    private String password;
    private String name;
}
