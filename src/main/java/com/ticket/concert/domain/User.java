package com.ticket.concert.domain;

import com.ticket.concert.domain.constant.Role;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
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

    public static User create(String email, String password, String name, String phone, PasswordEncoder passwordEncoder) {
        return new User(null, email, passwordEncoder.encode(password), name, phone, Role.USER.name());
    }

}
