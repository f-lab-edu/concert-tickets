package com.ticket.concert.product;

import com.ticket.concert.IntegrationTest;
import com.ticket.concert.LocalCacheTestConfig;
import com.ticket.concert.application.category.CategoryService;
import com.ticket.concert.application.dto.category.request.CreateCategoryRequest;
import com.ticket.concert.application.dto.product.request.CreateProductRequest;
import com.ticket.concert.application.product.ProductService;
import com.ticket.concert.domain.product.repository.ProductRepository;
import com.ticket.concert.global.config.RedisCacheConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("예정 상품 조회 캐시 테스트")
@Transactional
@Import(LocalCacheTestConfig.class)
public class ProductCacheTest extends IntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        login();
        categoryService.createCategory(new CreateCategoryRequest("연극"));
        cacheManager.getCache(RedisCacheConfig.UPCOMING_PRODUCTS).clear();
    }

    @Test
    @DisplayName("두 번 조회해도 DB 조회는 한 번만 발생한다.")
    void getUpcomingProducts_cached() {
        productService.getUpcomingProducts();
        productService.getUpcomingProducts();

        verify(productRepository, times(1))
                .findUpcomingProducts(any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    @DisplayName("상품 생성 시 예정 상품 캐시가 무효화되어 다시 조회한다.")
    void createProduct_evictsCache() {
        productService.getUpcomingProducts();

        productService.createProduct(generateRequest());

        productService.getUpcomingProducts();

        verify(productRepository, times(2))
                .findUpcomingProducts(any(LocalDateTime.class), any(Pageable.class));
    }

    private CreateProductRequest generateRequest() {
        return new CreateProductRequest(
                1L, "검정치마 콘서트", 120,
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12),
                LocalDateTime.of(2026, 6, 20, 18, 0), LocalDateTime.of(2026, 6, 22, 18, 0));
    }
}
