package cn.net.mall.product.entity.web;

import cn.net.mall.product.entity.ProductEntity;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 商品详情实体
 *
 * @date 2024/9/3 下午4:32
 */
@Data
@Schema(description = "ProductDetail信息")

public class ProductDetailWebEntity extends ProductEntity {

    /**
     * 是否已收藏商品
     */
    @Schema(description = "is Favorites")
    private Boolean isFavorites;

    /**
     * 购物车商品数量
     */
    @Schema(description = "total Cart Num")
    private int totalCartNum;

    /**
     * 商品评价统计
     */
    @Schema(description = "product Comment Statistic")
    private ProductCommentStatisticWebEntity productCommentStatistic;

    /**
     * 属性集合
     */
    @Schema(description = "group Attribute List")
    private List<ProductGroupAttributeWebEntity> groupAttributeList;

    /**
     * 同一组的商品列表
     */
    @Schema(description = "group Product List")
    private List<ProductEntity> groupProductList;
}
