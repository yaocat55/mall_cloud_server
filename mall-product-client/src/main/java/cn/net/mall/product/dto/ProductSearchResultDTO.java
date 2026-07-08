package cn.net.mall.product.dto;

import cn.net.mall.entity.EsBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品搜索结果实体
 *
 * @date 2024/8/15 下午8:57
 */
@Schema(description = "商品搜索结果实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductSearchResultDTO extends EsBaseEntity {

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "0")
    private Long categoryId;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", example = "测试数据")
    private String name;

    /**
     * 规格
     */
    @Schema(description = "规格", example = "型号")
    private String model;

    /**
     * 数量
     */
    @Schema(description = "数量", example = "10")
    private Integer quantity;

    /**
     * 剩余库存
     */
    @Schema(description = "剩余库存", example = "100")
    private Integer remainQuantity;

    /**
     * 原价
     */
    @Schema(description = "原价", example = "-")
    private String originalPrice;

    /**
     * 价格
     */
    @Schema(description = "价格", example = "99.99")
    private String price;

    /**
     * 封面图片
     */
    @Schema(description = "封面图片", example = "-")
    private String cover;

    /**
     * 商品类型
     */
    @Schema(description = "商品类型", example = "0")
    private Integer productType;

    /**
     * 销量
     */
    @Schema(description = "销量", example = "string")
    private String saleQuantity;

    /**
     * 评价数量
     */
    @Schema(description = "comment Count", example = "string")
    private String commentCount;

    /**
     * 好评率
     */
    @Schema(description = "好评率", example = "string")
    private String positiveRating;

    /**
     * 金额
     */
    @Schema(description = "金额", example = "99.99")
    private String totalAmount;
}
