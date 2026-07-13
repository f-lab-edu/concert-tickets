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

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다."),

    // User
    DUPLICATE_MEMBER(HttpStatus.BAD_REQUEST, "M001", "이미 가입된 회원입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "M002", "비밀번호 규칙에 맞지 않습니다."),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "M003", "아이디 또는 비밀번호가 올바르지 않습니다."),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "M004", "이미 사용중인 이메일입니다."),
    PHONE_DUPLICATED(HttpStatus.BAD_REQUEST, "M005", "이미 사용중인 연락처입니다."),

    // Email
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "E001", "잘못된 이메일 주소입니다."),
    MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E002", "메일 발송에 실패했습니다."),
    INVALID_RECIPIENT(HttpStatus.BAD_REQUEST, "E003", "존재하지 않는 메일 주소입니다"),
    MAIL_SERVER_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "E004", "메일 서비스가 일시적으로 불가합니다"),
    MAIL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E005", "메일 발송 중 오류가 발생했습니다"),
    EMAIL_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "E006", "잘못된 이메일 토큰입니다."),
    EMAIL_TOKEN_NOT_USABLE(HttpStatus.BAD_REQUEST, "E007", "사용할 수 없는 이메일 토큰입니다."),
    MAIL_TEMPLATE_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E008", "메일 템플릿을 불러오지 못했습니다."),

    // Category
    DUPLICATE_CATEGORY(HttpStatus.BAD_REQUEST, "CT001", "이미 존재하는 카테고리 입니다."),
    NOTFOUND_CATEGORY(HttpStatus.BAD_REQUEST, "CT002", "찾을 수 없는 카테고리 입니다."),

    // Product
    INVALID_SHOW_PERIOD(HttpStatus.BAD_REQUEST, "P001", "공연 종료일은 시작일보다 빠를 수 없습니다."),
    INVALID_BOOKING_PERIOD(HttpStatus.BAD_REQUEST, "P002", "예매 마감 일시는 시작 일시보다 빠를 수 없습니다."),
    PAST_SCHEDULE(HttpStatus.BAD_REQUEST, "P003", "공연·예매 일정은 현재 시각 이후여야 합니다."),
    BOOKING_AFTER_SHOW_START(HttpStatus.BAD_REQUEST, "P004", "예매는 공연 시작 전에 마감되어야 합니다."),
    NOTFOUND_PRODUCT(HttpStatus.BAD_REQUEST, "P005", "찾을 수 없는 상품입니다."),

    // Seat Inventory
    NOTFOUND_SEAT_INVENTORY(HttpStatus.BAD_REQUEST, "SI001", "찾을 수 없는 좌석 슬롯입니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;
}
