package com.ticket.concert.infrastructure.mail;

import com.ticket.concert.domain.email.repository.EmailSender;
import com.ticket.concert.global.exception.BusinessException;
import jakarta.mail.Address;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
@TestPropertySource(properties = "mail.from=test@example.com")
class SmtpEmailSenderTest {

    /**
     * Retry 테스트의 핵심. 전체 @SpringBootTest를 띄우지 않고, 재시도 검증에 필요한 최소한의 빈만 모아 구성
     */
    @Configuration
    @EnableRetry
    static class TestConfig {
        /**
         * 실제 SMTP 서버로 메일을 보내면 안 되니 JavaMailSender를 목으로 등록
         */
        @Bean
        JavaMailSender javaMailSender() {
            return mock(JavaMailSender.class);
        }

        /**
         * 검증 대상 빈 위 목을 주입 받아 생성하면, @EnalbeRetry가 이 빈을 재시도 프록시로 감쌉니다.
         */
        @Bean
        SmtpEmailSender smtpEmailSender(JavaMailSender javaMailSender) {
            return new SmtpEmailSender(javaMailSender);
        }

        /**
         * ${} 플레이스홀더를 실제ㅗ 치환해주는 빈입니다. static인 이유는 이 빈이 다른 빈들보다 먼저 생성돼야 하기 때문
         */
        @Bean
        static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    // 프록시는 인터페이스 타입으로 주입받는다 (JDK 동적 프록시)
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * 테스트 간 공유돼서 목 인스턴스가 재시용됩니다. .reset()으로 이전 테스트의 기록을 초기화해 테스트까지 간섭하지 않게합니다.
     * send() 내부에서 JavaMailSender.createMimeMEssage()를 호출합니다. 목은 기본적으로 null을 반환하므로,
     * 그대로 두면 MimeMessageHelper를 만들 때 NPE가 터져버립니다.
     */
    @BeforeEach
    void setUp() {
        reset(javaMailSender);
        when(javaMailSender.createMimeMessage())
                .thenReturn(new JavaMailSenderImpl().createMimeMessage());
    }

    /**
     * 프로덕션 코드에서 재발송 로직이 발생하는 조건을 만족하는 예외
     */
    private MailSendException retryable() throws AddressException {
        Address[] validUnsent = {new InternetAddress("unsent@example.com")};
        SendFailedException sfe = new SendFailedException(
                "temporary", new Exception(), new Address[0], validUnsent, new Address[0]);
        return new MailSendException("send failed", sfe);
    }

    @Test
    @DisplayName("재시도 대상 예외가 계속 발생하면 총 3회 시도 후 @Recover가 동작한다")
    void retryExhaustedThenRecover() throws AddressException {
        doThrow(retryable()).when(javaMailSender).send(any(MimeMessage.class));

        assertThatThrownBy(() -> emailSender.send("to@example.com", "subject", "<p>body</p>"))
                .isInstanceOf(BusinessException.class);

        verify(javaMailSender, times(3)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("재시도 중간에 성공하면 이후 재시도는 일어나지 않는다")
    void retryThenSuccess() throws AddressException {
        doThrow(retryable())
                .doNothing() // 2회차 성공
                .when(javaMailSender).send(any(MimeMessage.class));

        emailSender.send("to@example.com", "subject", "<p>body</p>");

        verify(javaMailSender, times(2)).send(any(MimeMessage.class));
    }

}