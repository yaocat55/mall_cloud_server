package cn.net.mall.inventory.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仓库实体
 */
@Schema(description = "仓库实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WarehouseEntity extends BaseEntity {

    @Schema(description = "仓库名称", example = "华东仓")
    private String name;

    @Schema(description = "仓库编码", example = "WH-EAST")
    private String code;

    @Schema(description = "仓库地址", example = "上海市浦东新区XX路XX号")
    private String address;

    @Schema(description = "联系人", example = "张三")
    private String contact;

    @Schema(description = "联系电话", example = "13800000001")
    private String phone;

    @Schema(description = "仓库状态 1:启用 0:停用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "主要仓库")
    private String remark;
}
