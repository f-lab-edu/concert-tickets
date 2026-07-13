package com.ticket.concert.domain.saetInventory.entity;

import lombok.Getter;

@Getter
public enum SeatInventoryStatus {
    AVAILABLE("예매 가능"),
    HELD("선점중"),
    SOLD("판매 완료"),
    BLOCKED("판매 불가");

    private final String description;

    SeatInventoryStatus(String description) {
        this.description = description;
    }
}
