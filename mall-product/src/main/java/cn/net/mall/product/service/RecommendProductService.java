package cn.net.mall.product.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.enums.JobResult;
import cn.net.mall.product.entity.IndexProductConditionEntity;
import cn.net.mall.product.entity.IndexProductEntity;
import cn.net.mall.product.entity.ProductViewRecordConditionEntity;
import cn.net.mall.product.entity.ProductViewRecordEntity;
import cn.net.mall.product.entity.ProductEntity;
import cn.net.mall.product.entity.web.ProductWebEntity;
import cn.net.mall.product.mapper.ProductViewRecordMapper;
import cn.net.mall.redis.RedisUtil;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RecommendProductService {

    private final IndexProductService indexProductService;
    private final ProductService productService;
    private final ProductViewRecordMapper productViewRecordMapper;
    private final RedisUtil redisUtil;

    public List<ProductWebEntity> recommendProduct() {
        List<ProductWebEntity> result = new ArrayList<>();
        Map<Long, ProductWebEntity> dedup = new LinkedHashMap<>();

        List<IndexProductEntity> hotList = getIndexProducts(1, 10);
        List<IndexProductEntity> latestList = getIndexProducts(2, 10);

        fillProductWebEntity(dedup, hotList);
        fillProductWebEntity(dedup, latestList);

        result.addAll(dedup.values());
        return result;
    }

    /**
     * 将用户商品推荐结果写入缓存
     *
     * @return 任务执行结果
     */
    public JobResult recommendProductToRedis() {
        try {
            List<ProductViewRecordEntity> records = loadViewRecords();
            if (CollectionUtils.isEmpty(records)) {
                return JobResult.SUCCESS;
            }
            Map<Long, Map<Long, Integer>> matrix = buildUserItemMatrix(records);
            Map<Long, Double> norms = computeUserNorms(matrix);
            Map<Long, Map<Long, Double>> dotSums = accumulateDotProducts(matrix);
            Map<Long, Map<Long, Double>> sims = toSimilarities(dotSums, norms);
            Map<Long, List<Long>> recs = generateRecommendations(matrix, sims, 20);
            writeRecommendations(recs);
            return JobResult.SUCCESS;
        } catch (Exception e) {
            return JobResult.FAILURE;
        }
    }

    private List<IndexProductEntity> getIndexProducts(int type, int size) {
        IndexProductConditionEntity condition = new IndexProductConditionEntity();
        condition.setType(type);
        condition.setPageNo(1);
        condition.setPageSize(size);
        ResponsePageEntity<IndexProductEntity> page = indexProductService.searchByPage(condition);
        if (page == null || CollectionUtils.isEmpty(page.getData())) {
            return new ArrayList<>();
        }
        return page.getData();
    }

    private void fillProductWebEntity(Map<Long, ProductWebEntity> dest, List<IndexProductEntity> src) {
        if (CollectionUtils.isEmpty(src)) {
            return;
        }
        for (IndexProductEntity ip : src) {
            if (ip == null || ip.getProductId() == null) {
                continue;
            }
            if (dest.containsKey(ip.getProductId())) {
                continue;
            }
            ProductEntity p = productService.findById(ip.getProductId());
            ProductWebEntity w = new ProductWebEntity();
            w.setId(p.getId() == null ? null : String.valueOf(p.getId()));
            w.setCategoryId(p.getCategoryId());
            w.setName(p.getName());
            w.setModel(p.getModel());
            w.setQuantity(p.getQuantity());
            w.setRemainQuantity(p.getRemainQuantity());
            w.setPrice(p.getPrice() == null ? null : p.getPrice().toPlainString());
            w.setCover(ip.getCover());
            dest.put(p.getId(), w);
        }
    }

    private List<ProductViewRecordEntity> loadViewRecords() {
        ProductViewRecordConditionEntity condition = new ProductViewRecordConditionEntity();
        condition.setPageSize(0);
        return productViewRecordMapper.searchByCondition(condition);
    }

    private Map<Long, Map<Long, Integer>> buildUserItemMatrix(List<ProductViewRecordEntity> records) {
        Map<Long, Map<Long, Integer>> matrix = new HashMap<>();
        for (ProductViewRecordEntity r : records) {
            if (Objects.isNull(r.getUserId()) || Objects.isNull(r.getProductId())) {
                continue;
            }
            Map<Long, Integer> itemMap = matrix.computeIfAbsent(r.getUserId(), k -> new HashMap<>());
            int count = Math.max(1, r.getViewCount());
            itemMap.put(r.getProductId(), itemMap.getOrDefault(r.getProductId(), 0) + count);
        }
        return matrix;
    }

    private Map<Long, Double> computeUserNorms(Map<Long, Map<Long, Integer>> matrix) {
        Map<Long, Double> norms = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Integer>> e : matrix.entrySet()) {
            long userId = e.getKey();
            double sumSq = 0D;
            for (int c : e.getValue().values()) {
                sumSq += (double) c * c;
            }
            norms.put(userId, Math.sqrt(sumSq));
        }
        return norms;
    }

    private Map<Long, Map<Long, Double>> accumulateDotProducts(Map<Long, Map<Long, Integer>> matrix) {
        Map<Long, Map<Long, Double>> dot = new HashMap<>();
        Map<Long, Map<Long, Integer>> inverted = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Integer>> e : matrix.entrySet()) {
            long userId = e.getKey();
            for (Map.Entry<Long, Integer> item : e.getValue().entrySet()) {
                long productId = item.getKey();
                int cnt = item.getValue();
                Map<Long, Integer> userCnts = inverted.computeIfAbsent(productId, k -> new HashMap<>());
                userCnts.put(userId, cnt);
            }
        }
        for (Map.Entry<Long, Map<Long, Integer>> entry : inverted.entrySet()) {
            Map<Long, Integer> userCnts = entry.getValue();
            List<Map.Entry<Long, Integer>> list = new ArrayList<>(userCnts.entrySet());
            int n = list.size();
            for (int i = 0; i < n; i++) {
                long u = list.get(i).getKey();
                int cu = list.get(i).getValue();
                for (int j = i + 1; j < n; j++) {
                    long v = list.get(j).getKey();
                    int cv = list.get(j).getValue();
                    double add = (double) cu * cv;
                    Map<Long, Double> m1 = dot.computeIfAbsent(u, k -> new HashMap<>());
                    Map<Long, Double> m2 = dot.computeIfAbsent(v, k -> new HashMap<>());
                    m1.put(v, m1.getOrDefault(v, 0D) + add);
                    m2.put(u, m2.getOrDefault(u, 0D) + add);
                }
            }
        }
        return dot;
    }

    private Map<Long, Map<Long, Double>> toSimilarities(Map<Long, Map<Long, Double>> dot, Map<Long, Double> norms) {
        Map<Long, Map<Long, Double>> sims = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Double>> e : dot.entrySet()) {
            long u = e.getKey();
            double nu = norms.getOrDefault(u, 0D);
            Map<Long, Double> inner = new HashMap<>();
            for (Map.Entry<Long, Double> p : e.getValue().entrySet()) {
                long v = p.getKey();
                double nv = norms.getOrDefault(v, 0D);
                double dv = p.getValue();
                double s = (nu > 0 && nv > 0) ? dv / (nu * nv) : 0D;
                if (s > 0) {
                    inner.put(v, s);
                }
            }
            if (!inner.isEmpty()) {
                sims.put(u, inner);
            }
        }
        return sims;
    }

    private Map<Long, List<Long>> generateRecommendations(Map<Long, Map<Long, Integer>> matrix,
                                                          Map<Long, Map<Long, Double>> sims,
                                                          int topN) {
        Map<Long, List<Long>> result = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Integer>> e : matrix.entrySet()) {
            long u = e.getKey();
            Set<Long> owned = e.getValue().keySet();
            Map<Long, Double> score = new HashMap<>();
            Map<Long, Double> neighbors = sims.getOrDefault(u, new HashMap<>());
            for (Map.Entry<Long, Double> nb : neighbors.entrySet()) {
                long v = nb.getKey();
                double sv = nb.getValue();
                Map<Long, Integer> items = matrix.getOrDefault(v, new HashMap<>());
                for (Map.Entry<Long, Integer> it : items.entrySet()) {
                    long pid = it.getKey();
                    if (owned.contains(pid)) {
                        continue;
                    }
                    double add = sv * it.getValue();
                    score.put(pid, score.getOrDefault(pid, 0D) + add);
                }
            }
            if (!score.isEmpty()) {
                List<Long> ids = score.entrySet().stream()
                        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                        .limit(topN)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                result.put(u, ids);
            }
        }
        return result;
    }

    private void writeRecommendations(Map<Long, List<Long>> recs) {
        for (Map.Entry<Long, List<Long>> e : recs.entrySet()) {
            String key = buildKey(e.getKey());
            redisUtil.set(key, JSON.toJSONString(e.getValue()));
        }
    }

    private String buildKey(Long userId) {
        return "recommendProduct:user:" + userId;
    }
}
