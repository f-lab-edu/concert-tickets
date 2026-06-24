package com.ticket.concert.domain.saetInventory.repository;

import com.ticket.concert.application.dto.seatInventory.response.SeatInventoryResponse;
import com.ticket.concert.domain.saetInventory.entity.SeatInventory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {
    @Query("""
            SELECT new com.ticket.concert.application.dto.seatInventory.response.SeatInventoryResponse(
                   si.id, s.id, si.performance.id, si.status, si.heldUntil)
            FROM SeatInventory si
                JOIN si.seat s
            WHERE si.performance.id = :performanceId
              AND si.deleted = false
            """)
    List<SeatInventoryResponse> findBySeatIdAndPerformanceIdAndDeleted(Long performanceId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "2000")
    })
    @Query("""
                SELECT si FROM SeatInventory si
                WHERE si.performance.id = :performanceId
                  AND si.seat.id = :seatId
                  AND si.deleted = false
            """)
    Optional<SeatInventory> findForUpdate(@Param("performanceId") Long performanceId,
                                          @Param("seatId") Long seatId);
}
