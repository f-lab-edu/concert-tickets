package com.ticket.concert.application.dto.seatInventory.response;

import com.ticket.concert.domain.saetInventory.entity.SeatInventoryStatus;

import java.time.LocalDateTime;

public record SeatInventoryResponse(
        Long id,
        Long seatId,
        Long performanceId,
        SeatInventoryStatus status,
        LocalDateTime heldUntil
) {

}
