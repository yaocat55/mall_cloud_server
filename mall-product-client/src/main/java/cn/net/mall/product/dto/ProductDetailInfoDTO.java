package cn.net.mall.product.dto;

import lombok.Data;

import java.util.List;


/**
 * 商品详情实体
 *
 * @date 2024/9/3 下午4:32
 */
@Data
public class ProductDetailInfoDTO extends ProductDTO {

    /**
     * 是否已收藏商品
     */
    private Boolean isFavorites;

    /**
     * 购物车商品数量
     */
    private int totalCartNum;

    /**
     * 商品评价统计
     */
    private ProductCommentStatisticDTO productCommentStatistic;

    /**
     * 属性集合
     */
    private List<ProductGroupAttributeDTO> groupAttributeList;

    /**
     * 同一组的商品列表
     */
    private List<ProductDTO> groupProductList;
}
