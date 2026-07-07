package com.ticket.concert.application.seatInventory;

import com.ticket.concert.application.dto.seatInventory.request.HoldSeatRequest;
import com.ticket.concert.domain.LoginUser;
import com.ticket.concert.global.cache.SeatHoldRedisManager;
import com.ticket.concert.global.exception.BusinessException;
import com.ticket.concert.global.exception.constant.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SeatInventoryRedisService {

    private final SeatHoldRedisManager seatHoldRedisManager;
    private final SeatInventoryService seatInventoryService;

    public void holdRedis(HoldSeatRequest request, LoginUser loginUser) {
        Long performanceId = request.performanceId();
        Long seatId = request.seatId();
        Long userId = loginUser.id();

        boolean acquired = seatHoldRedisManager.tryHold(performanceId, seatId, userId);
        if (!acquired) {
            throw new BusinessException(ErrorCode.HOLD_INVENTORY);
        }

        try {
            seatInventoryService.hold(request, loginUser);
        } catch (RuntimeException e) {
            seatHoldRedisManager.release(performanceId, seatId, userId);
            throw e;
        }
    }


}
