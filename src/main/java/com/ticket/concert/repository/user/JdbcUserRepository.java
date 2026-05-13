package com.ticket.concert.repository.user;

import com.ticket.concert.domain.User;
import com.ticket.concert.domain.constant.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
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
            rs.getString("phone"),
            rs.getString("role")
    );

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, email, password, name, phone, role FROM users WHERE email = :email AND status = :status";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("status", Status.ACTIVE.name());

        List<User> users = jdbcTemplate.query(sql, params, USER_ROW_MAPPER);
        return Optional.ofNullable(DataAccessUtils.singleResult(users));
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE email = :email AND status = :status)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("status", Status.ACTIVE.name());

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, params, Boolean.class));
    }

    @Override
    public boolean existsByPhone(String phone) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE phone = :phone AND status = :status)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("phone", phone)
                .addValue("status", Status.ACTIVE.name());

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, params, Boolean.class));
    }

    @Override
    public Long save(User user) {
        String sql = """
                INSERT INTO users (email, password, name, phone)
                VALUES (:email, :password, :name, :phone)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("name", user.getName())
                .addValue("phone", user.getPhone());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

}
