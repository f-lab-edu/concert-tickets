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

    public void createProduct(CreateProductRequest request) {
        Category category = categoryService.getCategory(request.categoryId());
        Product product = request.toProduct(category);
        Product saveProduct = productRepository.save(product);

        log.info("[PRODUCT_SAVE] success. productId={}", saveProduct.getId());
    }
}
