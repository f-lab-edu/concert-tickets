package com.ticket.concert.domain.user;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Long save(User user);
}
