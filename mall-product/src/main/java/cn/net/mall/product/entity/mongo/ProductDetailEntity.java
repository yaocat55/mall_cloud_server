package cn.net.mall.product.entity.mongo;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 商品详情实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-07-07 15:14:11
 */
@Document(collection = "ProductDetailEntity")
@Schema(name = "商品详情实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDetailEntity extends BaseEntity {

    /**
     * 商品ID
     */
    @Indexed
    @Schema(name = "商品ID")
    private Long productId;

    /**
     * 商品详情
     */
    @Schema(name = "商品详情")
    private String detail;
}
