package com.ticket.concert.seatInventory;


import com.ticket.concert.IntegrationTest;
import com.ticket.concert.application.dto.seatInventory.request.HoldSeatRequest;
import com.ticket.concert.application.seatInventory.SeatInventoryRedisService;
import com.ticket.concert.domain.LoginUser;
import com.ticket.concert.domain.category.entity.Category;
import com.ticket.concert.domain.category.repository.CategoryRepository;
import com.ticket.concert.domain.performance.entity.Performance;
import com.ticket.concert.domain.performance.entity.PerformanceStatus;
import com.ticket.concert.domain.performance.repository.PerformanceRepository;
import com.ticket.concert.domain.product.entity.Product;
import com.ticket.concert.domain.product.repository.ProductRepository;
import com.ticket.concert.domain.saetInventory.entity.SeatInventory;
import com.ticket.concert.domain.saetInventory.entity.SeatInventoryStatus;
import com.ticket.concert.domain.saetInventory.repository.SeatInventoryRepository;
import com.ticket.concert.domain.seat.entity.Seat;
import com.ticket.concert.domain.seat.repository.SeatRepository;
import com.ticket.concert.domain.user.constant.Role;
import com.ticket.concert.domain.user.entity.User;
import com.ticket.concert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DisplayName("좌석 선점 Redis 락 통합테스트")
public class SeatHoldRedisConcurrencyIntegrationTest extends IntegrationTest {

    /**
     *  static과 @Container 조합은 이 클래스의 모든 테스트가 컨테이너 하나를 공유합니다.<br/>
     * (테스트 메서드마다 새로 띄우고 싶으면 static을 빼면 되지만, 속도상 공유를 권장)
     */
    @Container
    private static final GenericContainer<?> REDIS_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(6379);

    /**
     * 컨테이너는 랜덤 포트로 뜨기 때문에, 스프링 컨텍스트가 뜨기 전에
     * 실제 매핑된 host/port를 프로퍼티로 주입해줘야 합니다.
     */
    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }

    @Autowired
    private SeatInventoryRedisService seatInventoryRedisService;
    @Autowired
    private SeatInventoryRepository seatInventoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private Long performanceId;
    private Long seatId;
    private final List<Long> userIds = new ArrayList<>();

    @BeforeEach
    void init() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        initSeatInventory();
        initUser();
    }

    @Test
    @DisplayName("동시에 같은 좌석을 Redis 분산 락으로 선점하면 한 명만 성공한다.")
    void holdRedis_concurrently_onlyOneSucceeds() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            Long userId = userIds.get(i);
            int finalI = i;
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    seatInventoryRedisService.holdRedis(
                            new HoldSeatRequest(performanceId, seatId),
                            new LoginUser(userId, "찬한", List.of(Role.USER))
                    );
                    success.incrementAndGet();
                    System.out.println("success : " + finalI);
                } catch (Exception e) {
                    fail.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        done.await();
        executor.shutdown();

        assertThat(success.get()).isEqualTo(1);
        assertThat(fail.get()).isEqualTo(threadCount - 1);

        SeatInventory result = transactionTemplate.execute(status ->
                seatInventoryRepository.findBySeat(performanceId, seatId).orElseThrow());
        assertThat(result.getStatus()).isEqualTo(SeatInventoryStatus.HELD);

         String key = "seat:hold:" + performanceId + ":" + seatId;
         assertThat(redisTemplate.hasKey(key)).isTrue();
         assertThat(redisTemplate.getExpire(key)).isGreaterThan(0);
    }

    @Test
    @DisplayName("이미 Redis에 선점된 좌석은 이후 요청이 즉시 실패한다.")
    void holdRedis_alreadyHeld_secondRequestFails() {
        seatInventoryRedisService.holdRedis(
                new HoldSeatRequest(performanceId, seatId),
                new LoginUser(userIds.get(0), "찬한", List.of(Role.USER))
        );

        AtomicInteger fail = new AtomicInteger();
        try {
            seatInventoryRedisService.holdRedis(
                    new HoldSeatRequest(performanceId, seatId),
                    new LoginUser(userIds.get(1), "다른유저", List.of(Role.USER))
            );
        } catch (Exception e) {
            fail.incrementAndGet();
        }
        assertThat(fail.get()).isEqualTo(1);
    }

    private void initSeatInventory() {
        Category category = categoryRepository.save(new Category("연극"));

        Product saveProduct = Product.builder()
                .category(category)
                .title("옥탑방 고양이")
                .runningTime(120)
                .startDate(LocalDate.of(2026, 8, 10))
                .endDate(LocalDate.of(2026, 8, 12))
                .bookingOpenAt(LocalDateTime.of(2026, 7, 10, 18, 0, 0))
                .bookingCloseAt(LocalDateTime.of(2026, 7, 12, 18, 0, 0))
                .build();
        Product product = productRepository.save(saveProduct);

        Performance savePerformance = Performance.builder()
                .product(product)
                .showAt(LocalDateTime.of(2026, 8, 10, 20, 0, 0))
                .status(PerformanceStatus.AVAILABLE)
                .build();
        Performance performance = performanceRepository.save(savePerformance);
        performanceId = performance.getId();

        Seat seat = seatRepository.save(Seat.builder()
                .product(product)
                .zone("A")
                .row("1")
                .seatNo("1")
                .price(30000)
                .grade("VIP")
                .color("RED")
                .build());
        seatId = seat.getId();

        SeatInventory seatInventory = SeatInventory.builder()
                .seat(seat)
                .performance(performance)
                .build();
        seatInventoryRepository.save(seatInventory);
    }

    private void initUser() {
        for (int i = 0; i < 100; i++) {
            User saveUser = User.builder()
                    .email(i + "aa@aa.aa")
                    .password("zxc123!@#")
                    .name(i + "이름")
                    .role(Role.USER)
                    .build();
            Long userId = userRepository.save(saveUser);
            userIds.add(userId);
        }
    }
}