package com.ticket.concert.infrastructure.mail;

import com.ticket.concert.domain.email.EmailSender;
import com.ticket.concert.global.exception.BusinessException;
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
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        } catch (MessagingException e) {
            log.error("MAIL SEND FAILED. to={}", to, e);
            throw new BusinessException(ErrorCode.MAIL_SEND_FAILED);
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
                throw new BusinessException(ErrorCode.MAIL_SERVER_UNAVAILABLE, e);
            }

            throw new BusinessException(ErrorCode.MAIL_SEND_FAILED, e);
        }

        if (cause instanceof SendFailedException sendEx) {
            Address[] invalid = sendEx.getInvalidAddresses();
            log.warn("[EMAIL SEND] 일부 수신자 거부. invalid={}", Arrays.toString(invalid));
            throw new BusinessException(ErrorCode.INVALID_RECIPIENT, e);
        }

        log.error("[EMAIL SEND] 메일 전송 실패. to={}", to, e);
        throw new BusinessException(ErrorCode.MAIL_SEND_FAILED, e);
    }
}
