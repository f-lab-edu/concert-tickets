package com.ticket.concert.application.seatInventory;

import com.ticket.concert.application.dto.seatInventory.request.HoldSeatRequest;
import com.ticket.concert.domain.saetInventory.entity.SeatInventory;
import com.ticket.concert.domain.saetInventory.repository.SeatInventoryRepository;
import com.ticket.concert.domain.user.entity.User;
import com.ticket.concert.global.exception.BusinessException;
import com.ticket.concert.global.exception.constant.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SeatHoldOptimisticExecutor {

    private final SeatInventoryRepository seatInventoryRepository;

    private static final long HOLD_MINUTES = 7;

    @Transactional
    public void hold(HoldSeatRequest request, User user) {
        SeatInventory inventory = seatInventoryRepository
                .findBySeat(request.performanceId(), request.seatId())
                .orElseThrow(() -> new BusinessException(ErrorCode.HOLD_INVENTORY));

        inventory.hold(user, LocalDateTime.now(), HOLD_MINUTES);
    }
}
