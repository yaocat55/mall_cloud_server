package cn.net.mall.product.controller.product;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.annotation.ValidSensitiveWord;
import cn.net.mall.product.dto.ProductSearchConditionDTO;
import cn.net.mall.product.dto.ProductSearchResultDTO;
import cn.net.mall.product.entity.ProductConditionEntity;
import cn.net.mall.product.entity.ProductEntity;
import cn.net.mall.product.entity.web.ProductConditionWebEntity;
import cn.net.mall.product.entity.web.ProductWebEntity;
import cn.net.mall.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 商品 接口层
 *
 * @date 2024-05-09 14:43:56
 */
@Tag(name = "商品管理", description = "管理后台：商品 CRUD 操作")
@RestController
@RequestMapping("/v1/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 通过id查询商品信息
     *
     * @param id 系统ID
     * @return 商品信息
     */
    @Operation(summary = "通过id查询商品信息", description = "通过id查询商品信息")
    @GetMapping("/findById")
    public ProductEntity findById(Long id) {
        return productService.findById(id);
    }

    /**
     * 根据条件查询商品列表
     \     * @param productConditionEntity 条件
     * @return 商品列表
     */
    @Operation(summary = "根据条件查询商品列表", description = "根据条件查询商品列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<ProductEntity> searchByPage(@RequestBody ProductConditionEntity productConditionEntity) {
        return productService.searchByPage(productConditionEntity);
    }

    /**
     * 根据条件从ES中分页搜索商品列表
     *
     * @param productSearchConditionDTO 商品信息
     * @return 商品集合
     */
    @Operation(summary = "根据条件从ES中分页搜索商品列表", description = "根据条件从ES中分页搜索商品列表")
    @PostMapping("/searchFromES")
    public ResponsePageEntity<ProductSearchResultDTO> searchFromES(@RequestBody ProductSearchConditionDTO productSearchConditionDTO) {
        return productService.searchFromES(productSearchConditionDTO);
    }


    /**
     * 新增商品
     *
     * @param productEntity 商品实体
     * 3
     * return 影响行数
     */
    @Operation(summary = "新增商品", description = "新增商品")
    @PostMapping("/insert")
    @ValidSensitiveWord
    public void insert(@RequestBody ProductEntity productEntity) {
        productService.insert(productEntity);
    }

    /**
     * 批量创建商品
     *
     * @param productEntityList 批量创建商品
     * @return 影响行数
     */
    @Operation(summary = "批量创建商品", description = "批量创建商品")
    @PostMapping("/generate")
    @ValidSensitiveWord
    public List<ProductEntity> generate(@RequestBody List<ProductEntity> productEntityList) {
        return productService.generate(productEntityList);
    }

    /**
     * 修改商品
     *
     * @param productEntity 商品实体
     * @return 影响行数
     */
    @Operation(summary = "修改商品", description = "修改商品")
    @PostMapping("/update")
    public void update(@RequestBody ProductEntity productEntity) {
        productService.update(productEntity);
    }

    /**
     * 批量删除商品
     *
     * @param ids 商品ID集合
     * @return 影响行数
     */
    @Operation(summary = "批量删除商品", description = "批量删除商品")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return productService.deleteByIds(ids);
    }

    /**
     * 导出商品数据
     *
     * @return 影响行数
     */
    @Operation(summary = "导出商品数据", description = "导出商品数据")
    @PostMapping("/export")
    public void export(@RequestBody ProductConditionEntity productConditionEntity) {
    }
}

