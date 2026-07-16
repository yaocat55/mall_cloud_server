package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.dto.IdsDTO;
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
import cn.net.mall.product.dto.ProductCommentConditionDTO;
import cn.net.mall.product.dto.ProductCommentDTO;

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

    @Operation(summary = "新增商品评论", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productComment/insert")
    public ApiResult<Integer> insertComment(@RequestBody ProductCommentDTO e) {
        return ApiResultUtil.success(commentFeignClient.insert(e));
    }

    @Operation(summary = "修改商品评论", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productComment/update")
    public ApiResult<Integer> updateComment(@RequestBody ProductCommentDTO e) {
        return ApiResultUtil.success(commentFeignClient.update(e));
    }

    @Operation(summary = "删除商品评论", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productComment/delete")
    public ApiResult<Integer> deleteComment(@RequestBody IdsDTO dto) {
        return ApiResultUtil.success(commentFeignClient.deleteByIds(dto.getIds()));
    }

    @Operation(summary = "查询商品评论详情", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/productComment/detail")
    public ApiResult<Object> findCommentById(@RequestParam("id") Long id) {
        return ApiResultUtil.success(commentFeignClient.findById(id));
    }
}
