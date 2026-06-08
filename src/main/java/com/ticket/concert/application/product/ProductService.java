package com.ticket.concert.application.product;

import com.ticket.concert.application.category.CategoryService;
import com.ticket.concert.application.dto.product.request.CreateProductRequest;
import com.ticket.concert.application.dto.product.response.UpcomingProductResponse;
import com.ticket.concert.domain.category.entity.Category;
import com.ticket.concert.domain.product.entity.Product;
import com.ticket.concert.domain.product.repository.ProductRepository;
import com.ticket.concert.global.exception.BusinessException;
import com.ticket.concert.global.exception.constant.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public Product createProduct(CreateProductRequest request) {
        validateSchedule(request);

        Category category = categoryService.getCategory(request.categoryId());
        Product product = request.toProduct(category);
        Product saveProduct = productRepository.save(product);

        log.info("[PRODUCT_SAVE] success. productId={}", saveProduct.getId());
        return saveProduct;
    }

    private void validateSchedule(CreateProductRequest request) {
        validateShowPeriod(request);
        validateBookingPeriod(request);
        validateNotPast(request);
    }

    private void validateShowPeriod(CreateProductRequest request) {
        if (request.startDate().isAfter(request.endDate())) {
            throw new BusinessException(ErrorCode.INVALID_SHOW_PERIOD);
        }
    }

    private void validateBookingPeriod(CreateProductRequest request) {
        if (request.bookingOpenAt().isAfter(request.bookingCloseAt())) {
            throw new BusinessException(ErrorCode.INVALID_BOOKING_PERIOD);
        }
    }

    private void validateNotPast(CreateProductRequest request) {
        if (request.startDate().isBefore(LocalDate.now())
                || request.bookingOpenAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.PAST_SCHEDULE);
        }
    }

    public List<UpcomingProductResponse> getUpcomingProducts() {
        return productRepository.findUpcomingProducts(
                        LocalDateTime.now(),
                        PageRequest.of(0, 6)
                ).stream()
                .map(UpcomingProductResponse::from)
                .toList();
    }
}
