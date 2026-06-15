package com.ticket.concert;

import com.ticket.concert.global.config.RedisCacheConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class LocalCacheTestConfig {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(RedisCacheConfig.UPCOMING_PRODUCTS);
    }
}
