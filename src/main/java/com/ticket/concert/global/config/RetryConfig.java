package com.ticket.concert.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * [@EnableRetry]
 * - Spring Retry 기능을 활성화. 내부적으로 RetryConfiguration을 import해 @Retryable 메서드를
 *   AOP 프록시로 감싸고, 재시도, 백오프, @Recover 흐름을 동작시킨다.
 * - @Configuration를 통해 해당 클래스를 설정 클래스로 등록해, @EnableRetry가 스프링 컨테이너
 *   기동 시점에 실제로 처리되도록 보장한다.
 */
@EnableRetry
@Configuration
public class RetryConfig {
}
