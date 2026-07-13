package com.ticket.concert.presentation;

import com.ticket.concert.application.dto.product.request.CreateProductRequest;
import com.ticket.concert.application.dto.product.response.ProductResponse;
import com.ticket.concert.application.dto.product.response.UpcomingProductResponse;
import com.ticket.concert.application.product.ProductService;
import com.ticket.concert.domain.product.entity.Product;
import com.ticket.concert.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/v1/products")
    public ApiResponse<Long> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(request);
        return ApiResponse.success(product.getId());
    }

    @GetMapping(value = "/v1/products/upcoming")
    public ApiResponse<List<UpcomingProductResponse>> getUpcomingProducts() {
        List<UpcomingProductResponse> products = productService.getUpcomingProducts();
        return ApiResponse.success(products);
    }

    @GetMapping(value = "/v1/products/{id}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable(name = "id") Long productId) {
        ProductResponse product = productService.getProduct(productId);
        return ApiResponse.success(product);
    }
}
