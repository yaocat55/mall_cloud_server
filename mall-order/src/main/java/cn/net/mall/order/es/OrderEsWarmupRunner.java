package cn.net.mall.order.es;

import cn.net.mall.order.es.repository.OrderEsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEsWarmupRunner {

    private static final Long WARMUP_USER_ID = -1L;
    private static final Integer WARMUP_ORDER_STATUS = -1;

    private final OrderEsRepository orderEsRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        CompletableFuture.runAsync(this::warmUp);
    }

    void warmUp() {
        long start = System.currentTimeMillis();
        Pageable pageable = PageRequest.of(0, 1);
        Date now = new Date();
        try {
            orderEsRepository.findByUserId(WARMUP_USER_ID, pageable);
            orderEsRepository.findByUserIdAndOrderStatus(WARMUP_USER_ID, WARMUP_ORDER_STATUS, pageable);
            orderEsRepository.findByUserIdAndOrderTimeLessThan(WARMUP_USER_ID, now, pageable);
            log.info("订单ES预热完成，耗时：{}ms", System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.warn("订单ES预热失败，耗时：{}ms", System.currentTimeMillis() - start, e);
        }
    }
}
