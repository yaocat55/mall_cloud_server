package cn.net.mall.product.controller.mobile;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.annotation.NoLogin;
import cn.net.mall.product.cache.MobileCacheService;
import cn.net.mall.product.dto.CategoryDTO;
import cn.net.mall.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * web分类操作
 *
 * @date 2024/11/1 下午4:40
 */
@Tag(name = "移动端分类相关接口", description = "移动端分类相关接口")
@AllArgsConstructor
@RestController
@RequestMapping("/v1/mobile/category")
@Validated
public class MobileCategoryController {

    private final CategoryService categoryService;
    private final MobileCacheService mobileCacheService;


    /**
     * 根据父分类ID查询分类列表
     *
     * @param parentId 父分类ID
     * @return 分类列表
     */
    @NoLogin
    @Operation(summary = "根据父分类ID查询分类列表", description = "根据父分类ID查询分类列表")
    @GetMapping("/getCategoryByParentId")
    public List<CategoryDTO> getCategoryByParentId(@RequestParam("parentId") Long parentId) {
        List<CategoryDTO> result = mobileCacheService.getCategoryList(parentId);
        if (result != null) {
            return result;
        }
        return BeanUtil.copyToList(categoryService.getCategoryByParentId(parentId), CategoryDTO.class);
    }
}
