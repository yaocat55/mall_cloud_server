package cn.net.mall.product.controller.internal;

import cn.net.mall.product.entity.IndexCarouselImageEntity;
import cn.net.mall.product.service.IndexCarouselImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部首页轮播图接口
 *
* **调用方：**
 *   - admin-bff（管理后台）— 首页轮播图 CRUD 操作
 *
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-首页轮播图", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/indexCarouselImage")
public class IndexCarouselImageInternalController {

    private final IndexCarouselImageService indexCarouselImageService;

    public IndexCarouselImageInternalController(IndexCarouselImageService indexCarouselImageService) {
        this.indexCarouselImageService = indexCarouselImageService;
    }

    @Operation(summary = "新增首页轮播图",
               description = "内部服务：由 admin-bff 通过 Feign 调用，新增首页轮播图记录")
    @PostMapping("/insert")
    public int insert(@RequestBody IndexCarouselImageEntity indexCarouselImageEntity) {
        return indexCarouselImageService.insert(indexCarouselImageEntity);
    }

    @Operation(summary = "修改首页轮播图",
               description = "内部服务：由 admin-bff 通过 Feign 调用，修改首页轮播图信息")
    @PostMapping("/update")
    public int update(@RequestBody IndexCarouselImageEntity indexCarouselImageEntity) {
        return indexCarouselImageService.update(indexCarouselImageEntity);
    }

    @Operation(summary = "批量删除首页轮播图",
               description = "内部服务：由 admin-bff 通过 Feign 调用，根据ID集合批量删除首页轮播图")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return indexCarouselImageService.deleteByIds(ids);
    }
}
