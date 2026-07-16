package cn.net.mall.product.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 属性 DTO
 *
 * @date 2025/07/15
 */
@Schema(description = "属性DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttributeDTO extends BaseEntity {

    /**
     * 属性名称
     */
    @Schema(description = "属性名称", example = "测试数据")
    private String name;
}
