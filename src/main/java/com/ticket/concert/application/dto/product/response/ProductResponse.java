package com.ticket.concert.application.dto.product.response;

import com.ticket.concert.domain.product.entity.Product;

import java.time.LocalDate;

public record ProductResponse(
        Long id,
        Long categoryId,
        String title,
        Integer runningTime,
        LocalDate startDate,
        LocalDate EndDate
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCategory().getId(),
                product.getTitle(),
                product.getRunningTime(),
                product.getStartDate(),
                product.getEndDate()
        );
    }
}
