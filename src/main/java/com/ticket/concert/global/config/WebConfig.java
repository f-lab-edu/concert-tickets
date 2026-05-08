package com.ticket.concert.global.config;

import com.ticket.concert.global.auth.AuthorizationFilter;
import com.ticket.concert.global.auth.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {


    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilter() {
        var reg = new FilterRegistrationBean<>(new AuthenticationFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(1);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilter() {
        var reg = new FilterRegistrationBean<>(new AuthorizationFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(2);
        return reg;
    }
}
