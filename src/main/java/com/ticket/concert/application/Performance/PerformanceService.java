package com.ticket.concert.application.Performance;

import com.ticket.concert.application.dto.performance.response.PerformanceResponse;
import com.ticket.concert.domain.performance.entity.Performance;
import com.ticket.concert.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    public List<PerformanceResponse> getPerformances(Long productId) {
        List<Performance> performanceResponses = performanceRepository.findByProductIdAndDeleted(productId, false);
        return performanceResponses.stream()
                .map(PerformanceResponse::from)
                .toList();
    }
}
