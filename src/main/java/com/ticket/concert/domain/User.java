package com.ticket.concert.domain;

import com.ticket.concert.domain.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class User extends BaseEntity {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private Role role;

    public User(Long id, String email, String password, String name, String phone, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = Role.valueOf(role);
    }

}
