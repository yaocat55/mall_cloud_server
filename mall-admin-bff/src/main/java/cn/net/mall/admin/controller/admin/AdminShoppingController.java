package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.client.CommentFeignClient;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import cn.net.mall.product.dto.ProductCommentConditionDTO;

@Slf4j
@RestController
@RequestMapping("/admin/v1/shopping")
@RequiredArgsConstructor
@Tag(name = "评价管理", description = "商品评论审核、回复")
public class AdminShoppingController {

    private final CommentFeignClient commentFeignClient;

    @Operation(summary = "分页查询商品评论", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productComment/page")
    public ApiResult<ResponsePageEntity<?>> searchCommentPage(@RequestBody ProductCommentConditionDTO c) {
        return ApiResultUtil.success(commentFeignClient.searchByPage(c));
    }

    @Operation(summary = "查询商品评论详情", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/productComment/detail")
    public ApiResult<Object> findCommentById(@RequestParam("id") Long id) {
        return ApiResultUtil.success(commentFeignClient.findById(id));
    }
}
