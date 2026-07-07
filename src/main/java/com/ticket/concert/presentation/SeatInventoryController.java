package com.ticket.concert.presentation;

import com.ticket.concert.application.dto.seatInventory.request.HoldSeatRequest;
import com.ticket.concert.application.dto.seatInventory.request.SeatInventoryRequest;
import com.ticket.concert.application.dto.seatInventory.response.SeatInventoryResponse;
import com.ticket.concert.application.seatInventory.SeatInventoryRedisService;
import com.ticket.concert.application.seatInventory.SeatInventoryService;
import com.ticket.concert.domain.LoginUser;
import com.ticket.concert.global.auth.resolver.CurrentUser;
import com.ticket.concert.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SeatInventoryController {

    private final SeatInventoryService seatInventoryService;
    private final SeatInventoryRedisService seatInventoryRedisService;

    @GetMapping(value = "/v1/seat-inventory")
    public ApiResponse<List<SeatInventoryResponse>> getSeatInventory(@Valid @ModelAttribute SeatInventoryRequest request) {
        List<SeatInventoryResponse> seatInventory = seatInventoryService.getSeatInventory(request);
        return ApiResponse.success(seatInventory);
    }

    /**
     * 비관적락 방식 좌석 선점 <br/>
     * SELECT ... FOR UPDATE 쿼리를 발생시켜, 조회 시점부터 그 행에 DB 락을 겁니다.<br/>
     * A가 좌석을 잡는 동안 B의 요청은 락이 풀릴 떄까지 대기하고, A가 커밋한 후에야 B가 조회됩니다.<br/>
     * 이때 B는 이미 HELD 상태인 좌석을 확인하여 isHoldable()에서 막힙니다.
     */
    @PostMapping(value = "/v1/seat-inventory")
    public ApiResponse<Void> holdSeatInventory(@Valid @RequestBody HoldSeatRequest request, @CurrentUser LoginUser user) {
        seatInventoryService.hold(request, user);
        return ApiResponse.success();
    }

    /**
     * 낙관적락 방식 좌석 선점 <br/>
     * DB락을 걸지 않고 @Version 필드로 충돌을 감지하는 방식입니다. <br/>
     * holdWithOptimisticLock은 재시도 루프만 돌리고, 실제 트랜잭션 작업은 SeatHoldOptimisticExecutor에 위임합니다. <br/>
     * '@Transactional'은 프록시 기반이라 같은 클래스 안에서 메서드를 직접 호출하면 트랜잭션이 새로 시작되지 않습니다.
     */
    @PostMapping(value = "/v1/seat-inventory/optimistic")
    public ApiResponse<Void> holdSeatInventoryOptimistic(@Valid @RequestBody HoldSeatRequest request,
                                                         @CurrentUser LoginUser user) {
        seatInventoryService.holdWithOptimisticLock(request, user);
        return ApiResponse.success();
    }

    /**
     * Redis 분산 락 방식 <br/>
     * DB에 부하를 주기 전 Redis에서 먼저 확인하는 방식입니다. 대규모 트래픽에서 DB 락 경합 자체를 줄일 수 있습니다. <br/>
     */
    @PostMapping(value = "/v1/seat-inventory-redis")
    public ApiResponse<Void> holdRedisSeatInventory(@Valid @RequestBody HoldSeatRequest request, @CurrentUser LoginUser user) {
        seatInventoryRedisService.holdRedis(request, user);
        return ApiResponse.success();
    }
}
