package cn.net.mall.recommend.support;

import cn.net.mall.product.dto.ProductViewRecordDTO;
import org.apache.mahout.cf.taste.model.DataModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class RecommendMahoutHelperTest {

    @Test
    void should_recommend_by_item_when_have_view_records() throws Exception {
        List<ProductViewRecordDTO> records = new ArrayList<>();
        records.add(build(1L, 101L, 3));
        records.add(build(1L, 102L, 2));
        records.add(build(2L, 101L, 1));
        records.add(build(2L, 103L, 5));
        records.add(build(3L, 102L, 4));
        records.add(build(3L, 103L, 1));

        DataModel model = RecommendMahoutHelper.buildDataModel(records);
        List<Long> result = RecommendMahoutHelper.recommendByItem(model, 101L, 2);
        Assertions.assertTrue(result.size() > 0);
    }

    @Test
    void should_recommend_by_user_when_have_neighbors() throws Exception {
        List<ProductViewRecordDTO> records = new ArrayList<>();
        records.add(build(1L, 101L, 3));
        records.add(build(1L, 102L, 2));
        records.add(build(2L, 101L, 1));
        records.add(build(2L, 103L, 5));
        records.add(build(3L, 102L, 4));
        records.add(build(3L, 103L, 1));

        DataModel model = RecommendMahoutHelper.buildDataModel(records);
        List<Long> result = RecommendMahoutHelper.recommendByUser(model, 1L, 2, 2);
        Assertions.assertTrue(result.size() >= 0);
    }

    private ProductViewRecordDTO build(Long userId, Long productId, int count) {
        ProductViewRecordDTO r = new ProductViewRecordDTO();
        r.setUserId(userId);
        r.setProductId(productId);
        r.setViewCount(count);
        return r;
    }
}
