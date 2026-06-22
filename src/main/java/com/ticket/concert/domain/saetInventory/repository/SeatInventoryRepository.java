package com.ticket.concert.domain.saetInventory.repository;

import com.ticket.concert.application.dto.seatInventory.response.SeatInventoryResponse;
import com.ticket.concert.domain.saetInventory.entity.SeatInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {
    @Query("""
            SELECT new com.ticket.concert.application.dto.seatInventory.response.SeatInventoryResponse(
                   si.id, s.id, si.performance.id, si.status, si.heldUntil,
                   s.zone, s.row, s.seatNo, s.grade, s.price)
            FROM SeatInventory si
                JOIN si.seat s
            WHERE si.performance.id = :performanceId
              AND si.deleted = :deleted
            """)
    List<SeatInventoryResponse> findBySeatIdAndPerformanceIdAndDeleted(Long performanceId, Boolean deleted);
}
