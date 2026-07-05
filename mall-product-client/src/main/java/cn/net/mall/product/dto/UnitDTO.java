package cn.net.mall.product.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单位 DTO
 * 
* 用于 mall-admin-api 通过 Feign 调用获取单位数据
 */
@Schema(description = "单位 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UnitDTO extends BaseEntity {

    /**
     * 单位名称
     */
    @Schema(description = "单位名称", example = "测试数据")
    private String name;
}
