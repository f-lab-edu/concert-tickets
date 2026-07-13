package com.ticket.concert.domain.performance.repository;

import com.ticket.concert.domain.performance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    List<Performance> findByProductIdAndDeleted(Long productId, Boolean deleted);
}
