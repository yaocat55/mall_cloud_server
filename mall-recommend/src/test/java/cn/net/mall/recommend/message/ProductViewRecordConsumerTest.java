package cn.net.mall.recommend.message;

import cn.net.mall.product.dto.ProductViewRecordDTO;
import cn.net.mall.recommend.entity.ProductViewRecordConditionEntity;
import cn.net.mall.recommend.entity.ProductViewRecordEntity;
import cn.net.mall.recommend.mapper.ProductViewRecordMapper;
import cn.net.mall.redis.RedisUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.*;

class ProductViewRecordConsumerTest {

    static class FakeRedis extends RedisUtil {
        private final Set<String> keys = new HashSet<>();
        @Override
        public boolean setIfAbsent(String key, String value) {
            if (keys.contains(key)) {
                return false;
            }
            keys.add(key);
            return true;
        }
    }

    @Test
    void should_insert_once_when_first_message() {
        ProductViewRecordMapper mapper = Mockito.mock(ProductViewRecordMapper.class);
        Mockito.when(mapper.searchByCondition(Mockito.any(ProductViewRecordConditionEntity.class)))
                .thenReturn(Collections.emptyList());
        FakeRedis redis = new FakeRedis();
        ProductViewRecordConsumer consumer = new ProductViewRecordConsumer(mapper, redis);

        ProductViewRecordDTO msg = new ProductViewRecordDTO();
        msg.setId(1L);
        msg.setUserId(10L);
        msg.setProductId(100L);
        msg.setViewCount(1);

        consumer.onMessage(msg);
        consumer.onMessage(msg);

        ArgumentCaptor<ProductViewRecordEntity> insertCaptor = ArgumentCaptor.forClass(ProductViewRecordEntity.class);
        Mockito.verify(mapper, Mockito.times(1)).insert(insertCaptor.capture());
        ProductViewRecordEntity saved = insertCaptor.getValue();
        Assertions.assertEquals(10L, saved.getUserId());
        Assertions.assertEquals(100L, saved.getProductId());
        Assertions.assertEquals(1, saved.getViewCount().intValue());
    }

    @Test
    void should_update_when_record_exists() {
        ProductViewRecordEntity existing = new ProductViewRecordEntity();
        existing.setId(999L);
        existing.setUserId(10L);
        existing.setProductId(100L);
        existing.setViewCount(2);

        ProductViewRecordMapper mapper = Mockito.mock(ProductViewRecordMapper.class);
        Mockito.when(mapper.searchByCondition(Mockito.any(ProductViewRecordConditionEntity.class)))
                .thenReturn(Collections.singletonList(existing));
        FakeRedis redis = new FakeRedis();
        ProductViewRecordConsumer consumer = new ProductViewRecordConsumer(mapper, redis);

        ProductViewRecordDTO msg = new ProductViewRecordDTO();
        msg.setId(2L);
        msg.setUserId(10L);
        msg.setProductId(100L);
        msg.setViewCount(3);

        consumer.onMessage(msg);

        ArgumentCaptor<ProductViewRecordEntity> updateCaptor = ArgumentCaptor.forClass(ProductViewRecordEntity.class);
        Mockito.verify(mapper, Mockito.times(1)).update(updateCaptor.capture());
        ProductViewRecordEntity updated = updateCaptor.getValue();
        Assertions.assertEquals(5, updated.getViewCount().intValue());
    }
}
