package cn.net.mall.product.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 属性值 DTO
 *
 * @date 2025/07/15
 */
@Schema(description = "属性值DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttributeValueDTO extends BaseEntity {

    @NotNull(message = "属性ID不能为空")
    @Schema(description = "属性ID", example = "0")
    private Long attributeId;

    @Schema(description = "属性名称", example = "-")
    private String attributeName;

    @NotEmpty(message = "属性值不能为空")
    @Schema(description = "属性值", example = "-")
    private String value;

    @Schema(description = "排序", example = "1")
    private Integer sort;
}
