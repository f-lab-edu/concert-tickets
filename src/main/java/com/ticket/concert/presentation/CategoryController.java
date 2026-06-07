package com.ticket.concert.presentation;

import com.ticket.concert.application.category.CategoryService;
import com.ticket.concert.application.dto.category.request.CreateCategoryRequest;
import com.ticket.concert.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(value = "/v1/category")
    public ApiResponse<Void> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        categoryService.createCategory(request);
        return ApiResponse.success();
    }
}
