package com.ticket.concert.application.dto.mail.request;

import com.ticket.concert.global.regex.UserRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MailSendRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Pattern(regexp = UserRegex.EMAIL, message = "이메일 형식을 확인해주세요.")
        String email
) {
}
