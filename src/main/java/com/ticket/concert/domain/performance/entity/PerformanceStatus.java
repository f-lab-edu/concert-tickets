package com.ticket.concert.domain.performance.entity;

import lombok.Getter;

@Getter
public enum PerformanceStatus {
    AVAILABLE("예매 가능"),
    SOLD_OUT("매진"),
    CANCELED("취소");

    private final String description;

    PerformanceStatus(String description) {
        this.description = description;
    }
}
