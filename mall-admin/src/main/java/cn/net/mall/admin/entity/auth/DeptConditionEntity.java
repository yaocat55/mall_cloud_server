package cn.net.mall.admin.entity.auth;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 部门查询条件实体
 *
 * @date 2024-01-08 17:18:17
 */
@Schema(description = "部门查询条件实体")
@Data
public class DeptConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * ids
     */
    @Schema(description = "ids", example = "0")
    private List<Long> idList;

    /**
     * 名称
     */
    @Schema(description = "名称", example = "测试数据")
    private String name;

    /**
     * 上级部门
     */
    @Schema(description = "上级部门", example = "0")
    private Long pid;

    /**
     * 有效状态 1:有效 0:无效
     */
    @Schema(description = "有效状态 1:有效 0:无效", example = "1")
    private Integer validStatus;

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
     * 是否查询树
     */
    @Schema(description = "是否查询树", example = "false")
    private Boolean queryTree;
}
