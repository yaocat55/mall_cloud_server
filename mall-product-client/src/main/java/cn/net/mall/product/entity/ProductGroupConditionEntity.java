package cn.net.mall.product.entity;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品组查询条件实体
 *
 * @date 2024-09-07 17:28:47
 */
@Data
@Schema(description = "商品分组")

public class ProductGroupConditionEntity extends RequestConditionEntity {

    /**
     * ID集合
     */
    @Schema(description = "系统ID列表", example = "string")
    private List<Long> idList;

    /**
     * ID
     */
    @Schema(description = "系统ID", example = "0")
    private Long id;
    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "0")
    private Long categoryId;


    /**
     * 单位ID
     */
    @Schema(description = "单位ID", example = "0")
    private Long unitId;

    /**
     * 商品组名称
     */
    @Schema(description = "名称", example = "0")
    private Long name;

    /**
     * 规格
     */
    @Schema(description = "model", example = "string")
    private String model;

    /**
     * hash值
     */
    @Schema(description = "hash", example = "string")
    private String hash;
    /**
     * 创建人ID
     */
    @Schema(description = "create User Id", example = "0")
    private Long createUserId;
    /**
     * 创建人名称
     */
    @Schema(description = "create User Name", example = "string")
    private String createUserName;
    /**
     * 修改人ID
     */
    @Schema(description = "update User Id", example = "0")
    private Long updateUserId;
    /**
     * 修改人名称
     */
    @Schema(description = "update User Name", example = "string")
    private String updateUserName;
    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(description = "是否删除", example = "0")
    private Integer isDel;

    /**
     * 商品组查询条件
     */
    @Schema(description = "product Group Entities", example = "string")
    private List<ProductGroupEntity> productGroupEntities;

    /**
     * 逻辑删除ID，默认是0，表示未删除
     */
    @Schema(description = "del Id", example = "0")
    private Long delId;
}
