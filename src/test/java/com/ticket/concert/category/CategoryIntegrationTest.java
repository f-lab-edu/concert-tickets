package com.ticket.concert.category;

import com.ticket.concert.IntegrationTest;
import com.ticket.concert.application.dto.category.request.CreateCategoryRequest;
import com.ticket.concert.domain.category.entity.Category;
import com.ticket.concert.domain.category.repository.CategoryRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("카테고리 통합테스트")
public class CategoryIntegrationTest extends IntegrationTest {

    private static final String CREATE_URL = "/v1/category";

    @Autowired
    private CategoryRepository categoryRepository;
    private CreateCategoryRequest createCategoryRequest;
    private String sessionId;

    @BeforeEach
    void init() {
        createCategoryRequest = new CreateCategoryRequest("뮤지컬");
        sessionId = login();
    }

    @Test
    @DisplayName("카테고리 생성하면 카테고리가 저장된다.")
    void create_success() {
        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .sessionId(sessionId)
                        .contentType(ContentType.JSON)
                        .body(createCategoryRequest)
                        .when()
                        .post(CREATE_URL)
                        .then().log().ifValidationFails()
                        .extract();

        Category saveCategory = categoryRepository.findByName(createCategoryRequest.name())
                .orElseThrow(() -> new AssertionError("등록된 카테고리를 찾을 수 없습니다."));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(saveCategory.getName()).isEqualTo(createCategoryRequest.name());
    }

    @Test
    @DisplayName("카테고리 이름이 비어 있으면 400을 반환하고 저장되지 않는다.")
    void create_blankName_returnsBadRequest() {
        CreateCategoryRequest blankRequest = new CreateCategoryRequest("");

        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .sessionId(sessionId)
                        .contentType(ContentType.JSON)
                        .body(blankRequest)
                        .when()
                        .post(CREATE_URL)
                        .then().log().ifValidationFails()
                        .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(categoryRepository.findByName("")).isEmpty();
    }
}
