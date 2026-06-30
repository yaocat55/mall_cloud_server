package cn.net.mall.product.entity;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品组属性查询条件实体
 *
 * @date 2024-09-07 17:28:48
 */
@Data
@Schema(description = "商品分组属性")

public class ProductGroupAttributeConditionEntity extends RequestConditionEntity {

    /**
     * ID集合
     */
    @Schema(description = "系统ID列表")
    private List<Long> idList;

    /**
     * ID
     */
    @Schema(description = "系统ID")
    private Long id;
    /**
     * 商品组ID
     */
    @Schema(description = "product Group Id")
    private Long productGroupId;
    /**
     * 属性ID
     */
    @Schema(description = "attribute Id")
    private Long attributeId;
    /**
     * 属性值ID
     */
    @Schema(description = "attribute Value Id")
    private Long attributeValueId;
    /**
     * 创建人ID
     */
    @Schema(description = "create User Id")
    private Long createUserId;
    /**
     * 创建人名称
     */
    @Schema(description = "create User Name")
    private String createUserName;
    /**
     * 修改人ID
     */
    @Schema(description = "update User Id")
    private Long updateUserId;
    /**
     * 修改人名称
     */
    @Schema(description = "update User Name")
    private String updateUserName;
    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(description = "是否删除")
    private Integer isDel;
}
