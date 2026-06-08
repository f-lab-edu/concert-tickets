package com.ticket.concert.product;

import com.ticket.concert.IntegrationTest;
import com.ticket.concert.application.dto.category.request.CreateCategoryRequest;
import com.ticket.concert.application.dto.product.request.CreateProductRequest;
import com.ticket.concert.domain.product.entity.Product;
import com.ticket.concert.domain.product.repository.ProductRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("상품 통합테스트")
public class ProductIntegrationTest extends IntegrationTest {

    private static final String CATEGORY_CREATE_URL = "/v1/category";
    private static final String PRODUCT_CREATE_URL = "/v1/product";

    @Autowired
    private ProductRepository productRepository;
    private CreateProductRequest createProductRequest;
    private String sessionId;

    @BeforeEach
    void init() {
        sessionId = login();

        RestAssured.given().log().ifValidationFails()
                .sessionId(sessionId)
                .contentType(ContentType.JSON)
                .body(new CreateCategoryRequest("뮤지컬"))
                .when()
                .post(CATEGORY_CREATE_URL)
                .then().log().ifValidationFails()
                .extract();

        createProductRequest = generateRequest();
    }

    @Test
    @DisplayName("상품을 생성하면 상품이 저장된다.")
    void create_success() {
        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .sessionId(sessionId)
                        .contentType(ContentType.JSON)
                        .body(createProductRequest)
                        .when()
                        .post(PRODUCT_CREATE_URL)
                        .then().log().ifValidationFails()
                        .extract();

        Product saveProduct = productRepository.findById(((Number) response.body().path("data")).longValue())
                .orElseThrow(() -> new AssertionError("등록된 상품을 찾을 수 없습니다."));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(saveProduct).usingRecursiveComparison()
                .comparingOnlyFields("title", "runningTime", "startDate", "endDate", "bookingOpenAt", "bookingCloseAt")
                .isEqualTo(createProductRequest);
    }

    @Test
    @DisplayName("로그인하지 않으면 401을 반환하고 상품은 저장되지 않는다.")
    void create_withoutLogin_returnsUnauthorized() {
        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .contentType(ContentType.JSON)
                        .body(createProductRequest)
                        .when()
                        .post(PRODUCT_CREATE_URL)
                        .then().log().ifValidationFails()
                        .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
        assertThat(productRepository.count()).isZero();
    }

    @Test
    @DisplayName("공연 시작일이 종료일보다 늦으면 400을 반환한다.")
    void create_startAfterEnd_returnsBadRequest() {
        CreateProductRequest request = generateRequest(
                LocalDate.now().plusMonths(2), LocalDate.now().plusMonths(1),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));

        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .sessionId(sessionId)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when().post(PRODUCT_CREATE_URL)
                        .then().log().ifValidationFails()
                        .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(productRepository.count()).isZero();
    }

    @Test
    @DisplayName("공연·예매 일정이 과거이면 400을 반환한다.")
    void create_pastSchedule_returnsBadRequest() {
        CreateProductRequest request = generateRequest(
                LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2),
                LocalDateTime.of(2019, 12, 1, 10, 0), LocalDateTime.of(2019, 12, 31, 10, 0));

        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .sessionId(sessionId)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when().post(PRODUCT_CREATE_URL)
                        .then().log().ifValidationFails()
                        .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(productRepository.count()).isZero();
    }

    private CreateProductRequest generateRequest() {
        return new CreateProductRequest(
                1L, "검정치마 콘서트", 120,
                LocalDate.of(2026, 07, 10),
                LocalDate.of(2026, 07, 12),
                LocalDateTime.of(2026, 06, 10, 18, 00, 00),
                LocalDateTime.of(2026, 06, 12, 18, 00, 00));
    }

    private CreateProductRequest generateRequest(
            LocalDate startDate, LocalDate endDate,
            LocalDateTime bookingOpenAt, LocalDateTime bookingCloseAt) {
        return new CreateProductRequest(
                1L, "검정치마 콘서트", 120, startDate, endDate, bookingOpenAt, bookingCloseAt);
    }
}
