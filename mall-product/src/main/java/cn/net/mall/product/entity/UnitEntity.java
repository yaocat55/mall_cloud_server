package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单位实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-05-09 14:43:55
 */
@Schema(description = "单位实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UnitEntity extends BaseEntity {


    /**
     * 单位名称
     */
    @Schema(description = "单位名称", example = "测试数据")
    private String name;
}
