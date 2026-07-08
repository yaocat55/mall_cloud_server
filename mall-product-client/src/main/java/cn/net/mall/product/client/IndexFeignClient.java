package cn.net.mall.product.client;

import cn.net.mall.entity.RequestPageEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.IndexCarouselImageDTO;
import cn.net.mall.product.dto.IndexNoticeDTO;
import cn.net.mall.product.dto.IndexNoticeDetailDTO;
import cn.net.mall.product.dto.IndexProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

/**
 * 首页相关接口
 *
 * @date 2025/5/11 12:01
 */
@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "indexFeignClient")
public interface IndexFeignClient {

    /**
     * 获取首页轮播图列表
     *
     * @return 首页轮播图列表
     */
    @Operation(summary = "首页轮播图列表", description = "首页轮播图列表")
    @GetMapping("/v1/internal/index/carouselList")
    List<IndexCarouselImageDTO> getIndexCarouselImageList();

    /**
     * 获取首页公告列表
     *
     * @return 首页公告列表
     */
    @Operation(summary = "获取首页公告列表", description = "获取首页公告列表")
    @GetMapping("/v1/internal/index/noticeList")
    List<IndexNoticeDTO> getIndexNoticeList();

    /**
     * 根据条件搜索公告列表
     *
     * @param requestPageEntity 条件
     * @return 公告列表
     */
    @Operation(summary = "根据条件搜索公告列表", description = "根据条件搜索公告列表")
    @PostMapping("/v1/mobile/index/searchIndexNoticeByPage")
    ResponsePageEntity<IndexNoticeDTO> searchIndexNoticeByPage(@RequestBody RequestPageEntity requestPageEntity);

    /**
     * 查询公告详情
     *
     * @param id 公告系统ID
     * @return 公告详情
     */
    @Operation(summary = "查询公告详情", description = "查询公告详情")
    @GetMapping("/v1/mobile/index/getIndexNoticeDetail")
    IndexNoticeDetailDTO getIndexNoticeDetail(@RequestParam("id") Long id);

    /**
     * 获取首页商品列表
     *
     * @return 首页商品列表
     */
    @Operation(summary = "获取首页商品列表", description = "获取首页商品列表")
    @GetMapping("/v1/internal/index/productList")
    List<IndexProductDTO> getIndexProductList(@RequestParam("type") int type);
}
