package cn.net.mall.product.es;

import cn.net.mall.entity.ResponsePageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ES 操作封装 — 基于 {@link ElasticsearchOperations}.
 *
 * @date 2024/5/14 下午4:30
 */
@Slf4j
@Component
public class EsTemplate {

    private final ElasticsearchOperations elasticsearchOperations;

    public EsTemplate(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    /**
     * 添加或更新数据到 ES.
     */
    public boolean insertOrUpdate(String indexName, Object entity) {
        try {
            elasticsearchOperations.save(entity, IndexCoordinates.of(indexName));
            return true;
        } catch (Exception e) {
            log.error("写入 ES 失败，index={}", indexName, e);
            return false;
        }
    }

    /**
     * 批量删除.
     */
    public <T> boolean deleteBatch(String indexName, Collection<T> idList) {
        boolean flag = true;
        IndexCoordinates idx = IndexCoordinates.of(indexName);
        for (T id : idList) {
            try {
                String result = elasticsearchOperations.delete(id.toString(), idx);
                if (result == null) {
                    flag = false;
                }
            } catch (Exception e) {
                log.error("删除 ES 文档失败，index={}, id={}", indexName, id, e);
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 批量添加数据到 ES.
     */
    public void batchInsert(String indexName, List<?> entities) {
        for (Object entity : entities) {
            try {
                elasticsearchOperations.save(entity, IndexCoordinates.of(indexName));
            } catch (Exception e) {
                log.error("批量写入 ES 失败，index={}", indexName, e);
            }
        }
    }

    /**
     * 查询数据（带分页）.
     *
     * @param <T>            结果类型
     * @param indexName      索引名称
     * @param query          Spring Data ES Query
     * @param clazz          结果类
     * @param responsePage   分页响应（回写 totalCount）
     * @return 数据列表
     */
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
            log.error("ES 查询失败，index={}", indexName, e);
            return List.of();
        }
    }
}
