package cn.net.mall.product.controller.internal;

import cn.net.mall.product.dto.ProductDTO;
import cn.net.mall.product.dto.ProductDetailDTO;
import cn.net.mall.product.dto.ShoppingCartDTO;
import cn.net.mall.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部商品接口
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>order-service（订单服务）— 下单时查询商品信息</li>
 *   <li>recommend-service（推荐服务）— 获取商品数据构建推荐列表</li>
 * </ul>
 * <b>不对外暴露</b>，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-商品", description = "内部微服务：order-service、recommend-service 通过 Feign 调用")
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

    @Operation(summary = "批量扣减商品库存",
               description = "内部服务：由 order-service 提交订单时通过 Feign 调用，批量扣减商品库存")
    @PostMapping("/reduceStockBatch")
    public void reduceStockBatch(@RequestBody List<ShoppingCartDTO> items) {
        productService.reduceStockBatch(items);
    }
}
