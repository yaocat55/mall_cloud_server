package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.ProductConditionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台商品管理 BFF 控制器.
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/product")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品列表、分类、品牌、单位、属性、首页管理")
public class AdminProductController {

    private final ProductFeignClient productFeignClient;

    @Operation(summary = "分页查询商品列表",
               description = "多条件分页查询商品列表",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/page")
    public ResponsePageEntity<?> searchByPage(@RequestBody ProductConditionDTO condition) {
        return productFeignClient.searchByPage(condition);
    }

    @Operation(summary = "查询商品详情",
               description = "根据 ID 查询单个商品的完整信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/detail")
    public Object findById(@RequestParam("id") Long id) {
        return productFeignClient.findById(id);
    }
}
