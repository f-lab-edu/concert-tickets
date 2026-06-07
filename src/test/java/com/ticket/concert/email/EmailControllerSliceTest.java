package com.ticket.concert.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.concert.application.dto.mail.request.MailSendRequest;
import com.ticket.concert.application.email.EmailService;
import com.ticket.concert.presentation.EmailController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
public class EmailControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmailService emailService;

    @Test
    @DisplayName("이메일 발송 요청에 성공한다.")
    void sendEmail_success() throws Exception {
        MailSendRequest request = new MailSendRequest("test@example.com");

        mockMvc.perform(post("/v1/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(emailService).sendEmail(any(MailSendRequest.class));
    }

    @Test
    @DisplayName("토큰 파라미터로 인증 요청에 성공한다.")
    void verifyEmail_success() throws Exception {
        mockMvc.perform(get("/v1/email/verify")
                        .param("token", "some-token"))
                .andExpect(status().isOk());

        verify(emailService).verifyToken("some-token");
    }

}
