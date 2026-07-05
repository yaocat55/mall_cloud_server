package cn.net.mall.product.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 品牌 DTO
 * 
* 用于 mall-admin-api 通过 Feign 调用获取品牌数据
 */
@Schema(description = "品牌 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BrandDTO extends BaseEntity {

    /**
     * 品牌名称
     */
    @Schema(description = "品牌名称", example = "测试数据")
    private String name;
}
