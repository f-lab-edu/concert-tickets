package com.ticket.concert.global.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부에 오류가 발생했습니다."),
    EXTERNAL_RESOURCE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "C004", "일시적인 오류가 발생했습니다."),

    // User
    DUPLICATE_MEMBER(HttpStatus.BAD_REQUEST, "M001", "이미 가입된 회원입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "M002", "비밀번호 규칙에 맞지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
