package com.ticket.concert.dto.auth.reqeust;

import com.ticket.concert.global.regex.Regex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = "아이디는 필수입니다.")
        @Pattern(regexp = Regex.USER_ID, message = "아이디 형식을 확인해주세요.")
        String userId,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = Regex.PASSWORD, message = "비밀번호 형식을 확인해주세요.")
        String password
) {
}
