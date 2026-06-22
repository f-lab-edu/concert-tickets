package com.ticket.concert.domain.seat.repository;

import com.ticket.concert.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByProductIdAndDeleted(Long productId, Boolean deleted);
}
