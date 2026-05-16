package com.ticket.concert.presentation;

import com.ticket.concert.application.dto.mail.request.MailSendRequest;
import com.ticket.concert.application.email.EmailService;
import com.ticket.concert.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping(value = "/v1/email/send")
    public ApiResponse<Void> sendEmail(@Valid @RequestBody MailSendRequest request) {
        emailService.sendEmail(request);
        return ApiResponse.success();
    }
}
