package com.ticket.concert.domain.saetInventory.entity;

import lombok.Getter;

@Getter
public enum Status {
    AVAILABLE("예매 가능"),
    HELD("결제 대기"),
    SOLD("판매 완료"),
    BLOCKED("판매 불가");

    private final String description;

    Status(String description) {
        this.description = description;
    }
}
