package cn.net.mall.product.controller.internal;

import cn.net.mall.product.entity.CategoryConditionEntity;
import cn.net.mall.product.entity.CategoryEntity;
import cn.net.mall.product.dto.CategoryTreeDTO;
import cn.net.mall.product.service.CategoryService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部分类接口
 *
* **调用方：**
 *   - admin-bff（管理后台）— 分类 CRUD 操作
 *
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-分类", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/category")
public class CategoryInternalController {

    private final CategoryService categoryService;

    public CategoryInternalController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "新增分类",
               description = "内部服务：由 admin-bff 通过 Feign 调用，新增商品分类记录")
    @PostMapping("/insert")
    public void insert(@RequestBody CategoryEntity categoryEntity) {
        categoryService.insert(categoryEntity);
    }

    @Operation(summary = "修改分类",
               description = "内部服务：由 admin-bff 通过 Feign 调用，修改商品分类信息")
    @PostMapping("/update")
    public int update(@RequestBody CategoryEntity categoryEntity) {
        return categoryService.update(categoryEntity);
    }

    @Operation(summary = "批量删除分类",
               description = "内部服务：由 admin-bff 通过 Feign 调用，根据ID集合批量删除商品分类")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return categoryService.deleteByIds(ids);
    }

    @Operation(summary = "分页查询分类",
               description = "内部服务：由 admin-bff 通过 Feign 调用，分页查询商品分类列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CategoryEntity> searchByPage(@RequestBody CategoryConditionEntity condition) {
        return categoryService.searchByPage(condition);
    }

    @Operation(summary = "查询分类树",
               description = "内部服务：由 admin-bff 通过 Feign 调用，按层级查询分类树结构")
    @PostMapping("/searchByTree")
    public List<CategoryTreeDTO> searchByTree(@RequestBody CategoryConditionEntity condition) {
        return categoryService.searchByTree(condition);
    }
}
