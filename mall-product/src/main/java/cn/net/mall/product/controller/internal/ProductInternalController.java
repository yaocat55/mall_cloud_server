package cn.net.mall.product.controller.internal;

import cn.net.mall.product.dto.ProductDTO;
import cn.net.mall.product.dto.ProductDetailDTO;
import cn.net.mall.product.dto.ShoppingCartDTO;
import cn.net.mall.product.entity.ProductEntity;
import cn.net.mall.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部商品接口
 *
* **调用方：**
 *
*   - order-service（订单服务）— 下单时查询商品信息
 *   - recommend-service（推荐服务）— 获取商品数据构建推荐列表
 *   - admin-bff（管理后台）— 商品 CRUD 操作
 *
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-商品", description = "内部微服务：order-service、recommend-service、admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/product")
public class ProductInternalController {

    private final ProductService productService;

    public ProductInternalController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "批量查询商品信息",
               description = "内部服务：由 order-service、recommend-service 通过 Feign 调用，根据ID集合批量获取商品基本信息")
    @PostMapping("/findByIds")
    public List<ProductDTO> findByIds(@RequestBody List<Long> ids) {
        return productService.findByIds(ids);
    }

    @Operation(summary = "查询商品详情信息",
               description = "内部服务：由 order-service 通过 Feign 调用，根据ID查询商品详情（不含评价、价格、规格）")
    @GetMapping("/findDetailById")
    public ProductDetailDTO findDetailById(Long id) {
        return productService.findDetailById(id);
    }

    @Operation(summary = "新增商品",
               description = "内部服务：由 admin-bff 通过 Feign 调用，新增商品记录")
    @PostMapping("/insert")
    public void insert(@RequestBody ProductEntity productEntity) {
        productService.insert(productEntity);
    }

    @Operation(summary = "修改商品",
               description = "内部服务：由 admin-bff 通过 Feign 调用，修改商品信息")
    @PostMapping("/update")
    public void update(@RequestBody ProductEntity productEntity) {
        productService.update(productEntity);
    }

    @Operation(summary = "批量删除商品",
               description = "内部服务：由 admin-bff 通过 Feign 调用，根据ID集合批量删除商品")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return productService.deleteByIds(ids);
    }

    @Operation(summary = "获取热销商品",
               description = "内部服务：由 admin-bff 通过 Feign 调用，获取销量最高的商品列表")
    @GetMapping("/getTopSales")
    public List<ProductDTO> getTopSales(@RequestParam(value = "limit", defaultValue = "5") int limit) {
        return productService.getTopProducts(limit);
    }
}
