package com.ticket.concert.application.dto.seatInventory.request;

public record CreateSeatInventoryRequest(
        Long seatId,
        Long performanceId
) {
}
