package cn.net.mall.product.dto;

import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 商品详情实体
 *
 * @date 2024/9/3 下午4:32
 */
@Data
@Schema(description = "商品详情信息")

public class ProductDetailInfoDTO extends ProductDTO {

    /**
     * 是否已收藏商品
     */
    @Schema(description = "是否已收藏")
    private Boolean isFavorites;

    /**
     * 购物车商品数量
     */
    @Schema(description = "购物车商品总数")
    private int totalCartNum;

    /**
     * 商品评价统计
     */
    @Schema(description = "商品评论统计")
    private ProductCommentStatisticDTO productCommentStatistic;

    /**
     * 属性集合
     */
    @Schema(description = "分组属性列表")
    private List<ProductGroupAttributeDTO> groupAttributeList;

    /**
     * 同一组的商品列表
     */
    @Schema(description = "分组商品列表")
    private List<ProductDTO> groupProductList;
}
