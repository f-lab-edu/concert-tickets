package com.ticket.concert.application.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailTemplateRenderer {

    private final EmailTemplateLoader templateLoader;

    public String renderVerifyEmail(String verifyUrl) {
        return templateLoader.getVerifyTemplate()
                .replace("${verifyUrl}", verifyUrl);
    }

}
