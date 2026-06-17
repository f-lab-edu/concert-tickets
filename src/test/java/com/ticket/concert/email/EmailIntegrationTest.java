package com.ticket.concert.email;

import com.ticket.concert.IntegrationTest;
import com.ticket.concert.application.dto.mail.request.MailSendRequest;
import com.ticket.concert.domain.email.repository.EmailSender;
import com.ticket.concert.domain.email.repository.EmailVerifyTokenRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("이메일 통합테스트")
public class EmailIntegrationTest extends IntegrationTest {

    private static final String EMAIL_SEND_URL = "/v1/email/send";
    private static final String EMAIL_VERIFY_URL = "/v1/email/verify";
    private static final String EMAIL = "test@example.com";

    @MockitoBean
    private EmailSender emailSender;

    @Autowired
    private EmailVerifyTokenRepository emailVerifyTokenRepository;

    private MailSendRequest emailSendRequest;

    @BeforeEach
    void init() {
        emailSendRequest = new MailSendRequest(EMAIL);
    }

    @Test
    @DisplayName("이메일 인증 메일 발송에 성공한다.")
    void send_success() {
        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .contentType(ContentType.JSON)
                        .body(emailSendRequest)
                        .when()
                        .post(EMAIL_SEND_URL)
                        .then().log().ifValidationFails()
                        .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);

        verify(emailSender, times(1)).send(eq(EMAIL), anyString(), anyString());
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 400을 반환하고 메일은 발송되지 않는다.")
    void send_invalidEmail_returnsBadRequest() {
        MailSendRequest request = new MailSendRequest("not-an-email");

        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post(EMAIL_SEND_URL)
                        .then().log().ifValidationFails()
                        .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("유효한 토큰으로 이메일 인증에 성공한다.")
    void verify_success() {
        String token = UUID.randomUUID().toString();
        emailVerifyTokenRepository.save(token, EMAIL, LocalDateTime.now().plusMinutes(30));

        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .queryParam("token", token)
                        .when()
                        .get(EMAIL_VERIFY_URL)
                        .then().log().ifValidationFails()
                        .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("token 파라미터가 없으면 400을 반환한다.")
    void verify_missingToken_returnsBadRequest() {
        ExtractableResponse<Response> response =
                RestAssured.given().log().ifValidationFails()
                        .when()
                        .get(EMAIL_VERIFY_URL)
                        .then().log().ifValidationFails()
                        .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    }

}
