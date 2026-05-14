package com.ticket.concert.presentation.dto.user.request;

import com.ticket.concert.domain.user.User;
import com.ticket.concert.global.regex.UserRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;

public record JoinRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Pattern(regexp = UserRegex.EMAIL, message = "이메일 형식을 확인해주세요.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = UserRegex.PASSWORD, message = "비밀번호 형식을 확인해주세요.")
        String password,

        @NotBlank(message = "이름은 필수입니다.")
        @Pattern(regexp = UserRegex.NAME, message = "이름 형식을 확인해주세요.")
        String name,

        @NotBlank(message = "연락처는 필수입니다.")
        @Pattern(regexp = UserRegex.PHONE, message = "연락처 형식을 확인해주세요.")
        String phone
) {

    public User toUser(PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(this.email())
                .password(passwordEncoder.encode(this.password))
                .name(this.name())
                .phone(this.phone())
                .build();
    }
}
