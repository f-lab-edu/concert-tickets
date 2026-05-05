package com.ticket.concert.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auth가 존재하면 로그인 필요
 * @Auth(roles = "ADMIN")가 존재하면 관리자 전용
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
    String[] roles() default {};
}
