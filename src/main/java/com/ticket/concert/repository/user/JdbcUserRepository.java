package com.ticket.concert.repository.user;

import com.ticket.concert.domain.User;
import com.ticket.concert.domain.constant.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("name"),
            rs.getString("role")
    );

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, email, password, name, role FROM users WHERE email = :email AND status = :status";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("status", Status.ACTIVE.name());

        List<User> users = jdbcTemplate.query(sql, params, USER_ROW_MAPPER);
        return Optional.ofNullable(DataAccessUtils.singleResult(users));
    }

}
