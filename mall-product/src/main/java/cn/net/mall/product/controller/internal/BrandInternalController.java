package cn.net.mall.product.controller.internal;

import cn.net.mall.product.entity.BrandEntity;
import cn.net.mall.product.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部品牌接口
 *
* **调用方：**
 *   - admin-bff（管理后台）— 品牌 CRUD 操作
 *
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-品牌", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/brand")
public class BrandInternalController {

    private final BrandService brandService;

    public BrandInternalController(BrandService brandService) {
        this.brandService = brandService;
    }

    @Operation(summary = "新增品牌",
               description = "内部服务：由 admin-bff 通过 Feign 调用，新增品牌记录")
    @PostMapping("/insert")
    public int insert(@RequestBody BrandEntity brandEntity) {
        return brandService.insert(brandEntity);
    }

    @Operation(summary = "修改品牌",
               description = "内部服务：由 admin-bff 通过 Feign 调用，修改品牌信息")
    @PostMapping("/update")
    public int update(@RequestBody BrandEntity brandEntity) {
        return brandService.update(brandEntity);
    }

    @Operation(summary = "批量删除品牌",
               description = "内部服务：由 admin-bff 通过 Feign 调用，根据ID集合批量删除品牌")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return brandService.deleteByIds(ids);
    }
}
