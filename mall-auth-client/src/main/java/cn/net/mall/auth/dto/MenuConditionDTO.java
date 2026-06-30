package cn.net.mall.auth.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 菜单查询条件 DTO
 * <p>
 * 用于 mall-admin-api 通过 Feign 调用查询菜单列表
 */
@Schema(description = "菜单查询条件 DTO")
@Data
public class MenuConditionDTO extends RequestConditionEntity {

    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称", example = "测试数据")
    private String name;

    /**
     * 上级菜单ID
     */
    @Schema(description = "上级菜单ID", example = "0")
    private Long pid;

    /**
     * 上级菜单ID集合
     */
    @Schema(description = "上级菜单ID集合")
    private List<Long> pidList;

    /**
     * 类型 1：目录 2：菜单 3：按钮
     */
    @Schema(description = "类型 1：目录 2：菜单 3：按钮", example = "1")
    private Integer type;

    /**
     * 功能权限
     */
    @Schema(description = "功能权限", example = "-")
    private String permission;

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
     * 修改时间
     */
    @Schema(description = "修改时间", example = "2024-01-01 00:00:00")
    private Date updateTime;
}
