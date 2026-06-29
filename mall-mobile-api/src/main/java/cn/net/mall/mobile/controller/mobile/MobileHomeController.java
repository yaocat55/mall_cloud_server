package cn.net.mall.mobile.controller.mobile;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.entity.RequestPageEntity;
import cn.net.mall.product.client.CategoryFeignClient;
import cn.net.mall.product.client.IndexFeignClient;
import cn.net.mall.product.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 移动端首页 BFF 控制器
 * 聚合轮播图、公告、推荐商品等首页数据
 */
@Slf4j
@RestController
@RequestMapping("/mobile/v1/home")
@RequiredArgsConstructor
@Tag(name = "移动端-首页", description = "首页数据聚合接口")
public class MobileHomeController {

    private final IndexFeignClient indexFeignClient;
    private final CategoryFeignClient categoryFeignClient;

    @Operation(summary = "获取首页聚合数据", description = "一次接口获取轮播图、分类导航、公告、推荐商品等所有首页数据")
    @GetMapping("/index")
    public Map<String, Object> getIndexData() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            result.put("carouselList", indexFeignClient.getIndexCarouselImageList());
        } catch (Exception e) {
            log.warn("获取轮播图失败", e);
            result.put("carouselList", Collections.emptyList());
        }
        try {
            // 获取根分类作为首页分类导航
            result.put("categoryList", categoryFeignClient.getCategoryByParentId(0L));
        } catch (Exception e) {
            log.warn("获取分类导航失败", e);
            result.put("categoryList", Collections.emptyList());
        }
        try {
            result.put("noticeList", indexFeignClient.getIndexNoticeList());
        } catch (Exception e) {
            log.warn("获取公告列表失败", e);
            result.put("noticeList", Collections.emptyList());
        }
        try {
            result.put("productList", indexFeignClient.getIndexProductList(0));
        } catch (Exception e) {
            log.warn("获取推荐商品失败", e);
            result.put("productList", Collections.emptyList());
        }
        return result;
    }

    @Operation(summary = "获取轮播图列表")
    @GetMapping("/carousel")
    public List<IndexCarouselImageDTO> getCarousel() {
        return indexFeignClient.getIndexCarouselImageList();
    }

    @Operation(summary = "获取公告列表")
    @GetMapping("/notices")
    public List<IndexNoticeDTO> getNotices() {
        return indexFeignClient.getIndexNoticeList();
    }

    @Operation(summary = "分页查询公告")
    @PostMapping("/notice/page")
    public ResponsePageEntity<IndexNoticeDTO> searchNoticeByPage(@RequestBody RequestPageEntity req) {
        return indexFeignClient.searchIndexNoticeByPage(req);
    }

    @Operation(summary = "获取公告详情")
    @GetMapping("/notice/detail")
    public IndexNoticeDetailDTO getNoticeDetail(@RequestParam("id") Long id) {
        return indexFeignClient.getIndexNoticeDetail(id);
    }

    @Operation(summary = "获取首页商品列表", description = "按类型获取首页展示商品")
    @GetMapping("/products")
    public List<IndexProductDTO> getIndexProducts(@RequestParam("type") int type) {
        return indexFeignClient.getIndexProductList(type);
    }
}
