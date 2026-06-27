package com.ticket.concert.presentation;

import com.ticket.concert.application.dto.seatInventory.request.CreateSeatInventoryRequest;
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

    @PostMapping(value = "/v1/seat-inventory")
    public ApiResponse<Void> holdSeatInventory(@Valid @RequestBody HoldSeatRequest request, @CurrentUser LoginUser user) {
        seatInventoryService.hold(request, user);
        return ApiResponse.success();
    }

    @PostMapping(value = "/v1/seat-inventory/optimistic")
    public ApiResponse<Void> holdSeatInventoryOptimistic(@Valid @RequestBody HoldSeatRequest request,
                                                         @CurrentUser LoginUser user) {
        seatInventoryService.holdWithOptimisticLock(request, user);
        return ApiResponse.success();
    }

    @PostMapping(value = "/v1/seat-inventory-redis")
    public ApiResponse<Void> holdRedisSeatInventory(@Valid @RequestBody HoldSeatRequest request, @CurrentUser LoginUser user) {
        seatInventoryRedisService.holdRedis(request, user);
        return ApiResponse.success();
    }
}
