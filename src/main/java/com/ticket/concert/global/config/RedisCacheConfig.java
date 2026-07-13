package com.ticket.concert.global.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * 캐시 처리한 데이터의 필드를 수정할 경우, 이미 Redis에 직렬화되어 남아있는 데이터와
 * 변경된 DTO의 구조가 달라 역직렬화 시 필드 불일치 문제가 발생할 수 있습니다.
 * (현재 직렬화 구성: GenericJackson2JsonRedisSerializer + DefaultTyping.EVERYTHING,
 * 즉 모든 객체에 @class 타입 정보가 함께 저장됩니다.)
 * <p>
 * DTO 필드 추가/수정/삭제 및 클래스/패키지 변경 시 케이스별 처리 방안은 다음과 같습니다.
 * <p>
 * 1. 추가 (새 필드 추가)
 * - 동작: 옛 JSON에 해당 필드가 없으므로 null(또는 기본값)으로 채워짐. 예외는 발생하지 않음(대체로 안전).
 * - 주의: record의 primitive 컴포넌트(int/long 등)는 값 누락 시 0으로 들어오거나 에러가 날 수 있음
 * → 새 필드는 래퍼 타입으로 두거나 null-safe하게 처리.
 * - 처리: 별도 조치 거의 불필요. 새 필드를 nullable 전제로 다룸.
 * <p>
 * 2. 수정 (타입 변경 / 필드명 변경)
 * - 타입 변경: 옛 값을 새 타입으로 강제변환 실패 → 역직렬화 예외. 호환 불가이므로
 * 캐시 버전 prefix를 올려 옛 엔트리를 격리(아래 [공통] 참고).
 * - 필드명 변경: 옛 이름은 unknown 필드, 새 이름은 값 누락 → @JsonAlias로 옛 이름도 함께 받아
 * 전환기를 흡수하거나, 버전 prefix를 올림.
 * (단 FAIL_ON_UNKNOWN_PROPERTIES=false 설정이 전제)
 * <p>
 * 3. 삭제 (필드 제거)
 * - 동작: 옛 JSON에 잉여 필드가 남아, FAIL_ON_UNKNOWN_PROPERTIES 기본값(true)에서는 역직렬화 예외 발생.
 * - 처리: ObjectMapper에 DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES = false 적용 시
 * 잉여 필드를 무시하여 무중단으로 넘어감.
 * <p>
 * 4. 클래스명 / 패키지 변경
 * - 동작: 저장된 @class 경로가 더 이상 해석되지 않아 해당 캐시의 모든 엔트리가 역직렬화 실패.
 * (DefaultTyping.EVERYTHING이라 중첩 객체까지 영향 범위가 넓음)
 * - 처리: @JsonAlias 등으로는 구제 불가. 캐시 버전 prefix를 올리거나(예: "upcomingProducts:v1" → ":v2")
 * 배포 시 해당 캐시 영역을 flush 필수.
 * <p>
 * [공통 안전망]
 * - 버전 prefix: 호환 불가 변경(타입/이름/클래스 변경) 시 캐시 이름의 버전을 올려 옛 스키마 격리.
 * 올린 직후 옛 키는 @CacheEvict(allEntries) 대상이 아니므로 TTL로만 소멸함.
 * - TTL(10분): 구 스키마 엔트리가 배포 후 자연 소멸하도록 하는 1차 안전망.
 * - 역직렬화 실패 → 미스 처리: 깨진 엔트리를 만나면 미스로 간주하고 새 스키마로 재적재(self-heal).
 * 단, SingleFlightCache는 cache.get()을 직접 호출하므로 CacheErrorHandler가 동작하지 않음
 * → read() 내부에서 try-catch로 직접 방어해야 함.
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    public static final String UPCOMING_PRODUCTS = "upcomingProducts";
    public static final String UPCOMING_KEY = "upcoming";
    public static final Duration TTL = Duration.ofMinutes(10);

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .activateDefaultTyping(
                        BasicPolymorphicTypeValidator.builder()
                                .allowIfBaseType(Object.class)
                                .build(),
                        ObjectMapper.DefaultTyping.EVERYTHING,
                        JsonTypeInfo.As.PROPERTY
                );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(TTL)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(serializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

}
