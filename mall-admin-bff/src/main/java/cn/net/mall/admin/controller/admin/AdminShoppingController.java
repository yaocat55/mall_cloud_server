package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.client.CommentFeignClient;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/v1/shopping")
@RequiredArgsConstructor
@Tag(name = "管理后台-商品评价", description = "商品评论管理接口，需携带 Bearer Token")
public class AdminShoppingController {

    private final CommentFeignClient commentFeignClient;

    @Operation(summary = "分页查询商品评论", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productComment/page")
    public ApiResult<ResponsePageEntity<?>> searchCommentPage(@RequestBody Map c) {
        return ApiResultUtil.success(commentFeignClient.searchByPage(c));
    }

    @Operation(summary = "新增商品评论", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productComment/insert")
    public ApiResult<Integer> insertComment(@RequestBody Object e) {
        return ApiResultUtil.success(commentFeignClient.insert(e));
    }

    @Operation(summary = "修改商品评论", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productComment/update")
    public ApiResult<Integer> updateComment(@RequestBody Object e) {
        return ApiResultUtil.success(commentFeignClient.update(e));
    }

    @Operation(summary = "删除商品评论", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productComment/delete")
    public ApiResult<Integer> deleteComment(@RequestBody @NotNull List ids) {
        return ApiResultUtil.success(commentFeignClient.deleteByIds(ids));
    }

    @Operation(summary = "查询商品评论详情", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/productComment/detail")
    public ApiResult<Object> findCommentById(@RequestParam("id") Long id) {
        return ApiResultUtil.success(commentFeignClient.findById(id));
    }
}
