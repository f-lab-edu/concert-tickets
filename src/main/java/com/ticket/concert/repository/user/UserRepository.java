package com.ticket.concert.repository.user;

import com.ticket.concert.domain.User;
import com.ticket.concert.domain.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("name"),
            rs.getString("role")
    );

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, email, password, name, role FROM users WHERE email = ? AND status = 'ACTIVE'";
        return jdbcTemplate.query(sql, USER_ROW_MAPPER, email).stream().findFirst();
    }

}
