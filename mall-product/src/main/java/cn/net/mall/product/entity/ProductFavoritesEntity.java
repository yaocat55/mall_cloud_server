package cn.net.mall.product.entity;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品收藏实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-09-04 15:12:10
 */
@Data
@Schema(description = "商品收藏")

public class ProductFavoritesEntity extends UserProductEntity {

}
