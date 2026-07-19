package cn.net.mall.product.controller.internal;

import cn.net.mall.product.entity.ProductGroupEntity;
import cn.net.mall.product.service.ProductGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部商品组接口
 *
* **调用方：**
 *   - admin-bff（管理后台）— 商品组 CRUD 操作
 *
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-商品组", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/productGroup")
public class ProductGroupInternalController {

    private final ProductGroupService productGroupService;

    public ProductGroupInternalController(ProductGroupService productGroupService) {
        this.productGroupService = productGroupService;
    }

    @Operation(summary = "新增商品组",
               description = "内部服务：由 admin-bff 通过 Feign 调用，新增商品组记录")
    @PostMapping("/insert")
    public int insert(@RequestBody ProductGroupEntity productGroupEntity) {
        return productGroupService.insert(productGroupEntity);
    }

    @Operation(summary = "修改商品组",
               description = "内部服务：由 admin-bff 通过 Feign 调用，修改商品组信息")
    @PostMapping("/update")
    public int update(@RequestBody ProductGroupEntity productGroupEntity) {
        return productGroupService.update(productGroupEntity);
    }

    @Operation(summary = "批量删除商品组",
               description = "内部服务：由 admin-bff 通过 Feign 调用，根据ID集合批量删除商品组")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return productGroupService.deleteByIds(ids);
    }
}
