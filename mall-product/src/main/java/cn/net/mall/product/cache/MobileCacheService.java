package cn.net.mall.product.cache;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.client.CategoryFeignClient;
import cn.net.mall.product.client.IndexFeignClient;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.*;
import cn.net.mall.product.entity.CategoryEntity;
import cn.net.mall.product.entity.IndexCarouselImageEntity;
import cn.net.mall.product.service.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 移动端本地缓存服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MobileCacheService {

    private final IndexCarouselImageService indexCarouselImageService;
    private final IndexNoticeService indexNoticeService;
    private final IndexProductService indexProductService;
    private final ProductService productService;
    private final CategoryService categoryService;


    // 本地缓存容器
    private List<IndexCarouselImageDTO> indexCarouselImageList;
    private final Map<Integer, List<IndexProductDTO>> indexProductListMap = new ConcurrentHashMap<>();
    private List<IndexNoticeDTO> indexNoticeList;
    private List<CategoryDTO> categoryList;
    private ResponsePageEntity<ProductSearchResultDTO> productSearchResult;

    /**
     * 初始化缓存
     */
    @PostConstruct
    public void init() {
        log.info("开始初始化本地缓存...");
        refreshCache();
        log.info("本地缓存初始化完成");
    }

    /**
     * 定时刷新缓存，每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000)
    public void refreshCache() {
        try {
            log.info("开始刷新本地缓存...");

            // 0. 预热缓存：从数据库加载数据到 Redis（空 Redis 时自动填充）
            indexCarouselImageService.refreshIndexCarouseImageToRedis(10, "create_time asc");
            indexProductService.refreshIndexProduct(10, "create_time asc");

            // 1. 刷新首页轮播图
            indexCarouselImageList = indexCarouselImageService.getIndexCarouselImageList();

            // 2. 刷新首页商品列表 (假设刷新类型1)
            List<IndexProductDTO> productList = indexProductService.getIndexProductList(1);
            indexProductListMap.put(1, productList != null ? productList : Collections.emptyList());

            // 3. 刷新首页公告
            indexNoticeList = indexNoticeService.getIndexNoticeList();

            // 4. 刷新分类列表 (父分类ID为0)
            categoryList = getCategoryList();

            // 5. 刷新商品搜索结果 (默认条件)
            ProductSearchConditionDTO condition = new ProductSearchConditionDTO();
            condition.setPageNo(1);
            condition.setPageSize(10);
            productSearchResult = productService.searchFromES(condition);

            log.info("本地缓存刷新完成");
        } catch (Exception e) {
            log.error("刷新本地缓存异常", e);
        }
    }

    private List<CategoryDTO> getCategoryList() {
        List<CategoryEntity> categoryEntities = categoryService.getCategoryByParentId(0L);
        if (CollectionUtils.isEmpty(categoryEntities)) {
            return Collections.EMPTY_LIST;
        }
        return BeanUtil.copyToList(categoryEntities, CategoryDTO.class);
    }

    public List<IndexCarouselImageDTO> getIndexCarouselImageList() {
        return indexCarouselImageList;
    }

    public List<IndexProductDTO> getIndexProductList(int type) {
        return indexProductListMap.get(type);
    }

    public List<IndexNoticeDTO> getIndexNoticeList() {
        return indexNoticeList;
    }

    public List<CategoryDTO> getCategoryList(Long parentId) {
        if (Long.valueOf(0).equals(parentId)) {
            return categoryList;
        }
        return null;
    }

    public ResponsePageEntity<ProductSearchResultDTO> getProductSearchResult() {
        return productSearchResult;
    }
}
