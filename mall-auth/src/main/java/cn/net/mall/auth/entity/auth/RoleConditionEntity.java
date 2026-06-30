package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 角色查询条件实体
 *
 * @date 2024-01-08 17:18:18
 */
@Schema(description = "角色查询条件实体")
@Data
public class RoleConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * 名称
     */
    @Schema(description = "名称", example = "测试数据")
    private String name;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "备注")
    private String remark;

    /**
     * 数据权限
     */
    @Schema(description = "数据权限", example = "-")
    private String dataScope;

    /**
     * 角色级别
     */
    @Schema(description = "角色级别", example = "1")
    private Integer level;

    /**
     * 功能权限
     */
    @Schema(description = "功能权限", example = "-")
    private String permission;

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
     * id集合
     */
    @Schema(description = "系统ID列表")
    private List<Long> idList;

    /**
     * 查询条件
     */
    @Schema(description = "blurry")
    private String blurry;
}
