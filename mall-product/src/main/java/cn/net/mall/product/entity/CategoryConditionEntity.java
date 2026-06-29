package cn.net.mall.product.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 分类查询条件实体
 *
 * @date 2024-05-09 14:43:55
 */
@Schema(description = "分类查询条件实体")
@Data
public class CategoryConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * ID集合
     */
    @Schema(description = "ID集合", example = "0")
    private List<Long> idList;

    /**
     * 父分类ID
     */
    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    /**
     * 定时任务名称
     */
    @Schema(description = "定时任务名称", example = "测试数据")
    private String name;

    /**
     * 层级
     */
    @Schema(description = "层级", example = "1")
    private Integer level;

    /**
     * 是否叶子节点
     */
    @Schema(description = "是否叶子节点", example = "0")
    private Integer isLeaf;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID", example = "1")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(description = "创建人名称", example = "admin")
    private String createUserName;

    /**
     * 创建日期
     */
    @Schema(description = "创建日期", example = "2024-01-01 00:00:00")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Schema(description = "修改人ID", example = "1")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(description = "修改人名称", example = "admin")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间", example = "2024-01-01 00:00:00")
    private Date updateTime;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(description = "是否删除 1：已删除 0：未删除", example = "0")
    private Integer isDel;

    /**
     * 是否查询整课分类树
     */
    @Schema(description = "是否查询整课分类树", example = "false")
    private Boolean queryTree;
}
