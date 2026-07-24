package cn.net.mall.inventory.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "仓库查询条件")
public class WarehouseConditionEntity extends RequestConditionEntity {

    @Schema(description = "仓库名称", example = "华东仓")
    private String name;

    @Schema(description = "仓库编码", example = "WH-EAST")
    private String code;

    @Schema(description = "仓库状态 1:启用 0:停用", example = "1")
    private Integer status;
}
