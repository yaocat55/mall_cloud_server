package cn.net.mall.product.controller.mobile;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.annotation.NoLogin;
import cn.net.mall.entity.RequestPageEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.cache.MobileCacheService;
import cn.net.mall.product.dto.IndexCarouselImageDTO;
import cn.net.mall.product.dto.IndexNoticeDTO;
import cn.net.mall.product.dto.IndexNoticeDetailDTO;
import cn.net.mall.product.dto.IndexProductDTO;
import cn.net.mall.product.entity.web.IndexNoticeDetailWebEntity;
import cn.net.mall.product.entity.web.IndexNoticeWebEntity;
import cn.net.mall.product.service.IndexCarouselImageService;
import cn.net.mall.product.service.IndexNoticeService;
import cn.net.mall.product.service.IndexProductService;
import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 移动端首页相关接口
 */
@Tag(name = "移动端-首页", description = "移动端：首页展示、轮播图、推荐")
@AllArgsConstructor
@RestController
@RequestMapping("/v1/mobile/index")
public class MobileIndexController {

    private final IndexCarouselImageService indexCarouselImageService;
    private final IndexNoticeService indexNoticeService;
    private final IndexProductService indexProductService;
    private final MobileCacheService mobileCacheService;

    private final Cache<String, Object> indexPageCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    /**
     * 获取首页轮播图列表
     *
     * @return 首页轮播图列表
     */
    @NoLogin
    @Operation(summary = "首页轮播图列表", description = "首页轮播图列表")
    @GetMapping("/getIndexCarouselImageList")
    @SuppressWarnings("unchecked")
    public List<IndexCarouselImageDTO> getIndexCarouselImageList() {
        List<IndexCarouselImageDTO> result = mobileCacheService.getIndexCarouselImageList();
        if (result != null) {
            return result;
        }
        return indexCarouselImageService.getIndexCarouselImageList();
    }

    /**
     * 获取首页商品列表
     *
     * @return 首页商品列表
     */
    @NoLogin
    @Operation(summary = "获取首页商品列表", description = "获取首页商品列表")
    @GetMapping("/getIndexProductList")
    @SuppressWarnings("unchecked")
    public List<IndexProductDTO> getIndexProductList(@Parameter(description = "商品类型")
    @RequestParam("type") int type) {
        List<IndexProductDTO> result = mobileCacheService.getIndexProductList(type);
        if (result != null) {
            return result;
        }
        return indexProductService.getIndexProductList(type);
    }

    /**
     * 获取首页公告列表
     *
     * @return 公告列表
     */
    @NoLogin
    @Operation(summary = "获取首页公告列表", description = "获取首页公告列表")
    @GetMapping("/getIndexNoticeList")
    @SuppressWarnings("unchecked")
    public List<IndexNoticeDTO> getIndexNoticeList() {
        List<IndexNoticeDTO> result = mobileCacheService.getIndexNoticeList();
        if (result != null) {
            return result;
        }
        return indexNoticeService.getIndexNoticeList();
    }

    /**
     * 根据条件搜索公告列表
     *
     * @param requestPageEntity 条件
     * @return 公告列表
     */
    @NoLogin
    @Operation(summary = "根据条件搜索公告列表", description = "根据条件搜索公告列表")
    @PostMapping("/searchIndexNoticeByPage")
    public ResponsePageEntity<IndexNoticeDTO> searchIndexNoticeByPage(@RequestBody RequestPageEntity requestPageEntity) {
        String key = "search_index_notice_" + JSON.toJSONString(requestPageEntity);
        ResponsePageEntity<IndexNoticeDTO> result = (ResponsePageEntity<IndexNoticeDTO>) indexPageCache.getIfPresent(key);
        if (result != null) {
            return result;
        }
        result = indexNoticeService.searchIndexNoticeByPage(requestPageEntity);
        if (result != null) {
            indexPageCache.put(key, result);
        }
        return result;
    }
    /**
     * 查询公告详情
     *
     * @param id 公告系统ID
     * @return 公告详情
     */
    @NoLogin
    @Operation(summary = "查询公告详情", description = "查询公告详情")
    @GetMapping("/getIndexNoticeDetail")
    public IndexNoticeDetailDTO getIndexNoticeDetail(@Parameter(description = "公告ID")
    @RequestParam("id") Long id) {
        IndexNoticeDetailWebEntity indexNoticeDetailEntity = indexNoticeService.getIndexNoticeDetail(id);
        if (Objects.nonNull(indexNoticeDetailEntity)) {
            return BeanUtil.toBean(indexNoticeDetailEntity, IndexNoticeDetailDTO.class);
        }
        return null;
    }
}