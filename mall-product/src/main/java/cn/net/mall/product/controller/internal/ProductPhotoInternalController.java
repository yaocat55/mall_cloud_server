package cn.net.mall.product.controller.internal;

import cn.net.mall.product.entity.ProductPhotoEntity;
import cn.net.mall.product.service.ProductPhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部商品图片接口
 *
* **调用方：**
 *   - admin-bff（管理后台）— 商品图片 CRUD 操作
 *
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-商品图片", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/productPhoto")
public class ProductPhotoInternalController {

    private final ProductPhotoService productPhotoService;

    public ProductPhotoInternalController(ProductPhotoService productPhotoService) {
        this.productPhotoService = productPhotoService;
    }

    @Operation(summary = "新增商品图片",
               description = "内部服务：由 admin-bff 通过 Feign 调用，新增商品图片记录")
    @PostMapping("/insert")
    public int insert(@RequestBody ProductPhotoEntity productPhotoEntity) {
        return productPhotoService.insert(productPhotoEntity);
    }

    @Operation(summary = "修改商品图片",
               description = "内部服务：由 admin-bff 通过 Feign 调用，修改商品图片信息")
    @PostMapping("/update")
    public int update(@RequestBody ProductPhotoEntity productPhotoEntity) {
        return productPhotoService.update(productPhotoEntity);
    }

    @Operation(summary = "批量删除商品图片",
               description = "内部服务：由 admin-bff 通过 Feign 调用，根据ID集合批量删除商品图片")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return productPhotoService.deleteByIds(ids);
    }
}
