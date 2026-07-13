package com.ticket.concert.presentation;

import com.ticket.concert.application.dto.seat.response.SeatResponse;
import com.ticket.concert.application.seat.SeatService;
import com.ticket.concert.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping(value = "/v1/seat/{productId}")
    public ApiResponse<List<SeatResponse>> getSeat(@PathVariable Long productId) {
        List<SeatResponse> seats = seatService.getSeats(productId);
        return ApiResponse.success(seats);
    }

}
