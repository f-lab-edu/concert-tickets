package com.ticket.concert.auth;


import com.ticket.concert.IntegrationTest;
import com.ticket.concert.application.dto.auth.request.LoginRequest;
import com.ticket.concert.application.dto.user.request.JoinRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("인증 통합테스트")
public class AuthIntegrationTest extends IntegrationTest {

    private static final String JOIN_URL = "/v1/user/join";
    private static final String LOGIN_URL = "/v1/auth/login";
    private static final String EMAIL = "test@exmaple.com";
    private static final String PASSWORD = "asdf1234!@";

    private LoginRequest loginRequest;

    @BeforeEach
    void init() {
        JoinRequest joinRequest = new JoinRequest(EMAIL, PASSWORD, "이찬한", "01000001234");
        RestAssured.given().log().ifValidationFails()
                .contentType(ContentType.JSON)
                .body(joinRequest)
                .when()
                .post(JOIN_URL)
                .then().log().ifValidationFails();

        loginRequest = generateLoginRequest(EMAIL, PASSWORD);
    }

    @Test
    @DisplayName("로그인 성공하면 세션이 생성된다.")
    void login_success() {
        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .contentType(ContentType.JSON)
                        .body(loginRequest)
                        .when()
                        .post(LOGIN_URL)
                        .then().log().ifValidationFails()
                        .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.cookie("JSESSIONID")).isNotNull();
    }

    private LoginRequest generateLoginRequest(String email, String password) {
        return new LoginRequest(email, password);
    }
}
