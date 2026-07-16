package cn.net.mall.product.controller.product;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.ProductCommentConditionEntity;
import cn.net.mall.product.entity.ProductCommentEntity;
import cn.net.mall.product.service.ProductCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品评论管理端控制器（FeignClient 调用）.
 */
@Tag(name = "商品评论管理", description = "管理端商品评论 CRUD")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/productComment")
public class ProductCommentController {

    private final ProductCommentService productCommentService;

    @Operation(summary = "分页查询商品评论", description = "管理端分页查询商品评论列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<?> searchByPage(@RequestBody ProductCommentConditionEntity condition) {
        return productCommentService.searchByPage(condition);
    }

    @Operation(summary = "新增商品评论", description = "管理端新增商品评论")
    @PostMapping("/insert")
    public int insert(@RequestBody ProductCommentEntity entity) {
        return productCommentService.insert(entity);
    }

    @Operation(summary = "修改商品评论", description = "管理端修改商品评论")
    @PostMapping("/update")
    public int update(@RequestBody ProductCommentEntity entity) {
        return productCommentService.update(entity);
    }

    @Operation(summary = "删除商品评论", description = "管理端批量删除商品评论")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return productCommentService.deleteByIds(ids);
    }

    @Operation(summary = "查询商品评论详情", description = "管理端根据ID查询商品评论详情")
    @GetMapping("/findById")
    public Object findById(@RequestParam("id") Long id) {
        return productCommentService.findById(id);
    }
}
