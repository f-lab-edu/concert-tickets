package com.ticket.concert.domain.seat.repository;

import com.ticket.concert.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
