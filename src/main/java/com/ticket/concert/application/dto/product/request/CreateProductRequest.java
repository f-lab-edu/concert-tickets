package com.ticket.concert.application.dto.product.request;

import com.ticket.concert.domain.category.entity.Category;
import com.ticket.concert.domain.product.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateProductRequest(
        @NotNull(message = "분류번호는 필수입니다.")
        Long categoryId,
        @NotBlank(message = "상품 제목은 필수입니다.")
        String title,
        @NotNull(message = "관람시간은 필수입니다.")
        Integer runningTime,
        @NotNull(message = "시작일은 필수입니다.")
        LocalDate startDate,
        @NotNull(message = "종료일은 필수입니다.")
        LocalDate endDate,
        @NotNull(message = "예매 시작일은 필수입니다.")
        LocalDateTime bookingOpenAt,
        @NotNull(message = "예매 종료일은 필수입니다.")
        LocalDateTime bookingCloseAt
) {
        public Product toProduct(Category category) {
                return Product.builder()
                        .category(category)
                        .title(this.title())
                        .runningTime(this.runningTime())
                        .startDate(this.startDate())
                        .endDate(this.endDate())
                        .bookingOpenAt(this.bookingOpenAt())
                        .bookingCloseAt(this.bookingCloseAt())
                        .build();
        }
}
