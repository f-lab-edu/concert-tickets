package com.ticket.concert.application.dto.product.response;

import com.ticket.concert.domain.product.entity.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpcomingProductResponse(
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime bookingOpenAt,
        LocalDateTime bookingCloseAt
) {
    public static UpcomingProductResponse from(Product product) {
        return new UpcomingProductResponse(
                product.getId(),
                product.getTitle(),
                product.getStartDate(),
                product.getEndDate(),
                product.getBookingOpenAt(),
                product.getBookingCloseAt()
        );
    }
}
