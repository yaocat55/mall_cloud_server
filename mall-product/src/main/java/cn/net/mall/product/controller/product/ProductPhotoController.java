package cn.net.mall.product.controller.product;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.ProductPhotoDTO;
import cn.net.mall.product.entity.ProductPhotoConditionEntity;
import cn.net.mall.product.entity.ProductPhotoEntity;
import cn.net.mall.product.service.ProductPhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品图片 接口层
 *
 * @date 2024-05-09 14:43:56
 */
@Tag(name = "商品图片操作", description = "商品图片接口")
@RestController
@RequestMapping("/v1/productPhoto")
public class ProductPhotoController {

    private final ProductPhotoService productPhotoService;

    public ProductPhotoController(ProductPhotoService productPhotoService) {
        this.productPhotoService = productPhotoService;
    }

    /**
     * 通过id查询商品图片信息
     *
     * @param id 系统ID
     * @return 商品图片信息
     */
    @Operation(summary = "通过id查询商品图片信息", description = "通过id查询商品图片信息")
    @GetMapping("/findById")
    public ProductPhotoEntity findById(Long id) {
        return productPhotoService.findById(id);
    }

    /**
     * 通过商品id集合批量查询商品图片信息
     *
     * @param productIds 商品ID
     * @return 商品图片信息
     */
    @Operation(summary = "通过商品id集合批量查询商品图片信息", description = "通过商品id集合批量查询商品图片信息")
    @PostMapping("/findByProductIds")
    public List<ProductPhotoDTO> findByProductIds(@RequestBody List<Long> productIds) {
        return productPhotoService.findByProductIds(productIds);
    }

    /**
     * 根据条件查询商品图片列表
     *
     * @param productPhotoConditionEntity 条件
     * @return 商品图片列表
     */
    @Operation(summary = "根据条件查询商品图片列表", description = "根据条件查询商品图片列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<ProductPhotoEntity> searchByPage(@RequestBody ProductPhotoConditionEntity productPhotoConditionEntity) {
        return productPhotoService.searchByPage(productPhotoConditionEntity);
    }


    /**
     * 添加商品图片
     *
     * @param productPhotoEntity 商品图片实体
     * @return 影响行数
     */
    @Operation(summary = "添加商品图片", description = "添加商品图片")
    @PostMapping("/insert")
    public int insert(@RequestBody ProductPhotoEntity productPhotoEntity) {
        return productPhotoService.insert(productPhotoEntity);
    }

    /**
     * 修改商品图片
     *
     * @param productPhotoEntity 商品图片实体
     * @return 影响行数
     */
    @Operation(summary = "修改商品图片", description = "修改商品图片")
    @PostMapping("/update")
    public int update(@RequestBody ProductPhotoEntity productPhotoEntity) {
        return productPhotoService.update(productPhotoEntity);
    }

    /**
     * 批量删除商品图片
     *
     * @param ids 商品图片ID集合
     * @return 影响行数
     */
    @Operation(summary = "批量删除商品图片", description = "批量删除商品图片")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return productPhotoService.deleteByIds(ids);
    }
}
