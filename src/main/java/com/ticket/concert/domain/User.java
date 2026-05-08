package com.ticket.concert.domain;

import com.ticket.concert.domain.constant.Role;
import lombok.Getter;

@Getter
public class User extends BaseEntity {
    private Long id;
    private String email;
    private String password;
    private String name;
    private Role role;

    public User(Long id, String email, String password, String name, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = Role.valueOf(role);
    }

}
