package cn.net.mall.product.entity.web;

import cn.net.mall.entity.EsBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 商品web实体
 *
 * @date 2024/8/15 下午8:57
 */
@Document(indexName = "#{businessConfig.productEsIndexName}")
@Schema(name = "商品web实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductWebEntity extends EsBaseEntity {

    /**
     * 分类ID
     */
    @Schema(name = "分类ID")
    private Long categoryId;

    /**
     * 商品名称
     */
    @Schema(name = "商品名称")
    private String name;

    /**
     * 规格
     */
    @Schema(name = "规格")
    private String model;

    /**
     * 数量
     */
    @Schema(name = "数量")
    private Integer quantity;

    /**
     * 剩余库存
     */
    @Schema(name = "剩余库存")
    private Integer remainQuantity;

    /**
     * 原价
     */
    @Schema(name = "原价")
    private String originalPrice;

    /**
     * 价格
     */
    @Schema(name = "价格")
    @Field(type = FieldType.Keyword)
    private String price;

    /**
     * 封面图片
     */
    @Schema(name = "封面图片")
    private String cover;

    /**
     * 商品类型
     */
    @Schema(name = "商品类型")
    private Integer productType;

    /**
     * 销量
     */
    @Field(type = FieldType.Keyword)
    private String saleQuantity;

    /**
     * 评价数量
     */
    private String commentCount;

    /**
     * 好评率
     */
    @Field(type = FieldType.Keyword)
    private String positiveRating;

    /**
     * 金额
     */
    @Schema(name = "金额")
    private String totalAmount;
}
