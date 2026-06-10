package cn.net.mall.product.client;

import cn.net.mall.annotation.NoLogin;
import cn.net.mall.product.dto.CategoryDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
    @NoLogin
    @Operation(summary = "根据父分类ID查询分类列表", description = "根据父分类ID查询分类列表")
    @GetMapping("/v1/mobile/category/getCategoryByParentId")
    List<CategoryDTO> getCategoryByParentId(@RequestParam("parentId") Long parentId);
}
