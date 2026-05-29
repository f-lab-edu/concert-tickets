package com.ticket.concert.application.email;

import com.ticket.concert.global.exception.BusinessException;
import com.ticket.concert.global.exception.constant.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class EmailTemplateLoader {

    private static final String VERIFY_TEMPLATE_PATH = "templates/email/verify.html";

    private String verifyTemplate;

    @PostConstruct
    public void init() {
        this.verifyTemplate = load(VERIFY_TEMPLATE_PATH);
        log.info("[EMAIL_TEMPLATE] loaded. path={}", VERIFY_TEMPLATE_PATH);
    }

    public String getVerifyTemplate() {
        return verifyTemplate;
    }

    private String load(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream is = resource.getInputStream()) {
            return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("[EMAIL_TEMPLATE] load failed. path={}", path, e);
            throw new BusinessException(ErrorCode.MAIL_TEMPLATE_LOAD_FAILED, e);
        }
    }
}
