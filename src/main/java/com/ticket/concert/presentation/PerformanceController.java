package com.ticket.concert.presentation;

import com.ticket.concert.application.Performance.PerformanceService;
import com.ticket.concert.application.dto.performance.response.PerformanceResponse;
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
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping(value = "/v1/performance/{productId}")
    public ApiResponse<List<PerformanceResponse>> getPerformance(@PathVariable Long productId) {
        List<PerformanceResponse> performancesResponse = performanceService.getPerformances(productId);
        return ApiResponse.success(performancesResponse);
    }
}
