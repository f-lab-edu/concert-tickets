package com.ticket.concert.application.product;

import com.ticket.concert.application.category.CategoryService;
import com.ticket.concert.application.dto.product.request.CreateProductRequest;
import com.ticket.concert.domain.category.entity.Category;
import com.ticket.concert.domain.product.entity.Product;
import com.ticket.concert.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public Product createProduct(CreateProductRequest request) {
        validate();

        Category category = categoryService.getCategory(request.categoryId());
        Product product = request.toProduct(category);
        Product saveProduct = productRepository.save(product);

        log.info("[PRODUCT_SAVE] success. productId={}", saveProduct.getId());
        return saveProduct;
    }

    private void validate() {
        // 시작일, 종료일, 예매 시작일, 예매 종료일이 현제 날짜보다 앞에 존재할 경우
    }
}
