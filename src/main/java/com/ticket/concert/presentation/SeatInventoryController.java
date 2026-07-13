package com.ticket.concert.presentation;

import com.ticket.concert.application.dto.seatInventory.request.SeatInventoryRequest;
import com.ticket.concert.application.dto.seatInventory.response.SeatInventoryResponse;
import com.ticket.concert.application.seatInventory.SeatInventoryService;
import com.ticket.concert.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SeatInventoryController {

    private final SeatInventoryService seatInventoryService;

    @GetMapping(value = "/v1/seat-inventory")
    public ApiResponse<List<SeatInventoryResponse>> getSeatInventory(@Valid @ModelAttribute SeatInventoryRequest request) {
        List<SeatInventoryResponse> seatInventory = seatInventoryService.getSeatInventory(request);
        return ApiResponse.success(seatInventory);
    }
}
