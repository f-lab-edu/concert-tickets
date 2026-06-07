package com.ticket.concert.presentation;

import com.ticket.concert.application.dto.product.request.CreateProductRequest;
import com.ticket.concert.application.product.ProductService;
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
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/v1/product")
    public ApiResponse<Void> createProduct(@Valid @RequestBody CreateProductRequest request) {
        productService.createProduct(request);
        return ApiResponse.success();
    }
}
