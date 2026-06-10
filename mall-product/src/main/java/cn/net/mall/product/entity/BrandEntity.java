package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 品牌实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-05-09 14:43:55
 */
@Schema(name = "品牌实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BrandEntity extends BaseEntity {


    /**
     * 品牌名称
     */
    @Schema(name = "品牌名称")
    private String name;
}
