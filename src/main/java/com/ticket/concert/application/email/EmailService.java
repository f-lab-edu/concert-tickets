package com.ticket.concert.application.email;

import com.ticket.concert.application.dto.mail.request.MailSendRequest;
import com.ticket.concert.domain.email.repository.EmailSender;
import com.ticket.concert.domain.email.entity.EmailVerifyToken;
import com.ticket.concert.domain.email.repository.EmailVerifyTokenRepository;
import com.ticket.concert.global.exception.BusinessException;
import com.ticket.concert.global.exception.constant.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);

    @Value("${app.base-url}")
    private String baseUrl;

    private final EmailSender emailSender;
    private final EmailVerifyTokenRepository emailVerifyTokenRepository;
    private final EmailTemplateRenderer emailTemplateRenderer;

    public void sendEmail(MailSendRequest request) {
        String token = generateUUID();
        saveEmailVerifyToken(request.email(), token);

        String verifyUrl = generateVerifyUrl(token);
        String htmlBody = emailTemplateRenderer.renderVerifyEmail(verifyUrl);
        emailSender.send(request.email(), "[Concert] 이메일 인증을 완료해주세요", htmlBody);
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private void saveEmailVerifyToken(String email, String token) {
        LocalDateTime expiresAt = LocalDateTime.now().plus(TOKEN_TTL);
        Long generatedId = emailVerifyTokenRepository.save(token, email, expiresAt);
        log.info("[EMAIL_VERIFY_TOKEN] save success. tokenId={}", generatedId);
    }

    private String generateVerifyUrl(String token) {
        return baseUrl + "/v1/email/verify?token=" + token;
    }

    @Transactional
    public void verifyToken(String token) {
        EmailVerifyToken emailVerifyToken = findByEmailVerifyTokenOrThrow(token);
        validateConsumable(emailVerifyToken);
        updateConsume(emailVerifyToken);
    }

    private EmailVerifyToken findByEmailVerifyTokenOrThrow(String token) {
        return emailVerifyTokenRepository.findByTokenAndStatus(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_TOKEN_NOT_FOUND));
    }

    private void validateConsumable(EmailVerifyToken emailVerifyToken) {
        if (!emailVerifyToken.isConsumable()) {
            throw new BusinessException(ErrorCode.EMAIL_TOKEN_NOT_USABLE);
        }
    }

    private void updateConsume(EmailVerifyToken emailVerifyToken) {
        emailVerifyTokenRepository.updateConsumeAt(emailVerifyToken.getId());
        log.info("[EMAIL_VERIFY_TOKEN] consume update success. tokenId={}", emailVerifyToken.getId());
    }

}
