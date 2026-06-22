package com.ticket.concert.application.dto.seatInventory.response;

import com.ticket.concert.domain.saetInventory.entity.Status;

import java.time.LocalDateTime;

public record SeatInventoryResponse(
        Long id,
        Long seatId,
        Long performanceId,
        Status status,
        LocalDateTime heldUntil,
        String zone,
        String row,
        String seatNo,
        String grade,
        Integer price
) {

}
