package cn.net.mall.product.controller.internal;

import cn.net.mall.product.entity.IndexProductEntity;
import cn.net.mall.product.service.IndexProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部首页商品接口
 *
* **调用方：**
 *   - admin-bff（管理后台）— 首页商品 CRUD 操作
 *
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-首页商品", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/indexProduct")
public class IndexProductInternalController {

    private final IndexProductService indexProductService;

    public IndexProductInternalController(IndexProductService indexProductService) {
        this.indexProductService = indexProductService;
    }

    @Operation(summary = "新增首页商品",
               description = "内部服务：由 admin-bff 通过 Feign 调用，新增首页商品记录")
    @PostMapping("/insert")
    public int insert(@RequestBody IndexProductEntity indexProductEntity) {
        return indexProductService.insert(indexProductEntity);
    }

    @Operation(summary = "修改首页商品",
               description = "内部服务：由 admin-bff 通过 Feign 调用，修改首页商品信息")
    @PostMapping("/update")
    public int update(@RequestBody IndexProductEntity indexProductEntity) {
        return indexProductService.update(indexProductEntity);
    }

    @Operation(summary = "批量删除首页商品",
               description = "内部服务：由 admin-bff 通过 Feign 调用，根据ID集合批量删除首页商品")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return indexProductService.deleteByIds(ids);
    }
}
