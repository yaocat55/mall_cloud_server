package cn.net.mall.product.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品组实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-09-07 17:28:47
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "商品分组")

public class ProductGroupEntity extends BaseProductEntity {

    /**
     * 商品组名称
     */
    @Schema(description = "名称", example = "string")
    private String name;

    /**
     * 规格
     */
    @Schema(description = "model", example = "string")
    private String model;

    /**
     * hash值
     */
    @Schema(description = "hash", example = "string")
    private String hash;

    /**
     * 是否新创建的商品组
     */
    @Schema(description = "is New", example = "true")
    private Boolean isNew;

    /**
     * 逻辑删除ID，默认是0，表示未删除
     */
    @Schema(description = "del Id", example = "0")
    private Long delId;
}
