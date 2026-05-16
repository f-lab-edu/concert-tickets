package com.ticket.concert.application.email;

import com.ticket.concert.application.dto.mail.request.MailSendRequest;
import com.ticket.concert.domain.email.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);
    // 토큰 저장소

    @Value("${app.base-url}")
    private String baseUrl;

    private final EmailSender emailSender;

    public void sendEmail(MailSendRequest request) {
        String token = generateUUID();
        // 토큰 저장소에 저장
        // tokenRepository.save(token, email, TOKEN_TTL);

        String verifyUrl = generateVerifyUrl(token);
        String htmlBody = buildHtml(verifyUrl);
        emailSender.send(request.email(), "[Concert] 이메일 인증을 완료해주세요", htmlBody);
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String generateVerifyUrl(String token) {
        return baseUrl + "/v1/email/verify?token=" + token;
    }

    private String buildHtml(String verifyUrl) {
        return """
                <div style="font-family: sans-serif; padding: 24px;">
                  <h2>이메일 인증</h2>
                  <p>아래 버튼을 눌러 이메일 인증을 완료해주세요. (30분간 유효)</p>
                  <a href="%s"
                     style="display:inline-block;padding:12px 24px;
                            background:#4F46E5;color:#fff;
                            text-decoration:none;border-radius:6px;">
                    이메일 인증하기
                  </a>
                  <p style="color:#888;font-size:12px;margin-top:24px;">
                    버튼이 동작하지 않으면 아래 링크를 복사해 주소창에 붙여넣어 주세요.<br/>
                    %s
                  </p>
                </div>
                """.formatted(verifyUrl, verifyUrl);
    }

}
