package cn.net.mall.product.controller.internal;

import cn.net.mall.product.entity.ProductCommentEntity;
import cn.net.mall.product.service.ProductCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部商品评论接口
 *
* **调用方：**
 *   - admin-bff（管理后台）— 商品评论 CRUD 操作
 *
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-商品评论", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/productComment")
public class ProductCommentInternalController {

    private final ProductCommentService productCommentService;

    public ProductCommentInternalController(ProductCommentService productCommentService) {
        this.productCommentService = productCommentService;
    }

    @Operation(summary = "新增商品评论",
               description = "内部服务：由 admin-bff 通过 Feign 调用，新增商品评论记录")
    @PostMapping("/insert")
    public int insert(@RequestBody ProductCommentEntity productCommentEntity) {
        return productCommentService.insert(productCommentEntity);
    }

    @Operation(summary = "修改商品评论",
               description = "内部服务：由 admin-bff 通过 Feign 调用，修改商品评论信息")
    @PostMapping("/update")
    public int update(@RequestBody ProductCommentEntity productCommentEntity) {
        return productCommentService.update(productCommentEntity);
    }

    @Operation(summary = "批量删除商品评论",
               description = "内部服务：由 admin-bff 通过 Feign 调用，根据ID集合批量删除商品评论")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return productCommentService.deleteByIds(ids);
    }
}
