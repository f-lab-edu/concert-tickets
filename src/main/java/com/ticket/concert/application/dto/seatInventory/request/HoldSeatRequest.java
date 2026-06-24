package com.ticket.concert.application.dto.seatInventory.request;

import jakarta.validation.constraints.NotNull;

public record HoldSeatRequest(
        @NotNull(message = "회차 고유번호는 필수입니다.")
        Long performanceId,
        @NotNull(message = "좌석 고유번호는 필수입니다.")
        Long seatId
) {
}
