package cn.net.mall.product.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品组实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-09-07 17:28:47
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductGroupEntity extends BaseProductEntity {

    /**
     * 商品组名称
     */
    private String name;

    /**
     * 规格
     */
    private String model;

    /**
     * hash值
     */
    private String hash;

    /**
     * 是否新创建的商品组
     */
    private Boolean isNew;

    /**
     * 逻辑删除ID，默认是0，表示未删除
     */
    private Long delId;
}
