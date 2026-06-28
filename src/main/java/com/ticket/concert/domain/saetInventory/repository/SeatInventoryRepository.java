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

    /**
     * 비관적 락을 거는 조회 메서드입니다. 핵심 목적은 같은 좌석에 동시 요청이 와도 한 번에 한 트랜잭션만 그 행을 다루게 만드는 것입니다.<br/>
     * 먼저 현재 상태를 조회하고 락을 획득할 수 있는지 판단한 후 수정해야 하는데, '읽가 > 판단 > 수정' 사이에 다른 요청이 끼어들면
     * 둘 다 같은 좌석을 잡아버리는 사고가 발생합니다. 그래서 조회하는 순간부터 그 행을 잠궈 다른 요청은 잠금이 풀릴 때까지 대기하게 만듭니다.
     */
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

    @Query("""
        SELECT si FROM SeatInventory si
        WHERE si.performance.id = :performanceId
          AND si.seat.id = :seatId
          AND si.deleted = false
        """)
    Optional<SeatInventory> findBySeat(@Param("performanceId") Long performanceId,
                                       @Param("seatId") Long seatId);
}
