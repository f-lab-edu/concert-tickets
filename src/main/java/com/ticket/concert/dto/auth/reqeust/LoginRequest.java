package com.ticket.concert.dto.auth.reqeust;

import com.ticket.concert.global.regex.UserRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Pattern(regexp = UserRegex.EMAIL, message = "이메일 형식을 확인해주세요.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = UserRegex.PASSWORD, message = "비밀번호 형식을 확인해주세요.")
        String password
) {
}
