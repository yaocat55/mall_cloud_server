package cn.net.mall.product.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.IndexProductConditionEntity;
import cn.net.mall.product.entity.IndexProductEntity;
import cn.net.mall.product.entity.ProductEntity;
import cn.net.mall.product.entity.web.ProductWebEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class RecommendProductServiceTest {

    @Autowired
    private RecommendProductService recommendProductService;

    @MockBean
    private IndexProductService indexProductService;
    @MockBean
    private ProductService productService;

    @Test
    void should_return_products_when_index_has_data() {
        IndexProductEntity ip = new IndexProductEntity();
        ip.setProductId(1001L);
        ResponsePageEntity<IndexProductEntity> page = ResponsePageEntity.build(new IndexProductConditionEntity(), 1, Collections.singletonList(ip));
        Mockito.when(indexProductService.searchByPage(Mockito.any(IndexProductConditionEntity.class))).thenReturn(page);

        ProductEntity p = new ProductEntity();
        p.setId(1001L);
        p.setCategoryId(10L);
        p.setName("测试商品");
        p.setModel("测试规格");
        p.setQuantity(10);
        p.setRemainQuantity(8);
        p.setPrice(new BigDecimal("99.99"));
        Mockito.when(productService.findById(1001L)).thenReturn(p);

        List<ProductWebEntity> list = recommendProductService.recommendProduct();
        Assertions.assertNotNull(list);
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals("1001", list.get(0).getId());
        Assertions.assertEquals("99.99", list.get(0).getPrice());
    }
}

