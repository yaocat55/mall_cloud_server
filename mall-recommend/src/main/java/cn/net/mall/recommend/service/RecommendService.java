package cn.net.mall.recommend.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.enums.JobResult;
import cn.net.mall.product.client.IndexFeignClient;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.IndexProductDTO;
import cn.net.mall.product.dto.ProductDTO;
import cn.net.mall.product.dto.ProductSearchResultDTO;
import cn.net.mall.product.client.ProductViewRecordFeignClient;
import cn.net.mall.recommend.support.RecommendMahoutHelper;
import cn.net.mall.product.dto.ProductViewRecordConditionDTO;
import cn.net.mall.product.dto.ProductViewRecordDTO;
import cn.net.mall.recommend.entity.ProductFavoritesEntity;
import cn.net.mall.recommend.entity.ProductFavoritesConditionEntity;
import cn.net.mall.recommend.entity.ProductViewRecordEntity;
import cn.net.mall.recommend.entity.ProductViewRecordConditionEntity;
import cn.net.mall.recommend.mapper.ProductFavoritesMapper;
import cn.net.mall.recommend.mapper.ProductViewRecordMapper;
import cn.net.mall.util.RedisUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.entity.auth.JwtUserEntity;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Service
public class RecommendService {

    private final IndexFeignClient indexFeignClient;
    private final ProductFeignClient productFeignClient;
    private final ProductViewRecordFeignClient productViewRecordFeignClient;
    private final ProductViewRecordMapper productViewRecordMapper;
    private final ProductFavoritesMapper productFavoritesMapper;
    private final RedisUtil redisUtil;
    private static final long USER_REC_TTL_SECONDS = 86400L;
    private static final long ITEM_SIM_TTL_SECONDS = 604800L;
    private static final int DEFAULT_DAYS_WINDOW = 30;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int VIEW_WEIGHT = 1;
    private static final int FAV_WEIGHT = 3;

    public List<ProductSearchResultDTO> recommendProduct() {
        JwtUserEntity user = FillUserUtil.getCurrentUserInfoOrNull();
        if (user != null) {
            List<Long> cached = readIdList(buildUserKey(user.getId()));
            if (CollectionUtils.isNotEmpty(cached)) {
                return toSearchResult(cached);
            }
        }
        Map<Long, ProductSearchResultDTO> dedup = new LinkedHashMap<>();

        List<IndexProductDTO> hotList = safeList(indexFeignClient.getIndexProductList(1));
        List<IndexProductDTO> latestList = safeList(indexFeignClient.getIndexProductList(2));

        fillSearchResult(dedup, hotList);
        fillSearchResult(dedup, latestList);

        return new ArrayList<>(dedup.values());
    }

