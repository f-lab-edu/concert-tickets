package com.ticket.concert.application.seatInventory;

import com.ticket.concert.application.dto.seatInventory.request.HoldSeatRequest;
import com.ticket.concert.application.dto.seatInventory.request.SeatInventoryRequest;
import com.ticket.concert.application.dto.seatInventory.response.SeatInventoryResponse;
import com.ticket.concert.domain.LoginUser;
import com.ticket.concert.domain.saetInventory.entity.SeatInventory;
import com.ticket.concert.domain.saetInventory.repository.SeatInventoryRepository;
import com.ticket.concert.domain.user.entity.User;
import com.ticket.concert.domain.user.repository.UserRepository;
import com.ticket.concert.global.exception.BusinessException;
import com.ticket.concert.global.exception.constant.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatInventoryService {

    private final SeatInventoryRepository seatInventoryRepository;
    private final UserRepository userRepository;
    private final SeatHoldOptimisticExecutor optimisticExecutor;

    private static final long HOLD_MINUTES = 7;
    private static final int MAX_RETRY = 3;


    public List<SeatInventoryResponse> getSeatInventory(SeatInventoryRequest request) {
        return seatInventoryRepository.findBySeatIdAndPerformanceIdAndDeleted(request.performanceId());
    }

    @Transactional
    public void hold(HoldSeatRequest request, LoginUser loginUser) {
        User user = findByUserOrThrow(loginUser);
        SeatInventory inventory = updateHoldInventory(request);
        inventory.hold(user, LocalDateTime.now(), HOLD_MINUTES);
    }

    private SeatInventory updateHoldInventory(HoldSeatRequest request) {
        return seatInventoryRepository
                .findForUpdate(request.performanceId(), request.seatId())
                .orElseThrow(() -> new BusinessException(ErrorCode.HOLD_INVENTORY));
    }

    private User findByUserOrThrow(LoginUser loginUser) {
        return userRepository.findById(loginUser.id())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTFOUND_USER));
    }

    public void holdWithOptimisticLock(HoldSeatRequest request, LoginUser loginUser) {
        User user = findUserOrThrows(loginUser);

        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                optimisticExecutor.hold(request, user);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("좌석 선점 버전 충돌, 재시도 {}/{}", attempt, MAX_RETRY);
                if (attempt == MAX_RETRY) {
                    throw new BusinessException(ErrorCode.HOLD_INVENTORY);
                }
            }
        }
    }

    private User findUserOrThrows(LoginUser loginUser) {
        return userRepository.findById(loginUser.id())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTFOUND_USER));
    }
}
