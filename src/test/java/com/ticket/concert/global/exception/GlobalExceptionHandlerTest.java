package com.ticket.concert.global.exception;

import com.ticket.concert.global.common.ApiResponse;
import com.ticket.concert.global.exception.constant.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void BusinessException_던져지면_정해진_ApiResponse와_상태코드_반환() {
        BusinessException ex = new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);

        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessException(ex);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(response.getBody().isSuccess()).isFalse();
        Assertions.assertThat(response.getBody().getError().getCode()).isEqualTo("INTERNAL_SERVER_ERROR");
    }

}