    public List<ProductSearchResultDTO> recommendByItem(Long productId, Integer topN) {
        try {
            List<Long> cached = readIdList(buildItemKey(productId));
            if (CollectionUtils.isNotEmpty(cached)) {
                int n = Optional.ofNullable(topN).orElse(20);
                return toSearchResult(cached.stream().limit(n).collect(Collectors.toList()));
            }
            List<ProductViewRecordDTO> records = loadViewRecordsLimited(5000, 10);
            if (CollectionUtils.isEmpty(records)) {
                return Collections.emptyList();
            }
            int n = Optional.ofNullable(topN).orElse(20);
            List<Long> ids = RecommendMahoutHelper.recommendByItem(RecommendMahoutHelper.buildDataModel(records), productId, n);
            return toSearchResult(ids);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<ProductSearchResultDTO> recommendByUser(Long userId, Integer topN) {
        try {
            List<Long> cached = readIdList(buildUserKey(userId));
            if (CollectionUtils.isNotEmpty(cached)) {
                int n = Optional.ofNullable(topN).orElse(20);
                return toSearchResult(cached.stream().limit(n).collect(Collectors.toList()));
            }
            Map<Long, Map<Long, Integer>> matrix = new HashMap<>();
            forEachViewRecordPageFromDb(5000, page -> {
                Map<Long, Map<Long, Integer>> sub = buildUserItemMatrixFromView(page);
                mergeMatrix(matrix, sub);
            });
            forEachFavoritesPageFromDb(5000, page -> {
                Map<Long, Map<Long, Integer>> sub = buildUserItemMatrixFromFavorites(page);
                mergeMatrix(matrix, sub);
            });
            Map<Long, Double> norms = computeUserNorms(matrix);
            Map<Long, Map<Long, Double>> dotSums = accumulateDotProducts(matrix);
            Map<Long, Map<Long, Double>> sims = toSimilarities(dotSums, norms);
            int n = Optional.ofNullable(topN).orElse(20);
            Map<Long, List<Long>> recs = generateRecommendations(matrix, sims, n);
            List<Long> ids = recs.getOrDefault(userId, Collections.emptyList());
            return toSearchResult(ids);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public JobResult recommendProductToRedis() {
        try {
            Map<Long, Map<Long, Integer>> matrix = new HashMap<>();
            forEachViewRecordPageFromDb(5000, page -> {
                Map<Long, Map<Long, Integer>> sub = buildUserItemMatrixFromView(page);
                mergeMatrix(matrix, sub);
            });
            forEachFavoritesPageFromDb(5000, page -> {
                Map<Long, Map<Long, Integer>> sub = buildUserItemMatrixFromFavorites(page);
                mergeMatrix(matrix, sub);
            });
            if (matrix.isEmpty()) {
                return JobResult.SUCCESS;
            }
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

    public JobResult recommendItemSimilaritiesToRedis() {
        try {
            Map<Long, Integer> itemViewCount = new HashMap<>();
            Map<Long, Map<Long, Integer>> coCount = new HashMap<>();
            forEachViewRecordPage(5000, page -> {
                Map<Long, Set<Long>> grouped = groupUserItems(page);
                for (Set<Long> items : grouped.values()) {
                    for (Long i : items) {
                        itemViewCount.put(i, itemViewCount.getOrDefault(i, 0) + 1);
                    }
                    List<Long> list = new ArrayList<>(items);
                    int size = list.size();
                    for (int a = 0; a < size; a++) {
                        Long i = list.get(a);
                        Map<Long, Integer> inner = coCount.computeIfAbsent(i, k -> new HashMap<>());
                        for (int b = a + 1; b < size; b++) {
                            Long j = list.get(b);
                            inner.put(j, inner.getOrDefault(j, 0) + 1);
                            Map<Long, Integer> back = coCount.computeIfAbsent(j, k -> new HashMap<>());
                            back.put(i, back.getOrDefault(i, 0) + 1);
                        }
                        if (inner.size() > 400) {
                            trimTopK(inner, 200);
                        }
                    }
                }
            });
            if (coCount.isEmpty()) {
                return JobResult.SUCCESS;
            }
            for (Map.Entry<Long, Map<Long, Integer>> e : coCount.entrySet()) {
                Long i = e.getKey();
                Map<Long, Integer> inner = e.getValue();
                List<Long> top = inner.entrySet().stream()
                        .map(x -> new AbstractMap.SimpleEntry<>(x.getKey(), scoreCosine(inner.get(x.getKey()), itemViewCount.getOrDefault(i, 1), itemViewCount.getOrDefault(x.getKey(), 1))))
                        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                        .limit(50)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                redisUtil.set(buildItemKey(i), JSON.toJSONString(top), ITEM_SIM_TTL_SECONDS);
            }
            return JobResult.SUCCESS;
        } catch (Exception e) {
            return JobResult.FAILURE;
        }
    }

    private List<ProductViewRecordDTO> loadViewRecords() {
        ProductViewRecordConditionDTO condition = new ProductViewRecordConditionDTO();
        condition.setPageSize(0);
        ResponsePageEntity<ProductViewRecordDTO> page = productViewRecordFeignClient.searchByPage(condition);
        if (page == null || CollectionUtils.isEmpty(page.getData())) {
            return Collections.emptyList();
        }
        return page.getData();
    }

    private List<ProductViewRecordDTO> loadViewRecordsLimited(int pageSize, int maxPages) {
        List<ProductViewRecordDTO> result = new ArrayList<>();
        ProductViewRecordConditionDTO condition = new ProductViewRecordConditionDTO();
        condition.setPageSize(pageSize);
        condition.setPageNo(1);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime begin = now.minusDays(DEFAULT_DAYS_WINDOW);
        condition.setCreateBeginTime(begin.format(DT_FMT));
        condition.setCreateEndTime(now.format(DT_FMT));
        int pages = 0;
        while (pages < maxPages) {
            List<ProductViewRecordDTO> list = productViewRecordFeignClient.searchList(condition);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            result.addAll(list);
            pages++;
            if (list.size() < pageSize) {
                break;
            }
            condition.setPageNo(condition.getPageNo() + 1);
        }
        return result;
    }

    private List<IndexProductDTO> safeList(List<IndexProductDTO> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    private List<ProductSearchResultDTO> toSearchResult(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<ProductDTO> products = productFeignClient.findByIds(ids);
        if (CollectionUtils.isEmpty(products)) {
            return Collections.emptyList();
        }
        List<ProductSearchResultDTO> list = new ArrayList<>();
        for (ProductDTO p : products) {
            ProductSearchResultDTO dto = new ProductSearchResultDTO();
            dto.setId(p.getId().toString());
            dto.setCategoryId(p.getCategoryId());
            dto.setName(p.getName());
            dto.setModel(p.getModel());
            dto.setQuantity(p.getQuantity());
            dto.setRemainQuantity(p.getRemainQuantity());
            dto.setPrice(p.getPrice() == null ? null : p.getPrice().toPlainString());
            dto.setCover(p.getCoverUrl());
            list.add(dto);
        }
        Map<Long, ProductSearchResultDTO> map = list.stream().collect(Collectors.toMap(x -> Long.valueOf(x.getId()), x -> x, (a, b) -> a));
        List<ProductSearchResultDTO> result = new ArrayList<>();
        for (Long id : ids) {
            ProductSearchResultDTO dto = map.get(id);
            if (dto != null) {
                result.add(dto);
            }
        }
        return result;
    }

    private void fillSearchResult(Map<Long, ProductSearchResultDTO> dest, List<IndexProductDTO> src) {
        if (CollectionUtils.isEmpty(src)) {
            return;
        }
        for (IndexProductDTO ip : src) {
            if (ip == null || ip.getProductId() == null) {
                continue;
            }
            if (dest.containsKey(ip.getProductId())) {
                continue;
            }
            ProductSearchResultDTO dto = new ProductSearchResultDTO();
            dto.setId(ip.getProductId().toString());
            dto.setName(ip.getProductName());
            dto.setModel(ip.getModel());
            dto.setPrice(ip.getPrice() == null ? null : ip.getPrice().toPlainString());
            dto.setCover(ip.getCover());
            dest.put(ip.getProductId(), dto);
        }
    }

    private Map<Long, Map<Long, Integer>> buildUserItemMatrix(List<ProductViewRecordDTO> records) {
        Map<Long, Map<Long, Integer>> matrix = new HashMap<>();
        for (ProductViewRecordDTO r : records) {
            if (r.getUserId() == null || r.getProductId() == null) {
                continue;
            }
            Map<Long, Integer> itemMap = matrix.computeIfAbsent(r.getUserId(), k -> new HashMap<>());
            int count = Math.max(1, r.getViewCount());
            itemMap.put(r.getProductId(), itemMap.getOrDefault(r.getProductId(), 0) + count);
        }
        return matrix;
    }

    private Map<Long, Map<Long, Integer>> buildUserItemMatrixFromView(List<ProductViewRecordEntity> records) {
        Map<Long, Map<Long, Integer>> matrix = new HashMap<>();
        for (ProductViewRecordEntity r : records) {
            if (r.getUserId() == null || r.getProductId() == null) {
                continue;
            }
            Map<Long, Integer> itemMap = matrix.computeIfAbsent(r.getUserId(), k -> new HashMap<>());
            int count = Math.max(1, r.getViewCount() == null ? 1 : r.getViewCount()) * VIEW_WEIGHT;
            itemMap.put(r.getProductId(), itemMap.getOrDefault(r.getProductId(), 0) + count);
        }
        return matrix;
    }

    private Map<Long, Map<Long, Integer>> buildUserItemMatrixFromFavorites(List<ProductFavoritesEntity> records) {
        Map<Long, Map<Long, Integer>> matrix = new HashMap<>();
        for (ProductFavoritesEntity r : records) {
            if (r.getUserId() == null || r.getProductId() == null) {
                continue;
            }
            Map<Long, Integer> itemMap = matrix.computeIfAbsent(r.getUserId(), k -> new HashMap<>());
            int count = FAV_WEIGHT;
            itemMap.put(r.getProductId(), itemMap.getOrDefault(r.getProductId(), 0) + count);
        }
        return matrix;
    }

    private Map<Long, Set<Long>> groupUserItems(List<ProductViewRecordDTO> records) {
        Map<Long, Set<Long>> grouped = new HashMap<>();
        for (ProductViewRecordDTO r : records) {
            if (r.getUserId() == null || r.getProductId() == null) {
                continue;
            }
            Set<Long> set = grouped.computeIfAbsent(r.getUserId(), k -> new HashSet<>());
            set.add(r.getProductId());
        }
        return grouped;
    }

    private void mergeMatrix(Map<Long, Map<Long, Integer>> base, Map<Long, Map<Long, Integer>> add) {
        for (Map.Entry<Long, Map<Long, Integer>> e : add.entrySet()) {
            Map<Long, Integer> dest = base.computeIfAbsent(e.getKey(), k -> new HashMap<>());
            for (Map.Entry<Long, Integer> it : e.getValue().entrySet()) {
                dest.put(it.getKey(), dest.getOrDefault(it.getKey(), 0) + it.getValue());
            }
        }
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
            String key = buildUserKey(e.getKey());
            redisUtil.set(key, JSON.toJSONString(e.getValue()), USER_REC_TTL_SECONDS);
        }
    }

    private String buildUserKey(Long userId) {
        return "recommendProduct:user:" + userId;
    }

    private String buildItemKey(Long itemId) {
        return "recommendProduct:item:" + itemId;
    }

    private void forEachViewRecordPage(int pageSize, java.util.function.Consumer<List<ProductViewRecordDTO>> consumer) {
        ProductViewRecordConditionDTO condition = new ProductViewRecordConditionDTO();
        condition.setPageSize(pageSize);
        condition.setPageNo(1);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime begin = now.minusDays(DEFAULT_DAYS_WINDOW);
        condition.setCreateBeginTime(begin.format(DT_FMT));
        condition.setCreateEndTime(now.format(DT_FMT));
        while (true) {
            List<ProductViewRecordDTO> list = productViewRecordFeignClient.searchList(condition);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            consumer.accept(list);
            if (list.size() < pageSize) {
                break;
            }
            condition.setPageNo(condition.getPageNo() + 1);
        }
    }

    private void forEachViewRecordPageFromDb(int pageSize, java.util.function.Consumer<List<ProductViewRecordEntity>> consumer) {
        ProductViewRecordConditionEntity condition = new ProductViewRecordConditionEntity();
        condition.setPageSize(pageSize);
        condition.setPageNo(1);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime begin = now.minusDays(DEFAULT_DAYS_WINDOW);
        condition.setCreateBeginTime(begin.format(DT_FMT));
        condition.setCreateEndTime(now.format(DT_FMT));
        while (true) {
            List<ProductViewRecordEntity> list = productViewRecordMapper.searchByCondition(condition);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            consumer.accept(list);
            if (list.size() < pageSize) {
                break;
            }
            condition.setPageNo(condition.getPageNo() + 1);
        }
    }

    private void forEachFavoritesPageFromDb(int pageSize, java.util.function.Consumer<List<ProductFavoritesEntity>> consumer) {
        ProductFavoritesConditionEntity condition = new ProductFavoritesConditionEntity();
        condition.setPageSize(pageSize);
        condition.setPageNo(1);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime begin = now.minusDays(DEFAULT_DAYS_WINDOW);
        condition.setCreateBeginTime(begin.format(DT_FMT));
        condition.setCreateEndTime(now.format(DT_FMT));
        while (true) {
            List<ProductFavoritesEntity> list = productFavoritesMapper.searchByCondition(condition);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            consumer.accept(list);
            if (list.size() < pageSize) {
                break;
            }
            condition.setPageNo(condition.getPageNo() + 1);
        }
    }

    private void trimTopK(Map<Long, Integer> map, int k) {
        if (map.size() <= k) {
            return;
        }
        List<Map.Entry<Long, Integer>> sorted = map.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(k)
                .collect(Collectors.toList());
        map.clear();
        for (Map.Entry<Long, Integer> e : sorted) {
            map.put(e.getKey(), e.getValue());
        }
    }

    private double scoreCosine(int co, int cntI, int cntJ) {
        double denom = Math.sqrt((double) cntI * (double) cntJ);
        return denom > 0 ? ((double) co) / denom : 0D;
    }

    private List<Long> readIdList(String key) {
        String val = redisUtil.get(key);
        if (val == null || val.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = JSON.parseArray(val, Long.class);
        return ids == null ? Collections.emptyList() : ids;
    }
}
