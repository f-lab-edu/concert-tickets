package com.ticket.concert.repository.user;

import com.ticket.concert.domain.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Long save(User user);
}
