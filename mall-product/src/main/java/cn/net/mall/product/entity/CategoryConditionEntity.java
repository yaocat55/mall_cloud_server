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
@Schema(name = "分类查询条件实体")
@Data
public class CategoryConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

    /**
     * ID集合
     */
    @Schema(name = "ID集合")
    private List<Long> idList;

    /**
     * 父分类ID
     */
    @Schema(name = "父分类ID")
    private Long parentId;

    /**
     * 定时任务名称
     */
    @Schema(name = "定时任务名称")
    private String name;

    /**
     * 层级
     */
    @Schema(name = "层级")
    private Integer level;

    /**
     * 是否叶子节点
     */
    @Schema(name = "是否叶子节点")
    private Integer isLeaf;

    /**
     * 创建人ID
     */
    @Schema(name = "创建人ID")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(name = "创建人名称")
    private String createUserName;

    /**
     * 创建日期
     */
    @Schema(name = "创建日期")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Schema(name = "修改人ID")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(name = "修改人名称")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(name = "修改时间")
    private Date updateTime;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(name = "是否删除 1：已删除 0：未删除")
    private Integer isDel;

    /**
     * 是否查询整课分类树
     */
    @Schema(name = "是否查询整课分类树")
    private Boolean queryTree;
}
