package com.ticket.concert.application.dto.seatInventory.request;

import jakarta.validation.constraints.NotNull;

public record SeatInventoryRequest(
        @NotNull(message = "회차 고유번호는 필수입니다.")
        Long performanceId
) {
}
