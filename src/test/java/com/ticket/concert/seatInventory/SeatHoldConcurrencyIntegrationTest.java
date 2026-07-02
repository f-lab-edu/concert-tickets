package com.ticket.concert.seatInventory;

import com.ticket.concert.IntegrationTest;
import com.ticket.concert.application.dto.seatInventory.request.HoldSeatRequest;
import com.ticket.concert.application.seatInventory.SeatInventoryService;
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
import com.ticket.concert.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("좌석 선점 락 통합테스트")
public class SeatHoldConcurrencyIntegrationTest extends IntegrationTest {

    @Autowired
    private SeatInventoryService seatInventoryService;
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

    private Long performanceId;
    private Long seatId;
    private final List<Long> userIds = new ArrayList<>();

    @BeforeEach
    void init() {
        initSeatInventory();
        initUser();
    }

    @Test
    @DisplayName("동시에 같은 좌석을 비관적락으로 선점하면 한 명만 성공한다.")
    void hold_concurrently_onlyOneSucceeds() throws InterruptedException {
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
                    seatInventoryService.hold(
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
                seatInventoryRepository.findForUpdate(performanceId, seatId).orElseThrow());
        assertThat(result.getStatus()).isEqualTo(SeatInventoryStatus.HELD);
    }

    @Test
    @DisplayName("동시에 같은 좌석을 낙관적 락으로 선점하면 한 명만 성공한다.")
    void holdWithOptimisticLock_concurrently_onlyOneSucceeds() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger businessFail = new AtomicInteger();
        AtomicInteger unexpectedFail = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            Long userId = userIds.get(i);
            int finalI = i;
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    seatInventoryService.holdWithOptimisticLock(
                            new HoldSeatRequest(performanceId, seatId),
                            new LoginUser(userId, "찬한", List.of(Role.USER))
                    );
                    success.incrementAndGet();
                    System.out.println("success : " + finalI);
                } catch (BusinessException e) {
                    businessFail.incrementAndGet();
                } catch (Exception e) {
                    // 낙관적 락 예외가 서비스 밖으로 새어나오면 여기로 잡힘 -> 0이어야 정상
                    unexpectedFail.incrementAndGet();
                    System.out.println("unexpected : " + e.getClass().getSimpleName());
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
        assertThat(businessFail.get()).isEqualTo(threadCount - 1);
        assertThat(unexpectedFail.get()).isZero();

        SeatInventory result = transactionTemplate.execute(status ->
                seatInventoryRepository.findBySeat(performanceId, seatId).orElseThrow());
        assertThat(result.getStatus()).isEqualTo(SeatInventoryStatus.HELD);
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
