package cn.net.mall.recommend.controller.mobile;

import cn.net.mall.product.dto.ProductSearchResultDTO;
import cn.net.mall.recommend.service.RecommendService;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.entity.auth.JwtUserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "移动端-推荐", description = "移动端：商品推荐列表")
@RestController
@RequestMapping("/v1/mobile/recommend")
@Validated
@AllArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @Operation(summary = "获取当前登录用户喜好的商品列表", description = "获取当前登录用户喜好的商品列表")
    @GetMapping("/recommendProduct")
    public List<ProductSearchResultDTO> recommendProduct() {
        return recommendService.recommendProduct();
    }

    @Operation(summary = "基于商品的相似推荐", description = "根据商品ID推荐相似商品")
    @GetMapping("/byItem")
    public List<ProductSearchResultDTO> recommendByItem(@Parameter(description = "商品ID")
    @RequestParam("productId") Long productId,
                                                        @Parameter(description = "返回数量")
    @RequestParam(value = "topN", required = false) Integer topN) {
        return recommendService.recommendByItem(productId, topN);
    }

    @Operation(summary = "基于相同爱好的用户推荐", description = "根据当前用户的相似用户推荐其喜欢的商品")
    @GetMapping("/byUser")
    public List<ProductSearchResultDTO> recommendByUser(@Parameter(description = "返回数量")
    @RequestParam(value = "topN", required = false) Integer topN) {
        JwtUserEntity jwt = FillUserUtil.getCurrentUserInfoOrNull();
        Long userId = jwt == null ? null : jwt.getId();
        if (userId == null) {
            return List.of();
        }
        return recommendService.recommendByUser(userId, topN);
    }
}
