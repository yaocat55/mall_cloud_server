package cn.net.mall.product.dto;

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
@Schema(description = "商品基本信息 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDTO {


    /**
     * 商品ID
     */
    @Schema(description = "商品ID", example = "1")
    private Long id;

    /**
     * 商品组ID
     */
    @Schema(description = "商品组ID", example = "0")
    private Long productGroupId;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", example = "0")
    private Long categoryId;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称", example = "-")
    private String categoryName;

    /**
     * 品牌ID
     */
    @NotNull(message = "品牌ID不能为空")
    @Schema(description = "品牌ID", example = "0")
    private Long brandId;

    /**
     * 品牌名称
     */
    @Schema(description = "品牌名称", example = "-")
    private String brandName;

    /**
     * 单位ID
     */
    @NotNull(message = "单位ID不能为空")
    @Schema(description = "单位ID", example = "0")
    private Long unitId;

    /**
     * 单位名称
     */
    @Schema(description = "单位名称", example = "-")
    private String unitName;

    /**
     * 商品名称
     */
    @NotEmpty(message = "商品名称不能为空")
    @Schema(description = "商品名称", example = "测试数据")
    private String name;

    /**
     * 规格
     */
    @Schema(description = "规格", example = "型号")
    private String model;

    /**
     * 规格hash值
     */
    @Schema(description = "规格hash值", example = "-")
    private String hash;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Schema(description = "数量", example = "10")
    private Integer quantity;

    /**
     * 剩余库存
     */
    @Schema(description = "剩余库存", example = "100")
    private Integer remainQuantity;

    /**
     * 库存
     */
    @Schema(description = "库存", example = "100")
    private Integer stock;

    /**
     * 价格
     */
    @NotNull(message = "数量不能为空")
    @Schema(description = "价格", example = "99.99")
    private BigDecimal price;

    /**
     * 封面图片url
     */
    @Schema(description = "封面图片url", example = "https://example.com/cover.png")
    private String coverUrl;

    /**
     * 是否新创建的商品
     */
    @Schema(description = "是否新品")
    private Boolean isNew;

    /**
     * 属性值组合
     */
    @Schema(description = "attribute Value Ids")
    private String attributeValueIds;

    /**
     * 封面图片
     */
    @Schema(description = "封面图URL")
    private List<String> cover;

    /**
     * 轮播图
     */
    @Schema(description = "轮播图列表")
    private List<String> swiper;

    /**
     * 详情
     */
    @Schema(description = "detail")
    private String detail;

    /**
     * 逻辑删除ID，默认是0，表示未删除
     */
    @Schema(description = "del Id")
    private Long delId;

    /**
     * 销量
     */
    @Schema(description = "销量", example = "0")
    private Integer saleCount;
}
