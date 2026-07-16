package cn.net.mall.product.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品组查询条件 DTO
 *
 * @date 2025/07/15
 */
@Schema(description = "商品组查询条件DTO")
@Data
public class ProductGroupConditionDTO extends RequestConditionEntity {

    @Schema(description = "ID集合")
    private List<Long> idList;

    @Schema(description = "系统ID")
    private Long id;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "单位ID")
    private Long unitId;

    @Schema(description = "商品组名称")
    private Long name;

    @Schema(description = "规格")
    private String model;

    @Schema(description = "hash值")
    private String hash;

    @Schema(description = "创建人ID")
    private Long createUserId;

    @Schema(description = "创建人名称")
    private String createUserName;

    @Schema(description = "修改人ID")
    private Long updateUserId;

    @Schema(description = "修改人名称")
    private String updateUserName;

    @Schema(description = "是否删除 1：已删除 0：未删除")
    private Integer isDel;

    @Schema(description = "逻辑删除ID")
    private Long delId;
}
