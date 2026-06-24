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
}
