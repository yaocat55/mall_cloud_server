package cn.net.mall.recommend.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.client.IndexFeignClient;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.ProductDTO;
import cn.net.mall.product.dto.ProductSearchResultDTO;
import cn.net.mall.product.client.ProductViewRecordFeignClient;
import cn.net.mall.product.dto.ProductViewRecordConditionDTO;
import cn.net.mall.recommend.entity.ProductFavoritesEntity;
import cn.net.mall.recommend.entity.ProductViewRecordEntity;
import cn.net.mall.recommend.mapper.ProductFavoritesMapper;
import cn.net.mall.recommend.mapper.ProductViewRecordMapper;
import cn.net.mall.util.RedisUtil;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.*;

class RecommendServiceTest {

    static class FakeRedisUtil extends RedisUtil {
        private final Map<String, String> store = new HashMap<>();
        @Override
        public boolean set(String key, String value) {
            store.put(key, value);
            return true;
        }
        @Override
        public boolean set(String key, String value, long expireTime) {
            store.put(key, value);
            return true;
        }
        @Override
        public String get(String key) {
            return store.get(key);
        }
    }

    @Test
    void should_return_redis_recommend_when_available() {
        IndexFeignClient index = Mockito.mock(IndexFeignClient.class);
        ProductFeignClient product = Mockito.mock(ProductFeignClient.class);
        ProductViewRecordFeignClient view = Mockito.mock(ProductViewRecordFeignClient.class);
        ProductViewRecordMapper viewMapper = Mockito.mock(ProductViewRecordMapper.class);
        ProductFavoritesMapper favMapper = Mockito.mock(ProductFavoritesMapper.class);
        Mockito.when(view.searchByPage(Mockito.any(ProductViewRecordConditionDTO.class)))
                .thenAnswer(invocation -> {
                    ProductViewRecordConditionDTO c = invocation.getArgument(0);
                    return ResponsePageEntity.buildEmpty(c);
                });
        Mockito.when(product.findByIds(Mockito.anyList())).thenAnswer(invocation -> {
            List<Long> ids = invocation.getArgument(0);
            List<ProductDTO> list = new ArrayList<>();
            for (Long id : ids) {
                ProductDTO dto = new ProductDTO();
                dto.setId(id);
                dto.setCategoryId(1L);
                dto.setName("P" + id);
                dto.setModel("M" + id);
                dto.setQuantity(100);
                dto.setRemainQuantity(90);
                dto.setPrice(new BigDecimal("9.99"));
                dto.setCoverUrl("c" + id);
                list.add(dto);
            }
            return list;
        });
        FakeRedisUtil redis = new FakeRedisUtil();
        redis.set("recommendProduct:user:1", JSON.toJSONString(Arrays.asList(1001L, 1002L)));
        RecommendService service = new RecommendService(index, product, view, viewMapper, favMapper, redis);
        List<ProductSearchResultDTO> list = service.recommendByUser(1L, 10);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("1001", list.get(0).getId());
        Assertions.assertEquals("1002", list.get(1).getId());
    }

    @Test
    void should_generate_weighted_recommendations_when_mixed_records() {
        IndexFeignClient index = Mockito.mock(IndexFeignClient.class);
        ProductFeignClient product = Mockito.mock(ProductFeignClient.class);
        ProductViewRecordFeignClient view = Mockito.mock(ProductViewRecordFeignClient.class);
        ProductViewRecordMapper viewMapper = Mockito.mock(ProductViewRecordMapper.class);
        ProductFavoritesMapper favMapper = Mockito.mock(ProductFavoritesMapper.class);
        FakeRedisUtil redis = new FakeRedisUtil();

        List<ProductViewRecordEntity> viewPage1 = new ArrayList<>();
        ProductViewRecordEntity v11 = new ProductViewRecordEntity();
        v11.setUserId(1L);
        v11.setProductId(200L);
        v11.setViewCount(1);
        viewPage1.add(v11);
        ProductViewRecordEntity v21 = new ProductViewRecordEntity();
        v21.setUserId(2L);
        v21.setProductId(100L);
        v21.setViewCount(1);
        viewPage1.add(v21);

        List<ProductFavoritesEntity> favPage1 = new ArrayList<>();
        ProductFavoritesEntity f12 = new ProductFavoritesEntity();
        f12.setUserId(1L);
        f12.setProductId(100L);
        favPage1.add(f12);
        ProductFavoritesEntity f2a = new ProductFavoritesEntity();
        f2a.setUserId(2L);
        f2a.setProductId(200L);
        favPage1.add(f2a);
        ProductFavoritesEntity f2b = new ProductFavoritesEntity();
        f2b.setUserId(2L);
        f2b.setProductId(300L);
        favPage1.add(f2b);

        Mockito.when(viewMapper.searchByCondition(Mockito.any()))
                .thenReturn(viewPage1, Collections.emptyList());
        Mockito.when(favMapper.searchByCondition(Mockito.any()))
                .thenReturn(favPage1, Collections.emptyList());

        RecommendService service = new RecommendService(index, product, view, viewMapper, favMapper, redis);
        service.recommendProductToRedis();
        String json = redis.get("recommendProduct:user:1");
        Assertions.assertNotNull(json);
        List<Long> ids = JSON.parseArray(json, Long.class);
        Assertions.assertTrue(ids.contains(300L));
    }
}
