package com.ticket.concert.application.dto.seat.response;

import com.ticket.concert.domain.seat.entity.Seat;

public record SeatResponse(
        Long id,
        String zone,
        String row,
        String seatNo,
        Integer price,
        String grade,
        String color
) {
    public static SeatResponse from(Seat seat){
        return new SeatResponse(
                seat.getId(),
                seat.getZone(),
                seat.getRow(),
                seat.getSeatNo(),
                seat.getPrice(),
                seat.getGrade(),
                seat.getColor()
        );
    }
}
