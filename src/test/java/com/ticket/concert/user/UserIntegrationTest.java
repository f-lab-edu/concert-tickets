package com.ticket.concert.user;

import com.ticket.concert.IntegrationTest;
import com.ticket.concert.application.dto.user.request.JoinRequest;
import com.ticket.concert.domain.user.User;
import com.ticket.concert.domain.user.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

@DisplayName("회원 통합테스트")
public class UserIntegrationTest extends IntegrationTest {

    private static final String JOIN_URL = "/v1/user/join";
    private static final String PASSWORD = "asdf1234!@";

    @Autowired
    private UserRepository userRepository;
    private JoinRequest joinRequest;

    @BeforeEach
    void init() {
        joinRequest = generateJoinRequest("test@exmaple.com", PASSWORD, "이찬한", "01000001234");
    }

    @Test
    @DisplayName("회원가입 성공하면 회원이 저장된다.")
    void join_success() {
        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .contentType(ContentType.JSON)
                        .body(joinRequest)
                        .when()
                        .post(JOIN_URL)
                        .then().log().ifValidationFails()
                        .extract();

        User savedUser = userRepository.findByEmail(joinRequest.email())
                .orElseThrow(() -> new AssertionError("가입한 회원을 찾을 수 없습니다."));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(savedUser).usingRecursiveComparison()
                .comparingOnlyFields("email", "name", "phone")
                .isEqualTo(joinRequest);

        assertThat(savedUser.getPassword()).isNotEqualTo(joinRequest.password());
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 400을 반환하고 회원은 저장되지 않는다.")
    void join_invalidEmail_returnsBadRequest() {
        JoinRequest request = generateJoinRequest("not-an-email", PASSWORD, "이찬한", "01000001234");

        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post(JOIN_URL)
                        .then().log().ifValidationFails()
                        .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(userRepository.findByEmail(request.email())).isEmpty();
    }

    @Test
    @DisplayName("이미 가입된 이메일로 가입하면 실패하고 기존 회원 정보는 유지된다.")
    void join_duplicateEmail_fails() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(joinRequest)
                .when()
                .post(JOIN_URL)
                .then().extract();

        JoinRequest duplicate = generateJoinRequest(joinRequest.email(), PASSWORD, "다른이름", "01099998888");

        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .contentType(ContentType.JSON)
                        .body(duplicate)
                        .when()
                        .post(JOIN_URL)
                        .then().log().ifValidationFails()
                        .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);

        User savedUser = userRepository.findByEmail(joinRequest.email())
                .orElseThrow(() -> new AssertionError("가입한 회원을 찾을 수 없습니다."));
        assertThat(savedUser.getName()).isEqualTo(joinRequest.name());
    }


    private JoinRequest generateJoinRequest(String email, String password, String name, String phone) {
        return new JoinRequest(email, password, name, phone);
    }

}
