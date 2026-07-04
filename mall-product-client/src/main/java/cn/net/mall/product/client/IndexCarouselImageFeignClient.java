package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

/**
 * 首页轮播图 Feign 客户端
 *
 * @date 2025/07/03
 */
@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "indexCarouselImageFeignClient")
public interface IndexCarouselImageFeignClient {

    @Operation(summary = "分页查询首页轮播图", description = "按条件分页查询首页轮播图列表，请求参数包含 pageNo、pageSize 及可选筛选条件（如图片标题、状态等）")
    @PostMapping("/v1/indexCarouselImage/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "新增首页轮播图", description = "新增首页轮播图记录，请求体包含图片地址、标题、排序、状态等字段")
    @PostMapping("/v1/indexCarouselImage/insert")
    int insert(@RequestBody Object entity);

    @Operation(summary = "修改首页轮播图", description = "修改首页轮播图记录，根据 ID 更新图片地址、标题、排序、状态等字段")
    @PostMapping("/v1/indexCarouselImage/update")
    int update(@RequestBody Object entity);

    @Operation(summary = "删除首页轮播图", description = "根据 ID 列表批量删除首页轮播图记录")
    @PostMapping("/v1/indexCarouselImage/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
