package com.ticket.concert.application.dto.performance.response;

import com.ticket.concert.domain.performance.entity.Performance;

import java.time.LocalDateTime;

public record PerformanceResponse(
        Long id,
        LocalDateTime showAt,
        String status
) {
    public static PerformanceResponse from(Performance performance) {
        return new PerformanceResponse(
                performance.getId(),
                performance.getShowAt(),
                performance.getStatus().name()
        );
    }
}
