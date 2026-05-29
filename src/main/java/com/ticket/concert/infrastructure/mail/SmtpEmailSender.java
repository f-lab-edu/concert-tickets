package com.ticket.concert.infrastructure.mail;

import com.ticket.concert.domain.email.EmailSender;
import com.ticket.concert.global.exception.BusinessException;
import com.ticket.concert.global.exception.RetryEmailException;
import com.ticket.concert.global.exception.constant.ErrorCode;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.eclipse.angus.mail.util.MailConnectException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Value("${mail.from}")
    private String from;

    /**
     * [@Retryable 내부 동작 방식]
     * 먼저 @EnableRetry가 등록한 인터셉터가 @Retry 메서드를 가진 빈 프록시로 감싸고,
     * 외부에서 메서드를 호출하면 실제 메서드 대신 프록시가 먼저 가로채 재시도 로직을 실행합니다.
     * - 재시도 트리거 : 메서드 실행 중 retry 속성에 지정한 예외가 던져질 때만 재시도합니다.
     * - 재시도 횟수 : maxAttempts = 3 옵션은 '재시도 3회'가 아니라 최초 호출 1회, 재시도 2회, 총 3회 실행을 의미합니다.
     * - 백오프 대기 : 실패 후 다음 시도까지 @Backoff 정책만큼 대시합니다. 이 대기는 호출 스레드를 Thread.sleep으로 블로킹합니다.
     */
    @Retryable(
            retryFor = RetryEmailException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 10000)
    )
    @Override
    public void send(String to, String content, String htmlBody) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            generateHelper(to, content, htmlBody, message);
            javaMailSender.send(message);
            log.info("[EMAIL SEND] success.");
        } catch (MailAuthenticationException e) {
            log.error("[EMAIL SEND] SMTP 인증 실패, SMTP 설정 확인 필요. ");
            throw new BusinessException(ErrorCode.MAIL_SERVER_ERROR, e);
        } catch (MailSendException e) {
            handleMailSendException(e, to);
        } catch (MailException e) {
            log.error("[EMAIL SEND] 메일 처리 실패. to={}", to, e);
            throw new BusinessException(ErrorCode.MAIL_SEND_FAILED, e);
        }
    }

    private void generateHelper(String to, String content, String htmlBody, MimeMessage message) {
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(content);
            helper.setText(htmlBody, true);
        } catch (AddressException e) {
            log.warn("INVALID EMAIL. to={}", to, e);
            throw new BusinessException(ErrorCode.INVALID_EMAIL, e);
        } catch (MessagingException e) {
            log.error("MAIL SEND FAILED. to={}", to, e);
            throw new BusinessException(ErrorCode.MAIL_SEND_FAILED, e);
        }
    }

    private void handleMailSendException(MailSendException e, String to) {
        Throwable cause = e.getCause();

        if (cause instanceof MailConnectException || cause instanceof ConnectException) {
            log.error("[EMAIL SEND] 메일 서버 연결 실패. to={}", to, e);
            throw new BusinessException(ErrorCode.MAIL_SERVER_UNAVAILABLE, e);
        }

        if (cause instanceof SMTPSendFailedException smtpEx) {
            int returnCode = smtpEx.getReturnCode();
            log.warn("[EMAIL SEND] SMTP 거부. to={}, code={}, msg={}", to, returnCode, smtpEx.getMessage());

            if (returnCode == 550 || returnCode == 553) { // 영구 실패
                throw new BusinessException(ErrorCode.INVALID_RECIPIENT, e);
            }
            if (returnCode == 535) {
                throw new BusinessException(ErrorCode.MAIL_SERVER_ERROR, e);
            }
            if (returnCode >= 400 && returnCode < 500) { // 일시 실패
                throw new RetryEmailException(e);
            }

            throw new BusinessException(ErrorCode.MAIL_SEND_FAILED, e);
        }

        if (cause instanceof SendFailedException sendEx) {
            Address[] invalid = sendEx.getInvalidAddresses();
            Address[] validUnsent = sendEx.getValidUnsentAddresses();

            log.warn("[EMAIL SEND] 일부 수신자 거부. invalid={}", Arrays.toString(invalid));

            if (validUnsent != null && validUnsent.length > 0) {
                throw new RetryEmailException(e);
            }
            throw new BusinessException(ErrorCode.INVALID_RECIPIENT, e);
        }

        log.error("[EMAIL SEND] 메일 전송 실패. to={}", to, e);
        throw new BusinessException(ErrorCode.MAIL_SEND_FAILED, e);
    }

    /**
     * @Retryable의 모든 시도가 소진되면 마지막에 발생한 예외 타입과 메서드 파라미터 시그니처가 일치하는
     * @Recover 메서드를 자동으로 찾아 호출합니다. @Recover의 파라미터는(발생 예외, 원본 메서드 파라미터..) 순서로
     * 매칭되어야 하고, 반환 타입도 원본 메서드와 호환되어야 합니다.
     */
    @Recover
    public void recover(RetryEmailException e, String to, String content, String htmlBody) {
        log.error("[EMAIL SEND] 재시도 모두 실패. to={}", to, e);
        throw new BusinessException(ErrorCode.MAIL_SERVER_UNAVAILABLE, e);
    }
}
