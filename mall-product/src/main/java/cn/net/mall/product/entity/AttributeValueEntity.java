package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 属性值实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-05-09 14:43:55
 */
@Schema(name = "属性值实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttributeValueEntity extends BaseEntity {


    /**
     * 属性ID
     */
    @NotNull(message = "属性ID不能为空 ")
    @Schema(name = "属性ID")
    private Long attributeId;

    /**
     * 属性名称
     */
    @Schema(name = "属性名称")
    private String attributeName;

    /**
     * 属性值
     */
    @NotEmpty(message = "属性值不能为空")
    @Schema(name = "属性值")
    private String value;

    /**
     * 排序
     */
    @Schema(name = "排序")
    private Integer sort;
}
