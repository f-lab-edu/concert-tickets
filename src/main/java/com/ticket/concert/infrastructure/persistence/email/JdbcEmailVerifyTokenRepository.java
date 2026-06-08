package com.ticket.concert.infrastructure.persistence.email;

import com.ticket.concert.domain.email.entity.EmailVerifyToken;
import com.ticket.concert.domain.email.repository.EmailVerifyTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcEmailVerifyTokenRepository implements EmailVerifyTokenRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public static final RowMapper<EmailVerifyToken> EMAIL_VERIFY_TOKEN_ROW_MAPPER = (rs, rowNum) -> new EmailVerifyToken(
            rs.getLong("id"),
            rs.getString("token"),
            rs.getString("email"),
            rs.getObject("expires_at", LocalDateTime.class),
            rs.getObject("consume_at", LocalDateTime.class)
    );

    @Override
    public Long save(String token, String email, LocalDateTime expiresAt) {
        String sql = """
                INSERT INTO email_verify_token (token, email, expires_at)
                VALUES (:token, :email, :expiresAt)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("token", token)
                .addValue("email", email)
                .addValue("expiresAt", expiresAt);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public Optional<EmailVerifyToken> findByTokenAndStatus(String token) {
        String sql = """
                SELECT id, token, email, expires_at, consume_at
                FROM email_verify_token
                WHERE token = :token
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("token", token);

        List<EmailVerifyToken> emailVerifyTokens = jdbcTemplate.query(sql, params, EMAIL_VERIFY_TOKEN_ROW_MAPPER);
        return Optional.ofNullable(DataAccessUtils.singleResult(emailVerifyTokens));
    }

    @Override
    public void updateConsumeAt(Long id) {
        String sql = """
                UPDATE email_verify_token
                SET consume_at = NOW()
                WHERE id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        jdbcTemplate.update(sql, params);
    }
}
