package com.ticket.concert.application.dto.category.request;

import com.ticket.concert.domain.category.entity.Category;
import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank(message = "분류명은 필수입니다.")
        String name
) {
    public Category toCategory() {
        return new Category(name);
    }
}
