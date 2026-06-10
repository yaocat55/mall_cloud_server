package cn.net.mall.product.entity;

import cn.net.mall.annotation.ValidSensitiveWordField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-05-09 14:43:56
 */
@Schema(name = "商品实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductEntity extends BaseProductEntity {

    /**
     * 商品组ID
     */
    @Schema(name = "商品组ID")
    private Long productGroupId;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    @Schema(name = "分类ID")
    private Long categoryId;

    /**
     * 分类名称
     */
    @Schema(name = "分类名称")
    private String categoryName;

    /**
     * 品牌ID
     */
    @NotNull(message = "品牌ID不能为空")
    @Schema(name = "品牌ID")
    private Long brandId;

    /**
     * 品牌名称
     */
    @Schema(name = "品牌名称")
    private String brandName;

    /**
     * 单位ID
     */
    @NotNull(message = "单位ID不能为空")
    @Schema(name = "单位ID")
    private Long unitId;

    /**
     * 单位名称
     */
    @Schema(name = "单位名称")
    private String unitName;

    /**
     * 商品名称
     */
    @NotEmpty(message = "商品名称不能为空")
    @Schema(name = "商品名称")
    @ValidSensitiveWordField
    private String name;

    /**
     * 规格
     */
    @Schema(name = "规格")
    @ValidSensitiveWordField
    private String model;

    /**
     * 规格hash值
     */
    @Schema(name = "规格hash值")
    private String hash;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Schema(name = "数量")
    private Integer quantity;

    /**
     * 剩余库存
     */
    @Schema(name = "剩余库存")
    private Integer remainQuantity;

    /**
     * 库存
     */
    @Schema(name = "库存")
    private Integer stock;

    /**
     * 销量
     */
    @Schema(name = "销量")
    private Integer saleCount;

    /**
     * 价格
     */
    @NotNull(message = "数量不能为空")
    @Schema(name = "价格")
    private BigDecimal price;

    /**
     * 封面图片url
     */
    @Schema(name = "封面图片url")
    private String coverUrl;

    /**
     * 商品组属性集合
     */
    @Size(message = "商品组集合不能为空")
    @Schema(name = "商品组集合")
    private List<AttributeValueEntity> spuAttributeEntityList;

    /**
     * 商品属性集合
     */
    @Size(message = "商品属性集合不能为空")
    @Schema(name = "商品属性集合")
    private List<AttributeValueEntity> skuAttributeEntityList;

    /**
     * 商品图片
     */
    @Schema(name = "商品图片")
    private List<ProductPhotoEntity> productPhotoEntityList;

    /**
     * 是否新创建的商品
     */
    private Boolean isNew;

    /**
     * 属性值组合
     */
    private String attributeValueIds;

    /**
     * 封面图片
     */
    private List<String> cover;

    /**
     * 轮播图
     */
    private List<String> swiper;

    /**
     * 详情
     */
    @ValidSensitiveWordField
    private String detail;

    /**
     * 商品组实体
     */
    private ProductGroupEntity productGroupEntity;

    /**
     * 逻辑删除ID，默认是0，表示未删除
     */
    private Long delId;
}
