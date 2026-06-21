package com.ticket.concert.application.dto.seat.response;

public record SeatResponse(
        Long id,
        String zone,
        String row,
        String seatNo,
        Integer price,
        String grade,
        String color
) {
}
