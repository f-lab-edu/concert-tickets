package com.ticket.concert.global.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class SingleFlightCache {

    private final CacheManager cacheManager;
    private final RedissonClient redissonClient;

    private static final long WAIT_TIME = 3L;

    public <T> T get(String cacheName, Object key, Supplier<T> loader) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return loader.get();
        }

        T cached = read(cache, key);
        if (cached != null) {
            return cached;
        }

        RLock lock = redissonClient.getLock("LOCK:" + cacheName + ":" + key);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(WAIT_TIME, TimeUnit.SECONDS);
            if (!acquired) {
                T afterWait = read(cache, key);
                return afterWait != null ? afterWait : loader.get();
            }

            cached = read(cache, key);
            if (cached != null) {
                return cached;
            }

            T result = loader.get();
            cache.put(key, result);
            return result;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return loader.get();
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T read(Cache cache, Object key) {
        try {
            Cache.ValueWrapper w = cache.get(key);
            return w == null ? null : (T) w.get();
        } catch (SerializationException e) {
            log.warn("[CACHE DESERIALIZE] 미스 처리. key={}, err={}", key, e.getMessage());
            try {
                cache.evict(key);
            } catch (RuntimeException ignore) {

            }
            return null;
        }
    }
}
