package com.ticket.concert.domain.saetInventory.entity;

import com.ticket.concert.domain.BaseTimeEntity;
import com.ticket.concert.domain.performance.entity.Performance;
import com.ticket.concert.domain.seat.entity.Seat;
import com.ticket.concert.domain.user.entity.User;
import com.ticket.concert.global.exception.BusinessException;
import com.ticket.concert.global.exception.constant.ErrorCode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "seat_inventory")
public class SeatInventory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @Enumerated(value = EnumType.STRING)
    private SeatInventoryStatus status;

    private LocalDateTime heldUntil;

    /**
     * `@Version`은 JPA에서 낙관적 락을 구현하기 위한 어노테이션입니다. <br/>
     * `@Version`이 붙은 필드는 엔티티가 수정될 때마다 자동으로 1씩 증가합니다. <br/>
     * JPA가 UPDATE 쿼리를 날릴 때 버전 값을 WHERE 조건에 포함시키는 것이 핵심입니다. <br/>
     * 동시에 여러 요청이 같은 좌석을 점유하려 할 때, UPDATE 시 version 불일치로
     * OptimisticLockException을 발생시켜 중복 점유를 방지합니다.
     */
    @Version
    private Long version;

    private Boolean deleted;


    public void hold(User user, LocalDateTime now, long holdMinutes) {
        if (!isHoldable(now)) {
            throw new BusinessException(ErrorCode.HOLD_INVENTORY);
        }
        this.user = user;
        this.status = SeatInventoryStatus.HELD;
        this.heldUntil = now.plusMinutes(holdMinutes);
    }

    private boolean isHoldable(LocalDateTime now) {
        if (status == SeatInventoryStatus.AVAILABLE) {
            return true;
        }
        return status == SeatInventoryStatus.HELD
                && heldUntil != null
                && heldUntil.isBefore(now);
    }

    @Builder
    public SeatInventory(Seat seat, Performance performance) {
        this.seat = seat;
        this.performance = performance;
        this.status = SeatInventoryStatus.AVAILABLE;
        this.deleted = false;
    }
}
