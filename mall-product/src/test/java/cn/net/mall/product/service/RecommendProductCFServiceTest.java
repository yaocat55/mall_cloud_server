package cn.net.mall.product.service;

import cn.net.mall.enums.JobResult;
import cn.net.mall.product.entity.ProductViewRecordConditionEntity;
import cn.net.mall.product.entity.ProductViewRecordEntity;
import cn.net.mall.product.mapper.ProductViewRecordMapper;
import cn.net.mall.redis.RedisUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class RecommendProductCFServiceTest {

    @Autowired
    private RecommendProductService recommendProductService;

    @MockBean
    private IndexProductService indexProductService;
    @MockBean
    private ProductService productService;
    @MockBean
    private ProductViewRecordMapper productViewRecordMapper;
    @MockBean
    private RedisUtil redisUtil;

    @Test
    void should_write_recommendations_when_records_exist() {
        ProductViewRecordEntity r1 = new ProductViewRecordEntity();
        r1.setUserId(1L);
        r1.setProductId(101L);
        r1.setViewCount(2);

        ProductViewRecordEntity r2 = new ProductViewRecordEntity();
        r2.setUserId(2L);
        r2.setProductId(101L);
        r2.setViewCount(1);

        ProductViewRecordEntity r3 = new ProductViewRecordEntity();
        r3.setUserId(2L);
        r3.setProductId(102L);
        r3.setViewCount(1);

        ProductViewRecordEntity r4 = new ProductViewRecordEntity();
        r4.setUserId(3L);
        r4.setProductId(102L);
        r4.setViewCount(2);

        List<ProductViewRecordEntity> records = Arrays.asList(r1, r2, r3, r4);
        Mockito.when(productViewRecordMapper.searchByCondition(Mockito.any(ProductViewRecordConditionEntity.class)))
                .thenReturn(records);

        JobResult result = recommendProductService.recommendProductToRedis();
        Assertions.assertEquals(JobResult.SUCCESS, result);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(redisUtil, Mockito.atLeastOnce()).set(keyCaptor.capture(), valueCaptor.capture());
        List<String> keys = keyCaptor.getAllValues();
        List<String> values = valueCaptor.getAllValues();
        boolean hasU1 = keys.stream().anyMatch(k -> k.equals("recommendProduct:user:1"));
        boolean hasU3 = keys.stream().anyMatch(k -> k.equals("recommendProduct:user:3"));
        Assertions.assertTrue(hasU1);
        Assertions.assertTrue(hasU3);
        String v1 = values.get(keys.indexOf("recommendProduct:user:1"));
        Assertions.assertTrue(v1.contains("102"));
        String v3 = values.get(keys.indexOf("recommendProduct:user:3"));
        Assertions.assertTrue(v3.contains("101"));
    }
}
