package com.ticket.concert.global.config;

import com.ticket.concert.global.auth.LoginUserFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private static final String[] EXCLUDE_PATHS = {
            "/v1/auth/login",
            "/v1/auth/join",
            "/error"
    };


    @Bean
    public FilterRegistrationBean<LoginUserFilter> loginUserFilter() {
        var reg = new FilterRegistrationBean<>(new LoginUserFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(1);
        return reg;
    }

}
