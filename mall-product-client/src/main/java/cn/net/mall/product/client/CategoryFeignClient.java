package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.CategoryDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

/**
 * 分类相关接口
 *
 * @date 2025/5/13 17:56
 */
@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "categoryFeignClient")
public interface CategoryFeignClient {

    /**
     * 根据父分类ID查询分类列表
     *
     * @param parentId 父分类ID
     * @return 分类列表
     */
    @Operation(summary = "根据父分类ID查询分类列表", description = "根据父分类ID查询分类列表")
    @GetMapping("/v1/mobile/category/getCategoryByParentId")
    List<CategoryDTO> getCategoryByParentId(@RequestParam("parentId") Long parentId);

    @Operation(summary = "分页查询分类（管理端）")
    @PostMapping("/v1/category/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "新增分类（管理端）")
    @PostMapping("/v1/category/insert")
    int insert(@RequestBody Object entity);

    @Operation(summary = "修改分类（管理端）")
    @PostMapping("/v1/category/update")
    int update(@RequestBody Object entity);

    @Operation(summary = "删除分类（管理端）")
    @PostMapping("/v1/category/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);

    @Operation(summary = "查询分类树（管理端）", description = "按层级查询分类树结构")
    @PostMapping("/v1/category/searchByTree")
    List<?> searchByTree(@RequestBody Map<String, Object> condition);
}