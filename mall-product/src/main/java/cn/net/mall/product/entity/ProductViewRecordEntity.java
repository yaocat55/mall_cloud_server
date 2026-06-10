package cn.net.mall.product.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品浏览记录实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-09-04 15:12:10
 */
@Data
public class ProductViewRecordEntity extends UserProductEntity {

    /**
     * 访问次数
     */
    @Schema(name = "访问次数")
    private int viewCount;
}
