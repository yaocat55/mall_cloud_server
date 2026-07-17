package cn.net.mall.marketing.es;

import cn.net.mall.entity.ResponsePageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ES 操作封装 — 基于 {@link ElasticsearchOperations}.
 */
@Slf4j
@Component
public class EsTemplate {

    private final ElasticsearchOperations elasticsearchOperations;

    public EsTemplate(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public boolean insertOrUpdate(String indexName, Object entity) {
        try {
            elasticsearchOperations.save(entity, IndexCoordinates.of(indexName));
            return true;
        } catch (Exception e) {
            log.error("写入ES失败，index={}", indexName, e);
            return false;
        }
    }

    public <T> boolean deleteBatch(String indexName, java.util.Collection<T> idList) {
        boolean flag = true;
        IndexCoordinates idx = IndexCoordinates.of(indexName);
        for (T id : idList) {
            try {
                String result = elasticsearchOperations.delete(id.toString(), idx);
                if (result == null) flag = false;
            } catch (Exception e) {
                log.error("删除ES文档失败，index={}, id={}", indexName, id, e);
                flag = false;
            }
        }
        return flag;
    }

    public void batchInsert(String indexName, List<?> entities) {
        for (Object entity : entities) {
            try {
                elasticsearchOperations.save(entity, IndexCoordinates.of(indexName));
            } catch (Exception e) {
                log.error("批量写入ES失败，index={}", indexName, e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> search(String indexName, Query query, Class<T> clazz,
                              ResponsePageEntity<?> responsePage) {
        try {
            SearchHits<T> hits = elasticsearchOperations.search(query, clazz,
                    IndexCoordinates.of(indexName));
            long total = hits.getTotalHits();
            responsePage.setTotalCount((int) total);
            return hits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("ES查询失败，index={}", indexName, e);
            return List.of();
        }
    }

    public <T> List<T> search(String indexName, Query query, Class<T> clazz) {
        try {
            SearchHits<T> hits = elasticsearchOperations.search(query, clazz,
                    IndexCoordinates.of(indexName));
            return hits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("ES查询失败，index={}", indexName, e);
            return List.of();
        }
    }
}
