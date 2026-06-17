package com.ticket.concert;

import com.ticket.concert.application.dto.auth.request.LoginRequest;
import com.ticket.concert.application.dto.user.request.JoinRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTest {

    private static final JoinRequest DEFAULT_USER =
            new JoinRequest("test@example.com", "asdf1234!@", "이찬한", "01000001234");

    private static boolean joined = false;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        if (!joined) {
            join(DEFAULT_USER);
            joined = true;
        }
    }

    protected void join(JoinRequest request) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/v1/user/join")
                .then().statusCode(HttpStatus.SC_OK);
    }

    protected String login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/v1/auth/login")
                .then().extract().sessionId();
    }

    protected String login() {
        return login(DEFAULT_USER.email(), DEFAULT_USER.password());
    }
}