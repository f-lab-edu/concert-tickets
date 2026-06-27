package com.ticket.concert.global.cache;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class SeatHoldRedisManager {

    private final RedissonClient redissonClient;

    private static final Duration HOLD_TTL = Duration.ofMinutes(7);
    private static final String KEY_PREFIX = "seat:hold:";

    private String key(Long performanceId, Long seatId) {
        return KEY_PREFIX + performanceId + ":" + seatId;
    }

    /**
     * Redis에 원자적으로 좌석을 점유한다. (SET key value NX EX ttl)
     * @return true면 선점 성공, false면 이미 다른 사용자가 점유함
     */
    public boolean tryHold(Long performanceId, Long seatId, Long userId) {
        RBucket<String> bucket = redissonClient.getBucket(key(performanceId, seatId));
        return bucket.setIfAbsent(String.valueOf(userId), HOLD_TTL);
    }

    /**
     * 점유 실패(보상) 시 호출. 본인이 잡은 키만 안전하게 삭제한다.
     */
    public void release(Long performanceId, Long seatId, Long userId) {
        RBucket<String> bucket = redissonClient.getBucket(key(performanceId, seatId));
        String holder = bucket.get();

        if (String.valueOf(userId).equals(holder)) {
            bucket.delete();
        }
    }
}
