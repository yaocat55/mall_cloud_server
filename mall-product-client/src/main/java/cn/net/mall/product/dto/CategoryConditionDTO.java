package cn.net.mall.product.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 分类查询条件 DTO
 *
 * 用于 mall-admin-bff 通过 Feign 调用分页查询分类列表或分类树
 */
@Schema(description = "分类查询条件 DTO")
@Data
public class CategoryConditionDTO extends RequestConditionEntity {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "ID集合")
    private List<Long> idList;

    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    @Schema(description = "分类名称", example = "测试数据")
    private String name;

    @Schema(description = "层级", example = "1")
    private Integer level;

    @Schema(description = "是否叶子节点", example = "0")
    private Integer isLeaf;

    @Schema(description = "是否查询整棵分类树", example = "false")
    private Boolean queryTree;
}
